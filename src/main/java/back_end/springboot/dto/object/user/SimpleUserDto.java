package back_end.springboot.dto.object.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleUserDto {
    private String id;
    private String name;
    private String profileImage;
    private boolean isFollowing;
}
