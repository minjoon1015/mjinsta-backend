package back_end.springboot.service.implement;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import back_end.springboot.service.AuthCodeService;

@Service
public class AuthCodeServiceImplement implements AuthCodeService {
    private RedisTemplate<String, Object> redisTemplate;

    private static final long EXPIRED_TIME = 3;

    public AuthCodeServiceImplement(@Qualifier("redisTemplate") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void saveAuthCode(String email, String authCode) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set(email, authCode, Duration.ofMinutes(EXPIRED_TIME));
    }

    @Override
    public boolean verifyAuthCode(String email, String inputCode) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        Object storedCode = ops.get(email);
        if (storedCode instanceof String && storedCode.equals(inputCode)) {
            deleteAuthCode(email);
            return true;
        }
        return false;
    }

    @Override
    public void deleteAuthCode(String email) {
        redisTemplate.delete(email);
    }
    
}
