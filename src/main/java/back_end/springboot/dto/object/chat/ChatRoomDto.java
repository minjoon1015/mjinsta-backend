package back_end.springboot.dto.object.chat;

import back_end.springboot.common.ChatRoomType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDto {
    Integer chatroomId;
    String title;
    String lastMessage;
    String profileImages;
    Integer unreadCount;
    ChatRoomType type;
}
