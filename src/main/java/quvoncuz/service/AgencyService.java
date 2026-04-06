package quvoncuz.service;

import org.springframework.data.domain.Page;
import quvoncuz.dto.agency.*;

public interface AgencyService {
    AgencyDTO applyForAgency(CreateAgencyRequestDTO dto, Long userId);

    Boolean approveAgency(AgencyApproveRequestDTO dto, Long userId);

    Page<AgencyShortInfo> getPendingAgencies(Long userId, int page, int size);

    Page<AgencyFullInfo> getAllAgencies(int page, int size);

    AgencyFullInfo update(Long agencyId, UpdateAgencyRequestDTO dto, Long userId);

    Boolean deleteById(Long agencyId, Long userId);

    AgencyDTO findByAgencyId(Long agencyId);
}
