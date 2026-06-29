package quvoncuz.integration.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import quvoncuz.dto.rating.RatingRequestDTO;
import quvoncuz.dto.rating.UpdateRatingRequestDTO;
import quvoncuz.entities.*;
import quvoncuz.enums.BookingStatus;
import quvoncuz.enums.RatingType;
import quvoncuz.enums.Role;
import quvoncuz.integration.BaseIntegrationTest;
import quvoncuz.repository.*;
import quvoncuz.util.SecurityUtil;

import static org.mockito.Mockito.mockStatic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RatingControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private TourRepository tourRepository;
    @Autowired
    private AgencyRepository agencyRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private RatingRepository ratingRepository;

    @MockitoBean
    private MockedStatic<SecurityUtil> mockedStatic;
    private static Long USER_ID = 1L;
    private ProfileEntity savedProfile;
    private AgencyEntity savedAgency;
    private TourEntity savedTour;
    private BookingEntity savedBooking;

    @BeforeEach
    void setUp() {
        ProfileEntity profile = ProfileEntity.builder()
                .role(Role.USER)
                .visible(true)
                .build();
        savedProfile = profileRepository.save(profile);
        USER_ID = savedProfile.getId();

        AgencyEntity agency = AgencyEntity.builder()
                .id(savedProfile.getId())
                .ownerId(savedProfile.getId())
                .name("Qwerty")
                .phone("9999999999")
                .email("mail")
                .city("qwerty")
                .build();
        savedAgency = agencyRepository.save(agency);

        TourEntity tour = TourEntity.builder()
                .agencyId(savedAgency.getId())
                .title("qwerty")
                .build();
        savedTour = tourRepository.save(tour);

        BookingEntity booking = BookingEntity.builder()
                .userId(savedProfile.getId())
                .tourId(savedTour.getId())
                .status(BookingStatus.CONFIRMED)
                .build();
        savedBooking = bookingRepository.save(booking);

        mockedStatic = mockStatic(SecurityUtil.class);
        mockedStatic.when(SecurityUtil::getCurrentUserId).thenReturn(USER_ID);
    }

    @AfterEach
    void tearDown() {
        mockedStatic.close();
    }

    @Test
    @WithMockUser
    void create_ForTour_Success() throws Exception {
        RatingRequestDTO dto = new RatingRequestDTO();
        dto.setSourceId(savedTour.getId());
        dto.setType(RatingType.TOUR);
        dto.setComment("Good");
        dto.setStars(4);

        mockMvc.perform(post("/ratings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void create_ForAgency_Success() throws Exception {
        RatingRequestDTO dto = new RatingRequestDTO();
        dto.setSourceId(savedAgency.getId());
        dto.setType(RatingType.AGENCY);
        dto.setComment("Good");
        dto.setStars(4);

        mockMvc.perform(post("/ratings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    void create_ForTour_DoNotMatch_Throws() throws Exception {
        RatingRequestDTO dto = new RatingRequestDTO();
        dto.setSourceId(savedAgency.getId());
        dto.setType(RatingType.TOUR);
        dto.setComment("Good");
        dto.setStars(4);

        BookingEntity booking = bookingRepository.findById(savedBooking.getId()).orElseThrow();
        booking.setStatus(BookingStatus.PENDING);
        bookingRepository.save(booking);

        mockMvc.perform(post("/ratings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("You can only rate completed bookings"));

        dto.setType(RatingType.AGENCY);
        mockMvc.perform(post("/ratings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("You can only rate agencies if you have completed a booking"));
    }

    @Test
    @WithMockUser
    void update_Success() throws Exception {
        RatingEntity rating = RatingEntity.builder()
                .userId(USER_ID)
                .sourceId(savedTour.getId())
                .type(RatingType.TOUR)
                .stars(4)
                .comment("Good")
                .build();
        rating = ratingRepository.save(rating);

        UpdateRatingRequestDTO dto = new UpdateRatingRequestDTO();
        dto.setStars(5);
        dto.setComment("The best of ever seen");

        mockMvc.perform(put("/ratings/{ratingId}", rating.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.comment").value("The best of ever seen"));
    }

    @Test
    @WithMockUser
    void update_DoNotMatch_Throws() throws Exception {
        ProfileEntity profile = ProfileEntity.builder()
                .role(Role.USER)
                .visible(true)
                .build();
        savedProfile = profileRepository.save(profile);
        RatingEntity rating = RatingEntity.builder()
                .userId(savedProfile.getId())
                .sourceId(savedTour.getId())
                .type(RatingType.TOUR)
                .stars(4)
                .comment("Good")
                .build();
        rating = ratingRepository.save(rating);

        UpdateRatingRequestDTO dto = new UpdateRatingRequestDTO();
        dto.setStars(5);
        dto.setComment("The best of ever seen");

        mockMvc.perform(put("/ratings/{ratingId}", rating.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("You don't have permission"));
    }
}