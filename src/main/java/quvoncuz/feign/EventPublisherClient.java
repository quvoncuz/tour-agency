package quvoncuz.feign;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import quvoncuz.events.NotificationEvent;
import quvoncuz.events.StatisticsEvent;

@FeignClient(name = "asistent-service", contextId = "eventPublisherClient")
public interface EventPublisherClient {

    @PostMapping("/api/v1/events/notification")
    ResponseEntity<Void> publishNotificationEvent(@RequestBody NotificationEvent request);

    @PostMapping("/api/v1/events/statistics")
    ResponseEntity<Void> publishStatisticsEvent(@RequestBody StatisticsEvent request);

}
