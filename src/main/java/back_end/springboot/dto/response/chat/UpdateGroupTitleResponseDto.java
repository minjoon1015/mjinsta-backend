package back_end.springboot.dto.response.chat;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import back_end.springboot.dto.response.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateGroupTitleResponseDto extends ResponseDto {
    private String title;

    public UpdateGroupTitleResponseDto(ResponseCode code, String title) {
        super(code);
        this.title = title;
    }
    
    public static ResponseEntity<UpdateGroupTitleResponseDto> success(String title) {
        return ResponseEntity.status(HttpStatus.OK).body(new UpdateGroupTitleResponseDto(ResponseCode.SC, title));
    }
}
