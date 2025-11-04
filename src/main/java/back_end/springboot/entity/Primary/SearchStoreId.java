package back_end.springboot.entity.Primary;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class SearchStoreId implements Serializable {
    private String userId;
    private String SearchId;

    public SearchStoreId(String userId, String searchId) {
        this.userId = userId;
        this.SearchId = searchId;
    }
}
