package quvoncuz.controller.agency;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import quvoncuz.dto.agency.UpdateAgencyRequestDTO;
import quvoncuz.enums.Role;
import quvoncuz.security.jwt.JwtUtil;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AgencyAgencyControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void update_Success() throws Exception {
        String token = jwtUtil.encodeAccessToken("quvonch", Role.AGENCY);

        UpdateAgencyRequestDTO dto = new UpdateAgencyRequestDTO();
        dto.setName("EURO Tour");
        dto.setEmail("tour_r@gmail.com");
        dto.setAddress("fasfeq");
        dto.setCity("sccda");
        dto.setDescription("sfefa");
        dto.setPhone("99985393403");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(dto)
                .when()
                .put("/api/v1/agency/agencies/{agencyId}", 3)
                .then()
                .log().body()
                .statusCode(200)
                .body("data.name", is("EURO Tour"))
                .body("data.email", is("tour_r@gmail.com"));
    }

    //
    @Test
    void update_UnAuthorized_Throws() throws Exception {
        UpdateAgencyRequestDTO dto = new UpdateAgencyRequestDTO();
        dto.setName("EURO Tour");
        dto.setEmail("tour_r@gmail.com");
        dto.setAddress("fasfeq");
        dto.setCity("sccda");
        dto.setDescription("sfefa");
        dto.setPhone("99985393403");

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .put("/api/v1/agency/agencies/{agencyId}", 3)
                .then()
                .log().body()
                .statusCode(401);
    }

    @Test
    void update_ValidationFail() {
        String token = jwtUtil.encodeAccessToken("quvonch", Role.AGENCY);

        UpdateAgencyRequestDTO dto = new UpdateAgencyRequestDTO();
        dto.setName("EURO Tour");
        dto.setEmail("email@gmail.com");
        dto.setAddress("fasfeq");
        dto.setCity("sccda");
        dto.setDescription("sfefa");
        dto.setPhone("99985393403");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(dto)
                .when()
                .put("/api/v1/agency/agencies/{agencyId}", -3)
                .then()
                .log().body()
                .statusCode(400);
    }
}