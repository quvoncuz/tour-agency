package quvoncuz.controller.common;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import quvoncuz.dto.booking.CreateBookingRequestDTO;
import quvoncuz.dto.booking.UpdateBookingRequestDTO;
import quvoncuz.enums.Role;
import quvoncuz.security.jwt.JwtUtil;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookingControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void createBooking_Success() {
        String token = jwtUtil.encodeAccessToken("user", Role.USER);

        CreateBookingRequestDTO dto = new CreateBookingRequestDTO();
        dto.setTourId(51);
        dto.setSeatsBooked(4);
        dto.setNote("To EUROPE");

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(dto)
                .when()
                .post("/api/v1/bookings")
                .then()
                .log().body()
                .statusCode(200)
                .body("success", is(true))
                .body("data", notNullValue());

    }

    @Test
    void updateBookingSeats_Success() {
        String token = jwtUtil.encodeAccessToken("user", Role.USER);

        UpdateBookingRequestDTO dto = new UpdateBookingRequestDTO();
        dto.setBookingId(5);
        dto.setSeats(6);

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(dto)
                .when()
                .put("/api/v1/bookings/{bookingId}", 5)
                .then()
                .log().body()
                .statusCode(200)
                .body("success", is(true))
                .body("data.seatsBooked", is(6))
                .body("data.totalPrice", is(600));
    }

    @Test
    void updateBookingSeats_403Forbidden_ThrowsException() throws Exception {
        String token = jwtUtil.encodeAccessToken("quvoncuz", Role.ADMIN);

        UpdateBookingRequestDTO dto = new UpdateBookingRequestDTO();
        dto.setBookingId(51);
        dto.setSeats(7);

        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer" + token)
                .body(dto)
                .when()
                .put("/api/v1/bookings/{bookingId}", 5)
                .then()
                .log().body()
                .statusCode(403);
    }

    @Test
    void findById_Success() {
        String token = jwtUtil.encodeAccessToken("user", Role.USER);

        given()
                .header("Authorization", "Bearer " + token)
                .get("/api/v1/bookings/{bookingIs}", 5)
                .then()
                .log().body()
                .statusCode(200)
                .body("data.totalPrice", is(600));
    }

    @Test
    void findById_NegativeId_ReturnsBadRequest() throws Exception {
        String token = jwtUtil.encodeAccessToken("user", Role.USER);

        given()
                .header("Authorization", "Bearer " + token)
                .get("/api/v1/bookings/{bookingId}", -5)
                .then()
                .log().body()
                .statusCode(400);
    }


}