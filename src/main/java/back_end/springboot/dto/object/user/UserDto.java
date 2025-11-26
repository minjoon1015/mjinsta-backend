package back_end.springboot.dto.object.user;

import back_end.springboot.common.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String id;
    private String name;
    private String comment;
    private Integer followCount;
    private Integer followerCount;
    private Integer postCount;
    private String profileImage;
    private UserType type;
}
