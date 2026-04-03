package quvoncuz.service;

import quvoncuz.dto.agency.*;

import java.util.List;

public interface AgencyService {
    AgencyDTO applyForAgency(CreateAgencyRequestDTO dto, Long userId);

    Boolean approveAgency(AgencyApproveRequestDTO dto, Long userId);

    List<AgencyShortInfo> getPendingAgencies(Long userId);

    List<AgencyFullInfo> getAllAgencies();

    AgencyFullInfo update(Long agencyId, UpdateAgencyRequestDTO dto, Long userId);

    Boolean deleteById(Long agencyId, Long userId);

    AgencyDTO findById(Long agencyId);
}
