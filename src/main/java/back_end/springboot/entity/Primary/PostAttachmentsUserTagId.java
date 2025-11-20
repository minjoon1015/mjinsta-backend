package back_end.springboot.entity.Primary;

import java.io.Serializable;

import back_end.springboot.entity.PostAttachmentsEntity;
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
public class PostAttachmentsUserTagId implements Serializable {
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_attachment_id", referencedColumnName = "id")
    private PostAttachmentsEntity postAttachments;

    public PostAttachmentsUserTagId(PostAttachmentsEntity postAttachments, String userId) {
        this.userId = userId;
        this.postAttachments = postAttachments;
    }
}
