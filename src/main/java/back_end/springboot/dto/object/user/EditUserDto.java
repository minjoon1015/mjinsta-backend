package back_end.springboot.dto.object.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditUserDto {
    private String name;
    private String sex;
    private String comment;
    private String address;
    private String addressDetail;
}
