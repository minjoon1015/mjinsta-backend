package back_end.springboot.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;

import back_end.springboot.common.MessageType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "chatroom_message")
@Getter
@Setter
@NoArgsConstructor
public class ChatRoomMessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer chatroomId;
    private String senderId;
    private String message;
    private LocalDateTime createAt;
    @Enumerated(EnumType.STRING)
    private MessageType type;

    @OneToMany(mappedBy = "message", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 100)
    private List<MessageAttachmentEntity> attachments = new ArrayList<>();

    public ChatRoomMessageEntity(Integer chatroomId, String senderId, String message, LocalDateTime createAt, MessageType type) {
        this.chatroomId = chatroomId;
        this.senderId = senderId;
        this.message = message;
        this.createAt = createAt;
        this.type = type;
    }
}
