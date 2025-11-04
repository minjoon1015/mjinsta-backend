package back_end.springboot.dto.response.chat;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import back_end.springboot.dto.object.chat.ChatRoomDto;
import back_end.springboot.dto.response.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetChatRoomListResponseDto extends ResponseDto {
    private List<ChatRoomDto> list = null;
    public GetChatRoomListResponseDto(ResponseCode code, List<ChatRoomDto> list) {
        super(code);
        this.list = list;
    }

    public static ResponseEntity<GetChatRoomListResponseDto> success(List<ChatRoomDto> list) {
        GetChatRoomListResponseDto responseDto = new GetChatRoomListResponseDto(ResponseCode.SC, list);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

}
