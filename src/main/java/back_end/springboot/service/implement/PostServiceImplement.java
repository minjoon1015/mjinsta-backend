package back_end.springboot.service.implement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import back_end.springboot.common.AlarmType;
import back_end.springboot.common.MessageType;
import back_end.springboot.common.PostTagsType;
import back_end.springboot.component.FileManager;
import back_end.springboot.component.FileValidateManager;
import back_end.springboot.dto.object.ai.AiAnalysisResultDto;
import back_end.springboot.dto.object.alarm.extend.PostCommentAlarmDto;
import back_end.springboot.dto.object.alarm.extend.PostLikeAlarmDto;
import back_end.springboot.dto.object.alarm.extend.PostTagAlarmDto;
import back_end.springboot.dto.object.event.NotificationEvent;
import back_end.springboot.dto.object.post.PostCommentDto;
import back_end.springboot.dto.object.post.PostDto;
import back_end.springboot.dto.object.post.PostImageTagsDto;
import back_end.springboot.dto.object.post.PostMetaDataDto;
import back_end.springboot.dto.object.post.PostPrevDto;
import back_end.springboot.dto.object.post.PostTagsDto;
import back_end.springboot.dto.object.user.SimpleUserDto;
import back_end.springboot.dto.request.post.PostAddViewHistoryRequestDto;
import back_end.springboot.dto.request.post.PostCommentRequestDto;
import back_end.springboot.dto.response.ResponseDto;
import back_end.springboot.dto.response.post.CommentPaginationListResponseDto;
import back_end.springboot.dto.response.post.CommentTopListResponseDto;
import back_end.springboot.dto.response.post.PostCommentResponseDto;
import back_end.springboot.dto.response.post.PostCreateResponseDto;
import back_end.springboot.dto.response.post.PostGetDetailsInfoResponseDto;
import back_end.springboot.dto.response.post.PostGetMeResponseDto;
import back_end.springboot.dto.response.post.PostLikeResponseDto;
import back_end.springboot.entity.AlarmEntity;
import back_end.springboot.entity.PostAttachmentsEntity;
import back_end.springboot.entity.PostAttachmentsUserTagsEntity;
import back_end.springboot.entity.PostCommentEntity;
import back_end.springboot.entity.PostEntity;
import back_end.springboot.entity.PostFavoriteEntity;
import back_end.springboot.entity.PostTagsEntity;
import back_end.springboot.entity.PostViewHistoryEntity;
import back_end.springboot.entity.UserEntity;
import back_end.springboot.repository.AlarmRepository;
import back_end.springboot.repository.PostAttachmentsRepository;
import back_end.springboot.repository.PostRepository;
import back_end.springboot.repository.PostTagsRepository;
import back_end.springboot.repository.PostViewHistoryRepository;
import back_end.springboot.repository.UserRepository;
import back_end.springboot.repository.PostAttachmentsUserTagRepository;
import back_end.springboot.repository.PostCommentRepository;
import back_end.springboot.repository.PostFavoriteRepository;
import back_end.springboot.service.AiAnalysisService;
import back_end.springboot.service.PostService;
import back_end.springboot.service.TagNormalizationService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServiceImplement implements PostService {
    private final ObjectMapper mapper;
    private final ApplicationEventPublisher eventPublisher;
    private final FileManager fileManager;
    private final AiAnalysisService aiAnalysisService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final TagNormalizationService tagNormalizationService;

    private final AlarmRepository alarmRepository;
    private final PostFavoriteRepository postFavoriteRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostAttachmentsRepository postAttachmentRepository;
    private final PostAttachmentsUserTagRepository postAttachmentsUserTagRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostTagsRepository postTagsRepository;
    private final PostViewHistoryRepository postViewHistoryRepository;

    @Override
    @Transactional
    public ResponseEntity<? super PostCreateResponseDto> createPost(String userId, List<MultipartFile> images,
            String data) {
        try {
            PostMetaDataDto metaData = null;
            try {
                metaData = mapper.readValue(data, PostMetaDataDto.class);
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseDto.badRequest();
            }
            UserEntity userEntity = userRepository.findById(userId).orElseGet(null);
            PostEntity savePostEntity = postRepository.save(
                    new PostEntity(userEntity, metaData.getComment(), metaData.getLocation(), 0, 0, LocalDateTime.now(),
                            null));
            LinkedHashMap<String, List<PostTagsDto>> tagsList = metaData.getTags();
            List<String> hashtags = metaData.getHashtags();

            int count = 0;
            UserEntity sender = userRepository.findById(userId).orElse(null);
            if (sender == null) {
                return ResponseDto.databaseError();
            }
            sender.plusPost();
            userRepository.save(sender);
            String folderName = "post" + "/" + savePostEntity.getId();
            Map<String, String> ids = new HashMap<>();
            for (MultipartFile image : images) {
                MessageType type = FileValidateManager.getFileType(image);
                if (type != MessageType.IMAGE) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    return ResponseDto.badRequest();
                }
                // 저장 파일 이름 : post/postId
                String url = fileManager.uploadFile(image, folderName);

                PostAttachmentsEntity saveAttachmentEntity = postAttachmentRepository
                        .save(new PostAttachmentsEntity(savePostEntity, url));
                if (count == 0) {
                    savePostEntity.setProfileImage(url);
                    AiAnalysisResultDto aiAnalysisResultDto = null;
                    try {
                        aiAnalysisResultDto = aiAnalysisService.analyzeImage(url);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    for (String tag : aiAnalysisResultDto.getObjectTag()) {
                        postTagsRepository.save(new PostTagsEntity(savePostEntity.getId(), tag, PostTagsType.AI_TAG));
                    }
                    postRepository.save(savePostEntity);
                }
                List<PostTagsDto> tags = tagsList.get(Integer.toString(count++));
                if (tags != null) {
                    for (PostTagsDto tag : tags) {
                        String id = tag.getUserId(); // 태그한 유저 id
                        boolean existsById = userRepository.existsById(id);
                        if (existsById == false) {
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                            return ResponseDto.badRequest();
                        }
                        Double x = tag.getX();
                        Double y = tag.getY();
                        PostAttachmentsUserTagsEntity pe = postAttachmentsUserTagRepository
                                .save(new PostAttachmentsUserTagsEntity(saveAttachmentEntity, id, x, y,
                                        savePostEntity.getCreateAt()));
                        ids.put(id, pe.getId().toString());
                    }
                }
            }
            if (hashtags != null && !hashtags.isEmpty()) {
                hashtags = tagNormalizationService.extractHeadNouns(hashtags);
                postTagsRepository.saveAll(hashtags.stream().map(h -> new PostTagsEntity(savePostEntity.getId(), h, PostTagsType.HASH_TAG))
                        .collect(Collectors.toList()));
            }
            for (String id : ids.keySet()) {
                eventPublisher.publishEvent(new NotificationEvent(id, "/queue/notify",
                        new PostTagAlarmDto(AlarmType.POST_TAG, savePostEntity.getCreateAt(), sender.getId(),
                                sender.getProfileImage(), savePostEntity.getId())));
                alarmRepository
                        .save(new AlarmEntity(id, AlarmType.POST_TAG, ids.get(id)));
            }
            return PostCreateResponseDto.success();
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseDto.databaseError();
        }
    }

    @Override
    public ResponseEntity<? super PostGetMeResponseDto> getList(String userId, Integer postId) {
        try {
            List<PostPrevDto> list = new ArrayList<>();
            if (postId == null) {
                List<PostEntity> saved = postRepository.findAllById(userId);
                list = saved.stream().map(
                        s -> new PostPrevDto(s.getId(), s.getCommentCount(), s.getFavoriteCount(), s.getProfileImage()))
                        .collect(Collectors.toList());
            } else {
                List<PostEntity> saved = postRepository.findAllByIdPaging(userId, postId);
                list = saved.stream().map(
                        s -> new PostPrevDto(s.getId(), s.getCommentCount(), s.getFavoriteCount(), s.getProfileImage()))
                        .collect(Collectors.toList());
            }
            return PostGetMeResponseDto.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    @Override
    public ResponseEntity<? super PostGetDetailsInfoResponseDto> getDetailsInfo(Integer postId, String userId) {
        try {
            boolean existed = postRepository.existsById(postId);
            if (existed == false)
                return ResponseDto.badRequest();
            PostEntity postEntity = postRepository.findByPostIdDetailsInfo(postId);
            SimpleUserDto user = new SimpleUserDto(postEntity.getUser().getId(), postEntity.getUser().getName(), postEntity.getUser().getProfileImage(), false);
            PostDto postDto = new PostDto(postEntity.getId(), user, postEntity.getComment(),
                    postEntity.getLocation(), postEntity.getFavoriteCount(), postEntity.getCommentCount(),
                    postEntity.getCreateAt(), null, false);
            List<PostAttachmentsEntity> postAttachmentsEntities = postEntity.getAttachments();
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
            PostFavoriteEntity savedFavoriteEntity = postFavoriteRepository
                    .findByPostIdAndUserId(postEntity.getId(), userId);
            if (savedFavoriteEntity != null) {
                postDto.setIsLiked(true);
            }
            postDto.setImageTags(imageTags);
            return PostGetDetailsInfoResponseDto.success(postDto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<? super PostLikeResponseDto> like(String userId, Integer postId) {
        try {
            // userId == sender
            UserEntity senderEntity = userRepository.findById(userId).orElse(null);
            PostEntity savedPostEntity = postRepository.findByIdWithLock(postId).orElse(null);

            if (savedPostEntity == null)
                return ResponseDto.badRequest();
            LocalDateTime createAt = LocalDateTime.now();
            PostFavoriteEntity favoriteEntity = postFavoriteRepository
                    .findByPostIdAndUserId(savedPostEntity.getId(), userId);
            if (favoriteEntity == null) {
                PostFavoriteEntity save = postFavoriteRepository
                        .save(new PostFavoriteEntity(savedPostEntity, userId, createAt));
                savedPostEntity.increaseLike();
                postRepository.save(savedPostEntity);
                if (!senderEntity.getId().equals(savedPostEntity.getUser().getId())) {
                    alarmRepository.save(new AlarmEntity(savedPostEntity.getUser().getId(), AlarmType.POST_LIKE_RECEIVE,
                            Integer.toString(save.getId())));
                    eventPublisher
                            .publishEvent(new NotificationEvent(savedPostEntity.getUser().getId(), "/queue/notify",
                                    new PostLikeAlarmDto(AlarmType.POST_LIKE_RECEIVE, createAt, savedPostEntity.getId(),
                                            new SimpleUserDto(senderEntity.getId(), senderEntity.getName(),
                                                    senderEntity.getProfileImage(), false))));
                }
                return PostLikeResponseDto.success();
            } else {
                postFavoriteRepository.delete(favoriteEntity);
                savedPostEntity.decreaseLike();
                postRepository.save(savedPostEntity);
                AlarmEntity alarmEntity = alarmRepository.findByUserIdAndReferenceId(savedPostEntity.getUser().getId(),
                        Integer.toString(favoriteEntity.getId()), AlarmType.POST_LIKE_RECEIVE);
                if (alarmEntity != null) {
                    alarmRepository.delete(alarmEntity);
                }
                return PostLikeResponseDto.success();
            }
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseDto.databaseError();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<? super PostCommentResponseDto> comment(PostCommentRequestDto requestDto) {
        try {
            UserEntity userEntity = userRepository.findById(requestDto.getUserId()).orElse(null);
            PostEntity savedPost = postRepository.findByIdWithLock(requestDto.getPostId()).orElse(null);
            if (savedPost == null)
                return ResponseDto.databaseError();
            PostCommentEntity postCommentEntity = postCommentRepository.save(new PostCommentEntity(
                    userEntity, savedPost.getId(), requestDto.getComment(), LocalDateTime.now()));
            PostCommentDto postCommentDto = new PostCommentDto(postCommentEntity.getId(), postCommentEntity.getPostId(),
                    postCommentEntity.getContent(), postCommentEntity.getCreateAt(), userEntity);
            savedPost.increaseComment();
            postRepository.save(savedPost);
            if (!requestDto.getUserId().equals(savedPost.getUser().getId())) {
                alarmRepository.save(
                        new AlarmEntity(savedPost.getUser().getId(), AlarmType.POST_COMMENT_RECEIVE,
                                Integer.toString(postCommentEntity.getId())));
                eventPublisher.publishEvent(
                        new NotificationEvent(savedPost.getUser().getId(), "/queue/notify", new PostCommentAlarmDto(
                                AlarmType.POST_COMMENT_RECEIVE, postCommentDto.getCreateAt(), postCommentDto)));
            }
            return PostCommentResponseDto.success(postCommentDto);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseDto.databaseError();
        }
    }

    @Override
    public ResponseEntity<? super CommentPaginationListResponseDto> getCommentPaginationList(String userId,
            Integer postId,
            Integer commentId) {
        try {
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            String key = "post:comment:top-list:" + postId + ":user:" + userId;
            Object Object = ops.get(key);
            int page = 30;
            Pageable pageable = PageRequest.of(0, page);
            List<PostCommentDto> commentDtos = new ArrayList<>();
            commentId = commentId == null ? Integer.MAX_VALUE : commentId;
            if (Object != null) {
                List<PostCommentDto> cached = objectMapper.convertValue(Object,
                        new TypeReference<List<PostCommentDto>>() {
                        });
                List<Integer> ExclusionIds = cached.stream().map(c -> c.getId()).collect(Collectors.toList());
                List<PostCommentEntity> saved = postCommentRepository
                        .findByPostIdAndIdGreaterThanAndIdNotInOrderByCreateAtDesc(postId, commentId, ExclusionIds,
                                pageable);
                commentDtos = saved.stream().map(
                        c -> new PostCommentDto(c.getId(), c.getPostId(), c.getContent(), c.getCreateAt(), c.getUser()))
                        .collect(Collectors.toList());
            } else {
                List<PostCommentEntity> saved = postCommentRepository
                        .findByPostIdAndIdGreaterThanOrderByCreateAtDesc(postId, commentId, pageable);
                commentDtos = saved.stream().map(
                        c -> new PostCommentDto(c.getId(), c.getPostId(), c.getContent(), c.getCreateAt(), c.getUser()))
                        .collect(Collectors.toList());
            }
            return CommentPaginationListResponseDto.success(commentDtos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    @Override
    public ResponseEntity<? super CommentTopListResponseDto> getCommentTopList(String userId, Integer postId) {
        try {
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            String key = "post:comment:top-list:" + postId + ":user:" + userId;
            Object Object = ops.get(key);
            if (Object != null) {
                return CommentTopListResponseDto
                        .success(objectMapper.convertValue(Object, new TypeReference<List<PostCommentDto>>() {
                        }));
            }
            int page = 3;
            Pageable pageable = PageRequest.of(0, page);
            List<PostCommentEntity> pcl = postCommentRepository
                    .findByPostIdAndUserIdOrderByCreateAtDesc(postId, userId, pageable);
            List<PostCommentDto> commentList = pcl.stream().map(pc -> new PostCommentDto(pc.getId(), pc.getPostId(),
                    pc.getContent(), pc.getCreateAt(), pc.getUser())).collect(Collectors.toList());

            if (!commentList.isEmpty()) {
                ops.set(key, commentList, 10, TimeUnit.MINUTES);
            }
            return CommentTopListResponseDto.success(commentList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    @Override
    public void addViewHistory(String userId, PostAddViewHistoryRequestDto requestDto) {
        try {
            postViewHistoryRepository.save(new PostViewHistoryEntity(requestDto.getPostId(), userId,
                    requestDto.getViewedAt(), requestDto.getTimeSpentSeconds()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
