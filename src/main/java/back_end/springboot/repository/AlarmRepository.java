package back_end.springboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import back_end.springboot.entity.AlarmEntity;

@Repository
public interface AlarmRepository extends JpaRepository<AlarmEntity, Integer> {

    @Query(value = "select * from alarm where user_id = :id order by create_at desc limit 20", nativeQuery = true)
    List<AlarmEntity> findAllByUserId(@Param("id") String id);
    
    @Query(value = "SELECT * FROM alarm where user_id = :id and reference_id = :referenceId", nativeQuery = true)
    AlarmEntity findByUserIdAndReferenceId(@Param("id") String id, @Param("referenceId") String referenceId);
}
