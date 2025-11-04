package back_end.springboot.dto.request.chat;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomInviteUserRequestDto {
    private List<String> users;
    private Integer chatRoomId;
}
