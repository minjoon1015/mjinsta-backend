package back_end.springboot.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.BatchSize;

import back_end.springboot.common.Role;
import back_end.springboot.common.UserType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
public class UserEntity {
    @Id
    private String id;
    private String password;
    private String name;
    private String sex;
    private String comment;
    private String email;
    private String address;
    private String addressDetail;
    private Integer followCount;
    private Integer followerCount;
    private Integer postCount;
    private String profileImage;
    @Enumerated(EnumType.STRING)
    private UserType type;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String socialId;
    private LocalDateTime lastLoginAt;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 100)
    private List<PostCommentEntity> comments;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<PostEntity> posts;
 
    public UserEntity(String id, String password, String name, String sex, String comment, String email, String address, String addressDetail, String profileImage, UserType type, String socialId) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.sex = sex;
        this.comment = comment;
        this.email = email;
        this.address = address;
        this.addressDetail = addressDetail;
        this.profileImage = profileImage;
        this.followCount = 0;
        this.followerCount = 0;
        this.postCount = 0;
        this.role = Role.USER;
        this.type = type;
        this.socialId = socialId;
        this.lastLoginAt = null;
    }
    
    public void plusFollow() {
        this.followCount++;
    }

    public void plusFollower() {
        this.followerCount++; 
    }

    public void minusFollow() {
        this.followCount--;
    }

    public void minusFollower() {
        this.followerCount--;
    }

    public void plusPost() {
        this.postCount++;
    }

    public void updateProfileImage(String url) {
        this.profileImage = url;
    }
}
