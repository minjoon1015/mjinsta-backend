package back_end.springboot.entity;

import java.time.LocalDateTime;

import back_end.springboot.common.ChatRoomType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter; 

@Entity
@Table(name = "chatroom")
@Getter
@Setter
@NoArgsConstructor
public class ChatRoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String title;
    @Enumerated(EnumType.STRING)
    ChatRoomType type;
    LocalDateTime createAt;
    LocalDateTime lastMessageTime;
    String lastMessage;
    String profileImage;

    public ChatRoomEntity(ChatRoomType type, LocalDateTime createAt) {
        this.type = type;
        this.createAt = createAt;
        this.lastMessageTime = createAt;
        this.profileImage = null;
        this.title = null;
    }

    public void updateLastMessage(String lastMessage, LocalDateTime lastMessageTime) {
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

}
