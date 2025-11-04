package back_end.springboot.dto.request.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequestDto {
    String id;
    boolean DuplicateCheckId;
    String password;
    String name;
    String sex;
    String email;
    String email_code;
    String address;
    String addressDetail;
}
