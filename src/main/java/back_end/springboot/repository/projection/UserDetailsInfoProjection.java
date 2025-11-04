package back_end.springboot.repository.projection;

public interface UserDetailsInfoProjection {
    String getId();
    String getName();
    String getProfileImage();
    String getComment();
    Integer getFollowCount();
    Integer getFollowerCount();
    Integer getPostCount();
    Integer getIsFollowed();
}
