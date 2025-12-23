package back_end.springboot.service.implement;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import back_end.springboot.component.RedisKeyManager;
import back_end.springboot.repository.FollowsRepository;
import back_end.springboot.repository.PostRepository;
import back_end.springboot.repository.PostViewHistoryRepository;
import back_end.springboot.repository.UserInterestRepository;
import back_end.springboot.repository.UserRepository;
import back_end.springboot.service.InterestBatchService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterestBatchServiceImplement implements InterestBatchService {
    private final UserInterestRepository userInterestRepository;
    private final PostViewHistoryRepository postViewHistoryRepository;
    private final UserRepository userRepository;
    private final FollowsRepository followsRepository;
    private final PostRepository postRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, String> redisTemplateZSet;

    private final ObjectMapper objectMapper;

    private final int DAYS_WINDOW = 30;
    private final int VIEW_DAYS_WINDOW = 7;
    private static final int RECOMMEND_LIMIT = 300;
    private static final double FOLLOW_BASE_SCORE = 10000.0;

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
        try {
            userInterestRepository.updateAiObjectInterests(DAYS_WINDOW);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void collectPostViewInterest() {
        try {
            userInterestRepository.updatePostViewInterests(VIEW_DAYS_WINDOW);
            userInterestRepository.updatePostViewAiInterests(DAYS_WINDOW);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void collectActiveUsers() {
        try {
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            List<String> ids = userRepository.findActiveUsers(DAYS_WINDOW);
            String key = RedisKeyManager.KEY_ACTIVE_USERS;
            ops.set(key, ids);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void collectActiveUsersInterest() {
        try {
            String key = RedisKeyManager.KEY_ACTIVE_USERS;
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            ListOperations<String, Object> lops = redisTemplate.opsForList();
            Object obj = ops.get(key);
            if (obj == null)
                return;
            List<String> ids = objectMapper.convertValue(obj, new TypeReference<List<String>>() {
            });
            for (String userId : ids) {
                List<String> topInterests = new ArrayList<>();
                topInterests.addAll(userInterestRepository.findByUserIdTypeOrderByScoreDesc(userId, "AI_TAG", 5));
                topInterests.addAll(userInterestRepository.findByUserIdTypeOrderByScoreDesc(userId, "HASH_TAG", 5));
                String interestKey = RedisKeyManager.getUserInterestsKey(userId);
                redisTemplate.delete(interestKey);
                if (!topInterests.isEmpty()) {
                    lops.rightPushAll(interestKey, topInterests.toArray());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void collectActiveUsersReadPost() {
        try {
            String key = RedisKeyManager.KEY_ACTIVE_USERS;
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            ListOperations<String, Object> lops = redisTemplate.opsForList();
            Object obj = ops.get(key);
            if (obj == null)
                return;
            List<String> ids = objectMapper.convertValue(obj, new TypeReference<List<String>>() {
            });
            for (String id : ids) {
                String historyKey = RedisKeyManager.getUserReadHistoryKey(id);
                List<Integer> phv = postViewHistoryRepository.findByUserIdDays(id, DAYS_WINDOW);
                if (!phv.isEmpty()) {
                    lops.rightPushAll(historyKey, phv.toArray());
                }
                
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void runPostRankingBatch() {
        ValueOperations<String, Object> opsValue = redisTemplate.opsForValue();
        ListOperations<String, Object> opsList = redisTemplate.opsForList();
        ZSetOperations<String, String> opsSet = redisTemplateZSet.opsForZSet();

        try {
            Object obj = opsValue.get(RedisKeyManager.KEY_ACTIVE_USERS);
            if (obj == null)
                return;
            List<String> userIds = objectMapper.convertValue(obj, new TypeReference<List<String>>() {
            });

            for (String userId : userIds) {
                String interestKey = RedisKeyManager.getUserInterestsKey(userId);
                String readHistoryKey = RedisKeyManager.getUserReadHistoryKey(userId);
                String recommendedKey = RedisKeyManager.getUserFeedKey(userId);
                Set<ZSetOperations.TypedTuple<String>> finalTuples = new HashSet<>();
                List<Integer> readPostIds = new ArrayList<>();
                readPostIds = objectMapper.convertValue(opsList.range(readHistoryKey, 0, -1),
                        new TypeReference<List<Integer>>() {
                        });

                if (readPostIds == null) {
                    readPostIds = Collections.emptyList();
                }

                List<String> followingIds = followsRepository.findByFollowingUserId(userId);
                if (!followingIds.isEmpty()) {
                    List<Object[]> followedResults = postRepository.findFollowedPosts(followingIds, readPostIds,
                            DAYS_WINDOW);

                    finalTuples = followedResults.stream().map(result -> {
                        Integer postId = (Integer) result[0];
                        Timestamp createAt = (Timestamp) result[1];
                        double score = FOLLOW_BASE_SCORE
                                + (1.0 / (System.currentTimeMillis() - createAt.getTime() + 1));
                        return ZSetOperations.TypedTuple.of(String.valueOf(postId), score);
                    }).collect(Collectors.toSet());
                }

                List<String> interestKeywords = objectMapper.convertValue(opsList.range(interestKey, 0, -1),
                        new TypeReference<List<String>>() {
                        });
                if (!interestKeywords.isEmpty()) {
                    List<Object[]> rankedResults = postRepository.findRecommendedPostScores(userId, interestKeywords,
                            readPostIds, RECOMMEND_LIMIT);
                    rankedResults.stream().map(result -> {
                        Integer postId = ((Number) result[0]).intValue();
                        Double rankScore = ((Number) result[1]).doubleValue();
                        return ZSetOperations.TypedTuple.of(String.valueOf(postId), rankScore);
                    }).forEach(finalTuples::add);
                }
                
                String feedPages = RedisKeyManager.getFeedSizeKey(userId);
                if (!finalTuples.isEmpty()) {
                    redisTemplate.delete(recommendedKey);
                    opsSet.add(recommendedKey, finalTuples);
                    opsValue.set(feedPages, (finalTuples.size() + 29) / 30);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
