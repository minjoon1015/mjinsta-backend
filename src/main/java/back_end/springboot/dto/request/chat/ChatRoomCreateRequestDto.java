package back_end.springboot.dto.request.chat;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomCreateRequestDto {
    List<String> users;
}
