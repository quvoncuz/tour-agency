package quvoncuz.feign;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import quvoncuz.events.NotificationEvent;
import quvoncuz.events.StatisticsEvent;

@Component
public class EventPublisherClientFallback implements EventPublisherClient {

    @Override
    public ResponseEntity<Void> publishNotificationEvent(NotificationEvent request) {
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> publishStatisticsEvent(StatisticsEvent request) {
        return ResponseEntity.noContent().build();
    }
}