package quvoncuz.events.listener;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import quvoncuz.events.NotificationEvent;
import quvoncuz.events.StatisticsEvent;
import quvoncuz.events.producer.EventPublisher;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventListener {

    private final EventPublisher eventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleStatisticsEvent(StatisticsEvent event) {
        try {
            eventPublisher.publishStatistics(
                    event.getBinding(),
                    StatisticsEvent.builder()
                            .entityId(event.getEntityId())
                            .eventType(event.getEventType())
                            .dateTime(event.getDateTime())
                            .build()
            );
            log.info("Statistics event published for type: {}", event.getEventType());

        } catch (Exception e) {
            log.error("Failed to publish statistics for type: {}", event.getEventType(), e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleNotificationEvent(NotificationEvent event) {
        try {
            eventPublisher.publishNotification(
                    event.getBinding(),
                    NotificationEvent.builder()
                            .entityId(event.getEntityId())
                            .eventType(event.getEventType())
                            .subjectName(event.getSubjectName())
                            .mails(event.getMails())
                            .dateTime(event.getDateTime())
                            .build()
            );
            log.info("Notification event published for type: {}", event.getEventType());

        } catch (Exception e) {
            log.error("Failed to publish notification for type: {}", event.getEventType(), e);
        }
    }
}