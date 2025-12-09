package back_end.springboot.entity.Primary;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInterestId implements Serializable {
    private String userId;
    private String interestKeyword;
    private String type;
}
