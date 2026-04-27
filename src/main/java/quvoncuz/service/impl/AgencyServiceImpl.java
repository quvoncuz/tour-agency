package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quvoncuz.dto.agency.*;
import quvoncuz.entities.AgencyEntity;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.enums.AgencyStatus;
import quvoncuz.enums.Role;
import quvoncuz.exceptions.AlreadyExistsException;
import quvoncuz.exceptions.DoNotMatchException;
import quvoncuz.exceptions.NotFoundException;
import quvoncuz.exceptions.PermissionDeniedException;
import quvoncuz.mapper.AgencyMapper;
import quvoncuz.repository.AgencyRepository;
import quvoncuz.repository.ProfileRepository;
import quvoncuz.service.AgencyService;
import quvoncuz.util.SecurityUtil;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AgencyServiceImpl implements AgencyService {

    private final Logger logger = LoggerFactory.getLogger(AgencyServiceImpl.class);
    private final ProfileRepository profileRepository;
    private final AgencyRepository agencyRepository;

    @Override
    @Transactional
    public AgencyDTO applyForAgency(CreateAgencyRequestDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        ProfileEntity profile = profileRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        if (!profile.getRole().equals(Role.USER)) {
            throw new PermissionDeniedException("You have a agency already");
        }
        if (profile.getIsCreateAgency()) {
            throw new AlreadyExistsException("You already created agency!");
        }
        AgencyEntity agency = AgencyEntity.builder()
                .id(profile.getId())
                .ownerId(profile.getId())
                .name(dto.getName())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .description(dto.getDescription())
                .city(dto.getCity())
                .address(dto.getAddress())
                .approved(false)
                .rating(0.0)
                .status(AgencyStatus.PENDING)
                .build();
        agencyRepository.save(agency);
        logger.info("User with id {} applied for agency with id {}", userId, agency.getId());
        return AgencyMapper.toDTO(agency);
    }

    @Override
    @Transactional
    public Boolean approveAgency(AgencyApproveRequestDTO dto) {

        AgencyEntity agency = findById(dto.getAgencyId());

        if (dto.getApprove()) {
            ProfileEntity profile = profileRepository.findById(agency.getOwnerId())
                    .orElseThrow(() -> new NotFoundException("User not found"));
            profile.setRole(Role.AGENCY);
            profile.setIsCreateAgency(true);
            agency.setStatus(AgencyStatus.ACCEPTED);
            agency.setApproved(true);
            profileRepository.save(profile);
        } else {
            agency.setStatus(AgencyStatus.REJECTED);
        }
        agencyRepository.save(agency);
        logger.info("Agency with id {}", dto.getApprove() ? "approved" : "rejected");
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AgencyShortInfo> getPendingAgencies(int page, int size) {
        logger.info("Admin requested pending agencies");

        PageRequest pageRequest = PageRequest.of(page - 1, size);

        Page<AgencyEntity> pageResult = agencyRepository.findAll(pageRequest);
        return pageResult
                .map(AgencyMapper::toShortInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AgencyFullInfo> getAllAgencies(int page, int size) {
        logger.info("Requested all agencies");
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return agencyRepository.findAll(pageRequest)
                .map(AgencyMapper::toFullInfo);
    }

    @Override
    @Transactional
    public AgencyFullInfo update(Long agencyId, UpdateAgencyRequestDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        ProfileEntity profile = profileRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        AgencyEntity agency = findById(agencyId);

        if (!profile.getId().equals(agency.getOwnerId())) {
            throw new DoNotMatchException("You are not owner");
        }

        agency.setName(dto.getName());
        agency.setPhone(dto.getPhone());
        agency.setEmail(dto.getEmail());
        agency.setDescription(dto.getDescription());
        agency.setCity(dto.getCity());
        agency.setAddress(dto.getAddress());

        agencyRepository.save(agency);
        logger.info("User with id {} updated agency with id {}", userId, agency.getId());
        return AgencyMapper.toFullInfo(agency);
    }

    @Override
    public Boolean deleteById(Long agencyId) {
        Long userId = SecurityUtil.getCurrentUserId();
        logger.info("Admin with id {} deleted agency with id {}", userId, agencyId);
        agencyRepository.deleteById(agencyId);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public AgencyDTO findByAgencyId(Long agencyId) {
        logger.info("Requested agency with id {}", agencyId);
        return AgencyMapper.toDTO(findById(agencyId));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AgencyEntity> findByOwnerId(Long ownerId) {
        return agencyRepository.findByOwnerId(ownerId);
    }

    private AgencyEntity findById(Long agencyId) {
        return agencyRepository.findById(agencyId)
                .orElseThrow(() -> new NotFoundException("Agency not found"));
    }
}
