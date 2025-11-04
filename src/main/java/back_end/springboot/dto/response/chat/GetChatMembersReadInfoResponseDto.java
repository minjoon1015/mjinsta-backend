package back_end.springboot.dto.response.chat;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import back_end.springboot.dto.object.chat.ChatMembersReadInfoDto;
import back_end.springboot.dto.response.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetChatMembersReadInfoResponseDto extends ResponseDto {
    List<ChatMembersReadInfoDto> list = null;

    public GetChatMembersReadInfoResponseDto(ResponseCode code, List<ChatMembersReadInfoDto> list) {
        super(code);
        this.list = list;
    }
    
    public static ResponseEntity<GetChatMembersReadInfoResponseDto> success(List<ChatMembersReadInfoDto> list) {
        GetChatMembersReadInfoResponseDto responseDto = new GetChatMembersReadInfoResponseDto(ResponseCode.SC, list);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
