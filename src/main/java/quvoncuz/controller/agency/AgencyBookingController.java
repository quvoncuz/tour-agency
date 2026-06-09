package quvoncuz.controller.agency;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import quvoncuz.dto.ApiResponse;
import quvoncuz.dto.booking.BookingShortInfo;
import quvoncuz.service.BookingService;
import quvoncuz.util.SecurityUtil;

@RestController
@PreAuthorize("hasRole('AGENCY')")
@RequestMapping("/agency/bookings")
@RequiredArgsConstructor
public class AgencyBookingController {

    private final BookingService bookingService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BookingShortInfo>>> findAllByAgency(
            @RequestParam(required = false) @Positive(message = "Id must be positive") long tourId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(bookingService.findAllForAgency(tourId, userId, page, size)));
    }
}
