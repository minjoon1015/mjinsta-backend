package back_end.springboot.entity;

import java.time.LocalDateTime;

import back_end.springboot.entity.Primary.PostFavoriteId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "post_favorite")
public class PostFavoriteEntity {
    @EmbeddedId
    private PostFavoriteId id;
    private LocalDateTime createAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    private PostEntity post;

    public PostFavoriteEntity(PostEntity postEntity, String userId, LocalDateTime createAt) {
        this.id = new PostFavoriteId(postEntity, userId);
        this.createAt = createAt;
    }
}
