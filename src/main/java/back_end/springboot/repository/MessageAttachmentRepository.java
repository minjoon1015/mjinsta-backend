package back_end.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import back_end.springboot.entity.MessageAttachmentEntity;

@Repository
public interface MessageAttachmentRepository extends JpaRepository<MessageAttachmentEntity, Integer> {
    
}
