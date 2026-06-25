package quvoncuz.controller.common;

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
import quvoncuz.dto.rating.RatingFullInfo;
import quvoncuz.dto.rating.RatingRequestDTO;
import quvoncuz.dto.rating.UpdateRatingRequestDTO;
import quvoncuz.enums.RatingType;
import quvoncuz.security.CustomUserDetailsService;
import quvoncuz.security.jwt.JwtUtil;
import quvoncuz.service.RatingService;
import quvoncuz.util.SecurityUtil;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RatingController.class)
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RatingService ratingService;
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private MockedStatic<SecurityUtil> mockedStatic;
    private static final Long USER_ID = 1L;
    private static final Long RATING_ID = 1L;
    private static final Long SOURCE_ID = 1L;

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
    @WithMockUser
    void create_Success() throws Exception {
        RatingRequestDTO dto = new RatingRequestDTO();
        dto.setSourceId(SOURCE_ID);
        dto.setStars(4);
        dto.setType(RatingType.AGENCY);
        dto.setComment("Good");

        RatingFullInfo fullInfo = RatingFullInfo.builder()
                .userId(USER_ID)
                .sourceId(SOURCE_ID)
                .build();

        when(ratingService.create(dto, USER_ID)).thenReturn(fullInfo);

        mockMvc.perform(post("/ratings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.userId").value(USER_ID))
                .andExpect(jsonPath("$.data.sourceId").value(SOURCE_ID));

        verify(ratingService, times(1)).create(any(), eq(USER_ID));
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "AGENCY"})
    void create_Forbidden_Throws() throws Exception {
        RatingRequestDTO dto = new RatingRequestDTO();
        dto.setSourceId(SOURCE_ID);
        dto.setStars(4);
        dto.setType(RatingType.AGENCY);
        dto.setComment("Good");

        mockMvc.perform(post("/ratings")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(ratingService);
    }

    @Test
    @WithMockUser
    void update_Success() throws Exception {
        UpdateRatingRequestDTO dto = new UpdateRatingRequestDTO();
        dto.setStars(5);
        dto.setComment("The best of ever seen");

        RatingFullInfo fullInfo = RatingFullInfo.builder()
                .stars(5)
                .comment("The best of ever seen")
                .build();

        when(ratingService.update(eq(RATING_ID), any(), eq(USER_ID))).thenReturn(fullInfo);

        mockMvc.perform(put("/ratings/{ratingId}", RATING_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.stars").value(5))
                .andExpect(jsonPath("$.data.comment").value("The best of ever seen"));

        verify(ratingService, times(1)).update(anyLong(), any(), anyLong());

    }

    @Test
    void update_ThrowsException() throws Exception {
        UpdateRatingRequestDTO dto = new UpdateRatingRequestDTO();
        dto.setStars(5);
        dto.setComment("The best of ever seen");

        mockMvc.perform(put("/ratings/{ratingId}", RATING_ID)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(ratingService);
    }
}