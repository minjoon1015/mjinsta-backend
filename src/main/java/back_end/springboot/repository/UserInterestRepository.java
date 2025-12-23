package back_end.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import back_end.springboot.entity.UserInterestEntity;
import back_end.springboot.entity.Primary.UserInterestId;
import jakarta.transaction.Transactional;

@Repository
public interface UserInterestRepository extends JpaRepository<UserInterestEntity, UserInterestId> {

        // 좋아요 가중치는 1.0 댓글 가중치는 2.0
        // 부분 성공이 발생할 수 있으므로 트랜잭션 지정
        // 시간에 따라서 감쇠 적용
        @Modifying
        @Transactional
        @Query(value = "INSERT INTO user_interest (user_id, interest_keyword, type, score) " +
                        "SELECT " +
                        "    aggregated.user_id, " +
                        "    aggregated.keyword, " +
                        "    'HASH_TAG' AS type, " +
                        "    SUM(aggregated.weighted_score) AS total_score " +
                        "FROM ( " +
                        "    SELECT " +
                        "        pf.user_id, " +
                        "        pt.tag_name AS keyword, " +
                        "        (1.0 * " +
                        "         GREATEST(0.1, 1.0 - (DATEDIFF(NOW(), pf.create_at) / :days) * 0.9)) AS weighted_score "
                        +
                        "    FROM post_favorite pf " +
                        "    JOIN post_tags pt ON pf.post_id = pt.post_id " +
                        "    WHERE pf.create_at >= DATE_SUB(NOW(), INTERVAL :days DAY) " +

                        "    UNION ALL " +

                        "    SELECT " +
                        "        pc.user_id, " +
                        "        pt.tag_name AS keyword, " +
                        "        (2.0 * " +
                        "         GREATEST(0.1, 1.0 - (DATEDIFF(NOW(), pc.create_at) / :days) * 0.9)) AS weighted_score "
                        +
                        "    FROM post_comment pc " +
                        "    JOIN post_tags pt ON pc.post_id = pt.post_id " +
                        "    WHERE pc.create_at >= DATE_SUB(NOW(), INTERVAL :days DAY) " +

                        ") AS aggregated " +
                        "GROUP BY aggregated.user_id, aggregated.keyword " +

                        "ON DUPLICATE KEY UPDATE " +
                        "    score = score + VALUES(score)", nativeQuery = true)
        void updateHashTagInterests(@Param("days") int days);

        @Modifying
        @Transactional
        @Query(value = "INSERT INTO user_interest (user_id, interest_keyword, type, score) " +
                        "SELECT " +
                        "    aggregated.user_id, " +
                        "    aggregated.keyword, " +
                        "    'AI_TAG' AS type, " +
                        "    SUM(aggregated.weighted_score) AS total_score " +
                        "FROM ( " +

                        "    SELECT " +
                        "        pf.user_id, " +
                        "        pt.tag_name AS keyword, " +
                        "        (0.5 * " +
                        "         GREATEST(0.1, 1.0 - (DATEDIFF(NOW(), pf.create_at) / :days) * 0.9)) AS weighted_score "
                        +
                        "    FROM post_favorite pf " +
                        "    JOIN post_tags pt ON pf.post_id = pt.post_id " +
                        "    WHERE pf.create_at >= DATE_SUB(NOW(), INTERVAL :days DAY) " +
                        "    AND pt.tag_name IS NOT NULL " +

                        "    UNION ALL " +

                        "    SELECT " +
                        "        pc.user_id, " +
                        "        pt.tag_name AS keyword, " +
                        "        (1.0 * " +
                        "         GREATEST(0.1, 1.0 - (DATEDIFF(NOW(), pc.create_at) / :days) * 0.9)) AS weighted_score "
                        +
                        "    FROM post_comment pc " +
                        "    JOIN post_tags pt ON pc.post_id = pt.post_id " +
                        "    WHERE pc.create_at >= DATE_SUB(NOW(), INTERVAL :days DAY) " +
                        "    AND pt.tag_name IS NOT NULL " +

                        ") AS aggregated " +
                        "GROUP BY aggregated.user_id, aggregated.keyword " +

                        "ON DUPLICATE KEY UPDATE " +
                        "    score = score + VALUES(score)", nativeQuery = true)
        void updateAiObjectInterests(@Param("days") int days);

        @Modifying
        @Transactional
        @Query(value = "INSERT INTO user_interest (user_id, interest_keyword, type, score) " +
                        "SELECT " +
                        "    pvh.user_id, " +
                        "    pt.tag_name AS keyword, " +
                        "    'HASH_TAG' AS type, " +
                        "    SUM(0.5 * " +
                        "        GREATEST(0.1, 1.0 - (DATEDIFF(NOW(), pvh.viewed_at) / :days) * 0.9)) AS total_score " +
                        "FROM " +
                        "    post_view_history pvh " +
                        "JOIN " +
                        "    post_tags pt ON pvh.post_id = pt.post_id " +
                        "WHERE " +
                        "    pvh.viewed_at >= DATE_SUB(NOW(), INTERVAL :days DAY) " +
                        "GROUP BY " +
                        "    pvh.user_id, pt.tag_name " +
                        "ON DUPLICATE KEY UPDATE " +
                        "    score = score + VALUES(score)", nativeQuery = true)
        void updatePostViewInterests(@Param("days") int days);

        @Modifying
        @Transactional
        @Query(value = """
                        INSERT INTO user_interest (
                            user_id,
                            interest_keyword,
                            type,
                            score
                        )
                        SELECT
                            pvh.user_id,
                            pt.tag_name AS interest_keyword,
                            'AI_TAG' AS type,
                            SUM(
                                0.05 *
                                GREATEST(
                                    0.1,
                                    1.0 - (DATEDIFF(NOW(), pvh.viewed_at) / :days) * 0.9
                                )
                            ) AS total_score
                        FROM post_view_history pvh
                        JOIN post_tags pt
                            ON pvh.post_id = pt.post_id
                        WHERE
                            pvh.viewed_at >= DATE_SUB(NOW(), INTERVAL :days DAY)
                            AND pt.tag_name IS NOT NULL
                        GROUP BY
                            pvh.user_id,
                            pt.tag_name
                        ON DUPLICATE KEY UPDATE
                            score = score + VALUES(score)
                        """, nativeQuery = true)
        void updatePostViewAiInterests(@Param("days") int days);

        @Query(value = "select interest_keyword from user_interest where user_id = :userId and type = :type  order by score desc limit :limit", nativeQuery = true)
        List<String> findByUserIdTypeOrderByScoreDesc(@Param("userId") String userId, @Param("type") String type, @Param("limit") int limit);
}