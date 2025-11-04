package back_end.springboot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import back_end.springboot.entity.FollowsEntity;
import back_end.springboot.repository.projection.SimpleUserProjection;

@Repository
public interface FollowsRepository extends JpaRepository<FollowsEntity, Integer> {
    @Query(value = "select * from follows where follower_id = ?1 and following_id = ?2", nativeQuery = true)
    Optional<FollowsEntity> findByFollowerIdAndFollowingId(String id1, String id2);

    @Query(value = "with recommend_list as (select f2.following_id, count(f2.following_id) as count\n" + //
                "from follows as f1 inner join follows as f2 \n" + //
                "on f1.following_id = f2.follower_id\n" + //
                "where f1.follower_id = :id and f2.following_id != :id and not exists (select 1\n" + //
                "from follows as f3\n" + //
                "where f3.follower_id = :id and f3.following_id = f2.following_id)\n" + //
                "group by f2.following_id\n" + //
                "order by count desc limit 20)\n" + //
                "\n" + //
                "select u.id, u.name, u.profile_image\n" + //
                "from recommend_list as rl inner join user as u\n" + //
                "on rl.following_id = u.id", nativeQuery = true)
    List<SimpleUserProjection> findRecommendUsersById(@Param("id") String id);
}
