package back_end.springboot.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import back_end.springboot.dto.object.chat.ChatMessageDto;
import back_end.springboot.dto.request.chat.ChatRoomCreateRequestDto;
import back_end.springboot.dto.request.chat.ChatRoomInviteUserRequestDto;
import back_end.springboot.dto.request.chat.UpdateGroupTitleRequestDto;
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

public interface ChatService {
    public ResponseEntity<? super ChatRoomCreateResponseDto> createRoom(ChatRoomCreateRequestDto requestDto, String id, LocalDateTime now);
    public ResponseEntity<? super GetRecommendInviteListResponseDto>getRecommendInviteList(String id);
    public ResponseEntity<? super GetChatRoomListResponseDto> getList(String id);
    public ResponseEntity<? super GetChatMessageListResponseDto> getHistory(Integer chatRoomId, String messageId, String userId);
    public ResponseEntity<? super GetChatMembersInfoResponseDto> getMembersInfo(String id, Integer chatRoomId);
    public ResponseEntity<? super GetChatMembersReadInfoResponseDto> getMembersReadInfo(String id, Integer chatRoomId);
    public ResponseEntity<? super InviteUserSearchResponseDto> inviteUserSearch(String id, Integer chatRoomId, String keyword);
    public ResponseEntity<? super ChatRoomInviteUserResponseDto> inviteUser(String id, ChatRoomInviteUserRequestDto requestDto);
    public ResponseEntity<? super ChatRoomLeaveResponseDto> roomLeave(String id, Integer chatRoomId);
    
    public void updateRead(String id, Integer messageId,Integer chatRoomId);
    public void sendMessage(ChatMessageDto requestDto);
    public void sendFile(List<MultipartFile> files, Integer chatRoomId, String userId);

    public ResponseEntity<? super UpdateGroupProfileImageResponseDto> updateGroupProfileImage(String id, Integer chatRoomId, MultipartFile image);
    public ResponseEntity<? super UpdateGroupTitleResponseDto> updateGroupTitle(String id, UpdateGroupTitleRequestDto requestDto);
}
