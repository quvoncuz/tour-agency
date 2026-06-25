package quvoncuz.controller.agency;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import quvoncuz.dto.tour.CreateTourRequestDTO;
import quvoncuz.dto.tour.TourFullInfo;
import quvoncuz.dto.tour.UpdateTourRequestDTO;
import quvoncuz.security.CustomUserDetailsService;
import quvoncuz.security.jwt.JwtUtil;
import quvoncuz.service.TourService;
import quvoncuz.util.SecurityUtil;

import java.time.LocalDate;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AgencyTourController.class)
class AgencyTourControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private TourService tourService;
    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private MockedStatic<SecurityUtil> mockedStatic;
    private static final Long USER_ID = 1L;
    private static final Long TOUR_ID = 1L;

    @BeforeEach
    void setUp() {
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
        dto.setMaxSeats(100);
        dto.setPrice(100L);
        dto.setStartDate(LocalDate.now().plusDays(10));
        dto.setDurationDays(10);
        dto.setDestination("wdewbeth");
        dto.setDescription("dqefwerth");

        TourFullInfo fullInfo = TourFullInfo.builder()
                .title("qwerty")
                .price(100L)
                .durationDays(10)
                .maxSeats(100)
                .availableSeats(100)
                .build();

        when(tourService.createTour(dto, USER_ID)).thenReturn(fullInfo);

        mockMvc.perform(post("/agency/tours")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("qwerty"))
                .andExpect(jsonPath("$.data.price").value(100L))
                .andExpect(jsonPath("$.data.maxSeats").value(100))
                .andExpect(jsonPath("$.data.durationDays").value(10));

        verify(tourService, times(1)).createTour(any(), anyLong());
    }

    @Test
    @WithMockUser(roles = "AGENCY")
    void createTour_ValidationThrows() throws Exception {
        CreateTourRequestDTO dto = new CreateTourRequestDTO();
        dto.setTitle("qwerty");
        dto.setMaxSeats(100);
        dto.setPrice(100L);
        dto.setStartDate(LocalDate.now().plusDays(10));
        dto.setDestination("wdewbeth");
        dto.setDescription("dqefwerth");

        mockMvc.perform(post("/agency/tours")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(tourService);
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    void createTour_ForbiddenThrows() throws Exception {
        CreateTourRequestDTO dto = new CreateTourRequestDTO();
        dto.setTitle("qwerty");
        dto.setMaxSeats(100);
        dto.setPrice(100L);
        dto.setDurationDays(10);
        dto.setStartDate(LocalDate.now().plusDays(10));
        dto.setDestination("wdewbeth");
        dto.setDescription("dqefwerth");

        mockMvc.perform(post("/agency/tours")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(tourService);
    }

    @Test
    @WithMockUser(roles = "AGENCY")
    void updateTour_Success() throws Exception {
        UpdateTourRequestDTO dto = new UpdateTourRequestDTO();
        dto.setTitle("qwerty");
        dto.setMaxSeats(100);
        dto.setPrice(100L);
        dto.setStartDate(LocalDate.now().plusDays(10));
        dto.setDurationDays(10);
        dto.setDestination("wdewbeth");
        dto.setDescription("dqefwerth");

        TourFullInfo fullInfo = TourFullInfo.builder()
                .title("qwerty")
                .price(100L)
                .durationDays(10)
                .maxSeats(100)
                .availableSeats(100)
                .build();

        when(tourService.updateTour(TOUR_ID, dto, USER_ID)).thenReturn(fullInfo);

        mockMvc.perform(put("/agency/tours/{tourId}", TOUR_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.price").value(100L))
                .andExpect(jsonPath("$.data.maxSeats").value(100));

        verify(tourService, times(1)).updateTour(anyLong(), any(), anyLong());
    }
}