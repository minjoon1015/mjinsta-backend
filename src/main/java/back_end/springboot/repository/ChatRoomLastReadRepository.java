package back_end.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import back_end.springboot.entity.ChatRoomLastReadEntity;
import back_end.springboot.entity.Primary.ChatRoomLastReadParticipantId;

public interface ChatRoomLastReadRepository extends JpaRepository<ChatRoomLastReadEntity, ChatRoomLastReadParticipantId> {
    @Query(value = "select * \n" +
            "from chatroom_last_read as lr \n" +
            "where lr.chatroom_id = :chatRoomId and exists (select * from chatroom_participant as cp where cp.chatroom_id = :chatRoomId and is_hidden = false and cp.user_id = lr.user_id )", nativeQuery = true)
    List<ChatRoomLastReadEntity> findById(@Param("chatRoomId") Integer chatRoomId);

    @Query(value = "select * from chatroom_last_read where chatroom_id = :chatRoomId", nativeQuery = true)
    List<ChatRoomLastReadEntity> findAllByChatRoomId(@Param("chatRoomId") Integer chatRoomId);
    
}
