package quvoncuz.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.booking.*;
import quvoncuz.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingFullInfo> createBooking(
            @Valid @RequestBody CreateBookingRequestDTO dto) {
        return ResponseEntity.ok(bookingService.createBooking(dto));
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Boolean> cancelBooking(
            @Valid @RequestBody CancelBookingRequestDTO dto) {
        return ResponseEntity.ok(bookingService.cancelBooking(dto));
    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<BookingFullInfo> updateBookingSeats(
            @PathVariable long bookingId,
            @Valid @RequestBody UpdateBookingRequestDTO dto) {
        return ResponseEntity.ok(bookingService.updateBookingSeats(bookingId, dto));
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
            @PathVariable long bookingId) {
        return ResponseEntity.ok(bookingService.findById(bookingId));
    }

    @GetMapping({"/by-user", "/by-user/{userId}"})
    public ResponseEntity<Page<BookingShortInfo>> findAllByUserId(
            @PathVariable(required = false) long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookingService.findAllByUserId(userId, page, size));
    }

    @GetMapping("/by-tour/{tourId}")
    public ResponseEntity<Page<BookingShortInfo>> findAllByTourId(
            @PathVariable long tourId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookingService.findAllByTourId(tourId, page, size));
    }

    @GetMapping("/by-agency/{agencyId}")
    public ResponseEntity<Page<BookingShortInfo>> findAllByAgencyId(
            @PathVariable long agencyId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookingService.findAllByAgencyId(agencyId, page, size));
    }
}
