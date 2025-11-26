package back_end.springboot.dto.object.post;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostCommentDto {
    private Integer id;
    private Integer postId;
    private String userId;
    private String name;
    private String profileImage;    
    private String content;
    private LocalDateTime createAt;

}
