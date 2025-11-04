package back_end.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import back_end.springboot.entity.UserEntity;
import back_end.springboot.repository.projection.SimpleUserProjection;
import back_end.springboot.repository.projection.UserDetailsInfoProjection;


@Repository
public interface UserRepository extends JpaRepository<UserEntity, String>  {
    UserEntity findByIdAndPassword(String id, String password);
    boolean existsByEmail(String email);
    UserEntity findByEmail(String email);

    @Query(value = "select u.id, u.name, u.profile_image\n" + //
                "from follows as f join user u \n" + //
                "on f.following_id = u.id\n" + //
                "where follower_id = :id and following_id like concat('%', :keyword, '%') limit 10;", nativeQuery = true)
    List<SimpleUserProjection> findAllByIdInFollowing(@Param("id") String id, @Param("keyword") String keyword);
    
    @Query(value = "select id, name, profile_image\n" + //
                "from user\n" + //
                "where id like concat('%', :keyword, '%');", nativeQuery = true)
    List<SimpleUserProjection> findAllByIdLimit(@Param("keyword") String keyword, @Param("keyword") int limit);

    @Query(value = "select u.id, u.name, u.profile_image, u.comment, u.follow_count, u.follower_count, u.post_count, " +
               "case when f.follower_id = :userId and f.following_id = :searchId then true else false end as isFollowed " +
               "from user as u " +
               "left join follows as f on u.id = f.following_id and f.follower_id = :userId " +
               "where u.id = :searchId limit 5", 
       nativeQuery = true)
    UserDetailsInfoProjection findUserDetailsInfoById(@Param("searchId") String searchId, @Param("userId") String userId); 
}