package quvoncuz.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import quvoncuz.events.NotificationEvent;
import quvoncuz.events.StatisticsEvent;

@FeignClient(name = "asistent-service", url = "http://localhost:8082", contextId = "eventPublisherClient")
public interface EventPublisherClient {

    @PostMapping("/api/v1/events/notification")
    ResponseEntity<Void> publishNotificationEvent(@RequestBody NotificationEvent request);

    @PostMapping("/api/v1/events/statistics")
    ResponseEntity<Void> publishStatisticsEvent(@RequestBody StatisticsEvent request);

}
