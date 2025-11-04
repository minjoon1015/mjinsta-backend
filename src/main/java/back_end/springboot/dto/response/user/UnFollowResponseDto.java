package back_end.springboot.dto.response.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import back_end.springboot.dto.response.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnFollowResponseDto extends ResponseDto {

    public UnFollowResponseDto(ResponseCode code) {
        super(code);        
    }
    
    public static ResponseEntity<UnFollowResponseDto> success() {
        UnFollowResponseDto responseDto = new UnFollowResponseDto(ResponseCode.SC);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
