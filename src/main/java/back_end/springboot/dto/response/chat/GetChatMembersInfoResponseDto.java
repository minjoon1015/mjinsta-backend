package back_end.springboot.dto.response.chat;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import back_end.springboot.dto.object.user.SimpleUserDto;
import back_end.springboot.dto.response.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetChatMembersInfoResponseDto extends ResponseDto {
    private List<SimpleUserDto> list = null;

    public GetChatMembersInfoResponseDto(ResponseCode code, List<SimpleUserDto> list) {
        super(code);
        this.list = list;
    }
    
    public static ResponseEntity<GetChatMembersInfoResponseDto> success(List<SimpleUserDto> list) {
        return ResponseEntity.status(HttpStatus.OK).body(new GetChatMembersInfoResponseDto(ResponseCode.SC, list));
    }
}
