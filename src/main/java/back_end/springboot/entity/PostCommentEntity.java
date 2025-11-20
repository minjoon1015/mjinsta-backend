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
@Table(name = "post_comment")
@Getter
@Setter
@NoArgsConstructor
public class PostCommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String userId;
    private Integer postId;
    private String content;
    private LocalDateTime createAt;

    public PostCommentEntity(String userId, Integer postId, String content, LocalDateTime createAt) {
        this.userId = userId;
        this.postId = postId;
        this.content = content;
        this.createAt = createAt;
    }
}
