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
        List<PostEntity> findAllById(@Param("userId") String userId);

        @Query(value = "select * from post where user_id = :userId and id < :postId order by id desc limit 9", nativeQuery = true)
        List<PostEntity> findAllByIdPaging(@Param("userId") String userId, @Param("postId") Integer postId);

        @Query("select p from PostEntity p where p.id = :postId")
        PostEntity findByPostIdDetailsInfo(@Param("postId") Integer postId);

        @Query(value = "select * from post where id = :postId for update", nativeQuery = true)
        Optional<PostEntity> findByIdWithLock(Integer postId);

        @Query(value = "SELECT p.id, p.create_at " +
                        "FROM post p " +
                        "WHERE p.user_id IN (:followedUserIds) " +
                        "AND p.id NOT IN (:readPostIds) " +
                        "ORDER BY p.create_at DESC " +
                        "LIMIT :limit", nativeQuery = true)
        List<Object[]> findFollowedPosts(
                        @Param("followedUserIds") List<String> followedUserIds,
                        @Param("readPostIds") List<Integer> readPostIds,
                        @Param("limit") int limit);

        @Query(value = "SELECT pt.post_id, " +
                        "       SUM( " +
                        "           (CASE " +
                        "               WHEN pt.type = 'HASH_TAG' THEN 100.0 " +
                        "               WHEN pt.type = 'AI_TAG' THEN 50.0 " +
                        "               ELSE 1.0 " +
                        "           END) " +
                        "           + (p.favorite_count * 1.0) " +
                        "           + (10.0 / (DATEDIFF(NOW(), p.create_at) + 1)) " +
                        "       ) AS rank_score " +
                        "FROM post_tags pt " +
                        "JOIN post p ON pt.post_id = p.id " +
                        "WHERE pt.tag_name IN (:interestKeywords) " +
                        "  AND p.user_id != :userId " +
                        "  AND p.id NOT IN (:readPostIds) " +
                        "GROUP BY pt.post_id " +
                        "ORDER BY rank_score DESC " +
                        "LIMIT :limit", nativeQuery = true)
        List<Object[]> findRecommendedPostScores(
                        @Param("userId") String userId,
                        @Param("interestKeywords") List<String> interestKeywords,
                        @Param("readPostIds") List<Integer> readPostIds,
                        @Param("limit") int limit);

        @Query(value = "select * from post where user_id != :userId and (favorite_count <:favoriteCount or (favorite_count = :favoriteCount and id < :postId)) order by id desc, favorite_count desc limit :limit", nativeQuery = true)
        List<PostEntity> findPopularPostsFallbackCursor(@Param("userId") String userId, @Param("postId") Integer postId, @Param("favoriteCount") Integer favoriteCount, @Param("limit") int limit);

        @Query(value = "select * from post where user_id != :userId order by id desc, favorite_count desc limit :limit", nativeQuery = true)
        List<PostEntity> findPopularPostsFallback(@Param("userId") String userId, @Param("limit") int limit);
}
