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
public class ChatRoomParticipantId implements Serializable {
    private Integer chatroomId;
    private String userId;

    public ChatRoomParticipantId(Integer chatroomId, String userId) {
        this.chatroomId = chatroomId;
        this.userId = userId;
    }
}
