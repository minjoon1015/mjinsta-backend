package back_end.springboot.dto.request.chat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateGroupTitleRequestDto {
    private Integer chatRoomId;
    private String updateTitle;
}
