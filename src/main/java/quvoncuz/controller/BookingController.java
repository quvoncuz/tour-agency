package quvoncuz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.booking.*;
import quvoncuz.service.BookingService;

@RestController
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/create")
    public ResponseEntity<BookingFullInfo> createBooking(@RequestBody CreateBookingRequestDTO dto,
                                                         @RequestHeader(value = "X-User-Id") Long userId) {
        return ResponseEntity.ok(bookingService.createBooking(dto, userId));
    }

    @GetMapping({"/by-user", "/by-user/{userId}"})
    public ResponseEntity<Page<BookingShortInfo>> findAllByUserId(@PathVariable(required = false) Long userId,
                                                                  @RequestHeader(value = "X-User-Id") Long loginId,
                                                                  @RequestParam(defaultValue = "1") int page,
                                                                  @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookingService.findAllByUserId(userId, loginId, page, size));
    }

    @GetMapping("/by-tour/{tourId}")
    public ResponseEntity<Page<BookingShortInfo>> findAllByTourId(@PathVariable Long tourId,
                                                                  @RequestHeader(value = "X-User-Id") Long userId,
                                                                  @RequestParam(defaultValue = "1") int page,
                                                                  @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookingService.findAllByTourId(tourId, userId, page, size));
    }

    @GetMapping("/by-agency/{agencyId}")
    public ResponseEntity<Page<BookingShortInfo>> findAllByAgencyId(@PathVariable Long agencyId,
                                                                    @RequestHeader(value = "X-User-Id") Long userId,
                                                                    @RequestParam(defaultValue = "1") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookingService.findAllByAgencyId(agencyId, userId, page, size));
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Boolean> cancelBooking(@RequestBody CancelBookingRequestDTO dto,
                                                 @RequestHeader(value = "X-User-Id") Long userId) {
        return ResponseEntity.ok(bookingService.cancelBooking(dto, userId));
    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<BookingFullInfo> updateBookingSeats(@PathVariable Long bookingId,
                                                              @RequestBody UpdateBookingRequestDTO dto,
                                                              @RequestHeader(value = "X-User-Id") Long userId) {
        return ResponseEntity.ok(bookingService.updateBookingSeats(bookingId, dto, userId));
    }
}
