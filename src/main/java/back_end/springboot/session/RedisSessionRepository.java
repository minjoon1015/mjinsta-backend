package back_end.springboot.session;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisSessionRepository {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String KEY_PREFIX = "stomp:session:";

    public void save(SimpSessionInfo sessionInfo) throws JsonProcessingException {
        String key = KEY_PREFIX + sessionInfo.getUsername() + ":" + sessionInfo.getSessionId();
        String json = objectMapper.writeValueAsString(sessionInfo);

        redisTemplate.opsForValue().set(key, json, Duration.ofMinutes(30));
        redisTemplate.opsForSet().add(KEY_PREFIX + sessionInfo.getUsername(), key);
    }

    public void delete(String sessionId, String username) {
        String key = KEY_PREFIX + username + ":" + sessionId;
        redisTemplate.delete(key);
        redisTemplate.opsForSet().remove(KEY_PREFIX + username, key);
    }

    public Set<SimpSessionInfo> findAllByUsername(String username) {
        Set<String> sessionKeys = redisTemplate.opsForSet().members(KEY_PREFIX + username);
        if (sessionKeys == null || sessionKeys.isEmpty()) {
            return Collections.emptySet();
        }

        List<String> sessionsJson = redisTemplate.opsForValue().multiGet(sessionKeys);
        return sessionsJson.stream().filter(Objects::nonNull).map(json->{
            try {
                return objectMapper.readValue(json, SimpSessionInfo.class);
            } catch (JsonProcessingException e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toSet());
    }
}
