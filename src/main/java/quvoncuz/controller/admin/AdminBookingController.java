package quvoncuz.controller.admin;

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

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/bookings")
@RequiredArgsConstructor
public class AdminBookingController {

    private final BookingService bookingService;


    @GetMapping
    public ResponseEntity<ApiResponse<Page<BookingShortInfo>>> findAll(
            @RequestParam(required = false) long userId,
            @RequestParam(required = false) long tourId,
            @RequestParam(required = false) long agencyId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse
                .success(bookingService.findAllForAdmin(userId, tourId, agencyId, page, size)));
    }
}
