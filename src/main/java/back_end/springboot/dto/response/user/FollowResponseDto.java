package back_end.springboot.dto.response.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import back_end.springboot.dto.response.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowResponseDto extends ResponseDto {
    private FollowResponseDto(ResponseCode responseCode) {
        super(responseCode);
    }

    public static ResponseEntity<FollowResponseDto> success() {
        FollowResponseDto responseDto = new FollowResponseDto(ResponseCode.SC);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
