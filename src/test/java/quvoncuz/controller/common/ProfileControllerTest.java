package quvoncuz.controller.common;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import quvoncuz.dto.profile.UpdateProfileRequestDTO;
import quvoncuz.enums.Role;
import quvoncuz.security.jwt.JwtUtil;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProfileControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void update_Success() {
        String token = jwtUtil.encodeAccessToken("user", Role.USER);

        UpdateProfileRequestDTO dto = new UpdateProfileRequestDTO();
        dto.setEmail("email@gmail.com");
        dto.setFullName("email");
        dto.setUsername("email");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(dto)
                .when()
                .put("/profiles/me")
                .then()
                .log().body()
                .statusCode(200)
                .body("data.fullName", is("email"))
                .body("data.email", is("email@gmail.com"));
    }

    @Test
    void update_ThrowsException() {
        String token = jwtUtil.encodeAccessToken("user", Role.USER);

        UpdateProfileRequestDTO dto = new UpdateProfileRequestDTO();
        dto.setEmail("email@gmail.com");
        dto.setFullName("email");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(dto)
                .when()
                .put("/api/v1/profiles/me")
                .then()
                .log().body()
                .statusCode(400);
    }
}