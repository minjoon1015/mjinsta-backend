package back_end.springboot.dto.response.chat;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import back_end.springboot.dto.response.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomCreateResponseDto extends ResponseDto {

    public ChatRoomCreateResponseDto(ResponseCode code) {
        super(code);
    }

    public static ResponseEntity<ChatRoomCreateResponseDto> success() {
        ChatRoomCreateResponseDto responseDto = new ChatRoomCreateResponseDto(ResponseCode.SC);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
    
}
