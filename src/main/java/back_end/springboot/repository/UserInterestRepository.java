package back_end.springboot.repository;

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
            "    SUM(aggregated.weighted_score) AS total_score " + // 여기서 total_score라는 별칭이 생성됨
            "FROM ( " +
            "    SELECT " +
            "        pf.user_id, " +
            "        pt.tag_name AS keyword, " +
            "        (1.0 * " +
            "         GREATEST(0.1, 1.0 - (DATEDIFF(NOW(), pf.create_at) / :days) * 0.9)) AS weighted_score " +
            "    FROM post_favorite pf " +
            "    JOIN post_tags pt ON pf.post_id = pt.post_id " +
            "    WHERE pf.create_at >= DATE_SUB(NOW(), INTERVAL :days DAY) " +

            "    UNION ALL " +

            "    SELECT " +
            "        pc.user_id, " +
            "        pt.tag_name AS keyword, " +
            "        (2.0 * " +
            "         GREATEST(0.1, 1.0 - (DATEDIFF(NOW(), pc.create_at) / :days) * 0.9)) AS weighted_score " +
            "    FROM post_comment pc " +
            "    JOIN post_tags pt ON pc.post_id = pt.post_id " +
            "    WHERE pc.create_at >= DATE_SUB(NOW(), INTERVAL :days DAY) " +

            ") AS aggregated " +
            "GROUP BY aggregated.user_id, aggregated.keyword " +

            "ON DUPLICATE KEY UPDATE " +
            // 수정: VALUES() 함수는 INSERT 대상 컬럼명인 score를 사용해야 합니다.
            "    score = score + VALUES(score)", nativeQuery = true)
    void updateHashTagInterests(@Param("days") int days);
}