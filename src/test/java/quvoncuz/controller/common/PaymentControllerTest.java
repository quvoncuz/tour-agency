package quvoncuz.controller.common;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import quvoncuz.enums.Role;
import quvoncuz.security.jwt.JwtUtil;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.isNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void findAllByUserId_Success() {
        String token = jwtUtil.encodeAccessToken("user", Role.USER);

        given()
                .header("Authorization", "Bearer " + token)
                .get("/api/v1/payments")
                .then()
                .log().body()
                .statusCode(200)
                .body("data.size()", is(2));
    }

    @Test
    void findAllByUserId_Forbidden() {
        String token = jwtUtil.encodeAccessToken("quvonc", Role.ADMIN);

        given()
                .header("Authorization", "Bearer " + token)
                .get("/api/v1/payments")
                .then()
                .log().body()
                .statusCode(403);
    }

}