package back_end.springboot.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "post_view_history")
@Getter
@Setter
@NoArgsConstructor
public class PostViewHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer postId;
    private String userId;
    private LocalDateTime viewedAt;
    private Integer timeSpentSeconds;

    public PostViewHistoryEntity(Integer postId, String userId, LocalDateTime viewedAt, Integer timeSpentSeconds) {
        this.postId = postId;
        this.userId = userId;
        this.viewedAt = viewedAt;
        this.timeSpentSeconds = timeSpentSeconds;
    }
}
