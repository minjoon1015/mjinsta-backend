package back_end.springboot.dto.response.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import back_end.springboot.dto.response.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditPasswordResponseDto extends ResponseDto {

    public EditPasswordResponseDto(ResponseCode code) {
        super(code);
    }

    public static ResponseEntity<EditPasswordResponseDto> success() {
        return ResponseEntity.status(HttpStatus.OK).body(new EditPasswordResponseDto(ResponseCode.SC));
    }

    public static ResponseEntity<EditPasswordResponseDto> notExistsPassword() {
        return ResponseEntity.status(HttpStatus.OK).body(new EditPasswordResponseDto(ResponseCode.NEP));
    }

}
