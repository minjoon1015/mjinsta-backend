package back_end.springboot.entity;

import back_end.springboot.entity.Primary.PostTagsId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "post_tags")
@Getter
@Setter
@NoArgsConstructor
public class PostTagsEntity {
    @EmbeddedId
    private PostTagsId id;

    public PostTagsEntity(Integer postId, String tagName) {
        this.id = new PostTagsId(postId, tagName);
    }
}
