package quvoncuz.dto.tour;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelTourDTO {

    @NotBlank
    @Size(max = 255, min = 10, message = "Length restricted")
    private String reason;
}
