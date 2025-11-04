package back_end.springboot.dto.response.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import back_end.springboot.dto.response.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpResponseDto extends ResponseDto {

    public SignUpResponseDto(ResponseCode code) {
        super(code);
    }
    
    public static ResponseEntity<SignUpResponseDto> success() {
        SignUpResponseDto responseDto = new SignUpResponseDto(ResponseCode.SC);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    public static ResponseEntity<SignUpResponseDto> duplicateIdNotCheck() {
        SignUpResponseDto responseDto = new SignUpResponseDto(ResponseCode.DU);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }

    public static ResponseEntity<SignUpResponseDto> duplicateEmail() {
        SignUpResponseDto responseDto = new SignUpResponseDto(ResponseCode.DUE);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }
    
    public static ResponseEntity<SignUpResponseDto> notVerifyCode() {
        SignUpResponseDto responseDto = new SignUpResponseDto(ResponseCode.NV);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }

}
