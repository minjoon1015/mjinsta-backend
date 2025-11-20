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
public class PostCreateResponseDto extends ResponseDto {
    
    public PostCreateResponseDto(ResponseCode code) {
        super(code);
    }

    public static ResponseEntity<? super PostCreateResponseDto> success() {
        return ResponseEntity.status(HttpStatus.OK).body(new PostCreateResponseDto(ResponseCode.SC));
    }
}
