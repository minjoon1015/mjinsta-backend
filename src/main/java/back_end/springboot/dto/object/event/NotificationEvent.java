package back_end.springboot.dto.object.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class NotificationEvent {
    private final String userId;
    private final String destination;
    private final Object payload;
}
