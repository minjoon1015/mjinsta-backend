package back_end.springboot.dto.object.post;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;

import back_end.springboot.dto.object.user.SimpleUserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    // 팔로우 기능은 게시글에서 지워주는게 나을 듯
    private Integer postId;
    private SimpleUserDto user;
    private String comment;
    private String location;
    private Integer favoriteCount;
    private Integer commentCount;
    private LocalDateTime createdAt;
    private List<PostImageTagsDto> imageTags;
    private Boolean isLiked;
}
