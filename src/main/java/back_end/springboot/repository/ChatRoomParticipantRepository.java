package back_end.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import back_end.springboot.entity.ChatRoomParticipantEntity;
import back_end.springboot.entity.Primary.ChatRoomParticipantId;
import back_end.springboot.repository.projection.SimpleUserProjection;

public interface ChatRoomParticipantRepository extends JpaRepository<ChatRoomParticipantEntity, ChatRoomParticipantId> {
    @Query(value = "select * from chatroom_participant where user_id = :id", nativeQuery = true)
    List<ChatRoomParticipantEntity> findAllByUserId(@Param("id") String id);

    @Query(value = "SELECT u.id, u.name, u.profile_image " +
            "FROM user AS u " +
            "WHERE u.id LIKE CONCAT(:id, '%') " +
            "AND NOT EXISTS ( " +
            "    SELECT * " +
            "    FROM chatroom_participant AS cp " +
            "    WHERE chatroom_id = :chatRoomId " +
            "    AND u.id = cp.user_id and cp.is_hidden = false" +
            ")", nativeQuery = true)
    List<SimpleUserProjection> searchKeywordNotJoinedChatRoomId(@Param("id") String id, @Param("chatRoomId") Integer chatRoomId);

    @Query(value = "select * from chatroom_participant where chatroom_id = :chatRoomId", nativeQuery = true)
    List<ChatRoomParticipantEntity> findByChatRoomId(@Param("chatRoomId") Integer chatRoomId);
}
