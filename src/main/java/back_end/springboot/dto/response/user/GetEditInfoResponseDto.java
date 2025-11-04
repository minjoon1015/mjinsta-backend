package back_end.springboot.dto.response.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import back_end.springboot.dto.object.user.EditUserDto;
import back_end.springboot.dto.response.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetEditInfoResponseDto extends ResponseDto {
    private EditUserDto user;

    public GetEditInfoResponseDto(ResponseCode code, EditUserDto user) {
        super(code);
        this.user = user;
    }

    public static ResponseEntity<GetEditInfoResponseDto> success(EditUserDto user) {
        return ResponseEntity.status(HttpStatus.OK).body(new GetEditInfoResponseDto(ResponseCode.SC, user));
    }
    
}
