package back_end.springboot.dto.object.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostPrevDto {
    private Integer postId;
    private Integer commentCount;
    private Integer favoriteCount;
    private String profileImage;
}
