package back_end.springboot.dto.response.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import back_end.springboot.dto.response.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInResponseDto extends ResponseDto {
    private String token;

    public SignInResponseDto(ResponseCode code, String token) {
        super(code);
        this.token = token;
    }

    public SignInResponseDto(ResponseCode code) {
        super(code);
    }

    public static ResponseEntity<SignInResponseDto> success(String token) {
        SignInResponseDto responseDto = new SignInResponseDto(ResponseCode.SC, token);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    public static ResponseEntity<SignInResponseDto> notExistedId() {
        SignInResponseDto responseDto = new SignInResponseDto(ResponseCode.NEI);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDto);
    }

    public static ResponseEntity<SignInResponseDto> notExistedPassword() {
        SignInResponseDto responseDto = new SignInResponseDto(ResponseCode.NEP);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDto);
    }

    public static ResponseEntity<SignInResponseDto> newOauthSignIn(String id) {
        return ResponseEntity.status(HttpStatus.OK).body(new SignInResponseDto(ResponseCode.NEW_SIGN, id));
    }
}
