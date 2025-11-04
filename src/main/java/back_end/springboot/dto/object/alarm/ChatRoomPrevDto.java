package back_end.springboot.dto.object.alarm;

import java.time.LocalDateTime;

import back_end.springboot.common.AlarmType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomPrevDto extends AlarmDto {

    public ChatRoomPrevDto(AlarmType alarmType, LocalDateTime create_at, Integer chatRoomId, String message) {
        super(alarmType, create_at);
        this.chatRoomId = chatRoomId;
        this.message = message;
    }
    
    private Integer chatRoomId;
    private String message;
}
