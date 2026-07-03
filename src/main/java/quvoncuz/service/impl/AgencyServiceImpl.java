package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quvoncuz.dto.agency.*;
import quvoncuz.entities.AgencyEntity;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.enums.AgencyStatus;
import quvoncuz.enums.EventType;
import quvoncuz.enums.Role;
import quvoncuz.events.NotificationEvent;
import quvoncuz.events.StatisticsEvent;
import quvoncuz.exceptions.AlreadyExistsException;
import quvoncuz.exceptions.DoNotMatchException;
import quvoncuz.exceptions.NotFoundException;
import quvoncuz.exceptions.PermissionDeniedException;
import quvoncuz.mapper.AgencyMapper;
import quvoncuz.repository.AgencyRepository;
import quvoncuz.repository.ProfileRepository;
import quvoncuz.repository.TourRepository;
import quvoncuz.service.AgencyService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgencyServiceImpl implements AgencyService {

    private final ProfileRepository profileRepository;
    private final AgencyRepository agencyRepository;
    private final TourRepository tourRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public AgencyDTO applyForAgency(CreateAgencyRequestDTO dto, Long userId) {
        ProfileEntity profile = profileRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        if (!profile.getRole().equals(Role.USER)) {
            throw new PermissionDeniedException("You have a agency already");
        }
        if (profile.getIsCreatedAgency()) {
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
        try {
            agencyRepository.save(agency);
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistsException("You already created agency");
        }

        profile.setIsCreatedAgency(true);
        profileRepository.save(profile);

        log.info("User with id {} applied for agency with id {}", userId, agency.getId());
        return AgencyMapper.toDTO(agency);
    }

    @Override
    @Transactional
    public void approveAgency(AgencyApproveRequestDTO dto) {

        AgencyEntity agency = findById(dto.getAgencyId());

        if (dto.getApprove()) {
            ProfileEntity profile = profileRepository.findById(agency.getOwnerId())
                    .orElseThrow(() -> new NotFoundException("User not found"));
            profile.setRole(Role.AGENCY);
            agency.setStatus(AgencyStatus.ACCEPTED);
            agency.setApproved(true);
            profileRepository.save(profile);

            applicationEventPublisher.publishEvent(
                    NotificationEvent.builder()
                            .entityId(agency.getId())
                            .eventType(EventType.AGENCY_APPROVED)
                            .subjectName(agency.getName())
                            .mails(List.of(agency.getEmail()))
                            .dateTime(LocalDateTime.now())
                            .build());

            applicationEventPublisher.publishEvent(
                    StatisticsEvent.builder()
                            .entityId(profile.getId())
                            .eventType(EventType.USER_REGISTERED)
                            .dateTime(LocalDateTime.now())
                            .build());

        } else {
            agency.setStatus(AgencyStatus.REJECTED);
            agency.setApproved(false);
        }
        agencyRepository.save(agency);
        log.info("Agency with id {} {}", dto.getAgencyId(), dto.getApprove() ? "approved" : "rejected");
    }

    @Override
    public Page<AgencyShortInfo> getAllAgencies(boolean pending, int page, int size) {
        if (pending) {
            log.info("Requested pending agencies");
            PageRequest pageRequest = PageRequest.of(page - 1, size);
            Page<AgencyEntity> pageResult = agencyRepository.findAllByStatus(AgencyStatus.PENDING, pageRequest);
            return pageResult
                    .map(AgencyMapper::toShortInfo);
        } else {
            log.info("Requested all agencies");
            PageRequest pageRequest = PageRequest.of(page - 1, size);
            return agencyRepository.findAll(pageRequest)
                    .map(AgencyMapper::toShortInfo);
        }
    }

    @Override
    @Transactional
    public AgencyFullInfo update(Long id, Long userId, UpdateAgencyRequestDTO dto) {

        ProfileEntity profile = profileRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        AgencyEntity agency = findById(id);

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
        log.info("User with id {} updated agency with id {}", userId, agency.getId());
        return AgencyMapper.toFullInfo(agency);
    }

    @Override
    public void deleteById(Long agencyId) {
        log.info("Admin deleted agency. Id: {}", agencyId);
        AgencyEntity agency = findById(agencyId);
        agency.setVisible(false);
        tourRepository.updateVisibleByAgencyId(false, agencyId);
    }

    @Override
    public AgencyDTO findByAgencyId(Long agencyId) {
        log.info("Requested agency with id {}", agencyId);
        return AgencyMapper.toDTO(findById(agencyId));
    }

    @Override
    public Optional<AgencyEntity> findByOwnerId(Long ownerId) {
        return agencyRepository.findByOwnerId(ownerId);
    }

    private AgencyEntity findById(Long agencyId) {
        return agencyRepository.findById(agencyId)
                .orElseThrow(() -> new NotFoundException("Agency not found"));
    }
}
