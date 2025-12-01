package back_end.springboot.session;

import java.util.Set;

import org.springframework.lang.Nullable;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpSubscriptionMatcher;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisSimpUserRegistry implements SimpUserRegistry {
    private final RedisSessionRepository repository;

    @Override
    @Nullable
    public SimpUser getUser(String userName) {
        Set<SimpSessionInfo> sessionInfo = repository.findAllByUsername(userName);

        if (sessionInfo.isEmpty()) {
            return null;
        }
        return new RedisBasedSimpUser(userName, sessionInfo);
    }

    @Override
    public Set<SimpUser> getUsers() {
        return null;
    }

    @Override
    public int getUserCount() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserCount'");
    }

    @Override
    public Set<SimpSubscription> findSubscriptions(SimpSubscriptionMatcher matcher) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findSubscriptions'");
    }

}
