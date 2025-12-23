package back_end.springboot.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import back_end.springboot.entity.PostViewHistoryEntity;

@Repository
public interface PostViewHistoryRepository extends JpaRepository<PostViewHistoryEntity, Integer> {
    @Query(value = "select distinct post_id from post_view_history where user_id = :userId and viewed_at >= DATE_SUB(NOW(), INTERVAL :days DAY)", nativeQuery = true)
    public List<Integer> findByUserIdDays(@Param("userId") String userId, @Param("days") Integer days);
}
