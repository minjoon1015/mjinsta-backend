package back_end.springboot.session;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SimpSessionInfo implements Serializable {
    private String sessionId;
    private String username;
    private long connectAt;

    @Builder
    public SimpSessionInfo(String sessionId, String username) {
        this.sessionId = sessionId;
        this.username = username;
        this.connectAt = System.currentTimeMillis();
    }
}
