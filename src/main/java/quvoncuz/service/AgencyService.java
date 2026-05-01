package quvoncuz.service;

import org.springframework.data.domain.Page;
import quvoncuz.dto.agency.*;
import quvoncuz.entities.AgencyEntity;

import java.util.Optional;

public interface AgencyService {
    AgencyDTO applyForAgency(CreateAgencyRequestDTO dto);

    Boolean approveAgency(AgencyApproveRequestDTO dto);

    Page<AgencyShortInfo> getPendingAgencies(int page, int size);

    Page<AgencyFullInfo> getAllAgencies(int page, int size);

    AgencyFullInfo update(Long agencyId, UpdateAgencyRequestDTO dto);

    Boolean deleteById(Long agencyId);

    AgencyDTO findByAgencyId(Long agencyId);

    Optional<AgencyEntity> findByOwnerId(Long ownerId);
}
