package back_end.springboot.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import back_end.springboot.entity.ChatRoomEntity;
import back_end.springboot.repository.projection.ChatRoomProjection;
import back_end.springboot.repository.projection.SimpleUserProjection;

import org.springframework.data.repository.query.Param;

public interface ChatRoomRepository extends JpaRepository<ChatRoomEntity, Integer> {
        @Query(value = "with join_chatroom_list as (\n" + //
                        "    select chatroom_id\n" + //
                        "    from (\n" + //
                        "        select cp.chatroom_id, \n" + //
                        "               row_number() over (order by c.last_message_time desc) as rn\n" + //
                        "        from chatroom_participant cp\n" + //
                        "        join chatroom c on cp.chatroom_id = c.id\n" + //
                        "        where cp.user_id = :id\n" + //
                        "    ) as t\n" + //
                        "    where rn <= 3\n" + //
                        ")\n" + //
                        "\n" + //
                        "select u.id, u.name, u.profile_image, sum(c.weight) as total_weight\n" + //
                        "from (select chatroom_id, user_id, dense_rank() over(order by c.last_message_time desc) as weight\n"
                        + //
                        "from chatroom_participant as cp join chatroom as c\n" + //
                        "on cp.chatroom_id = c.id\n" + //
                        "where exists(select * from join_chatroom_list as j where j.chatroom_id = cp.chatroom_id)) as c join user as u\n"
                        + //
                        "on c.user_id = u.id\n" + //
                        "where c.user_id != :id\n" + //
                        "group by user_id\n" + //
                        "order by total_weight desc", nativeQuery = true)
        List<SimpleUserProjection> getRecommendInviteUserList(@Param("id") String id);

        // @Query(value = "select cp.chatroom_id, coalesce(c.title,
        // json_arrayagg(u.name)) as title, c.type, c.last_message,
        // coalesce(c.profile_image, json_arrayagg(u.profile_image)) as
        // profile_images\n" + //
        // "from chatroom_participant as cp join chatroom as c on cp.chatroom_id =
        // c.id\n" + //
        // "join chatroom_participant as cp2 on cp.chatroom_id = cp2.chatroom_id\n" + //
        // "join user as u on cp2.user_id = u.id\n" + //
        // "where cp.user_id = :id and cp2.user_id != :id \n" + //
        // "group by cp.chatroom_id, c.title, c.last_message, c.profile_image\n" + //
        // "order by c.last_message_time desc", nativeQuery = true)
        // List<ChatRoomProjection> findByUserId(@Param("id") String id);

        @Query(value = """
                        select
                            cp.chatroom_id as chatroomId,
                            coalesce(c.title, json_arrayagg(u.name)) as title,
                            c.type as type,
                            c.last_message as lastMessage,
                            coalesce(c.profile_image, json_arrayagg(u.profile_image), '[]') as profileImages,
                            coalesce(sub.unreadCount, 0) as unreadCount
                        from chatroom_participant as cp
                            join chatroom as c on cp.chatroom_id = c.id
                            join chatroom_participant as cp2 on cp.chatroom_id = cp2.chatroom_id
                            join user as u on cp2.user_id = u.id
                            left join (
                                select
                                    cm.chatroom_id,
                                    least(count(*), 99) as unreadCount
                                from chatroom_last_read as clr
                                    join chatroom_message as cm
                                        on clr.chatroom_id = cm.chatroom_id
                                        and clr.last_read_message_id < cm.id
                                where clr.user_id = :id
                                  and (cm.type not in ('LEAVE', 'INVITE'))
                                group by cm.chatroom_id
                            ) as sub on cp.chatroom_id = sub.chatroom_id
                        where
                            cp.user_id = :id
                            and cp2.user_id != :id
                            and cp.is_hidden = false
                            and cp2.is_hidden = false
                        group by
                            cp.chatroom_id, c.title, c.type, c.last_message, c.profile_image, sub.unreadCount
                        order by
                            c.last_message_time desc
                        """, nativeQuery = true)
        List<ChatRoomProjection> findByUserId(@Param("id") String id);

        @Query(value = "select u.id, u.name, u.profile_image as profileImage\n" +
                        "from chatroom_participant as cp join user as u\n" +
                        "on cp.user_id = u.id\n" +
                        "where cp.chatroom_id = :id and cp.is_hidden = false", nativeQuery = true)
        Set<SimpleUserProjection> findByJoinedChatRoomUsers(@Param("id") Integer id);

        @Query(value = "select u.id, u.name, u.profile_image as profileImage\n" +
                        "from chatroom_participant as cp join user as u\n" +
                        "on cp.user_id = u.id\n" +
                        "where cp.chatroom_id = :id", nativeQuery = true)
        Set<SimpleUserProjection> findByJoinedChatRoomUsersForHistory(@Param("id") Integer id);

        @Query(value = "select c.id\t\n" + //
                        "from chatroom_participant as cp inner join chatroom_participant as cp2 \n" + //
                        "on cp.chatroom_id = cp2.chatroom_id\n" + //
                        "inner join chatroom as c\n" + //
                        "on cp.chatroom_id = c.id\n" + //
                        "where cp.user_id = :id1 and cp2.user_id = :id2 and c.type = 'DIRECT'", nativeQuery = true)
        Integer existedJoinChatRoom(@Param("id1") String id1, @Param("id2") String id2);

}
