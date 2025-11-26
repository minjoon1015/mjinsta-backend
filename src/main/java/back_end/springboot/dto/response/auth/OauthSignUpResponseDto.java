package back_end.springboot.dto.response.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import back_end.springboot.dto.response.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OauthSignUpResponseDto extends ResponseDto {
    private String token;

    public OauthSignUpResponseDto(ResponseCode code, String token) {
        super(code);
        this.token = token;
    }

    public static ResponseEntity<OauthSignUpResponseDto> success(String token) {
        return ResponseEntity.status(HttpStatus.OK).body(new OauthSignUpResponseDto(ResponseCode.SC, token));
    }
    
}
