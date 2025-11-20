package back_end.springboot.dto.response.post;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import back_end.springboot.dto.object.post.PostCommentDto;
import back_end.springboot.dto.response.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCommentResponseDto extends ResponseDto {
    private PostCommentDto comment;

    public PostCommentResponseDto(ResponseCode code, PostCommentDto comment) {
        super(code);
        this.comment = comment;
    }
    
    public static ResponseEntity<PostCommentResponseDto> success(PostCommentDto comment) {
        return ResponseEntity.status(HttpStatus.OK).body(new PostCommentResponseDto(ResponseCode.SC, comment));
    }
}
