package back_end.springboot.service.implement;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import back_end.springboot.common.AlarmType;
import back_end.springboot.dto.object.alarm.AlarmDto;
import back_end.springboot.dto.object.alarm.extend.FollowAlarmDto;
import back_end.springboot.dto.object.alarm.extend.PostCommentAlarmDto;
import back_end.springboot.dto.object.alarm.extend.PostLikeAlarmDto;
import back_end.springboot.dto.object.alarm.extend.PostTagAlarmDto;
import back_end.springboot.dto.object.post.PostCommentDto;
import back_end.springboot.dto.object.user.SimpleUserDto;
import back_end.springboot.dto.response.ResponseDto;
import back_end.springboot.dto.response.alarm.GetAlarmListResponseDto;
import back_end.springboot.entity.AlarmEntity;
import back_end.springboot.entity.FollowsEntity;
import back_end.springboot.entity.PostAttachmentsUserTagsEntity;
import back_end.springboot.entity.PostCommentEntity;
import back_end.springboot.entity.PostFavoriteEntity;
import back_end.springboot.entity.UserEntity;
import back_end.springboot.repository.AlarmRepository;
import back_end.springboot.repository.FollowsRepository;
import back_end.springboot.repository.PostAttachmentsUserTagRepository;
import back_end.springboot.repository.PostCommentRepository;
import back_end.springboot.repository.PostFavoriteRepository;
import back_end.springboot.repository.UserRepository;
import back_end.springboot.service.AlarmService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlarmServiceImplement implements AlarmService {
    private final AlarmRepository alarmRepository;
    private final PostFavoriteRepository postFavoriteRepository; 
    private final PostAttachmentsUserTagRepository postAttachmentsUserTagRepository;
    private final PostCommentRepository postCommentRepository;
    private final FollowsRepository followsRepository;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<? super GetAlarmListResponseDto> getList(String id) {
        try {
            List<AlarmDto> alarmList = new ArrayList<>();
            List<AlarmEntity> list = alarmRepository.findAllByUserId(id);
            if (!list.isEmpty()) {
                for (AlarmEntity l : list) {
                    if (l.getAlarmType() == AlarmType.FOLLOW) {
                        FollowsEntity followsEntity = followsRepository.findById(Integer.parseInt(l.getReferenceId()))
                                .orElse(null);
                        if (followsEntity == null) {
                            alarmRepository.delete(l);
                            continue;
                        }
                        UserEntity getUser = userRepository.findById(followsEntity.getFollowerId()).orElse(null);
                        FollowAlarmDto followAlarmDto = new FollowAlarmDto(AlarmType.FOLLOW, l.getCreate_at(),
                                getUser.getId(), getUser.getProfileImage());
                        alarmList.add(followAlarmDto);
                    } else if (l.getAlarmType() == AlarmType.POST_TAG) {
                        PostAttachmentsUserTagsEntity tag = postAttachmentsUserTagRepository.findById(Integer.parseInt(l.getReferenceId())).orElse(null);
                        UserEntity user = userRepository.findById(tag.getUserId()).orElse(null);
                        alarmList.add(new PostTagAlarmDto(AlarmType.POST_TAG, l.getCreate_at(), user.getId(), user.getProfileImage(), tag.getPostAttachments().getPost().getId()));
                    } else if (l.getAlarmType() == AlarmType.POST_LIKE_RECEIVE) {     
                        PostFavoriteEntity favoriteEntity = postFavoriteRepository.findById(Integer.parseInt(l.getReferenceId())).orElse(null);
                        UserEntity user = userRepository.findById(favoriteEntity.getUserId()).orElse(null);
                        alarmList.add(new PostLikeAlarmDto(AlarmType.POST_LIKE_RECEIVE, l.getCreate_at(), favoriteEntity.getPost().getId(), new SimpleUserDto(user.getId(), user.getName(), user.getProfileImage(), false)));
                    } else if (l.getAlarmType() == AlarmType.POST_COMMENT_RECEIVE) {
                        PostCommentEntity commentEntity = postCommentRepository.findById(Integer.parseInt(l.getReferenceId())).orElse(null);
                        alarmList.add(new PostCommentAlarmDto(AlarmType.POST_COMMENT_RECEIVE, l.getCreate_at(), new PostCommentDto(commentEntity.getId(), commentEntity.getPostId(), commentEntity.getContent(), commentEntity.getCreateAt(), commentEntity.getUser())));
                    }
                }
            }

            return GetAlarmListResponseDto.success(alarmList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

}
