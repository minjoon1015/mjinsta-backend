package back_end.springboot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "follows")
@NoArgsConstructor
@Getter
@Setter
public class FollowsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String followerId;
    String followingId;

    public FollowsEntity(String followerId, String followingId) {
        this.followerId = followerId;
        this.followingId = followingId;
    }
}
