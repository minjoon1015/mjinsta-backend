package back_end.springboot.dto.object.alarm.extend;

import java.time.LocalDateTime;

import back_end.springboot.common.AlarmType;
import back_end.springboot.dto.object.alarm.AlarmDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostTagAlarmDto extends AlarmDto {
    private String senderId;
    private String senderProfileImage;
    private Integer postId;

    public PostTagAlarmDto(AlarmType alarmType, LocalDateTime create_at, String senderId, String senderProfileImage,
            Integer postId) {
        super(alarmType, create_at);
        this.senderId = senderId;
        this.senderProfileImage = senderProfileImage;
        this.postId = postId;
    }

}
