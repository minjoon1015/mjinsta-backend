package back_end.springboot.dto.response.user;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import back_end.springboot.common.ResponseCode;
import back_end.springboot.dto.object.user.SimpleUserDto;
import back_end.springboot.dto.response.ResponseDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetRecommendListResponseDto extends ResponseDto {
    List<SimpleUserDto> list = null;

    public GetRecommendListResponseDto(ResponseCode code, List<SimpleUserDto> list) {
        super(code);
        this.list = list;
    }

    public static ResponseEntity<GetRecommendListResponseDto> success(List<SimpleUserDto> list) {
        GetRecommendListResponseDto responseDto = new GetRecommendListResponseDto(ResponseCode.SC, list);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
