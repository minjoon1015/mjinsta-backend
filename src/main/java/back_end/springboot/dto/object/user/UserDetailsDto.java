package back_end.springboot.dto.object.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDetailsDto {
    private String id;
    private String name;
    private String profileImage;
    private String comment;
    private Integer followCount;
    private Integer followerCount;
    private Integer postCount;
    private Boolean isFollowed;
}
