package quvoncuz.controller.common;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.ApiResponse;
import quvoncuz.dto.booking.*;
import quvoncuz.service.BookingService;
import quvoncuz.util.SecurityUtil;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PreAuthorize("hasAnyRole('AGENCY', 'USER')")
    @PostMapping
    public ResponseEntity<ApiResponse<BookingFullInfo>> createBooking(
            @Valid @RequestBody CreateBookingRequestDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(bookingService.createBooking(dto, userId)));
    }

    @PreAuthorize("hasAnyRole('AGENCY', 'USER')")
    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> cancelBooking(
            @Valid @RequestBody CancelBookingRequestDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        bookingService.cancelBooking(dto, userId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('AGENCY', 'USER')")
    @PutMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<BookingFullInfo>> updateBookingSeats(
            @PathVariable @Positive(message = "Id must be positive") long bookingId,
            @Valid @RequestBody UpdateBookingRequestDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(bookingService.updateBookingSeats(bookingId, dto, userId)));
    }

    @PreAuthorize("hasAnyRole('AGENCY', 'USER')")
    @GetMapping("/updated")
    public ResponseEntity<ApiResponse<List<BookingFullInfo>>> getUpdatedBooking() {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(bookingService.getUpdatedBooking(userId)));
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<BookingFullInfo>> confirmUpdatedBooking(
            @PathVariable @Positive(message = "Id must be positive") long bookingId,
            @RequestBody ConfirmBookingDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(bookingService.confirmUpdatedBooking(bookingId, dto, userId)));
    }

    @PreAuthorize("hasAnyRole('AGENCY', 'USER')")
    @GetMapping("{bookingId}")
    public ResponseEntity<ApiResponse<BookingFullInfo>> findById(
            @PathVariable @Positive(message = "Id must be positive") long bookingId) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(bookingService.findFullInfoById(bookingId, userId)));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<BookingShortInfo>>> findAllByUserId(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(bookingService.findAllByUserId(userId, page, size)));
    }
}
