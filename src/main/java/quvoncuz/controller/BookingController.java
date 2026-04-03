package quvoncuz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.booking.BookingFullInfo;
import quvoncuz.dto.booking.BookingShortInfo;
import quvoncuz.dto.booking.CreateBookingRequestDTO;
import quvoncuz.dto.booking.UpdateBookingRequestDTO;
import quvoncuz.service.BookingService;

import java.util.List;

@Controller
@RequestMapping("booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/create")
    public ResponseEntity<BookingFullInfo> createBooking(@RequestBody CreateBookingRequestDTO dto, Long userId) {
        return ResponseEntity.ok(bookingService.createBooking(dto, userId));
    }

    @GetMapping({"/by-user", "/by-user/{userId}"})
    public ResponseEntity<List<BookingShortInfo>> findAllByUserId(@PathVariable(required = false) Long userId,
                                                                  @RequestHeader(value = "X-User-Id") Long headerUserId,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookingService.findAllByUserId(userId, headerUserId, page, size));
    }

    @GetMapping("/by-tour/{tourId}")
    public ResponseEntity<List<BookingShortInfo>> findAllByTourId(@PathVariable Long tourId,
                                                                  @RequestHeader(value = "X-User-Id") Long userId,
                                                                  @RequestParam int page,
                                                                  @RequestParam int size) {
        return ResponseEntity.ok(bookingService.findAllByTourId(tourId, userId, page, size));
    }

    @GetMapping("/by-agency/{agencyId}")
    public ResponseEntity<List<BookingShortInfo>> findAllByAgencyId(@PathVariable Long agencyId,
                                                                    @RequestHeader(value = "X-User-Id") Long userId,
                                                                    @RequestParam int page,
                                                                    @RequestParam int size) {
        return ResponseEntity.ok(bookingService.findAllByAgencyId(agencyId, userId, page, size));
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Boolean> cancelBooking(@PathVariable Long bookingId,
                                                 @RequestHeader(value = "X-User-Id") Long userId) {
        return ResponseEntity.ok(bookingService.cancelBooking(bookingId, userId));
    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<BookingFullInfo> updateBookingSeats(@PathVariable Long bookingId,
                                                              @RequestBody UpdateBookingRequestDTO dto,
                                                              @RequestHeader(value = "X-User-Id") Long userId) {
        return ResponseEntity.ok(bookingService.updateBookingSeats(bookingId, dto, userId));
    }
}
