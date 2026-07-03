package quvoncuz.integration.agency;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import quvoncuz.dto.tour.CancelTourDTO;
import quvoncuz.dto.tour.CreateTourRequestDTO;
import quvoncuz.dto.tour.UpdateTourRequestDTO;
import quvoncuz.entities.AgencyEntity;
import quvoncuz.entities.BookingEntity;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.entities.TourEntity;
import quvoncuz.enums.AgencyStatus;
import quvoncuz.enums.BookingStatus;
import quvoncuz.enums.Role;
import quvoncuz.enums.TourStatus;
import quvoncuz.exceptions.NotFoundException;
import quvoncuz.integration.BaseIntegrationTest;
import quvoncuz.repository.AgencyRepository;
import quvoncuz.repository.BookingRepository;
import quvoncuz.repository.ProfileRepository;
import quvoncuz.repository.TourRepository;
import quvoncuz.util.SecurityUtil;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AgencyTourControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TourRepository tourRepository;
    @Autowired
    private AgencyRepository agencyRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private MockedStatic<SecurityUtil> mockedStatic;
    private static Long USER_ID = 1L;
    private static Long AGENCY_ID = 1L;
    private static Long TOUR_ID = 1L;
    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {

        ProfileEntity profile = new ProfileEntity();
        profile.setEmail("agency@mail.com");
        profile.setRole(Role.AGENCY);
        profile.setIsActive(true);
        profile.setVisible(true);
        profile = profileRepository.save(profile);
        USER_ID = profile.getId();

        AgencyEntity agency = new AgencyEntity();
        agency.setId(USER_ID);
        agency.setOwnerId(USER_ID);
        agency.setName("Test Agency");
        agency.setPhone("998909009090");
        agency.setEmail("email@gmail.com");
        agency.setCity("Tashkent");
        agency.setStatus(AgencyStatus.ACCEPTED);
        agency.setVisible(true);
        agency = agencyRepository.save(agency);
        AGENCY_ID = agency.getId();

        mockedStatic = mockStatic(SecurityUtil.class);
        mockedStatic.when(SecurityUtil::getCurrentUserId).thenReturn(USER_ID);
    }

    @AfterEach
    void tearDown() {
        mockedStatic.close();
    }

    @Test
    @WithMockUser(roles = "AGENCY")
    void createTour_Success() throws Exception {
        CreateTourRequestDTO dto = new CreateTourRequestDTO();
        dto.setTitle("qwerty");
        dto.setImageUrl("https://example.com/image.jpg");
        dto.setMaxSeats(100);
        dto.setPrice(100L);
        dto.setStartDate(LocalDate.now().plusDays(10));
        dto.setDurationDays(10);
        dto.setDestination("wdewbeth");
        dto.setDescription("dqefwerth");

        mockMvc.perform(post("/agency/tours")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("qwerty"));

        List<TourEntity> tours = tourRepository.findAll();
        assertEquals(1, tours.size());
        assertEquals("qwerty", tours.get(0).getTitle());
    }

    @Test
    @WithMockUser(roles = {"AGENCY"})
    void createTour_PermissionDenied_ThrowsException() throws Exception {

        AgencyEntity agency = agencyRepository.findByOwnerId(USER_ID).orElseThrow(() -> new NotFoundException("Agency not found"));
        agency.setVisible(false);

        CreateTourRequestDTO dto = new CreateTourRequestDTO();
        dto.setTitle("qwerty");
        dto.setImageUrl("https://example.com/image.jpg");
        dto.setMaxSeats(100);
        dto.setPrice(100L);
        dto.setStartDate(LocalDate.now().plusDays(10));
        dto.setDurationDays(10);
        dto.setDestination("wdewbeth");
        dto.setDescription("dqefwerth");


        mockMvc.perform(post("/agency/tours")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You can't create tour"));

        agency.setStatus(AgencyStatus.PENDING);

        mockMvc.perform(post("/agency/tours")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You don't have permission!"));


        List<TourEntity> tours = tourRepository.findAll();
        assertEquals(0, tours.size());
//        assertEquals("qwerty", tours.get(0).getTitle());
    }

    @Test
    @WithMockUser(roles = "AGENCY")
    void updateTour_Success() throws Exception {
        TourEntity tour = TourEntity.builder()
                .agencyId(AGENCY_ID)
                .title("qwerty")
                .price(100L)
                .build();
        tour = tourRepository.save(tour);

        UpdateTourRequestDTO dto = new UpdateTourRequestDTO();
        dto.setTitle("qwerty");
        dto.setImageUrl("https://example.com/image.jpg");
        dto.setMaxSeats(100);
        dto.setPrice(200L);
        dto.setStartDate(LocalDate.now().plusDays(10));
        dto.setDurationDays(10);
        dto.setDestination("wdewbeth");
        dto.setDescription("dqefwerth");

        mockMvc.perform(put("/agency/tours/{tourId}", tour.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("qwerty"))
                .andExpect(jsonPath("$.data.durationDays").value(10));

        List<TourEntity> all = tourRepository.findAll();
        assertEquals(1, all.size());
        assertEquals("qwerty", all.get(0).getTitle());
        assertEquals(10, all.get(0).getDurationDays());
    }

    @Test
    @WithMockUser(roles = "AGENCY")
    void updateTour_PermissionDenied_Throws() throws Exception {
        AgencyEntity agency = new AgencyEntity();
        agency.setId(2L);
        agency.setName("TestAgency");
        agency.setPhone("99899009090");
        agency.setEmail("emil@gmail.com");
        agency.setCity("Tashkent");
        agency.setStatus(AgencyStatus.ACCEPTED);
        agency.setVisible(true);
        agencyRepository.save(agency);

        TourEntity tour = TourEntity.builder()
                .agencyId(2L)
                .title("qwerty")
                .price(100L)
                .build();
        tour = tourRepository.save(tour);
        TOUR_ID = tour.getId();

        UpdateTourRequestDTO dto = new UpdateTourRequestDTO();
        dto.setTitle("qwerty");
        dto.setImageUrl("https://example.com/image.jpg");
        dto.setMaxSeats(100);
        dto.setPrice(200L);
        dto.setStartDate(LocalDate.now().plusDays(10));
        dto.setDurationDays(10);
        dto.setDestination("wdewbeth");
        dto.setDescription("dqefwerth");

        mockMvc.perform(put("/agency/tours/{tourId}", TOUR_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You don't have permission"));
    }

    @Test
    @WithMockUser(roles = "AGENCY")
    void cancelTour_Success() throws Exception {
        CancelTourDTO dto = new CancelTourDTO();
        dto.setReason("just kidding");

        TourEntity tour = TourEntity.builder()
                .agencyId(AGENCY_ID)
                .title("qwerty")
                .price(100L)
                .build();
        tour = tourRepository.save(tour);
        TOUR_ID = tour.getId();

        BookingEntity booking = BookingEntity.builder()
                .userId(USER_ID)
                .tourId(tour.getId())
                .status(BookingStatus.CONFIRMED)
                .build();
        booking = bookingRepository.save(booking);

        mockMvc.perform(patch("/agency/tours/{id}", TOUR_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isNoContent());

        tour = tourRepository.findById(TOUR_ID).orElseThrow(() -> new NotFoundException("T"));
        booking = bookingRepository.findById(booking.getId()).orElseThrow(() -> new NotFoundException("B"));

        assertEquals(TourStatus.CANCELLED, tour.getStatus());
        assertEquals(BookingStatus.CANCELED, booking.getStatus());
    }
}