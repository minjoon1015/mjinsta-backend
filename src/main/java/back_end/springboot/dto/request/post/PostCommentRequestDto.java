package back_end.springboot.dto.request.post;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCommentRequestDto {
    private String userId;
    private Integer postId;
    private String comment;
}
