package back_end.springboot.dto.response.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import back_end.springboot.dto.response.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileUrlResponseDto extends ResponseDto {
    private String url = null;

    public UpdateProfileUrlResponseDto(ResponseCode code, String url) {
        super(code);
        this.url = url;
    }
    
    public static ResponseEntity<UpdateProfileUrlResponseDto> success(String url) {
        UpdateProfileUrlResponseDto responseDto = new UpdateProfileUrlResponseDto(ResponseCode.SC, url);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
