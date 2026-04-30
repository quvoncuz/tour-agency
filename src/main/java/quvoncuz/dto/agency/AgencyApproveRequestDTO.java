package quvoncuz.dto.agency;

import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AgencyApproveRequestDTO {
    @Positive(message = "Id must be positive")
    private long agencyId;
    private Boolean approve;
}
