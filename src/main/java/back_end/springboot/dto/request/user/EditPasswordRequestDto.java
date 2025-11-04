package back_end.springboot.dto.request.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditPasswordRequestDto {
    private String oldPassword;
    private String newPassword;
}
