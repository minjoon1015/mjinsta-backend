package back_end.springboot.dto.response.post;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import back_end.springboot.dto.object.post.PostDto;
import back_end.springboot.dto.response.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostGetDetailsInfoResponseDto extends ResponseDto {
    private PostDto post = null;

    public PostGetDetailsInfoResponseDto(ResponseCode code, PostDto post) {
        super(code);
        this.post = post;
    }
    
    public static ResponseEntity<PostGetDetailsInfoResponseDto> success(PostDto post) {
        return ResponseEntity.status(HttpStatus.OK).body(new PostGetDetailsInfoResponseDto(ResponseCode.SC, post)); 
    }
}
