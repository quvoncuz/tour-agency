package quvoncuz.dto.payment;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDTO {
    @NotNull(message = "Tour is mandatory")
    @Positive(message = "Tour is invalid")
    private Long tourId;

    @NotNull(message = "Booking is mandatory")
    @Positive(message = "Booking is invalid")
    private Long bookingId;

    private String returnUrl;
}
