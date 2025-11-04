package back_end.springboot.entity;

import java.time.LocalDateTime;

import back_end.springboot.entity.Primary.SearchStoreId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "search_store")
@Getter
@Setter
public class SearchStoreEntity {
    @EmbeddedId
    private SearchStoreId searchStoreId;
    private LocalDateTime searchedAt;

    public SearchStoreEntity(String userId, String searchId, LocalDateTime searchedAt) {
        this.searchStoreId = new SearchStoreId(userId, searchId);
        this.searchedAt = searchedAt;
    }
}
