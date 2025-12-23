package back_end.springboot.dto.request.post;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostAddViewHistoryRequestDto {
    private Integer postId;
    private LocalDateTime viewedAt;
    private Integer timeSpentSeconds;
}
