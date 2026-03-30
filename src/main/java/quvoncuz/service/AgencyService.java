package quvoncuz.service;

import quvoncuz.dto.agency.AgencyDTO;
import quvoncuz.dto.agency.AgencyFullInfo;
import quvoncuz.dto.agency.CreateAgencyRequestDTO;
import quvoncuz.dto.agency.UpdateAgencyRequestDTO;
import quvoncuz.entities.AgencyEntity;

public interface AgencyService {
    public AgencyDTO applyForAgency(CreateAgencyRequestDTO dto);
    public boolean approveAgency(Long agencyId, Boolean approve);
    public AgencyFullInfo update(UpdateAgencyRequestDTO dto);
    public Boolean deleteById(Long agencyId);
    public AgencyDTO findById(Long agencyId);
}
