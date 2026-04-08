package quvoncuz.dto.payment;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class TransactionRequestDTO {
    @Length(min = 16, max = 16, message = "Card number must contains 16 numbers")
    private String cardNumber;
    @Positive(message = "Balance must be positive")
    private Long balance;
}
