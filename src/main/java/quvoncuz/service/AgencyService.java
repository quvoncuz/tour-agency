package quvoncuz.service;

import quvoncuz.dto.agency.*;

import java.util.List;

public interface AgencyService {
    AgencyDTO applyForAgency(CreateAgencyRequestDTO dto, Long userId);

    Boolean approveAgency(AgencyApproveRequestDTO dto, Long userId);

    List<AgencyShortInfo> getPendingAgencies(Long userId, int page, int size);

    List<AgencyFullInfo> getAllAgencies(int page, int size);

    AgencyFullInfo update(Long agencyId, UpdateAgencyRequestDTO dto, Long userId);

    Boolean deleteById(Long agencyId, Long userId);

    AgencyDTO findByAgencyId(Long agencyId);
}
