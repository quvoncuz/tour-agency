package quvoncuz.service;

import quvoncuz.dto.agency.*;
import quvoncuz.entities.AgencyEntity;

import java.util.List;
import java.util.Optional;

public interface AgencyService {
    AgencyDTO applyForAgency(CreateAgencyRequestDTO dto, Long userId);

    Boolean approveAgency(AgencyApproveRequestDTO dto, Long userId);

    List<AgencyShortInfo> getPendingAgencies(Long userId, int page, int size);

    List<AgencyFullInfo> getAllAgencies(int page, int size);

    AgencyFullInfo update(Long agencyId, UpdateAgencyRequestDTO dto, Long userId);

    Boolean deleteById(Long agencyId, Long userId);

    AgencyDTO findByAgencyId(Long agencyId);

    Optional<AgencyEntity> findByOwnerId(Long ownerId);
}
