package back_end.springboot.dto.object.post;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostImageTagsDto {
    private String url;
    private List<PostTagsDto> tags;
}
