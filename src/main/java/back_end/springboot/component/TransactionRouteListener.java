package back_end.springboot.component;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import back_end.springboot.dto.object.event.NotificationEvent;
import back_end.springboot.dto.object.event.TopicEvent;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TransactionRouteListener {
    private final SimpMessagingTemplate simpMessagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationEvent(NotificationEvent event) {
        simpMessagingTemplate.convertAndSendToUser(event.getUserId(), event.getDestination(), event.getPayload());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTopicEvent(TopicEvent event) {
        simpMessagingTemplate.convertAndSend(event.getDestination(), event.getPayload());
    }
}
