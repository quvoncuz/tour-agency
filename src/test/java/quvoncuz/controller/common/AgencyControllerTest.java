package quvoncuz.controller.common;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import quvoncuz.dto.agency.CreateAgencyRequestDTO;
import quvoncuz.enums.Role;
import quvoncuz.security.jwt.JwtUtil;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AgencyControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void applyForAgencies_Success() {
        String token = jwtUtil.encodeAccessToken("user", Role.USER);

        CreateAgencyRequestDTO dto = new CreateAgencyRequestDTO();
        dto.setName("EURO TOUR");
        dto.setEmail("eurotour@mail.ru");
        dto.setPhone("99899099899");
        dto.setDescription("qefgbteb");
        dto.setCity("dgehdz");
        dto.setAddress("fjernjigneri");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(dto)
                .when()
                .post("/api/v1/agencies")
                .then()
                .log().body()
                .statusCode(201)
                .body("success", is(true))
                .body("data.id", notNullValue())
                .body("data.email", is("eurotour@mail.ru"));
    }

    @Test
    void applyForAgencies_Forbidden_ThrowsException() {
        String token = jwtUtil.encodeAccessToken("quvonc", Role.AGENCY);

        CreateAgencyRequestDTO dto = new CreateAgencyRequestDTO();
        dto.setName("EURO TOUR");
        dto.setEmail("eurotour@mail.ru");
        dto.setPhone("99899099899");
        dto.setDescription("qefgbteb");
        dto.setCity("dgehdz");
        dto.setAddress("fjernjigneri");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(dto)
                .when()
                .post("/api/v1/agencies")
                .then()
                .statusCode(403);
    }

    @Test
    void findById_Success() {
        Long agencyId = 2L;

        given()
                .when()
                .get("/api/v1/agencies/{agencyId}", agencyId)
                .then()
                .statusCode(200);
    }
}