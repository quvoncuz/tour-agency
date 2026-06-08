package quvoncuz.events.helper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import quvoncuz.enums.EventType;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsEventHelper {

    private String binding;

    private Long superId; // agencyId <- tourId,   tourId <- bookingId

    private Long entityId;

    private EventType eventType;

    private LocalDateTime dateTime;
}
