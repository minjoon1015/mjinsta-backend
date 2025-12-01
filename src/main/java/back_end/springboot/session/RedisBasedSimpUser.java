package back_end.springboot.session;

import java.security.Principal;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.user.SimpSession;
import org.springframework.messaging.simp.user.SimpUser;

public class RedisBasedSimpUser implements SimpUser {
    private final String name;
    private final Set<SimpSession> sessions;

    public RedisBasedSimpUser(String name, Set<SimpSessionInfo> sessionsInfo) {
        this.name = name;
        this.sessions = sessionsInfo.stream()
                .map(info -> new RedisBasedSimpSession(info.getSessionId(), this)) 
                .collect(Collectors.toSet());
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean hasSessions() {
        return !this.sessions.isEmpty();
    }
    
    @Override
    public Set<SimpSession> getSessions() {
        return this.sessions;
    }

    @Override
    @Nullable
    public Principal getPrincipal() {
        return new Principal() {
            @Override
            public String getName() {
                return RedisBasedSimpUser.this.name;
            }
        };
    }
    
    @Override
    @Nullable
    public SimpSession getSession(String sessionId) {
        return this.sessions.stream()
                .filter(s -> s.getId().equals(sessionId))
                .findFirst()
                .orElse(null);
    }
    @Override
    public boolean equals(@Nullable Object other) {
        if (other == this) {
            return true;
        }
        if (other == null || other.getClass() != getClass()) {
            return false;
        }
        return this.name.equals(((RedisBasedSimpUser) other).name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}