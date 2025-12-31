package back_end.springboot.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import back_end.springboot.entity.PostCommentEntity;

@Repository
public interface PostCommentRepository extends JpaRepository<PostCommentEntity, Integer> {
    @Query("select pc from PostCommentEntity pc where pc.postId = :postId and pc.user.id = :userId order by pc.id DESC")
    List<PostCommentEntity> findByPostIdAndUserIdOrderByCreateAtDesc(Integer postId, String userId, Pageable pageable);

    @Query("select pc from PostCommentEntity pc where pc.postId = :postId and pc.id < :commentId and pc.id NOT IN (:ExclusionIds) order by pc.id DESC")
    List<PostCommentEntity> findByPostIdAndIdGreaterThanAndIdNotInOrderByCreateAtDesc(Integer postId, Integer commentId, List<Integer> ExclusionIds, Pageable pageable);   

    @Query("select pc from PostCommentEntity pc where pc.postId = :postId and pc.id < :commentId order by pc.id DESC")
    List<PostCommentEntity> findByPostIdAndIdGreaterThanOrderByCreateAtDesc(Integer postId, Integer commentId, Pageable pageable);
}
