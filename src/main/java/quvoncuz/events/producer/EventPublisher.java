package quvoncuz.events.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;
import quvoncuz.config.RabbitMQConfig;
import quvoncuz.events.NotificationEvent;
import quvoncuz.events.StatisticsEvent;

@Service
@RequiredArgsConstructor
public class EventPublisher {

    private final AmqpTemplate amqpTemplate;

    public void publishNotification(String routingKey, NotificationEvent event) {
        amqpTemplate.convertAndSend(
                RabbitMQConfig.APP_EXCHANGE,
                routingKey,
                event
        );
    }
    public void publishStatistics(String routingKey, StatisticsEvent event) {
        amqpTemplate.convertAndSend(
                RabbitMQConfig.APP_EXCHANGE,
                routingKey,
                event
        );
    }
}
