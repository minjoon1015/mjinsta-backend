package back_end.springboot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import back_end.springboot.entity.PostEntity;

@Repository
public interface PostRepository extends JpaRepository<PostEntity, Integer> {
    @Query(value = "select * from post where user_id = :userId order by id desc limit 9", nativeQuery = true)
    List<PostEntity> findAllById(@Param("userId")String userId);

    @Query(value = "select * from post where user_id = :userId and id < :postId order by id desc limit 9", nativeQuery = true)
    List<PostEntity> findAllByIdPaging(@Param("userId")String userId, @Param("postId") Integer postId);

    @Query("select p from PostEntity p where p.id = :postId")
    PostEntity findByPostIdDetailsInfo(@Param("postId") Integer postId);
    
    @Query(value = "select * from post where id = :postId for update", nativeQuery = true)
    Optional<PostEntity> findByIdWithLock(Integer postId);
}
