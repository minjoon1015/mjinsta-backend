package back_end.springboot.service.implement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import back_end.springboot.component.RedisKeyManager;
import back_end.springboot.dto.object.post.PostDto;
import back_end.springboot.dto.object.post.PostImageTagsDto;
import back_end.springboot.dto.object.post.PostTagsDto;
import back_end.springboot.dto.object.user.SimpleUserDto;
import back_end.springboot.dto.response.ResponseDto;
import back_end.springboot.dto.response.feed.GetFeedResponseDto;
import back_end.springboot.entity.PostAttachmentsEntity;
import back_end.springboot.entity.PostAttachmentsUserTagsEntity;
import back_end.springboot.entity.PostEntity;
import back_end.springboot.repository.PostFavoriteRepository;
import back_end.springboot.repository.PostRepository;
import back_end.springboot.service.FeedService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedServiceImplement implements FeedService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, String> redisTemplateZSet;

    private final PostRepository postRepository;
    private final PostFavoriteRepository postFavoriteRepository;

    @Override
    public ResponseEntity<? super GetFeedResponseDto> getFeed(String userId, Integer pages, Integer postId,
            Integer favoriteCount) {
        try {
            List<PostDto> posts = new ArrayList<>();
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            ZSetOperations<String, String> zops = redisTemplateZSet.opsForZSet();
            // ListOperations lops = redisTemplate.opsForList();
            String feedKey = RedisKeyManager.getUserFeedKey(userId);
            String feedSizeKey = RedisKeyManager.getFeedSizeKey(userId);
            String feedPointerKey = RedisKeyManager.getUserFeedPointerKey(userId);

            Object sizeObj = ops.get(feedSizeKey);
            Integer size = (sizeObj != null) ? Integer.valueOf(sizeObj.toString()) : -1;

            if (size == -1) {
                // 3티어 조회로 바로 패스
                posts.addAll(tier3SearchAlgorithm(userId, postId, favoriteCount));
                return GetFeedResponseDto.successForSearchFeed(posts);
            }

            // 캐싱된 게시글이 있지만, 첫 조회일 때
            if (pages == null) {
                Object pointerObj = ops.get(feedPointerKey);
                Integer pointer = (pointerObj != null) ? Integer.valueOf(pointerObj.toString()) : 0;
                if (size >= pointer) {
                    int start = pointer * 30;
                    int end = start + 30 - 1;
                    Set<Integer> feed = zops.reverseRange(feedKey, start, end).stream().map(f -> Integer.valueOf(f))
                            .collect(Collectors.toSet());
                    posts.addAll(getPostsInOrder(feed));
                    int newPointer = pointer + 1;
                    ops.set(RedisKeyManager.getUserFeedPointerKey(userId), newPointer);
                    return GetFeedResponseDto.successForCachingFeed(posts, pointer);
                } else {
                    posts.addAll(tier3SearchAlgorithm(userId, postId, favoriteCount));
                    return GetFeedResponseDto.successForSearchFeed(posts);
                }

            } else {
                Object pointerObj = ops.get(feedPointerKey);
                Integer pointer = Integer.valueOf(pointerObj.toString());
                if (pointer != pages)
                    return ResponseDto.badRequest();
                if (size >= pointer) {
                    int start = pointer * 30;
                    int end = start + 30 - 1;
                    Set<Integer> feed = zops.reverseRange(feedKey, start, end).stream().map(f -> Integer.valueOf(f))
                            .collect(Collectors.toSet());
                    posts.addAll(getPostsInOrder(feed));
                    int newPointer = pointer + 1;
                    ops.set(RedisKeyManager.getUserFeedPointerKey(userId), newPointer);
                    return GetFeedResponseDto.successForCachingFeed(posts, pointer);
                    
                } else {
                    posts.addAll(tier3SearchAlgorithm(userId, postId, favoriteCount));
                    return GetFeedResponseDto.successForSearchFeed(posts);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    public List<PostDto> tier3SearchAlgorithm(String userId, Integer postId, Integer favoriteCount) {
        try {
            List<PostDto> posts = new ArrayList<>();
            List<PostEntity> saved = new ArrayList<>();
            if (postId == null || favoriteCount == null) {
                saved = postRepository.findPopularPostsFallback(userId, 30);        
            }
            else {
                saved = postRepository.findPopularPostsFallbackCursor(userId, postId, favoriteCount, 30);
            }
            for (PostEntity p : saved) {
                PostDto postDto = parsePostDto(p);
                if (postDto != null) {
                    posts.add(postDto);
                }
            }
            return posts;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public PostDto parsePostDto(PostEntity s) {
        SimpleUserDto user = new SimpleUserDto(s.getUser().getId(), s.getUser().getName(), s.getUser().getProfileImage(), false);
        PostDto postDto = new PostDto(s.getId(), user, s.getComment(), s.getLocation(),
                s.getFavoriteCount(), s.getCommentCount(), s.getCreateAt(), null, null);
        try {
            List<PostAttachmentsEntity> postAttachmentsEntities = s.getAttachments();
            List<PostImageTagsDto> imageTags = new ArrayList<>();
            for (PostAttachmentsEntity attachments : postAttachmentsEntities) {
                PostImageTagsDto it = new PostImageTagsDto();
                it.setUrl(attachments.getUrl());
                List<PostAttachmentsUserTagsEntity> attachmentTags = attachments.getAttachmentsTags();
                List<PostTagsDto> tags = attachmentTags.stream()
                        .map(a -> new PostTagsDto(a.getUserId(), a.getXCoordinate(), a.getYCoordinate()))
                        .collect(Collectors.toList());
                it.setTags(tags);
                imageTags.add(it);
            }
            postDto.setImageTags(imageTags);
            postDto.setIsLiked(
                    postFavoriteRepository.findByPostIdAndUserId(s.getId(), s.getUser().getId()) != null ? true : false);
            return postDto;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<PostDto> getPostsInOrder(Set<Integer> ids) {
        if (ids.isEmpty())
            return new ArrayList<>();

        List<PostEntity> saved = postRepository.findAllById(ids);

        Map<Integer, PostEntity> entityMap = saved.stream()
                .collect(Collectors.toMap(PostEntity::getId, s -> s));

        return ids.stream()
                .map(entityMap::get) 
                .filter(Objects::nonNull)
                .map(this::parsePostDto) 
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
