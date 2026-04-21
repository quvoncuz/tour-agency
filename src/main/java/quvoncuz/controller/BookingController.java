package quvoncuz.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.booking.*;
import quvoncuz.service.BookingService;

import java.util.List;

@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingFullInfo> createBooking(
            @Valid @RequestBody CreateBookingRequestDTO dto,
            @RequestHeader(value = "X-User-Id") long userId) {
        return ResponseEntity.ok(bookingService.createBooking(dto, userId));
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Boolean> cancelBooking(
            @Valid @RequestBody CancelBookingRequestDTO dto,
            @RequestHeader(value = "X-User-Id") long userId) {
        return ResponseEntity.ok(bookingService.cancelBooking(dto, userId));
    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<BookingFullInfo> updateBookingSeats(
            @PathVariable long bookingId,
            @Valid @RequestBody UpdateBookingRequestDTO dto,
            @RequestHeader(value = "X-User-Id") long userId) {
        return ResponseEntity.ok(bookingService.updateBookingSeats(bookingId, dto, userId));
    }

    @GetMapping("/updated")
    public ResponseEntity<List<BookingFullInfo>> getUpdatedBooking(@RequestHeader(value = "X-User-Id") long userId) {
        return ResponseEntity.ok(bookingService.getUpdatedBooking(userId));
    }

    @PostMapping("/{bookingId}/confirm")
    public ResponseEntity<BookingFullInfo> confirmUpdatedBooking(@PathVariable long bookingId,
                                                                 @RequestHeader(value = "X-User-Id") long userId) {
        return ResponseEntity.ok(bookingService.confirmUpdatedBooking(bookingId, userId));
    }
    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingFullInfo> cancelUpdateBooking(@PathVariable long bookingId,
                                                                 @RequestHeader(value = "X-User-Id") long userId) {
        return ResponseEntity.ok(bookingService.cancelUpdateBooking(bookingId, userId));
    }

    @GetMapping("{bookingId}")
    public ResponseEntity<BookingFullInfo> findById(
            @PathVariable long bookingId,
            @RequestHeader(value = "X-User-Id") long userId) {
        return ResponseEntity.ok(bookingService.findById(bookingId, userId));
    }

    @GetMapping({"/by-user", "/by-user/{userId}"})
    public ResponseEntity<List<BookingShortInfo>> findAllByUserId(
            @PathVariable(required = false) long userId,
            @RequestHeader(value = "X-User-Id") long loginId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookingService.findAllByUserId(userId, loginId, page, size));
    }

    @GetMapping("/by-tour/{tourId}")
    public ResponseEntity<List<BookingShortInfo>> findAllByTourId(
            @PathVariable long tourId,
            @RequestHeader(value = "X-User-Id") long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookingService.findAllByTourId(tourId, userId, page, size));
    }

    @GetMapping("/by-agency/{agencyId}")
    public ResponseEntity<List<BookingShortInfo>> findAllByAgencyId(
            @PathVariable long agencyId,
            @RequestHeader(value = "X-User-Id") long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookingService.findAllByAgencyId(agencyId, userId, page, size));
    }
}
