package back_end.springboot.dto.response.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import back_end.springboot.dto.response.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditInfoResponseDto extends ResponseDto {
    public EditInfoResponseDto(ResponseCode code) {
        super(code);
    }
    
    public static ResponseEntity<EditInfoResponseDto> success() {        
        return ResponseEntity.status(HttpStatus.OK).body(new EditInfoResponseDto(ResponseCode.SC));
    }
}
