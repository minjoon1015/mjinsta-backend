package back_end.springboot.dto.request.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OauthSignUpRequestDto {
    private String socialId;
    private String id;
    private String sex;
    private String address;
    private String address_detail;
}
