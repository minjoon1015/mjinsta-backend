package back_end.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import back_end.springboot.entity.PostTagsEntity;
import back_end.springboot.entity.Primary.PostTagsId;

@Repository
public interface PostTagsRepository extends JpaRepository<PostTagsEntity, PostTagsId> {
    
}
