package quvoncuz.integration.admin;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.enums.Role;
import quvoncuz.repository.ProfileRepository;
import quvoncuz.util.SecurityUtil;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mockStatic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminProfileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProfileRepository profileRepository;

    private MockedStatic<SecurityUtil> mockedStatic;
    private static Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        ProfileEntity profile = ProfileEntity.builder()
                .role(Role.USER)
                .visible(true)
                .build();
        profile = profileRepository.save(profile);
        USER_ID = profile.getId();

        mockedStatic = mockStatic(SecurityUtil.class);
        mockedStatic.when(SecurityUtil::getCurrentUserId).thenReturn(USER_ID);
    }

    @AfterEach
    void tearDown() {
        mockedStatic.close();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteById_Success() throws Exception {
        mockMvc.perform(delete("/admin/profiles/{profileId}", USER_ID)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());

        ProfileEntity profile = profileRepository.findById(USER_ID).orElseThrow();
        assertFalse(profile.getVisible());
    }
}