package quvoncuz.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String APP_EXCHANGE = "app.exchange";
    public static final String DLQ_EXCHANGE = "dlq.exchange";

    public static final String NOTIFICATION_QUEUE = "notification.queue";
    public static final String STATISTICS_QUEUE = "statistics.queue";
    public static final String DLQ_QUEUE = "dlq.queue";

    public static final String USER_REGISTERED = "user.registered";
    public static final String AGENCY_CREATED = "agency.created";
    public static final String AGENCY_APPROVED = "agency.approved";
    public static final String NOTIFICATION_TOUR_CREATED = "notification.tour.created";
    public static final String STATISTICS_TOUR_CREATED = "statistics.tour.created";
    public static final String TOUR_UPDATED = "tour.updated";
    public static final String TOUR_CANCELED = "tour.canceled";
    public static final String NOTIFICATION_BOOKING_COMPLETED = "notification.booking.completed";
    public static final String STATISTICS_BOOKING_COMPLETED = "statistics.booking.completed";
    public static final String DLQ_ROUTING_KEY = "dlq.routing.key";

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    // exchange
    @Bean
    public TopicExchange appExchange() {
        return new TopicExchange(APP_EXCHANGE);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLQ_EXCHANGE);
    }
    //

    // queue
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE)
                .withArgument("x-dead-letter-exchange", DLQ_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue statisticsQueue() {
        return QueueBuilder.durable(STATISTICS_QUEUE)
                .withArgument("x-dead-letter-exchange", DLQ_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue dlqQueue() {
        return QueueBuilder.durable(DLQ_QUEUE)
                .build();
    }

    // binding
    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(dlqQueue())
                .to(deadLetterExchange()).with(DLQ_ROUTING_KEY);
    }

    // notification
    @Bean
    public Binding notificationAgencyApprovedBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(appExchange()).with(AGENCY_APPROVED);
    }

    @Bean
    public Binding notificationTourCreatedBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(appExchange()).with(NOTIFICATION_TOUR_CREATED);
    }

    @Bean
    public Binding notificationTourUpdatedBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(appExchange()).with(TOUR_UPDATED);
    }

    @Bean
    public Binding notificationTourCanceledBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(appExchange()).with(TOUR_CANCELED);
    }

    @Bean
    public Binding notificationBookingCompleted() {
        return BindingBuilder.bind(notificationQueue())
                .to(appExchange()).with(NOTIFICATION_BOOKING_COMPLETED);
    }

    //statistics
    @Bean
    public Binding statisticsUserRegisteredBinding() {
        return BindingBuilder.bind(statisticsQueue())
                .to(appExchange()).with(USER_REGISTERED);
    }

    @Bean
    public Binding statisticsAgencyCreatedBinding() {
        return BindingBuilder.bind(statisticsQueue())
                .to(appExchange()).with(AGENCY_CREATED);
    }

    @Bean
    public Binding statisticsTourCreatedBinding() {
        return BindingBuilder.bind(statisticsQueue())
                .to(appExchange()).with(STATISTICS_TOUR_CREATED);
    }

    @Bean
    public Binding statisticsBookingCompletedBinding() {
        return BindingBuilder.bind(statisticsQueue())
                .to(appExchange()).with(NOTIFICATION_BOOKING_COMPLETED);
    }
}