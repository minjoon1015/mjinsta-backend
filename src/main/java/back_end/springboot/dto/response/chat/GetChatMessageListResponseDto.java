package back_end.springboot.dto.response.chat;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import back_end.springboot.dto.object.chat.ChatMessageDto;
import back_end.springboot.dto.response.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetChatMessageListResponseDto extends ResponseDto{
    List<ChatMessageDto> list = null;

    public GetChatMessageListResponseDto(ResponseCode code, List<ChatMessageDto> list) {
        super(code);
        this.list = list;
    }

    public static ResponseEntity<GetChatMessageListResponseDto> success(List<ChatMessageDto> list) {
        GetChatMessageListResponseDto responseDto = new GetChatMessageListResponseDto(ResponseCode.SC, list);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
    
}
