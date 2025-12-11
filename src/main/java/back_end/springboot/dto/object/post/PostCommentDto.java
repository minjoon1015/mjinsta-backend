package back_end.springboot.dto.object.post;

import java.time.LocalDateTime;

import back_end.springboot.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostCommentDto {
    private Integer id;
    private Integer postId;
    private String userId;
    private String name;
    private String profileImage;    
    private String content;
    private LocalDateTime createAt;

    public PostCommentDto(Integer id, Integer postId, String content, LocalDateTime createAt, UserEntity user) {
        this.id = id;
        this.postId = postId;
        this.content = content;
        this.createAt = createAt;
        this.userId = user.getId();
        this.name = user.getName();
        this.profileImage = user.getProfileImage();        
    }
}
