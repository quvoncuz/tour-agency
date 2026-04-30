package quvoncuz.dto.payment;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDTO {
    @Positive(message = "Tour is invalid")
    private long tourId;

    @Positive(message = "Booking is invalid")
    private long bookingId;

    private String returnUrl;
}
