package quvoncuz.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import quvoncuz.dto.agency.*;
import quvoncuz.entities.AgencyEntity;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.enums.AgencyStatus;
import quvoncuz.enums.Role;
import quvoncuz.exceptions.AlreadyExistsException;
import quvoncuz.exceptions.DoNotMatchException;
import quvoncuz.repository.AgencyRepository;
import quvoncuz.repository.ProfileRepository;
import quvoncuz.repository.TourRepository;
import quvoncuz.service.impl.AgencyServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgencyServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private AgencyRepository agencyRepository;

    @Mock
    private TourRepository tourRepository;

    @InjectMocks
    private AgencyServiceImpl agencyService;

    private ProfileEntity profile;
    private AgencyEntity agency;
    private static final Long USER_ID = 1L;
    private static final Long AGENCY_ID = 1L;

    @BeforeEach
    void setUp() {
        profile = new ProfileEntity();
        profile.setId(USER_ID);
        profile.setRole(Role.USER);

        agency = new AgencyEntity();
        agency.setName("Tour Agency");
        agency.setId(AGENCY_ID);
        agency.setOwnerId(USER_ID);
    }

    @Test
    void applyForAgency_Success() {
        CreateAgencyRequestDTO dto = new CreateAgencyRequestDTO();
        dto.setName("Tour Agency");

        when(profileRepository.findById(USER_ID)).thenReturn(Optional.of(profile));
        when(agencyRepository.save(any(AgencyEntity.class))).thenReturn(agency);

        AgencyDTO agencyDTO = agencyService.applyForAgency(dto, USER_ID);

        assertNotNull(agencyDTO);
        assertEquals("Tour Agency", agencyDTO.getName());
        assertEquals(AGENCY_ID, agencyDTO.getId());

        assertTrue(profile.getIsCreatedAgency());

        verify(profileRepository, times(1)).findById(USER_ID);
        verify(agencyRepository, times(1)).save(any(AgencyEntity.class));
        verify(profileRepository, times(1)).save(profile);
    }

    @Test
    void applyForAgency_AlreadyExists_ThrowsException() {
        CreateAgencyRequestDTO dto = new CreateAgencyRequestDTO();
        dto.setName("Tour Agency");

        profile.setIsCreatedAgency(true);

        when(profileRepository.findById(USER_ID)).thenReturn(Optional.of(profile));

        assertThrows(AlreadyExistsException.class, () -> agencyService.applyForAgency(dto, USER_ID));

        verify(agencyRepository, never()).save(any(AgencyEntity.class));
        verify(profileRepository, never()).save(any(ProfileEntity.class));
    }

    @Test
    void approveAgency_Success() {

        agency.setStatus(AgencyStatus.PENDING);
        agency.setOwnerId(USER_ID);

        AgencyApproveRequestDTO dto = new AgencyApproveRequestDTO();
        dto.setAgencyId(AGENCY_ID);
        dto.setApprove(true);

        when(agencyRepository.findById(AGENCY_ID)).thenReturn(Optional.of(agency));
        when(profileRepository.findById(USER_ID)).thenReturn(Optional.of(profile));

        agencyService.approveAgency(dto);

        assertEquals(Role.AGENCY, profile.getRole());
        assertEquals(AgencyStatus.ACCEPTED, agency.getStatus());
        assertTrue(agency.getApproved());

        verify(agencyRepository, times(1)).save(any());
        verify(profileRepository, times(1)).save(any());
    }

    @Test
    void approveAgency_Rejection() {
        agency.setStatus(AgencyStatus.PENDING);
        agency.setOwnerId(USER_ID);

        AgencyApproveRequestDTO dto = new AgencyApproveRequestDTO();
        dto.setAgencyId(AGENCY_ID);
        dto.setApprove(false);

        when(agencyRepository.findById(AGENCY_ID)).thenReturn(Optional.of(agency));

        agencyService.approveAgency(dto);

        assertEquals(AgencyStatus.REJECTED, agency.getStatus());
        assertFalse(agency.getApproved());

        verify(profileRepository, never()).save(any());
        verify(agencyRepository, times(1)).save(any());
    }

    @Test
    void getAllAgencies_Success() {

        PageRequest pageRequest = PageRequest.of(0, 10);

        AgencyEntity agencyEntity = new AgencyEntity();
        agencyEntity.setId(2L);

        Page<AgencyEntity> resultPage = new PageImpl<>(List.of(agency, agencyEntity));

        when(agencyRepository.findAll(pageRequest)).thenReturn(resultPage);

        Page<AgencyShortInfo> allAgencies = agencyService.getAllAgencies(false, 1, 10);

        assertNotNull(allAgencies);
        assertEquals(2, allAgencies.getContent().size());
        assertEquals(1L, allAgencies.getContent().get(0).getId());

        verify(agencyRepository, times(1)).findAll(pageRequest);
    }

    @Test
    void getAllAgencies_WithPending() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        AgencyEntity agencyEntity = new AgencyEntity();
        agencyEntity.setId(2L);
        agencyEntity.setStatus(AgencyStatus.PENDING);

        Page<AgencyEntity> pageResult = new PageImpl<>(List.of(agencyEntity));

        when(agencyRepository.findAllByStatus(AgencyStatus.PENDING, pageRequest)).thenReturn(pageResult);

        Page<AgencyShortInfo> allAgencies = agencyService.getAllAgencies(true, 1, 10);

        assertNotNull(allAgencies);
        assertEquals(1, allAgencies.getContent().size());
        assertEquals(2L, allAgencies.getContent().get(0).getId());

        verify(agencyRepository, times(1)).findAllByStatus(AgencyStatus.PENDING, pageRequest);
    }

    @Test
    void update_Success() {
        UpdateAgencyRequestDTO dto = new UpdateAgencyRequestDTO();
        dto.setName("EURO TOUR");
        dto.setPhone("998909009090");

        when(profileRepository.findById(USER_ID)).thenReturn(Optional.of(profile));
        when(agencyRepository.findById(AGENCY_ID)).thenReturn(Optional.of(agency));
        when(agencyRepository.save(any(AgencyEntity.class))).thenReturn(agency);

        AgencyFullInfo updated = agencyService.update(AGENCY_ID, USER_ID, dto);

        assertNotNull(updated);
        assertEquals("EURO TOUR", updated.getName());
        assertEquals("998909009090", updated.getPhone());

        verify(agencyRepository, times(1)).save(any());
        verify(agencyRepository, times(1)).findById(AGENCY_ID);
        verify(profileRepository, times(1)).findById(USER_ID);
    }

    @Test
    void update_DoNotMatch_ThrowsException() {
        UpdateAgencyRequestDTO dto = new UpdateAgencyRequestDTO();
        dto.setName("EURO TOUR");
        dto.setPhone("998909009090");

        profile.setId(2L);

        when(profileRepository.findById(2L)).thenReturn(Optional.of(profile));
        when(agencyRepository.findById(AGENCY_ID)).thenReturn(Optional.of(agency));

        assertThrows(DoNotMatchException.class, () -> agencyService.update(AGENCY_ID, 2L, dto));

        verify(agencyRepository, never()).save(any());
        verify(profileRepository, times(1)).findById(2L);
        verify(agencyRepository, times(1)).findById(AGENCY_ID);
    }
}