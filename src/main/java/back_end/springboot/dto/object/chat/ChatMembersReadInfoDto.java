package back_end.springboot.dto.object.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatMembersReadInfoDto {
    private Integer chatRoomId;
    private String userId;
    private Integer lastReadMessageId;
}
