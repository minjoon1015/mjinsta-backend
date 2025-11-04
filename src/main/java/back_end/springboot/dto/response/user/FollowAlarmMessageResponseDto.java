package back_end.springboot.dto.response.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import back_end.springboot.dto.response.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowAlarmMessageResponseDto extends ResponseDto {

    private String receiverId;

    public FollowAlarmMessageResponseDto(ResponseCode code, String receiverId) {
        super(code);
        this.receiverId = receiverId;
    }
    
    public static ResponseEntity<FollowAlarmMessageResponseDto> success(String receiverId) {
        FollowAlarmMessageResponseDto responseDto = new FollowAlarmMessageResponseDto(ResponseCode.SC, receiverId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
