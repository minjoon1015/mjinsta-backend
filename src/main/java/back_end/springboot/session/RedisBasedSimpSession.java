package back_end.springboot.session;

import java.util.Collections;
import java.util.Set;

import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpUser;

public class RedisBasedSimpSession implements SimpSession {
    private final String sessionId;
    private final SimpUser user; 
    public RedisBasedSimpSession(String sessionId, SimpUser user) {
        this.sessionId = sessionId;
        this.user = user;
    }

    @Override
    public String getId() {
        return this.sessionId;
    }

    @Override
    public SimpUser getUser() {
        return this.user;
    }

    @Override
    public Set<SimpSubscription> getSubscriptions() {
        return Collections.emptySet(); 
    }
}