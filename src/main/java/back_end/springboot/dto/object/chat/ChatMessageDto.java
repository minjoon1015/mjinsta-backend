package back_end.springboot.dto.object.chat;

import java.time.LocalDateTime;
import java.util.List;

import back_end.springboot.common.MessageType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageDto {
    private MessageType type;
    private Integer chatRoomId;
    private Integer messageId;
    private String senderId;
    private String senderName;
    private String senderProfileImage;
    private String message;
    private LocalDateTime createAt;

    // file
    private List<AttachmentDto> attachments;

    public ChatMessageDto(MessageType type, Integer chatRoomId, Integer messageId, String senderId, String senderName, String senderProfileImage, String message, LocalDateTime createAt) {
        this.type = type;
        this.chatRoomId = chatRoomId;
        this.messageId = messageId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderProfileImage = senderProfileImage;
        this.message = message;
        this.createAt = createAt;
    }

    public ChatMessageDto(MessageType type, Integer chatRoomId, Integer messageId, String senderId, String senderName, String senderProfileImage, List<AttachmentDto> attachments, LocalDateTime createAt) {
        this.type = type;
        this.chatRoomId = chatRoomId;
        this.messageId = messageId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderProfileImage = senderProfileImage;
        this.attachments = attachments;
        this.createAt = createAt;
    }
}
