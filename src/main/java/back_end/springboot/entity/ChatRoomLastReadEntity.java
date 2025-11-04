package back_end.springboot.entity;

import back_end.springboot.entity.Primary.ChatRoomLastReadParticipantId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "chatroom_last_read")
@Getter
@Setter
@NoArgsConstructor
public class ChatRoomLastReadEntity {
    @EmbeddedId
    private ChatRoomLastReadParticipantId id;
    private Integer lastReadMessageId;

    public ChatRoomLastReadEntity(ChatRoomLastReadParticipantId id, Integer lastReadMessageId) {
        this.id = id;
        this.lastReadMessageId = lastReadMessageId;
    }
}
