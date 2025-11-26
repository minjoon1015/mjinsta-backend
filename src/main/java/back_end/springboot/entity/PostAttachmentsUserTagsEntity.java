package back_end.springboot.entity;

import java.time.LocalDateTime;
 
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String userId;
    private Double xCoordinate;
    private Double yCoordinate;
    private LocalDateTime createAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_attachment_id", referencedColumnName = "id")
    private PostAttachmentsEntity postAttachments;

    public PostAttachmentsUserTagsEntity(PostAttachmentsEntity postAttachments, String userId, Double xCoordinate, Double yCoordinate, LocalDateTime createAt) {
        this.postAttachments = postAttachments;
        this.userId = userId;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.createAt = createAt;
    }
}
