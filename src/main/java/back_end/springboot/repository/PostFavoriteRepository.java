package back_end.springboot.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import back_end.springboot.entity.PostFavoriteEntity;

@Repository
public interface PostFavoriteRepository extends JpaRepository<PostFavoriteEntity, Integer> {

    @Query(value="select * from post_favorite where post_id = :postId and user_id = :userId", nativeQuery=true)
    PostFavoriteEntity findByPostIdAndUserId(@Param("postId") Integer postId, @Param("userId")String userId);
    
}
