package back_end.springboot.entity.Primary;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class ChatRoomLastReadParticipantId implements Serializable {
    private Integer chatroomId;
    private String userId;

    public ChatRoomLastReadParticipantId(Integer chatroomId, String userId) {
        this.chatroomId = chatroomId;
        this.userId = userId;
    }
}
