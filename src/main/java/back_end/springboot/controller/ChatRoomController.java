package back_end.springboot.controller;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
import back_end.springboot.service.ChatService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatService chatService;

    @GetMapping("/getList")
    public ResponseEntity<? super GetChatRoomListResponseDto> getList(@AuthenticationPrincipal UserDetails userDetails) {
        return chatService.getList(userDetails.getUsername());
    }

    @PostMapping("/create")
    public ResponseEntity<? super ChatRoomCreateResponseDto> createRoom(@RequestBody ChatRoomCreateRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails) {
        return chatService.createRoom(requestDto, userDetails.getUsername(), LocalDateTime.now());
    }

    @GetMapping("/recommend/invite/list")
    public ResponseEntity<? super GetRecommendInviteListResponseDto> getRecommendInviteList(@AuthenticationPrincipal UserDetails userDetails) {
        return chatService.getRecommendInviteList(userDetails.getUsername());
    }

    @GetMapping("/history")
    public ResponseEntity<? super GetChatMessageListResponseDto> getHistory(
        @RequestParam("chatRoomId") Integer chatRoomId, 
        @RequestParam(value = "messageId", required = false) String messageId, 
        @AuthenticationPrincipal UserDetails userDetails) {
        return chatService.getHistory(chatRoomId, messageId, userDetails.getUsername());
    }

    @GetMapping("/get/members/read_info")
    public ResponseEntity<? super GetChatMembersReadInfoResponseDto> getMembersReadInfo(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("chatRoomId") Integer chatRoomId) {
        return chatService.getMembersReadInfo(userDetails.getUsername(), chatRoomId);
    }

    @PutMapping("/update/group/profile_image")
    public ResponseEntity<? super UpdateGroupProfileImageResponseDto> updateGroupProfileImage(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("file") MultipartFile image, @RequestParam("chatRoomId") Integer chatRoomId) {
        return chatService.updateGroupProfileImage(userDetails.getUsername(), chatRoomId, image);
    }

    @PutMapping("/update/group/title")
    public ResponseEntity<? super UpdateGroupTitleResponseDto> updateGroupTitle(@AuthenticationPrincipal UserDetails userDetails, @RequestBody UpdateGroupTitleRequestDto requestDto) {
        return chatService.updateGroupTitle(userDetails.getUsername(), requestDto);
    }

    @GetMapping("/get/members/info")
    public ResponseEntity<? super GetChatMembersInfoResponseDto> getMembersInfo(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("chatRoomId") Integer chatRoomId) {
        return chatService.getMembersInfo(userDetails.getUsername(), chatRoomId);
    }

    @GetMapping("/invite/user_search")
    public ResponseEntity<? super InviteUserSearchResponseDto> inviteUserSearch(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("keyword")String keyword, @RequestParam("chatRoomId") Integer chatRoomId) {
        return chatService.inviteUserSearch(userDetails.getUsername(), chatRoomId, keyword);
    }

    @PostMapping("/invite/user")
    public ResponseEntity<? super ChatRoomInviteUserResponseDto> inviteUser(@AuthenticationPrincipal UserDetails userDetails, @RequestBody ChatRoomInviteUserRequestDto requestDto) {
        return chatService.inviteUser(userDetails.getUsername(), requestDto);
    }

    @DeleteMapping("/room/leave")
    public ResponseEntity<? super ChatRoomLeaveResponseDto> roomLeave(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("chatRoomId") Integer chatRoomId) {
        return chatService.roomLeave(userDetails.getUsername(), chatRoomId);
    }
}
