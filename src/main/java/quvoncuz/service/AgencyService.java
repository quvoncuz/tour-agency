package quvoncuz.service;

import quvoncuz.dto.agency.*;
import quvoncuz.entities.AgencyEntity;

public interface AgencyService {
    public AgencyDTO applyForAgency(CreateAgencyRequestDTO dto, Long userId);

    public Boolean approveAgency(AgencyApproveRequestDTO dto, Long userId);

    public AgencyFullInfo update(UpdateAgencyRequestDTO dto, Long userId);

    public Boolean deleteById(Long agencyId, Long userId);

    public AgencyDTO findById(Long agencyId);
}
