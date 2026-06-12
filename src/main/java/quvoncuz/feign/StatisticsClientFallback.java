package quvoncuz.feign;

import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class StatisticsClientFallback implements StatisticsClient {

    @Override
    public StatisticsResponse getAgencyStats(LocalDate from, LocalDate to) {
        return StatisticsResponse.builder()
                .count(0L)
                .message("Statistika servisi hozir mavjud emas")
                .build();
    }

    @Override
    public StatisticsResponse getBookingStats(LocalDate from, LocalDate to) {
        return StatisticsResponse.builder()
                .count(0L)
                .message("Statistika servisi hozir mavjud emas")
                .build();
    }

    @Override
    public StatisticsResponse getTourStats(LocalDate from, LocalDate to) {
        return StatisticsResponse.builder()
                .count(0L)
                .message("Statistika servisi hozir mavjud emas")
                .build();
    }

    @Override
    public StatisticsResponse getUserStats(LocalDate from, LocalDate to) {
        return StatisticsResponse.builder()
                .count(0L)
                .message("Statistika servisi hozir mavjud emas")
                .build();
    }
}