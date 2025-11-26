package back_end.springboot.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.BatchSize;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "post")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String userId;
    private String comment;
    private String location;
    private Integer favoriteCount;
    private Integer commentCount;
    private LocalDateTime createAt;
    private String profileImage;

    public void increaseLike() {
        this.favoriteCount++;
    }

    public void decreaseLike() {
        this.favoriteCount--;
    }

    public void increaseComment() {
        this.commentCount++;
    }

    public void decreaseComment() {
        this.commentCount--;
    }

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 100)
    private List<PostAttachmentsEntity> attachments = new ArrayList<>();

    public PostEntity(String userId, String comment, String location, Integer favoriteCount, Integer commentCount, LocalDateTime createAt, String profileImage) {
        this.userId = userId;
        this.comment = comment;
        this.location = location;
        this.favoriteCount = favoriteCount;
        this.commentCount = commentCount;
        this.createAt = createAt;
        this.profileImage = profileImage;
    }
}
