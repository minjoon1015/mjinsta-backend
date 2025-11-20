package back_end.springboot.entity.Primary;

import java.io.Serializable;

import back_end.springboot.entity.PostEntity;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class PostFavoriteId implements Serializable {
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private PostEntity post;

    public PostFavoriteId(PostEntity post, String userId) {
        this.post = post;
        this.userId = userId;
    }
}

