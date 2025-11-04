package back_end.springboot.service.implement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import back_end.springboot.common.AlarmType;
import back_end.springboot.dto.object.alarm.alarm.FollowAlarmDto;
import back_end.springboot.dto.object.user.EditUserDto;
import back_end.springboot.dto.object.user.SimpleUserDto;
import back_end.springboot.dto.object.user.UserDetailsDto;
import back_end.springboot.dto.object.user.UserDto;
import back_end.springboot.dto.request.user.EditPasswordRequestDto;
import back_end.springboot.dto.request.user.FollowRequestDto;
import back_end.springboot.dto.request.user.UnFollowRequestDto;
import back_end.springboot.dto.response.ResponseDto;
import back_end.springboot.dto.response.user.EditInfoResponseDto;
import back_end.springboot.dto.response.user.EditPasswordResponseDto;
import back_end.springboot.dto.response.user.FollowResponseDto;
import back_end.springboot.dto.response.user.GetEditInfoResponseDto;
import back_end.springboot.dto.response.user.GetRecommendListResponseDto;
import back_end.springboot.dto.response.user.GetUserDetailsInfoResponseDto;
import back_end.springboot.dto.response.user.GetUserListForKeywordResponseDto;
import back_end.springboot.dto.response.user.UnFollowResponseDto;
import back_end.springboot.dto.response.user.UpdateProfileUrlResponseDto;
import back_end.springboot.dto.response.user.UserMeResponseDto;
import back_end.springboot.entity.AlarmEntity;
import back_end.springboot.entity.ChatRoomParticipantEntity;
import back_end.springboot.entity.FollowsEntity;
import back_end.springboot.entity.UserEntity;
import back_end.springboot.repository.AlarmRepository;
import back_end.springboot.repository.ChatRoomParticipantRepository;
import back_end.springboot.repository.ChatRoomRepository;
import back_end.springboot.repository.FollowsRepository;
import back_end.springboot.repository.UserRepository;
import back_end.springboot.repository.projection.SimpleUserProjection;
import back_end.springboot.repository.projection.UserDetailsInfoProjection;
import back_end.springboot.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImplement implements UserService {
    private final UserRepository userRepository;
    private final FollowsRepository followsRepository;
    private final AlarmRepository alarmRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<? super UserMeResponseDto> me(String id) {
        try {
            if (id == null || id.equals(""))
                return ResponseDto.badRequest();
            boolean existed = userRepository.existsById(id);
            if (!existed)
                return UserMeResponseDto.invalidToken();
            UserEntity userEntity = userRepository.findById(id).orElse(null);
            UserDto user = new UserDto(userEntity.getId(), userEntity.getName(), userEntity.getComment(),
                    userEntity.getFollowCount(), userEntity.getFollowerCount(), userEntity.getPostCount(),
                    userEntity.getProfileImage());
            return UserMeResponseDto.success(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<? super FollowResponseDto> follow(FollowRequestDto requestDto, String id) {
        try {
            String followingId = requestDto.getFollowingId();
            UserEntity followingEntity = userRepository.findById(followingId).orElse(null);
            UserEntity followerEntity = userRepository.findById(id).orElse(null);
            if (followingEntity == null || followerEntity == null)
                return ResponseDto.badRequest();
            Optional<FollowsEntity> FindFollowsEntity = followsRepository.findByFollowerIdAndFollowingId(id,
                    followingId);
            if (!FindFollowsEntity.isEmpty())
                return ResponseDto.badRequest();
            FollowsEntity followsEntity = new FollowsEntity(id, followingId);
            FollowsEntity saveEntity = followsRepository.save(followsEntity);
            followingEntity.plusFollower();
            followerEntity.plusFollow();
            userRepository.save(followerEntity);
            userRepository.save(followingEntity);

            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            String key = "recommend" + id;
            Object saveRecommendList = ops.get(key);
            if (saveRecommendList != null) {
                List<SimpleUserDto> list = objectMapper.convertValue(saveRecommendList,
                        new TypeReference<List<SimpleUserDto>>() {
                        });
                List<SimpleUserDto> updateList = list.stream().map((l) -> {
                    if (l.getId().equals(requestDto.getFollowingId())) {
                        l.setFollowing(true);
                    }
                    return l;
                }).collect(Collectors.toList());
                ops.set(key, updateList);
            }

            AlarmEntity alarmEntity = new AlarmEntity(followingEntity.getId(), AlarmType.FOLLOW, saveEntity.getId());
            alarmRepository.save(alarmEntity);

            FollowAlarmDto followAlarmDto = new FollowAlarmDto(AlarmType.FOLLOW, LocalDateTime.now(),
                    followerEntity.getId(), followerEntity.getProfileImage());
            simpMessagingTemplate.convertAndSendToUser(followingEntity.getId(), "/queue/notify", followAlarmDto);
            return FollowResponseDto.success();

        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseDto.databaseError();
        }
    }

    @Override
    public ResponseEntity<? super UpdateProfileUrlResponseDto> updateProfileImage(String id, String url) {
        try {
            if (id == null || id.equals(""))
                return ResponseDto.badRequest();
            UserEntity userEntity = userRepository.findById(id).orElse(null);
            if (userEntity == null)
                return ResponseDto.badRequest();
            userEntity.updateProfileImage(url);
            userRepository.save(userEntity);
            List<ChatRoomParticipantEntity> saved = chatRoomParticipantRepository.findAllByUserId(id);
            if (saved.size() > 0) {
                for (ChatRoomParticipantEntity c : saved) {
                    redisTemplate.delete("chatRoom:members:" + Integer.toString(c.getId().getChatroomId()));
                }
            }
            return UpdateProfileUrlResponseDto.success(userEntity.getProfileImage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    @Override
    public String existProfileImage(String id) {
        UserEntity userEntity = userRepository.findById(id).orElse(null);
        if (userEntity == null)
            return null;
        return userEntity.getProfileImage();
    }

    @Override
    public ResponseEntity<? super GetUserListForKeywordResponseDto> getUserList(String id, String keyword) {
        try {
            Map<String, SimpleUserDto> result = new HashMap<>();
            List<SimpleUserProjection> searchList = userRepository.findAllByIdInFollowing(id, keyword);
            List<SimpleUserDto> simpleUsers = searchList.stream()
                    .map(p -> new SimpleUserDto(p.getId(), p.getName(), p.getProfileImage(), true))
                    .collect(Collectors.toList());
            for (SimpleUserDto s : simpleUsers) {
                result.put(s.getId(), s);
            }
            searchList.clear();
            if (simpleUsers.size() < 10) {
                int count = 10 - result.size();
                searchList = userRepository.findAllByIdLimit(keyword, count);
                if (searchList.size() > 0) {
                    List<SimpleUserDto> additional = searchList.stream()
                            .map(p -> new SimpleUserDto(p.getId(), p.getName(), p.getProfileImage(), false))
                            .collect(Collectors.toList());
                    simpleUsers.addAll(additional);
                    for (SimpleUserDto s : simpleUsers) {
                        result.putIfAbsent(s.getId(), s);
                    }
                }
            }
            List<SimpleUserDto> list = new ArrayList<>();
            for (String key : result.keySet()) {
                list.add(result.get(key));
            }
            return GetUserListForKeywordResponseDto.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    @Override
    public ResponseEntity<? super GetUserDetailsInfoResponseDto> getUserDetailsInfo(String searchId, String userId) {
        try {
            UserDetailsInfoProjection user = userRepository.findUserDetailsInfoById(searchId, userId);
            if (user == null) {
                return ResponseDto.badRequest();
            }
            return GetUserDetailsInfoResponseDto.success(new UserDetailsDto(user.getId(), user.getName(),
                    user.getProfileImage(), user.getComment(), user.getFollowCount(), user.getFollowerCount(),
                    user.getPostCount(), user.getIsFollowed() == 1 ? true : false));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<? super UnFollowResponseDto> unFollow(UnFollowRequestDto requestDto, String id) {
        try {
            FollowsEntity saved = followsRepository.findByFollowerIdAndFollowingId(id, requestDto.getUnFollowingId())
                    .orElse(null);
            if (saved == null)
                return ResponseDto.badRequest();
            UserEntity user = userRepository.findById(saved.getFollowerId()).orElse(null);
            UserEntity unFollowingUser = userRepository.findById(saved.getFollowingId()).orElse(null);
            user.minusFollow();
            unFollowingUser.minusFollower();
            List<UserEntity> list = new ArrayList<>();
            list.add(user);
            list.add(unFollowingUser);
            userRepository.saveAll(list);
            followsRepository.delete(saved);
            AlarmEntity entity = alarmRepository.findByUserIdAndReferenceId(unFollowingUser.getId(), Integer.toString(saved.getId()));
            alarmRepository.delete(entity);
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            String key = "recommend:" + id;
            Object saveRecommendList = ops.get(key);
            if (saveRecommendList != null) {
                List<SimpleUserDto> redisList = objectMapper.convertValue(saveRecommendList,
                        new TypeReference<List<SimpleUserDto>>() {
                        });
                List<SimpleUserDto> updateList = redisList.stream().map((l) -> {
                    if (l.getId().equals(requestDto.getUnFollowingId())) {
                        l.setFollowing(false);
                    }
                    return l;
                }).collect(Collectors.toList());
                ops.set(key, updateList);
            }
            return UnFollowResponseDto.success();
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseDto.databaseError();
        }
    }

    @Override
    public ResponseEntity<? super GetRecommendListResponseDto> getRecommendList(String id, Boolean isAll) {
        try {
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            String key = "recommend:" + id;
            Object saveRecommendList = ops.get(key);

            if (saveRecommendList != null) {
                List<SimpleUserDto> list = objectMapper.convertValue(saveRecommendList,
                        new TypeReference<List<SimpleUserDto>>() {
                        });
                if (!isAll) {
                    return GetRecommendListResponseDto.success(list.subList(0, Math.min(5, list.size())));
                }
                return GetRecommendListResponseDto.success(list);
            }

            List<SimpleUserProjection> users = followsRepository.findRecommendUsersById(id);
            List<SimpleUserDto> list = users.stream()
                    .map((u) -> new SimpleUserDto(u.getId(), u.getName(), u.getProfileImage(), false))
                    .collect(Collectors.toList());
            ops.set(key, list, 24, TimeUnit.HOURS);
            if (!isAll) {
                return GetRecommendListResponseDto.success(list.subList(0, Math.min(5, list.size())));
            }
            return GetRecommendListResponseDto.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    @Override
    public ResponseEntity<? super GetEditInfoResponseDto> getEditInfo(String id) {
        try {
            UserEntity userEntity = userRepository.findById(id).orElse(null);
            return GetEditInfoResponseDto.success(new EditUserDto(userEntity.getName(), userEntity.getSex(),
                    userEntity.getComment(), userEntity.getAddress(), userEntity.getAddressDetail()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<? super EditInfoResponseDto> editInfo(String id, EditUserDto requestDto) {
        try {
            UserEntity userEntity = userRepository.findById(id).orElse(null);
            if (!userEntity.getName().equals(requestDto.getName())) {
                userEntity.setName(requestDto.getName());
            }
            if (!userEntity.getSex().equals(requestDto.getSex())) {
                userEntity.setSex(requestDto.getSex());
            }
            if (!userEntity.getComment().equals(requestDto.getComment())) {
                userEntity.setComment(requestDto.getComment());
            }
            if (!userEntity.getAddress().equals(requestDto.getAddress())) {
                userEntity.setAddress(requestDto.getAddress());
            }
            if (!userEntity.getAddressDetail().equals(requestDto.getAddressDetail())) {
                userEntity.setAddressDetail(requestDto.getAddressDetail());
            }
            userRepository.save(userEntity);
             List<ChatRoomParticipantEntity> saved = chatRoomParticipantRepository.findAllByUserId(id);
            if (saved.size() > 0) {
                for (ChatRoomParticipantEntity c : saved) {
                    redisTemplate.delete("chatRoom:members:" + Integer.toString(c.getId().getChatroomId()));
                }
            }
            return EditInfoResponseDto.success();
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseDto.databaseError();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<? super EditPasswordResponseDto> editPassword(String id, EditPasswordRequestDto requestDto) {
        try {
            UserEntity user = userRepository.findById(id).orElse(null);
            if (!passwordEncoder.matches(requestDto.getOldPassword(), user.getPassword())) {
                return EditPasswordResponseDto.notExistsPassword();
            }
            user.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
            userRepository.save(user);
            return EditPasswordResponseDto.success();
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseDto.databaseError();
        }
    }

}
