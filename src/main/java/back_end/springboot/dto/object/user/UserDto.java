package back_end.springboot.dto.object.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    String id;
    String name;
    String comment;
    Integer followCount;
    Integer followerCount;
    Integer postCount;
    String profileImage;
}
