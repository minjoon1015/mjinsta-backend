package back_end.springboot.service.implement;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import back_end.springboot.common.AlarmType;
import back_end.springboot.common.ChatRoomType;
import back_end.springboot.common.MessageType;
import back_end.springboot.component.FileManager;
import back_end.springboot.component.FileValidateManager;
import back_end.springboot.dto.object.alarm.AlarmDto;
import back_end.springboot.dto.object.alarm.ChatRoomPrevDto;
import back_end.springboot.dto.object.chat.AttachmentDto;
import back_end.springboot.dto.object.chat.ChatMembersReadInfoDto;
import back_end.springboot.dto.object.chat.ChatMessageDto;
import back_end.springboot.dto.object.chat.ChatRoomDto;
import back_end.springboot.dto.object.event.NotificationEvent;
import back_end.springboot.dto.object.event.TopicEvent;
import back_end.springboot.dto.object.user.SimpleUserDto;
import back_end.springboot.dto.request.chat.ChatRoomCreateRequestDto;
import back_end.springboot.dto.request.chat.ChatRoomInviteUserRequestDto;
import back_end.springboot.dto.request.chat.UpdateGroupTitleRequestDto;
import back_end.springboot.dto.response.ResponseDto;
import back_end.springboot.dto.response.chat.ChatRoomCreateResponseDto;
import back_end.springboot.dto.response.chat.ChatRoomInviteUserResponseDto;
import back_end.springboot.dto.response.chat.ChatRoomLeaveResponseDto;
import back_end.springboot.dto.response.chat.GetChatMembersInfoResponseDto;
import back_end.springboot.dto.response.chat.GetChatMembersReadInfoResponseDto;
import back_end.springboot.dto.response.chat.GetChatMessageListResponseDto;
import back_end.springboot.dto.response.chat.GetChatRoomListResponseDto;
import back_end.springboot.dto.response.chat.GetRecommendInviteListResponseDto;
import back_end.springboot.dto.response.chat.InviteUserSearchResponseDto;
import back_end.springboot.dto.response.chat.UpdateGroupProfileImageResponseDto;
import back_end.springboot.dto.response.chat.UpdateGroupTitleResponseDto;
import back_end.springboot.entity.ChatRoomEntity;
import back_end.springboot.entity.ChatRoomLastReadEntity;
import back_end.springboot.entity.ChatRoomMessageEntity;
import back_end.springboot.entity.ChatRoomParticipantEntity;
import back_end.springboot.entity.MessageAttachmentEntity;
import back_end.springboot.entity.UserEntity;
import back_end.springboot.entity.Primary.ChatRoomLastReadParticipantId;
import back_end.springboot.entity.Primary.ChatRoomParticipantId;
import back_end.springboot.repository.ChatRoomLastReadRepository;
import back_end.springboot.repository.ChatRoomMessageRepository;
import back_end.springboot.repository.ChatRoomParticipantRepository;
import back_end.springboot.repository.ChatRoomRepository;
import back_end.springboot.repository.MessageAttachmentRepository;
import back_end.springboot.repository.UserRepository;
import back_end.springboot.repository.projection.ChatRoomProjection;
import back_end.springboot.repository.projection.SimpleUserProjection;
import back_end.springboot.service.ChatService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatServiceImplement implements ChatService {
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ApplicationEventPublisher eventPublisher;
    // private final SimpMessagingTemplate simpMessagingTemplate;
    private final FileManager fileManager;

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomLastReadRepository chatRoomLastReadRepository;
    private final ChatRoomParticipantRepository chatRoomParticipantRepository;
    private final ChatRoomMessageRepository chatRoomMessageRepository;
    private final MessageAttachmentRepository messageAttachmentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ResponseEntity<? super ChatRoomCreateResponseDto> createRoom(ChatRoomCreateRequestDto requestDto, String id,
            LocalDateTime now) {
        try {
            // 그룹 채팅방은 조건 없이 create
            // 개인 채팅방은 채팅방이 남아있을 때 hidden이 true면 false로 변경하고 아니면 return
            List<String> users = requestDto.getUsers();
            int length = users.size();
            ChatRoomType type;
            ChatRoomEntity chatRoomEntity = null;
            if (length == 1) {
                type = ChatRoomType.DIRECT;
                Integer existsId = chatRoomRepository.existedJoinChatRoom(id, users.get(0));
                if (existsId != null) {
                    ChatRoomParticipantEntity chatRoomParticipantEntity = chatRoomParticipantRepository
                            .findById(new ChatRoomParticipantId(existsId, id)).orElse(null);
                    if (chatRoomParticipantEntity == null)
                        return ResponseDto.badRequest();
                    chatRoomParticipantEntity.setIsHidden(false);
                    chatRoomParticipantRepository.save(chatRoomParticipantEntity);
                    AlarmDto alarmDto = new AlarmDto(AlarmType.CREATE_ROOM, now);
                    // simpMessagingTemplate.convertAndSendToUser(id, "/queue/notify", alarmDto);
                    eventPublisher.publishEvent(new NotificationEvent(id, "/queue/notify", alarmDto));
                    return ChatRoomCreateResponseDto.success();
                }

            } else {
                type = ChatRoomType.GROUP;
            }

            chatRoomEntity = new ChatRoomEntity(type, now);
            ChatRoomEntity saved = chatRoomRepository.save(chatRoomEntity);
            Integer chatRoomId = saved.getId();
            users.add(id);
            for (String userId : users) {
                ChatRoomParticipantId chatRoomParticipantId = new ChatRoomParticipantId(chatRoomId, userId);
                ChatRoomParticipantEntity chatRoomParticipantEntity = null;
                ChatRoomLastReadEntity chatRoomLastReadEntity = new ChatRoomLastReadEntity(
                        new ChatRoomLastReadParticipantId(chatRoomId, userId), null);
                chatRoomParticipantEntity = new ChatRoomParticipantEntity(chatRoomParticipantId, now, false);
                chatRoomLastReadRepository.save(chatRoomLastReadEntity);
                chatRoomParticipantRepository.save(chatRoomParticipantEntity);
            }

            AlarmDto alarmDto = new AlarmDto(AlarmType.CREATE_ROOM, now);
            for (String userId : users) {
                // simpMessagingTemplate.convertAndSendToUser(userId, "/queue/notify",
                // alarmDto);
                eventPublisher.publishEvent(new NotificationEvent(userId, "/queue/notify", alarmDto));
            }
            return ChatRoomCreateResponseDto.success();
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseDto.databaseError();
        }
    }

    @Override
    public ResponseEntity<? super GetRecommendInviteListResponseDto> getRecommendInviteList(String id) {
        try {
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            String key = "recommend:invite:" + id;
            Object saveRecommendList = ops.get(key);
            if (saveRecommendList != null) {
                List<SimpleUserDto> list = objectMapper.convertValue(saveRecommendList,
                        new TypeReference<List<SimpleUserDto>>() {
                        });
                return GetRecommendInviteListResponseDto.success(list);
            }
            List<SimpleUserProjection> getList = chatRoomRepository.getRecommendInviteUserList(id);
            List<SimpleUserDto> list = getList.stream()
                    .map(l -> new SimpleUserDto(l.getId(), l.getName(), l.getProfileImage(), true))
                    .collect(Collectors.toList());
            ops.set(key, list, 24, TimeUnit.HOURS);
            return GetRecommendInviteListResponseDto.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    @Override
    public ResponseEntity<? super GetChatRoomListResponseDto> getList(String id) {
        try {
            List<ChatRoomProjection> saved = chatRoomRepository.findByUserId(id);
            List<ChatRoomDto> list = saved.stream().map((c) -> {
                try {
                    List<String> names = objectMapper.readValue(c.getTitle(), new TypeReference<List<String>>() {
                    });
                    int length = names.size();
                    String title;
                    if (length == 1) {
                        title = names.get(0);
                    } else {
                        title = names.get(0) + " 외 " + --length + "명";
                    }
                    return new ChatRoomDto(c.getChatroomId(), title, c.getLastMessage(), c.getProfileImages(),
                            c.getUnreadCount(), c.getType());
                } catch (JsonProcessingException e) {
                    return new ChatRoomDto(c.getChatroomId(), c.getTitle(), c.getLastMessage(), c.getProfileImages(),
                            c.getUnreadCount(), c.getType());
                }
            }).collect(Collectors.toList());
            return GetChatRoomListResponseDto.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    @Override
    @Transactional
    public void updateRead(String id, Integer chatRoomId, Integer messageId) {
        try {
            ChatRoomLastReadParticipantId chatRoomLastReadParticipantId = new ChatRoomLastReadParticipantId(chatRoomId,
                    id);
            ChatRoomLastReadEntity chatRoomLastReadEntity = chatRoomLastReadRepository
                    .findById(chatRoomLastReadParticipantId).orElse(null);
            if (chatRoomLastReadEntity == null)
                return;
            if (chatRoomLastReadEntity.getLastReadMessageId() == messageId)
                return;
            chatRoomLastReadEntity.setLastReadMessageId(messageId);
            chatRoomLastReadRepository.save(chatRoomLastReadEntity);
            // simpMessagingTemplate.convertAndSend("/topic/members.info." + chatRoomId,
            // new ChatMembersReadInfoDto(chatRoomId, id, messageId));
            eventPublisher.publishEvent(new TopicEvent("/topic/members.info." + chatRoomId,
                    new ChatMembersReadInfoDto(chatRoomId, id, messageId)));
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    @Override
    public ResponseEntity<? super GetChatMessageListResponseDto> getHistory(Integer chatRoomId, String messageId,
            String userId) {
        try {
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            String key = "chatRoom:members:total:" + chatRoomId;
            Object saved = ops.get(key);
            Set<SimpleUserDto> userList = new HashSet<>();
            if (saved != null) {
                userList = objectMapper.convertValue(saved, new TypeReference<Set<SimpleUserDto>>() {
                });
            } else {
                Set<SimpleUserProjection> users = chatRoomRepository.findByJoinedChatRoomUsersForHistory(chatRoomId);
                userList = users.stream()
                        .map((u) -> new SimpleUserDto(u.getId(), u.getName(), u.getProfileImage(), false))
                        .collect(Collectors.toSet());
                ops.set(key, userList, 24, TimeUnit.HOURS);
            }

            SimpleUserDto isJoined = userList.stream().filter((u) -> u.getId().equals(userId)).findFirst().orElse(null);
            if (isJoined == null) {
                return ResponseDto.badRequest();
            }

            List<ChatRoomMessageEntity> messages = new ArrayList<>();

            int pageSize = 30;
            Pageable pageable = PageRequest.of(0, pageSize);

            if (messageId == null || messageId.equals("")) {
                messages = chatRoomMessageRepository.findMessagesWithAttachments(chatRoomId, pageable);
            } else {
                messages = chatRoomMessageRepository.findMessagesWithAttachmentsIndex(chatRoomId, messageId, pageable);
            }

            List<ChatMessageDto> chatMessageList = new ArrayList<>();
            for (ChatRoomMessageEntity m : messages) {
                SimpleUserDto user = userList.stream().filter(u -> m.getSenderId().equals(u.getId())).findFirst()
                        .orElse(null);
                ChatMessageDto chatMessage = null;
                if (m.getAttachments().size() == 0) {
                    if (m.getType() == MessageType.TEXT) {
                        chatMessage = new ChatMessageDto(MessageType.TEXT, chatRoomId, m.getId(), user.getId(),
                                user.getName(), user.getProfileImage(), m.getMessage(), m.getCreateAt());
                    } else if (m.getType() == MessageType.INVITE) {
                        chatMessage = new ChatMessageDto(MessageType.INVITE, chatRoomId, m.getId(), user.getId(),
                                user.getName(), user.getProfileImage(), m.getMessage(), m.getCreateAt());
                    } else if (m.getType() == MessageType.LEAVE) {
                        chatMessage = new ChatMessageDto(MessageType.LEAVE, chatRoomId, m.getId(), user.getId(),
                                user.getName(), user.getProfileImage(), m.getMessage(), m.getCreateAt());
                    }

                } else if (m.getAttachments().size() > 0) {
                    if (m.getAttachments().get(0).getType() == MessageType.IMAGE) {
                        chatMessage = new ChatMessageDto(MessageType.IMAGE, chatRoomId, m.getId(), user.getId(),
                                user.getName(), user.getProfileImage(),
                                m.getAttachments().stream()
                                        .map((attach) -> new AttachmentDto(attach.getFileName(), attach.getUrl()))
                                        .collect(Collectors.toList()),
                                m.getCreateAt());
                    } else {
                        chatMessage = new ChatMessageDto(MessageType.FILE, chatRoomId, m.getId(), user.getId(),
                                user.getName(), user.getProfileImage(),
                                m.getAttachments().stream()
                                        .map((attach) -> new AttachmentDto(attach.getFileName(), attach.getUrl()))
                                        .collect(Collectors.toList()),
                                m.getCreateAt());
                    }
                }
                chatMessageList.add(chatMessage);
            }
            return GetChatMessageListResponseDto.success(chatMessageList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    @Override
    public ResponseEntity<? super GetChatMembersReadInfoResponseDto> getMembersReadInfo(String id, Integer chatRoomId) {
        try {
            Set<SimpleUserDto> set = new HashSet<>();
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            String key = "chatRoom:members:" + chatRoomId;
            Object save = ops.get(key);

            if (save != null) {
                set = objectMapper.convertValue(save, new TypeReference<Set<SimpleUserDto>>() {
                });
            } else {
                Set<SimpleUserProjection> st = chatRoomRepository
                        .findByJoinedChatRoomUsers(chatRoomId);
                set = st.stream().map(s -> new SimpleUserDto(s.getId(), s.getName(), s.getProfileImage(), false))
                        .collect(Collectors.toSet());
                ops.set(key, set, 24, TimeUnit.HOURS);
            }
            SimpleUserDto senderDto = set.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
            if (senderDto == null)
                return ResponseDto.badRequest();

            List<ChatRoomLastReadEntity> saved = chatRoomLastReadRepository.findById(chatRoomId);
            List<ChatMembersReadInfoDto> list = null;
            if (saved.size() > 0) {
                list = saved.stream().map((s) -> new ChatMembersReadInfoDto(s.getId().getChatroomId(),
                        s.getId().getUserId(), s.getLastReadMessageId())).collect(Collectors.toList());
            }
            return GetChatMembersReadInfoResponseDto.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    @Override
    @Transactional
    public void sendMessage(ChatMessageDto requestDto) {
        try {
            Set<SimpleUserDto> list = new HashSet<>();
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            String key = "chatRoom:members:" + requestDto.getChatRoomId();
            Object saved = ops.get(key);

            if (saved != null) {
                list = objectMapper.convertValue(saved, new TypeReference<Set<SimpleUserDto>>() {
                });
            } else {
                Set<SimpleUserProjection> users = chatRoomRepository
                        .findByJoinedChatRoomUsersForHistory(requestDto.getChatRoomId());
                list = users.stream()
                        .map((u) -> new SimpleUserDto(u.getId(), u.getName(), u.getProfileImage(), false))
                        .collect(Collectors.toSet());
                ops.set(key, list, 24, TimeUnit.HOURS);
            }

            Set<SimpleUserDto> userList = new HashSet<>();
            String key2 = "chatRoom:members:total:" + requestDto.getChatRoomId();
            Object saved2 = ops.get(key2);

            if (saved2 != null) {
                userList = objectMapper.convertValue(saved2, new TypeReference<Set<SimpleUserDto>>() {
                });
            } else {
                Set<SimpleUserProjection> set = chatRoomRepository
                        .findByJoinedChatRoomUsers(requestDto.getChatRoomId());
                userList = set.stream().map(s -> new SimpleUserDto(s.getId(), s.getName(), s.getProfileImage(), false))
                        .collect(Collectors.toSet());
                ops.set(key2, userList, 24, TimeUnit.HOURS);
            }

            SimpleUserDto senderDto = list.stream().filter(u -> u.getId().equals(requestDto.getSenderId())).findFirst()
                    .orElse(null);
            requestDto.setSenderName(senderDto.getName());
            requestDto.setSenderProfileImage(senderDto.getProfileImage());
            ChatRoomEntity savedChatRoom = chatRoomRepository.findById(requestDto.getChatRoomId()).orElse(null);
            if (savedChatRoom == null)
                return;

            if (savedChatRoom.getType() == ChatRoomType.DIRECT) {
                for (SimpleUserDto u : userList) {
                    ChatRoomParticipantEntity up = chatRoomParticipantRepository
                            .findById(new ChatRoomParticipantId(requestDto.getChatRoomId(), u.getId())).orElse(null);
                    if (up == null)
                        continue;
                    if (up.getIsHidden() == true) {
                        up.setIsHidden(false);
                        chatRoomParticipantRepository.save(up);
                        redisTemplate.delete(key);
                        // simpMessagingTemplate.convertAndSendToUser(up.getId().getUserId(),
                        // "/queue/notify",
                        // new AlarmDto(AlarmType.CREATE_ROOM, LocalDateTime.now()));
                        eventPublisher.publishEvent(new NotificationEvent(up.getId().getUserId(), "/queue/notify",
                                new AlarmDto(AlarmType.CREATE_ROOM, LocalDateTime.now())));
                    }
                }
            }

            for (SimpleUserDto u : userList) {
                // simpMessagingTemplate.convertAndSendToUser(u.getId(), "/queue/notify", new
                // ChatRoomPrevDto(
                // AlarmType.CHAT, LocalDateTime.now(), requestDto.getChatRoomId(),
                // requestDto.getMessage()));
                eventPublisher.publishEvent(new NotificationEvent(u.getId(), "/queue/notify", new ChatRoomPrevDto(
                        AlarmType.CHAT, LocalDateTime.now(), requestDto.getChatRoomId(), requestDto.getMessage())));
            }

            ChatRoomMessageEntity chatRoomMessageEntity = new ChatRoomMessageEntity(requestDto.getChatRoomId(),
                    requestDto.getSenderId(), requestDto.getMessage(), requestDto.getCreateAt(), MessageType.TEXT);
            savedChatRoom.updateLastMessage(requestDto.getMessage(), requestDto.getCreateAt());
            chatRoomRepository.save(savedChatRoom);
            ChatRoomMessageEntity messageEntity = chatRoomMessageRepository.save(chatRoomMessageEntity);
            requestDto.setMessageId(messageEntity.getId());
            String topic = "/topic/chat." + requestDto.getChatRoomId();
            // simpMessagingTemplate.convertAndSend(topic, requestDto);
            eventPublisher.publishEvent(new TopicEvent(topic, requestDto));
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    @Override
    @Transactional
    public void sendFile(List<MultipartFile> files, Integer chatRoomId,
            String userId) {
        try {
            Set<SimpleUserDto> list = new HashSet<>();
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            String key = "chatRoom:members:" + chatRoomId;
            Object saved = ops.get(key);

            if (saved != null) {
                list = objectMapper.convertValue(saved, new TypeReference<Set<SimpleUserDto>>() {
                });
            } else {
                Set<SimpleUserProjection> set = chatRoomRepository
                        .findByJoinedChatRoomUsers(chatRoomId);
                list = set.stream().map(s -> new SimpleUserDto(s.getId(), s.getName(), s.getProfileImage(), false))
                        .collect(Collectors.toSet());
                ops.set(key, list, 24, TimeUnit.HOURS);
            }

            Set<SimpleUserDto> userList = new HashSet<>();
            String key2 = "chatRoom:members:total:" + chatRoomId;
            Object saved2 = ops.get(key2);

            if (saved2 != null) {
                userList = objectMapper.convertValue(saved2, new TypeReference<Set<SimpleUserDto>>() {
                });
            } else {
                Set<SimpleUserProjection> set = chatRoomRepository
                        .findByJoinedChatRoomUsers(chatRoomId);
                userList = set.stream().map(s -> new SimpleUserDto(s.getId(), s.getName(), s.getProfileImage(), false))
                        .collect(Collectors.toSet());
                ops.set(key2, userList, 24, TimeUnit.HOURS);
            }
            SimpleUserDto senderDto = list.stream().filter(u -> u.getId().equals(userId)).findFirst().orElse(null);

            String folderName = "chat" + "/" + chatRoomId;
            int fileCount = 0;
            MessageType type = FileValidateManager.getFileType(files.get(0));

            ChatRoomMessageEntity chatRoomMessageEntity = chatRoomMessageRepository
                    .save(new ChatRoomMessageEntity(chatRoomId, userId, null,
                            LocalDateTime.now(), MessageType.FILE));

            List<AttachmentDto> attachments = new ArrayList<>();
            for (MultipartFile file : files) {
                boolean isValid = FileValidateManager.isValidFileType(file);
                if (!isValid) {
                    continue;
                }
                String url = fileManager.uploadFile(file, folderName);
                MessageAttachmentEntity savedEntity = messageAttachmentRepository
                        .save(new MessageAttachmentEntity(chatRoomMessageEntity, file.getOriginalFilename(), url, type,
                                file.getSize()));
                attachments.add(new AttachmentDto(savedEntity.getFileName(), savedEntity.getUrl()));
                fileCount = fileCount + 1;
            }

            ChatRoomEntity savedChatRoom = chatRoomRepository.findById(chatRoomId).orElse(null);
            if (savedChatRoom == null)
                return;

            if (savedChatRoom.getType() == ChatRoomType.DIRECT) {
                for (SimpleUserDto u : userList) {
                    ChatRoomParticipantEntity up = chatRoomParticipantRepository
                            .findById(new ChatRoomParticipantId(chatRoomId, u.getId())).orElse(null);
                    if (up == null)
                        continue;
                    if (up.getIsHidden() == true) {
                        up.setIsHidden(false);
                        chatRoomParticipantRepository.save(up);
                        redisTemplate.delete(key);
                        // simpMessagingTemplate.convertAndSendToUser(up.getId().getUserId(),
                        // "/queue/notify",
                        // new AlarmDto(AlarmType.CREATE_ROOM, LocalDateTime.now()));
                        eventPublisher.publishEvent(new NotificationEvent(up.getId().getUserId(), "/queue/notify",
                                new AlarmDto(AlarmType.CREATE_ROOM, LocalDateTime.now())));
                    }
                }
            }

            ChatMessageDto chatMessageDto = new ChatMessageDto(type, chatRoomMessageEntity.getChatroomId(),
                    chatRoomMessageEntity.getId(), senderDto.getId(), senderDto.getName(), senderDto.getProfileImage(),
                    attachments, chatRoomMessageEntity.getCreateAt());
            String message = type == MessageType.IMAGE ? "사진 " + fileCount + "장을 보냈습니다."
                    : "파일 " + fileCount + "개를 보냈습니다.";
            for (SimpleUserDto u : userList) {
                // simpMessagingTemplate.convertAndSendToUser(u.getId(), "/queue/notify", new
                // ChatRoomPrevDto(
                // AlarmType.CHAT, LocalDateTime.now(), chatRoomMessageEntity.getChatroomId(),
                // message));
                eventPublisher.publishEvent(new NotificationEvent(u.getId(), "/queue/notify", new ChatRoomPrevDto(
                        AlarmType.CHAT, LocalDateTime.now(), chatRoomMessageEntity.getChatroomId(), message)));
            }

            savedChatRoom.updateLastMessage(message, chatMessageDto.getCreateAt());
            chatRoomRepository.save(savedChatRoom);

            String topic = "/topic/chat." + chatMessageDto.getChatRoomId();
            // simpMessagingTemplate.convertAndSend(topic, chatMessageDto);
            eventPublisher.publishEvent(new TopicEvent(topic, chatMessageDto));
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<? super UpdateGroupProfileImageResponseDto> updateGroupProfileImage(String id,
            Integer chatRoomId, MultipartFile image) {
        try {
            ChatRoomEntity savedChatRoom = chatRoomRepository.findById(chatRoomId).orElse(null);
            if (savedChatRoom == null)
                return ResponseDto.badRequest();
            Set<SimpleUserDto> list = new HashSet<>();
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            String key = "chatRoom:members:" + chatRoomId;
            Object saved = ops.get(key);

            if (saved != null) {
                list = objectMapper.convertValue(saved, new TypeReference<Set<SimpleUserDto>>() {
                });
            } else {
                Set<SimpleUserProjection> set = chatRoomRepository
                        .findByJoinedChatRoomUsers(chatRoomId);
                list = set.stream().map(s -> new SimpleUserDto(s.getId(), s.getName(), s.getProfileImage(), false))
                        .collect(Collectors.toSet());
                ops.set(key, list, 24, TimeUnit.HOURS);
            }
            SimpleUserDto user = list.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
            if (user == null)
                return ResponseDto.badRequest();

            MessageType messageType = FileValidateManager.getFileType(image);
            if (messageType != MessageType.IMAGE)
                return ResponseDto.badRequest();
            if (!FileValidateManager.isValidFileType(image))
                return ResponseDto.badRequest();

            String savedUrl = savedChatRoom.getProfileImage();
            if (savedUrl.length() > 0) {
                fileManager.deleteFile(savedUrl);
            }
            String folderName = "chatRoom/" + chatRoomId + "/profile";
            String url = fileManager.uploadFile(image, folderName);
            savedChatRoom.setProfileImage(url);
            chatRoomRepository.save(savedChatRoom);
            return UpdateGroupProfileImageResponseDto.success(url);
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseDto.databaseError();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<? super UpdateGroupTitleResponseDto> updateGroupTitle(String id,
            UpdateGroupTitleRequestDto requestDto) {
        try {
            ChatRoomEntity savedChatRoom = chatRoomRepository.findById(requestDto.getChatRoomId()).orElse(null);
            if (savedChatRoom == null)
                return ResponseDto.badRequest();
            Set<SimpleUserDto> list = new HashSet<>();
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            String key = "chatRoom:members:" + requestDto.getChatRoomId();
            Object saved = ops.get(key);

            if (saved != null) {
                list = objectMapper.convertValue(saved, new TypeReference<Set<SimpleUserDto>>() {
                });
            } else {
                Set<SimpleUserProjection> set = chatRoomRepository
                        .findByJoinedChatRoomUsers(requestDto.getChatRoomId());
                list = set.stream().map(s -> new SimpleUserDto(s.getId(), s.getName(), s.getProfileImage(), false))
                        .collect(Collectors.toSet());
                ops.set(key, list, 24, TimeUnit.HOURS);
            }
            SimpleUserDto user = list.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
            if (user == null)
                return ResponseDto.badRequest();

            savedChatRoom.setTitle(requestDto.getUpdateTitle());
            ChatRoomEntity chatRoomEntity = chatRoomRepository.save(savedChatRoom);
            return UpdateGroupTitleResponseDto.success(chatRoomEntity.getTitle());
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseDto.databaseError();
        }
    }

    @Override
    public ResponseEntity<? super GetChatMembersInfoResponseDto> getMembersInfo(String id, Integer chatRoomId) {
        try {
            Set<SimpleUserDto> list = new HashSet<>();
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            String key = "chatRoom:members:" + chatRoomId;
            Object saved = ops.get(key);

            if (saved != null) {
                list = objectMapper.convertValue(saved, new TypeReference<Set<SimpleUserDto>>() {
                });
            } else {
                Set<SimpleUserProjection> set = chatRoomRepository
                        .findByJoinedChatRoomUsers(chatRoomId);
                list = set.stream().map(s -> new SimpleUserDto(s.getId(), s.getName(), s.getProfileImage(), false))
                        .collect(Collectors.toSet());
                ops.set(key, list, 24, TimeUnit.HOURS);
            }
            SimpleUserDto senderDto = list.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
            if (senderDto == null)
                return ResponseDto.badRequest();
            List<SimpleUserDto> result = list.stream()
                    .map((u) -> new SimpleUserDto(u.getId(), u.getName(), u.getProfileImage(), false))
                    .collect(Collectors.toList());
            result.sort((m1, m2) -> m1.getId().compareToIgnoreCase(m2.getId()));
            return GetChatMembersInfoResponseDto.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    @Override
    public ResponseEntity<? super InviteUserSearchResponseDto> inviteUserSearch(String id, Integer chatRoomId,
            String keyword) {
        try {
            Set<SimpleUserDto> list = new HashSet<>();
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            String key = "chatRoom:members:" + chatRoomId;
            Object saved = ops.get(key);

            if (saved != null) {
                list = objectMapper.convertValue(saved, new TypeReference<Set<SimpleUserDto>>() {
                });
            } else {
                Set<SimpleUserProjection> set = chatRoomRepository
                        .findByJoinedChatRoomUsers(chatRoomId);
                list = set.stream().map(s -> new SimpleUserDto(s.getId(), s.getName(), s.getProfileImage(), false))
                        .collect(Collectors.toSet());
                ops.set(key, list, 24, TimeUnit.HOURS);
            }
            SimpleUserDto senderDto = list.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
            if (senderDto == null)
                return ResponseDto.badRequest();
            List<SimpleUserProjection> simpleUserProjections = chatRoomParticipantRepository
                    .searchKeywordNotJoinedChatRoomId(keyword, chatRoomId);
            List<SimpleUserDto> result = simpleUserProjections.stream()
                    .map((s) -> new SimpleUserDto(s.getId(), s.getName(), s.getProfileImage(), false))
                    .collect(Collectors.toList());
            return InviteUserSearchResponseDto.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseDto.databaseError();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<? super ChatRoomInviteUserResponseDto> inviteUser(String id,
            ChatRoomInviteUserRequestDto requestDto) {
        try {
            Set<SimpleUserDto> list = new HashSet<>();
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            String key = "chatRoom:members:" + requestDto.getChatRoomId();
            Object saved = ops.get(key);

            if (saved != null) {
                list = objectMapper.convertValue(saved, new TypeReference<Set<SimpleUserDto>>() {
                });
            } else {
                Set<SimpleUserProjection> set = chatRoomRepository
                        .findByJoinedChatRoomUsers(requestDto.getChatRoomId());
                list = set.stream().map(s -> new SimpleUserDto(s.getId(), s.getName(), s.getProfileImage(), false))
                        .collect(Collectors.toSet());
                ops.set(key, list, 24, TimeUnit.HOURS);
            }
            SimpleUserDto senderDto = list.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
            if (senderDto == null)
                return ResponseDto.badRequest();
            ChatRoomEntity savedChatRoom = chatRoomRepository.findById(requestDto.getChatRoomId()).orElse(null);
            if (savedChatRoom == null)
                return ResponseDto.badRequest();
            if (savedChatRoom.getType() == ChatRoomType.DIRECT)
                return ResponseDto.badRequest();

            StringBuffer sb = new StringBuffer();
            int index = 0;
            for (String userId : requestDto.getUsers()) {
                SimpleUserDto redisSaved = list.stream().filter((u) -> u.getId().equals(userId)).findFirst()
                        .orElse(null);
                if (redisSaved != null)
                    return ChatRoomInviteUserResponseDto.success(); // 현재 hidden이 아닌 유저의 경우
                ChatRoomParticipantEntity cp = chatRoomParticipantRepository
                        .findById(new ChatRoomParticipantId(requestDto.getChatRoomId(), userId)).orElse(null);
                if (cp != null) {
                    // 기존에 이미 유저가 있고 is_hidden = true 라면
                    if (cp.getIsHidden() != true)
                        return ChatRoomInviteUserResponseDto.success();
                    cp.setIsHidden(false);
                    chatRoomParticipantRepository.save(cp);
                } else {
                    ChatRoomParticipantEntity chatRoomParticipantEntity = new ChatRoomParticipantEntity(
                            new ChatRoomParticipantId(requestDto.getChatRoomId(), userId), LocalDateTime.now(), false);
                    ChatRoomLastReadEntity chatRoomLastReadEntity = new ChatRoomLastReadEntity(
                            new ChatRoomLastReadParticipantId(requestDto.getChatRoomId(), userId), null);
                    chatRoomParticipantRepository.save(chatRoomParticipantEntity);
                    chatRoomLastReadRepository.save(chatRoomLastReadEntity);
                }
                UserEntity u = userRepository.findById(userId).orElse(null);
                if (u == null)
                    return ResponseDto.badRequest();
                if (index++ == 0) {
                    sb.append(id + "님이 " + u.getName() + "님");
                } else {
                    sb.append(", " + u.getName() + "님");
                }
            }

            sb.append("을 초대하였습니다.");
            ChatRoomMessageEntity message = new ChatRoomMessageEntity(requestDto.getChatRoomId(), id, sb.toString(),
                    LocalDateTime.now(), MessageType.INVITE);
            ChatRoomMessageEntity savedMessage = chatRoomMessageRepository.save(message);
            String topic = "/topic/chat." + requestDto.getChatRoomId();
            // 인자 개수가 똑같을 경우 String에 null을 주입하면 에러 발생함
            // simpMessagingTemplate.convertAndSend(topic,
            // new ChatMessageDto(MessageType.INVITE, savedMessage.getChatroomId(),
            // savedMessage.getId(),
            // savedMessage.getSenderId(), senderDto.getName(), "",
            // savedMessage.getMessage(), savedMessage.getCreateAt()));
            eventPublisher.publishEvent(new TopicEvent(topic,
                    new ChatMessageDto(MessageType.INVITE, savedMessage.getChatroomId(), savedMessage.getId(),
                            savedMessage.getSenderId(), senderDto.getName(), "", savedMessage.getMessage(),
                            savedMessage.getCreateAt())));
            redisTemplate.delete(key);
            for (String userId : requestDto.getUsers()) {
                // 유저에게 알림
                // simpMessagingTemplate.convertAndSendToUser(userId, "/queue/notify", new
                // AlarmDto(AlarmType.INVITE_ROOM, LocalDateTime.now()));
                eventPublisher.publishEvent(new NotificationEvent(userId, "/queue/notify",
                        new AlarmDto(AlarmType.INVITE_ROOM, LocalDateTime.now())));
            }

            return ChatRoomInviteUserResponseDto.success();
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseDto.databaseError();
        }
    }

    @Override
    @Transactional
    public ResponseEntity<? super ChatRoomLeaveResponseDto> roomLeave(String id, Integer chatRoomId) {
        try {
            ChatRoomEntity chatRoomEntity = chatRoomRepository.findById(chatRoomId).orElse(null);
            if (chatRoomEntity == null)
                return ResponseDto.badRequest();
            Set<SimpleUserDto> list = new HashSet<>();
            ValueOperations<String, Object> ops = redisTemplate.opsForValue();
            String key = "chatRoom:members:" + chatRoomId;
            Object saved = ops.get(key);
            if (saved != null) {
                list = objectMapper.convertValue(saved, new TypeReference<Set<SimpleUserDto>>() {
                });
            } else {
                Set<SimpleUserProjection> set = chatRoomRepository
                        .findByJoinedChatRoomUsers(chatRoomId);
                list = set.stream().map(s -> new SimpleUserDto(s.getId(), s.getName(), s.getProfileImage(), false))
                        .collect(Collectors.toSet());
                ops.set(key, list, 24, TimeUnit.HOURS);
            }
            SimpleUserDto senderDto = list.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
            if (senderDto == null)
                return ResponseDto.badRequest();

            if (chatRoomEntity.getType() == ChatRoomType.DIRECT) {
                // 개인 채팅
                ChatRoomParticipantEntity cp = chatRoomParticipantRepository
                        .findById(new ChatRoomParticipantId(chatRoomId, id)).orElse(null);
                if (cp == null)
                    return ResponseDto.badRequest();
                if (cp.getIsHidden() == true)
                    ResponseDto.badRequest();
                cp.setIsHidden(true);
                chatRoomParticipantRepository.save(cp);
                eventPublisher.publishEvent(new NotificationEvent(id, "/queue/notify",
                        new AlarmDto(AlarmType.LEAVE_ROOM, LocalDateTime.now())));
                redisTemplate.delete(key);
            } else {
                // 그룹 채팅
                // 삭제할 때 participant, lastRead, chatRoom 전부 다 삭제하는 로직 넣으면 될듯
                ChatRoomParticipantEntity cp = chatRoomParticipantRepository
                        .findById(new ChatRoomParticipantId(chatRoomId, id)).orElse(null);
                if (cp == null)
                    return ResponseDto.badRequest();
                if (cp.getIsHidden() == true)
                    return ChatRoomLeaveResponseDto.success();
                cp.setIsHidden(true);
                chatRoomParticipantRepository.save(cp);

                // 그룹 채팅 참여하고 있는 인원들이 전부 다 hidden이면 채팅방 기록 모두 삭제
                List<ChatRoomParticipantEntity> membersParticipants = chatRoomParticipantRepository
                        .findByChatRoomId(chatRoomId);
                int count = 0;
                for (ChatRoomParticipantEntity c : membersParticipants) {
                    if (c.getIsHidden() == true)
                        count++;
                }
                if (count == membersParticipants.size()) {
                    // 모든 유저가 hidden으로 설정
                    chatRoomParticipantRepository.deleteAll(membersParticipants);
                    List<ChatRoomLastReadEntity> membersLastReads = chatRoomLastReadRepository
                            .findAllByChatRoomId(chatRoomId);
                    chatRoomLastReadRepository.deleteAll(membersLastReads);
                    chatRoomRepository.delete(chatRoomEntity);
                    redisTemplate.delete(key);
                    // simpMessagingTemplate.convertAndSendToUser(id, "/queue/notify",
                    // new AlarmDto(AlarmType.LEAVE_ROOM, LocalDateTime.now()));
                    eventPublisher.publishEvent(new NotificationEvent(id, "/queue/notify",
                            new AlarmDto(AlarmType.LEAVE_ROOM, LocalDateTime.now())));
                } else {
                    // notify로 보내는게 아니라 채팅방에 속해있는 애들은 topic으로 보내줘야함
                    // simpMessagingTemplate.convertAndSendToUser(id, "/queue/notify",
                    // new AlarmDto(AlarmType.LEAVE_ROOM, LocalDateTime.now()));
                    eventPublisher.publishEvent(new NotificationEvent(id, "/queue/notify",
                            new AlarmDto(AlarmType.LEAVE_ROOM, LocalDateTime.now())));

                    redisTemplate.delete(key);
                    ChatRoomMessageEntity message = new ChatRoomMessageEntity(chatRoomId, id,
                            senderDto.getName() + "님이 퇴장하셨습니다.", LocalDateTime.now(),
                            MessageType.LEAVE);
                    ChatRoomMessageEntity messageEntity = chatRoomMessageRepository.save(message);

                    String topic = "/topic/chat." + chatRoomId;
                    // 인자 개수가 똑같을 경우 String에 null을 주입하면 에러 발생함
                    eventPublisher.publishEvent(new TopicEvent(topic,
                            new ChatMessageDto(MessageType.LEAVE, chatRoomId, messageEntity.getId(),
                                    messageEntity.getSenderId(), senderDto.getName(), "", messageEntity.getMessage(),
                                    messageEntity.getCreateAt())));
                }
            }
            return ChatRoomLeaveResponseDto.success();
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseDto.databaseError();
        }
    }

}
