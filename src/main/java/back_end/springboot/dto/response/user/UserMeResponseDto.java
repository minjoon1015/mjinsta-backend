package back_end.springboot.dto.response.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import back_end.springboot.dto.object.user.UserDto;
import back_end.springboot.dto.response.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserMeResponseDto extends ResponseDto {
    UserDto user = null;

    private UserMeResponseDto(ResponseCode code) {
        super(code);
    }

    private UserMeResponseDto(ResponseCode code, UserDto user) {
        super(code);
        this.user = user;
    }

    public static ResponseEntity<UserMeResponseDto> success(UserDto user) {
        UserMeResponseDto responseDto = new UserMeResponseDto(ResponseCode.SC, user);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
    
    public static ResponseEntity<UserMeResponseDto> invalidToken() {
        UserMeResponseDto responseDto = new UserMeResponseDto(ResponseCode.IVT);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }
}
