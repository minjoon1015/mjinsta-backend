package back_end.springboot.dto.response.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import back_end.springboot.dto.object.user.UserDetailsDto;
import back_end.springboot.dto.response.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetUserDetailsInfoResponseDto extends ResponseDto {
    private UserDetailsDto user;

    public GetUserDetailsInfoResponseDto(ResponseCode code, UserDetailsDto user) {
        super(code);
        this.user = user;
    }
    
    public static ResponseEntity<GetUserDetailsInfoResponseDto> success(UserDetailsDto user) {
        GetUserDetailsInfoResponseDto responseDto = new GetUserDetailsInfoResponseDto(ResponseCode.SC, user);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
