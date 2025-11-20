package back_end.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import back_end.springboot.entity.PostFavoriteEntity;
import back_end.springboot.entity.Primary.PostFavoriteId;

@Repository
public interface PostFavoriteRepository extends JpaRepository<PostFavoriteEntity, PostFavoriteId> {
    
}
