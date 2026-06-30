package quvoncuz.controller.agency;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import quvoncuz.dto.tour.CreateTourRequestDTO;
import quvoncuz.dto.tour.TourFullInfo;
import quvoncuz.dto.tour.UpdateTourRequestDTO;
import quvoncuz.enums.Role;
import quvoncuz.security.CustomUserDetailsService;
import quvoncuz.security.jwt.JwtUtil;
import quvoncuz.service.TourService;
import quvoncuz.util.SecurityUtil;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AgencyTourControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void createTour_Success() {
        String token = jwtUtil.encodeAccessToken("quvonch", Role.AGENCY);

        CreateTourRequestDTO dto = new CreateTourRequestDTO();
        dto.setTitle("qwerty");
        dto.setMaxSeats(100);
        dto.setPrice(100L);
        dto.setStartDate(LocalDate.now().plusDays(10));
        dto.setDurationDays(10);
        dto.setDestination("wdewbeth");
        dto.setDescription("dqefwerth");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(dto)
                .when()
                .post("/api/v1/agency/tours")
                .then()
                .log().body()
                .statusCode(201)
                .body("data.title", is("qwerty"))
                .body("data.durationDays", is(10));
    }

    @Test
    void createTour_ValidationThrows() {
        String token = jwtUtil.encodeAccessToken("quvonch", Role.AGENCY);

        CreateTourRequestDTO dto = new CreateTourRequestDTO();
        dto.setTitle("qwerty");
        dto.setMaxSeats(100);
        dto.setPrice(100L);
        dto.setStartDate(LocalDate.now().plusDays(10));
        dto.setDestination("wdewbeth");
        dto.setDescription("dqefwerth");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(dto)
                .when()
                .post("/api/v1/agency/tours")
                .then()
                .log().body()
                .statusCode(400);
    }

    @Test
    void createTour_ForbiddenThrows() throws Exception {
        CreateTourRequestDTO dto = new CreateTourRequestDTO();
        dto.setTitle("qwerty");
        dto.setMaxSeats(100);
        dto.setPrice(100L);
        dto.setDurationDays(10);
        dto.setStartDate(LocalDate.now().plusDays(10));
        dto.setDestination("wdewbeth");
        dto.setDescription("dqefwerth");

        String token = jwtUtil.encodeAccessToken("quvonc", Role.USER);

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(dto)
                .when()
                .post("/api/v1/agency/tours")
                .then()
                .log().body()
                .statusCode(403);

    }

    @Test
    void updateTour_Success() {
        String token = jwtUtil.encodeAccessToken("quvonch", Role.AGENCY);

        UpdateTourRequestDTO dto = new UpdateTourRequestDTO();
        dto.setTitle("QWERTY");
        dto.setMaxSeats(100);
        dto.setPrice(100L);
        dto.setStartDate(LocalDate.now().plusDays(10));
        dto.setDurationDays(15);
        dto.setDestination("wdewbeth");
        dto.setDescription("dqefwerth");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(dto)
                .when()
                .put("/api/v1/agency/tours/{tourId}", 55)
                .then()
                .log().body()
                .statusCode(200)
                .body("data.title", is("QWERTY"))
                .body("data.durationDays", is(15));

    }
}