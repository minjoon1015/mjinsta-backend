package back_end.springboot.entity;

import back_end.springboot.entity.Primary.UserInterestId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_interest")
@Getter
@Setter
@NoArgsConstructor
public class UserInterestEntity {
    @EmbeddedId
    private UserInterestId id;
    private Integer score;

    public UserInterestEntity(String userId, String interestKeyword, String type, Integer score) {
        this.id = new UserInterestId(userId, interestKeyword, type);
        this.score = score;
    }
}
