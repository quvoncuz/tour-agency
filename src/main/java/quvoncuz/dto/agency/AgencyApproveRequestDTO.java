package quvoncuz.dto.agency;

import lombok.Data;

@Data
public class AgencyApproveRequestDTO {
    private Long agencyId;
    private Boolean approve;
}
