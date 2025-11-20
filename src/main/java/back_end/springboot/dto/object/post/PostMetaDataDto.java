package back_end.springboot.dto.object.post;

import java.util.LinkedHashMap;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostMetaDataDto {
    private String comment;
    private String location;
    private LinkedHashMap<String, List<PostTagsDto>> tags;
}
