package back_end.springboot.dto.response.post;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import back_end.springboot.dto.object.post.PostPrevDto;
import back_end.springboot.dto.response.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostGetMeResponseDto extends ResponseDto {
    private List<PostPrevDto> list = null;

    public PostGetMeResponseDto(ResponseCode code, List<PostPrevDto> list) {
        super(code);
        this.list = list;
    }

    public static ResponseEntity<PostGetMeResponseDto> success(List<PostPrevDto> list) {
        return ResponseEntity.status(HttpStatus.OK).body(new PostGetMeResponseDto(ResponseCode.SC, list));
    }
    
}
