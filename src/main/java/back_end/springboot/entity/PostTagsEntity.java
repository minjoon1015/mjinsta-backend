package back_end.springboot.entity;

import back_end.springboot.common.PostTagsType;
import back_end.springboot.entity.Primary.PostTagsId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
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
    @Enumerated(EnumType.STRING)
    private PostTagsType type;

    public PostTagsEntity(Integer postId, String tagName, PostTagsType type) {
        this.id = new PostTagsId(postId, tagName);
        this.type = type;
    }
}
