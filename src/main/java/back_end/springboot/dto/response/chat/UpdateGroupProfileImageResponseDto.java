package back_end.springboot.dto.response.chat;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import back_end.springboot.dto.response.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateGroupProfileImageResponseDto extends ResponseDto {
    private String url;

    public UpdateGroupProfileImageResponseDto(ResponseCode code, String url) {
        super(code);
        this.url = url;
    }

    public static ResponseEntity<? super UpdateGroupProfileImageResponseDto> success(String url) {
        return ResponseEntity.status(HttpStatus.OK).body(new UpdateGroupProfileImageResponseDto(ResponseCode.SC, url));
    }
    
}
