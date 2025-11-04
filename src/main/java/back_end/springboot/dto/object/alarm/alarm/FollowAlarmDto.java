package back_end.springboot.dto.object.alarm.alarm;

import java.time.LocalDateTime;

import back_end.springboot.common.AlarmType;
import back_end.springboot.dto.object.alarm.AlarmDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowAlarmDto extends AlarmDto {
    public FollowAlarmDto(AlarmType alarmCode, LocalDateTime create_at, String senderId, String senderProfileImage) {
        super(alarmCode, create_at);
        this.senderId = senderId;
        this.senderProfileImage = senderProfileImage;
    }
    private String senderId;
    private String senderProfileImage;
}
