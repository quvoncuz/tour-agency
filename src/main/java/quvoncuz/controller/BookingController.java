package quvoncuz.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.booking.*;
import quvoncuz.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PreAuthorize("hasAnyRole('AGENCY', 'USER')")
    @PostMapping
    public ResponseEntity<BookingFullInfo> createBooking(
            @Valid @RequestBody CreateBookingRequestDTO dto) {
        return ResponseEntity.ok(bookingService.createBooking(dto));
    }

    @PreAuthorize("hasAnyRole('AGENCY', 'USER')")
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Boolean> cancelBooking(
            @Valid @RequestBody CancelBookingRequestDTO dto) {
        return ResponseEntity.ok(bookingService.cancelBooking(dto));
    }

    @PreAuthorize("hasAnyRole('AGENCY', 'USER')")
    @PutMapping("/{bookingId}")
    public ResponseEntity<BookingFullInfo> updateBookingSeats(
            @PathVariable @Positive(message = "Id must be positive") long bookingId,
            @Valid @RequestBody UpdateBookingRequestDTO dto) {
        return ResponseEntity.ok(bookingService.updateBookingSeats(bookingId, dto));
    }

    @PreAuthorize("hasAnyRole('AGENCY', 'USER')")
    @GetMapping("/updated")
    public ResponseEntity<List<BookingFullInfo>> getUpdatedBooking() {
        return ResponseEntity.ok(bookingService.getUpdatedBooking());
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{bookingId}/confirm")
    public ResponseEntity<BookingFullInfo> confirmUpdatedBooking(
            @PathVariable @Positive(message = "Id must be positive") long bookingId) {
        return ResponseEntity.ok(bookingService.confirmUpdatedBooking(bookingId));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingFullInfo> cancelUpdateBooking(
            @PathVariable @Positive(message = "Id must be positive") long bookingId) {
        return ResponseEntity.ok(bookingService.cancelUpdateBooking(bookingId));
    }

    @PreAuthorize("hasAnyRole('AGENCY', 'USER')")
    @GetMapping("{bookingId}")
    public ResponseEntity<BookingFullInfo> findById(
            @PathVariable @Positive(message = "Id must be positive") long bookingId) {
        return ResponseEntity.ok(bookingService.findFullInfoById(bookingId));
    }

    @PreAuthorize("hasAnyRole('AGENCY', 'USER')")
    @GetMapping("/all")
    public ResponseEntity<Page<BookingShortInfo>> findAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookingService.findAllByUser(page, size));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping({"/by-user", "/by-user/{userId}"})
    public ResponseEntity<Page<BookingShortInfo>> findAllByUserId(
            @PathVariable(required = false) @Positive(message = "Id must be positive") long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookingService.findAllByUserId(userId, page, size));
    }

    @PreAuthorize("hasAnyRole('AGENCY', 'ADMIN')")
    @GetMapping("/by-tour/{tourId}")
    public ResponseEntity<Page<BookingShortInfo>> findAllByTourId(
            @PathVariable @Positive(message = "Id must be positive") long tourId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookingService.findAllByTourId(tourId, page, size));
    }

    @PreAuthorize("hasAnyRole('AGENCY', 'ADMIN')")
    @GetMapping("/by-agency/{agencyId}")
    public ResponseEntity<Page<BookingShortInfo>> findAllByAgencyId(
            @PathVariable @Positive(message = "Id must be positive") long agencyId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookingService.findAllByAgencyId(agencyId, page, size));
    }
}
