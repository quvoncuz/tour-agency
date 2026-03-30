package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import quvoncuz.dto.agency.*;
import quvoncuz.entities.AgencyEntity;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.enums.AgencyStatus;
import quvoncuz.enums.Role;
import quvoncuz.exceptions.NotFoundException;
import quvoncuz.exceptions.PermissionDeniedException;
import quvoncuz.mapper.AgencyMapper;
import quvoncuz.repository.AgencyRepository;
import quvoncuz.repository.ProfileRepository;
import quvoncuz.service.AgencyService;
import quvoncuz.service.ProfileService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgencyServiceImpl implements AgencyService {

    private final ProfileService profileService;
    private final AgencyRepository agencyRepository;
    private final ProfileRepository profileRepository;

    @Override
    public AgencyDTO applyForAgency(CreateAgencyRequestDTO dto, Long userId) {
        ProfileEntity profile = profileRepository.findById(userId);
        if (!profile.getRole().equals(Role.USER)) {
            throw new PermissionDeniedException("You have a agency already");
        }
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
    public Boolean approveAgency(AgencyApproveRequestDTO dto, Long userId) {
        ProfileEntity profile = profileRepository.findById(userId);
        if (!profile.getRole().equals(Role.ADMIN)) {
            throw new PermissionDeniedException("You don't have permission to approve or reject this agency");
        }
        List<AgencyEntity> allAgencies = agencyRepository.getAllAgencies();
        AgencyEntity agency = allAgencies
                .stream()
                .filter(a -> a.getId().equals(dto.getAgencyId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Agency not found"));

        if (dto.getApprove()) {
            profileService.updateRole(Role.AGENCY, profileRepository.findById(agency.getOwnerId()).getId());
            agency.setStatus(AgencyStatus.ACCEPTED);
            agency.setApproved(true);
        } else {
            agency.setStatus(AgencyStatus.REJECTED);
        }
        agencyRepository.createOrUpdate(allAgencies, false);
        return true;
    }

    public List<AgencyShortInfo> getPendingAgencies(Long userId) {
        ProfileEntity profile = profileRepository.findById(userId);
        if (!profile.getRole().equals(Role.ADMIN)) {
            throw new PermissionDeniedException("You don't have permission to view pending agencies");
        }
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
    public AgencyFullInfo update(UpdateAgencyRequestDTO dto, Long userId) {
        List<AgencyEntity> allAgencies = agencyRepository.getAllAgencies();
        ProfileEntity profile = profileRepository.findById(userId);
        AgencyEntity agency = allAgencies
                .stream()
                .filter(a -> a.getId().equals(dto.getId())
                        /*update if he is admin*/ && a.getOwnerId().equals(userId))
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
    public Boolean deleteById(Long agencyId, Long userId) {
        ProfileEntity admin = profileRepository.findById(userId);
        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new PermissionDeniedException("You don't have permission to delete this agency");
        }
        List<AgencyEntity> allAgencies = agencyRepository.getAllAgencies();
        allAgencies.stream().filter(a -> a.getId().equals(agencyId)).findFirst().orElseThrow(() -> new NotFoundException("Agency not found"));
        allAgencies.removeIf(a -> a.getId().equals(agencyId));
        agencyRepository.createOrUpdate(allAgencies, false);
        return true;
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
