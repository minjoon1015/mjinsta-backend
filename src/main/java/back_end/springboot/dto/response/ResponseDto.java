package back_end.springboot.dto.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseDto {
    private ResponseCode code;

    public ResponseDto(ResponseCode code) {
        this.code = code;
    }

    public static ResponseEntity<ResponseDto> badRequest() {
        ResponseDto responseDto = new ResponseDto(ResponseCode.BR);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }

    public static ResponseEntity<ResponseDto> databaseError() {
        ResponseDto responseDto = new ResponseDto(ResponseCode.DE);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
    }
}
