package back_end.springboot.repository.projection;

import back_end.springboot.common.ChatRoomType;

public interface ChatRoomProjection {
    Integer getChatroomId();
    String getTitle();
    ChatRoomType getType();
    String getLastMessage();
    String getProfileImages();
    Integer getUnreadCount();
}

 