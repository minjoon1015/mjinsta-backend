package back_end.springboot.dto.response.post;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import back_end.springboot.dto.object.post.PostCommentDto;
import back_end.springboot.dto.response.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentTopListResponseDto extends ResponseDto {
    private List<PostCommentDto> list;

    public CommentTopListResponseDto(ResponseCode code, List<PostCommentDto> list) {
        super(code);
        this.list = list;
    }
    
    public static ResponseEntity<CommentTopListResponseDto> success(List<PostCommentDto> list) {
        return ResponseEntity.status(HttpStatus.OK).body(new CommentTopListResponseDto(ResponseCode.SC, list));
    }
}
