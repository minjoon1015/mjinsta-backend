package back_end.springboot.dto.object.alarm;

import java.time.LocalDateTime;

import back_end.springboot.common.AlarmType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlarmDto {
    AlarmType alarmType;
    LocalDateTime create_at;
    
    public AlarmDto(AlarmType alarmType, LocalDateTime create_at) {
        this.alarmType = alarmType;
        this.create_at = create_at;
    }
    
}
