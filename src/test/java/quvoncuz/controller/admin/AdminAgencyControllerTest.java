package quvoncuz.controller.admin;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import quvoncuz.dto.agency.AgencyApproveRequestDTO;
import quvoncuz.enums.Role;
import quvoncuz.security.jwt.JwtUtil;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AdminAgencyControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void approveAgency_Success() throws Exception {
        String token = jwtUtil.encodeAccessToken("admin", Role.ADMIN);
        AgencyApproveRequestDTO dto = new AgencyApproveRequestDTO();
        dto.setAgencyId(9);
        dto.setApprove(true);

        String result = "Successfully accepted";

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(dto)
                .when()
                .post("/api/v1/admin/agencies")
                .then()
                .log().body()
                .statusCode(200)
                .body("data", is(result));
    }
}