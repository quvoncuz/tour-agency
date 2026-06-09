package quvoncuz.events.helper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import quvoncuz.enums.EventType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEventHelper {

    private String binding;

    private Long entityId;

    private EventType eventType;

    private String subjectName;

    private List<String> mails;

    private LocalDateTime dateTime;
}
