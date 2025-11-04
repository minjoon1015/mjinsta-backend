package back_end.springboot.dto.response.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import back_end.springboot.dto.response.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DuplicateCheckIdResponseDto extends ResponseDto {
    public DuplicateCheckIdResponseDto(ResponseCode code) {
        super(code);
    }

    public static ResponseEntity<DuplicateCheckIdResponseDto> success() {
        DuplicateCheckIdResponseDto responseDto = new DuplicateCheckIdResponseDto(ResponseCode.SC);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    public static ResponseEntity<DuplicateCheckIdResponseDto> faild() {
        DuplicateCheckIdResponseDto responseDto = new DuplicateCheckIdResponseDto(ResponseCode.DU);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
    }
}
