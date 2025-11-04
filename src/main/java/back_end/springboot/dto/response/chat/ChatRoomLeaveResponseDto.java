package back_end.springboot.dto.response.chat;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import back_end.springboot.dto.response.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomLeaveResponseDto extends ResponseDto {

    public ChatRoomLeaveResponseDto(ResponseCode code) {
        super(code);
    }
    
    public static ResponseEntity<ChatRoomLeaveResponseDto> success() {
        return ResponseEntity.status(HttpStatus.OK).body(new ChatRoomLeaveResponseDto(ResponseCode.SC));
    }
}
