package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import quvoncuz.dto.agency.*;
import quvoncuz.entities.AgencyEntity;
import quvoncuz.enums.AgencyStatus;
import quvoncuz.exceptions.NotFoundException;
import quvoncuz.mapper.AgencyMapper;
import quvoncuz.repository.AgencyRepository;
import quvoncuz.service.AgencyService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgencyServiceImpl implements AgencyService {

    private final AgencyRepository agencyRepository;

    @Override
    public AgencyDTO applyForAgency(CreateAgencyRequestDTO dto) {
        AgencyEntity agency = new AgencyEntity();
        agency.setName(dto.getName());
        agency.setPhone(dto.getPhone());
        agency.setEmail(dto.getEmail());
        agency.setDescription(dto.getDescription());
        agency.setCity(dto.getCity());
        agency.setAddress(dto.getAddress());
        agency.setStatus(AgencyStatus.PENDING);
        agencyRepository.createOrUpdate(List.of(agency), true);
        return AgencyMapper.toDTO(agency);
    }

    @Override
    public boolean approveAgency(Long agencyId, Boolean approve) {
        List<AgencyEntity> allAgencies = agencyRepository.getAllAgencies();
        AgencyEntity agency = allAgencies
                .stream()
                .filter(a -> a.getId().equals(agencyId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Agency not found"));

        if (approve) {
            agency.setStatus(AgencyStatus.ACCEPTED);
            agency.setApproved(true);
        } else {
            agency.setStatus(AgencyStatus.REJECTED);
        }
        agencyRepository.createOrUpdate(allAgencies, false);
        return true;
    }

    public List<AgencyShortInfo> getPendingAgencies() {
        return agencyRepository.getAllAgencies()
                .stream()
                .filter(agency -> agency.getStatus() == AgencyStatus.PENDING)
                .map(AgencyMapper::toShortInfo)
                .toList();
    }

    public List<AgencyFullInfo> getAllAgencies() {
        return agencyRepository.getAllAgencies()
                .stream()
                .map(AgencyMapper::toFullInfo)
                .toList();
    }

    @Override
    public AgencyFullInfo update(UpdateAgencyRequestDTO dto) {
        List<AgencyEntity> allAgencies = agencyRepository.getAllAgencies();
        AgencyEntity agency = allAgencies
                .stream()
                .filter(a -> a.getId().equals(dto.getId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Agency not found"));

        agency.setName(dto.getName());
        agency.setPhone(dto.getPhone());
        agency.setEmail(dto.getEmail());
        agency.setDescription(dto.getDescription());
        agency.setCity(dto.getCity());
        agency.setAddress(dto.getAddress());
        agencyRepository.createOrUpdate(allAgencies, false);
        return AgencyMapper.toFullInfo(agency);
    }

    @Override
    public Boolean deleteById(Long agencyId) {
        return null;
    }

    @Override
    public AgencyDTO findById(Long agencyId) {
        return agencyRepository.getAllAgencies()
                .stream()
                .filter(a -> a.getId().equals(agencyId))
                .map(AgencyMapper::toDTO)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Agency not found"));
    }
}
