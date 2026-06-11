package quvoncuz.feign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import quvoncuz.enums.EventType;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponse {
    private long count;
    private EventType eventType;
}
