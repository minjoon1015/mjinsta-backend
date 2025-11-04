package back_end.springboot.dto.response.user;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import back_end.springboot.dto.object.user.SimpleUserDto;
import back_end.springboot.dto.response.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetUserListForKeywordResponseDto extends ResponseDto {
    private List<SimpleUserDto> users = null;

    public GetUserListForKeywordResponseDto(ResponseCode code) {
        super(code);
    }

    public GetUserListForKeywordResponseDto(ResponseCode code, List<SimpleUserDto> users) {
        super(code);
        this.users = users;
    }
    
    public static ResponseEntity<GetUserListForKeywordResponseDto> success(List<SimpleUserDto> users) {
        GetUserListForKeywordResponseDto responseDto = new GetUserListForKeywordResponseDto(ResponseCode.SC, users);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
