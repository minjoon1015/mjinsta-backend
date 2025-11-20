package back_end.springboot.dto.object.alarm.alarm;

import java.time.LocalDateTime;

import back_end.springboot.common.AlarmType;
import back_end.springboot.dto.object.alarm.AlarmDto;
import back_end.springboot.dto.object.user.SimpleUserDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostLikeAlarmDto extends AlarmDto {
    private SimpleUserDto user;

    public PostLikeAlarmDto(AlarmType alarmType, LocalDateTime create_at, SimpleUserDto user) {
        super(alarmType, create_at);
        this.user = user;
    }
    
}
