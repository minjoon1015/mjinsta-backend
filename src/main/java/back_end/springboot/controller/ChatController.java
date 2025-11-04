package back_end.springboot.controller;

import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import back_end.springboot.common.MessageType;
import back_end.springboot.dto.object.chat.ChatMessageDto;
import back_end.springboot.dto.object.chat.UpdateReadMessageDto;
import back_end.springboot.service.ChatService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @MessageMapping("/send")
    public void sendMessage(@Payload ChatMessageDto requestDto, Principal principal) {
        requestDto.setType(MessageType.TEXT);
        requestDto.setSenderId(principal.getName());
        requestDto.setCreateAt(LocalDateTime.now());
        chatService.sendMessage(requestDto);
    }

    @MessageMapping("/update/read")
    public void read(@Payload UpdateReadMessageDto updateReadMessageDto, Principal principal) {
        chatService.updateRead(principal.getName(), updateReadMessageDto.getChatRoomId(), updateReadMessageDto.getMessageId());
    }
}
