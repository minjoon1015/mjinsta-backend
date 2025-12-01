package back_end.springboot.session;

import java.security.Principal;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SessionEventListener {
    private final RedisSessionRepository repository;

    @EventListener
    public void handleConnectEvent(SessionConnectEvent event) throws JsonProcessingException {
        Principal principal = event.getUser();
        if (principal == null) return;

        MessageHeaders headers = event.getMessage().getHeaders();
        String sessionId = SimpMessageHeaderAccessor.getSessionId(headers);

        SimpSessionInfo sessionInfo = new SimpSessionInfo(sessionId, principal.getName());
        repository.save(sessionInfo);
        System.out.println(principal.getName() + "세션 저장 완료");
    }

    @EventListener
    public void handleDisconnectEvent(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        Principal principal = event.getUser();
        
        if (principal != null && sessionId != null) {
            repository.delete(sessionId, principal.getName());
        }
    }

}
