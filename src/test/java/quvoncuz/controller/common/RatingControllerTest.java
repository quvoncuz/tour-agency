package quvoncuz.controller.common;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import quvoncuz.dto.rating.RatingRequestDTO;
import quvoncuz.dto.rating.UpdateRatingRequestDTO;
import quvoncuz.enums.RatingType;
import quvoncuz.enums.Role;
import quvoncuz.security.jwt.JwtUtil;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RatingControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void create_Success() {
        String token = jwtUtil.encodeAccessToken("user", Role.USER);

        RatingRequestDTO dto = new RatingRequestDTO();
        dto.setSourceId(41);
        dto.setStars(4);
        dto.setType(RatingType.TOUR);
        dto.setComment("Good");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(dto)
                .when()
                .post("/api/v1/ratings")
                .then()
                .log().body()
                .statusCode(201)
                .body("data.sourceId", is(41))
                .body("data.comment", is("Good"));

    }

    @Test
    void create_Forbidden_Throws() {
        String token = jwtUtil.encodeAccessToken("quvonc", Role.ADMIN);

        RatingRequestDTO dto = new RatingRequestDTO();
        dto.setSourceId(41);
        dto.setStars(4);
        dto.setType(RatingType.AGENCY);
        dto.setComment("Good");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(dto)
                .when()
                .post("/api/v1/ratings")
                .then()
                .log().body()
                .statusCode(403);
    }

    @Test
    void update_Success() {
        String token = jwtUtil.encodeAccessToken("user", Role.USER);

        UpdateRatingRequestDTO dto = new UpdateRatingRequestDTO();
        dto.setStars(5);
        dto.setComment("The best of ever seen");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(dto)
                .when()
                .put("/api/v1/ratings/{ratingId}", 1)
                .then()
                .log().body()
                .statusCode(200)
                .body("data.stars", is(5))
                .body("data.comment", is("The best of ever seen"));


    }

    @Test
    void update_ThrowsException() throws Exception {
        UpdateRatingRequestDTO dto = new UpdateRatingRequestDTO();
        dto.setStars(1);
        dto.setComment("Peace of sh**");

        given()
                .contentType(ContentType.JSON)
                .body(dto)
                .when()
                .put("/api/v1/ratings/{ratingId}", 1)
                .then()
                .log().body()
                .statusCode(401);
    }
}