package back_end.springboot.dto.object.alarm.extend;

import java.time.LocalDateTime;

import back_end.springboot.common.AlarmType;
import back_end.springboot.dto.object.alarm.AlarmDto;
import back_end.springboot.dto.object.post.PostCommentDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCommentAlarmDto extends AlarmDto {
    private PostCommentDto post = null;

    public PostCommentAlarmDto(AlarmType alarmType, LocalDateTime create_at, PostCommentDto post) {
        super(alarmType, create_at);
        this.post = post;
    }
    
}
