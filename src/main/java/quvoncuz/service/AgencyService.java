package quvoncuz.service;

import org.springframework.data.domain.Page;
import quvoncuz.dto.agency.*;
import quvoncuz.entities.AgencyEntity;

import java.util.Optional;

public interface AgencyService {
    AgencyDTO applyForAgency(CreateAgencyRequestDTO dto, Long userId);

    void approveAgency(AgencyApproveRequestDTO dto);

    Page<AgencyShortInfo> getAllAgencies(boolean pending, int page, int size);

    AgencyFullInfo update(Long id, Long agencyId, UpdateAgencyRequestDTO dto);

    void deleteById(Long agencyId);

    AgencyDTO findByAgencyId(Long agencyId);

    Optional<AgencyEntity> findByOwnerId(Long ownerId);
}
