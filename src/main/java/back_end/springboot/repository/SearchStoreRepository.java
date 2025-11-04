package back_end.springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import back_end.springboot.entity.SearchStoreEntity;
import back_end.springboot.entity.Primary.SearchStoreId;

@Repository
public interface SearchStoreRepository extends JpaRepository<SearchStoreEntity, SearchStoreId> {
    
}
