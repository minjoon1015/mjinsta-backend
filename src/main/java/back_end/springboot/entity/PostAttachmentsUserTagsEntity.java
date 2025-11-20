package back_end.springboot.entity;

import java.time.LocalDateTime;

import back_end.springboot.entity.Primary.PostAttachmentsUserTagId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Table(name = "post_attachments_user_tags")
@Getter
@Setter
public class PostAttachmentsUserTagsEntity {
    @EmbeddedId
    private PostAttachmentsUserTagId id;
    private Double xCoordinate;
    private Double yCoordinate;
    private LocalDateTime createAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_attachment_id", insertable = false, updatable = false)
    private PostAttachmentsEntity postAttachments;

    public PostAttachmentsUserTagsEntity(PostAttachmentsEntity postAttachment, String userId, Double xCoordinate, Double yCoordinate, LocalDateTime createAt) {
        this.id = new PostAttachmentsUserTagId(postAttachment, userId);
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.createAt = createAt;
    }
}
