package back_end.springboot.repository;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import back_end.springboot.entity.ChatRoomMessageEntity;

public interface ChatRoomMessageRepository extends JpaRepository<ChatRoomMessageEntity, Integer> {
    // @Query(value = "select *\n" + //
    // "from chatroom_message \n" + //
    // "where chatroom_id = :id\n" + //
    // "order by create_at desc limit 30", nativeQuery = true)
    // List<ChatRoomMessageEntity> findByChatRoomId(@Param("id") Integer
    // chatRoomId);

    // @Query(value = "select *\n" + //
    // "from chatroom_message\n" + //
    // "where chatroom_id = :id and id < :messageId \n" + //
    // "order by create_at desc limit 30", nativeQuery = true )
    // List<ChatRoomMessageEntity> findByChatRoomIdIndexCreateAt(@Param("id")
    // Integer chatRoomId, @Param("messageId") String messageId);

    @Query("SELECT m FROM ChatRoomMessageEntity m WHERE m.chatroomId = :chatRoomId ORDER BY m.createAt DESC")
    List<ChatRoomMessageEntity> findMessagesWithAttachments(
            @Param("chatRoomId") Integer chatRoomId,
            Pageable pageable);

    // int pageSize = 30;
    // Pageable pageable = PageRequest.of(0, pageSize);

    @Query("SELECT m FROM ChatRoomMessageEntity m WHERE m.chatroomId = :chatRoomId and m.id < :messageId ORDER BY m.createAt DESC")
    List<ChatRoomMessageEntity> findMessagesWithAttachmentsIndex(
            @Param("chatRoomId") Integer chatRoomId,
            @Param("messageId") String messageId,
            Pageable pageable);
}
