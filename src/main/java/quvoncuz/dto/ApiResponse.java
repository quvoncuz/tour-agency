package quvoncuz.dto;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;

    private String message;

    private LocalDateTime timestamp = LocalDateTime.now();

    private T data;

    public static <T> ApiResponse<T> success(
            T data
    ) {

        return ApiResponse.<T>builder()
                .success(true)
                .message("success")
                .timestamp(LocalDateTime.now())
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(
            String message
    ) {

        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
