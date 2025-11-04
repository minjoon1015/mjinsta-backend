package back_end.springboot.dto.response.chat;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import back_end.springboot.dto.response.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomInviteUserResponseDto extends ResponseDto {

    public ChatRoomInviteUserResponseDto(ResponseCode code) {
        super(code);
    }
    
    public static ResponseEntity<ChatRoomInviteUserResponseDto> success() {
        return ResponseEntity.status(HttpStatus.OK).body(new ChatRoomInviteUserResponseDto(ResponseCode.SC));
    }
}
