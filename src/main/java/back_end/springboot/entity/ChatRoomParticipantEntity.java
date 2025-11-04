package back_end.springboot.entity;

import java.time.LocalDateTime;

import back_end.springboot.entity.Primary.ChatRoomParticipantId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "chatroom_participant")
@Getter
@Setter
@NoArgsConstructor
public class ChatRoomParticipantEntity {
    @EmbeddedId
    private ChatRoomParticipantId id;
    private LocalDateTime joinedAt;
    private Boolean isHidden;

    public ChatRoomParticipantEntity(ChatRoomParticipantId chatRoomParticipantId, LocalDateTime joinedAt, Boolean isHidden) {
        this.id = chatRoomParticipantId;
        this.joinedAt = joinedAt;
        this.isHidden = isHidden;
    }
}
