package back_end.springboot.service.implement;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import back_end.springboot.entity.PostEntity;
import back_end.springboot.entity.PostFavoriteEntity;
import back_end.springboot.entity.UserInterestEntity;
import back_end.springboot.repository.PostFavoriteRepository;
import back_end.springboot.repository.UserInterestRepository;
import back_end.springboot.service.InterestBatchService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterestBatchServiceImplement implements InterestBatchService {
    private final UserInterestRepository userInterestRepository;
    private final PostFavoriteRepository postFavoriteRepository;
    private final ObjectMapper objectMapper;

    private final int DAYS_WINDOW = 30;

    @Override
    public void collectHashTagInterest() {
        try {
            userInterestRepository.updateHashTagInterests(DAYS_WINDOW);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void collectAiObjectInterest() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(DAYS_WINDOW);
        List<PostFavoriteEntity> recentFavorites = postFavoriteRepository.findRecentFavoritesWithPost(cutoffDate);
        Map<String, UserInterestEntity> aggregateInterests = new HashMap<>();

        for (PostFavoriteEntity pf : recentFavorites) {
            PostEntity postEntity = pf.getPost();
            if (postEntity == null || postEntity.getAiObjectTag() == null) continue;
            
            String userId = pf.getUserId();
            double weight = calculateDecayWeight(pf.getCreateAt());
            
            try {
                List<String> aiTags = objectMapper.readValue(postEntity.getAiObjectTag(), new TypeReference<>() {
                });

                int scoreToAdd = (int) Math.round(10 * weight);

                for (String tag : aiTags) {
                    String key = userId + ":" + tag;

                    UserInterestEntity interestEntity = aggregateInterests.getOrDefault(key, new UserInterestEntity(userId, tag, "AI_OBJECT", 0));
                    interestEntity.setScore(interestEntity.getScore() + scoreToAdd);
                    aggregateInterests.put(key, interestEntity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            userInterestRepository.saveAll(aggregateInterests.values());
        }
    }

    private double calculateDecayWeight(LocalDateTime createAt) {
        long daysAgo = ChronoUnit.DAYS.between(createAt.toLocalDate(), LocalDate.now());
        return Math.max(0.1, (30 - daysAgo) / 30.0); // 최소 감쇠 값은 0.1
    }
}
