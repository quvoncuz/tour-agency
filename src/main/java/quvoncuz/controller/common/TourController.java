package quvoncuz.controller.common;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.ApiResponse;
import quvoncuz.dto.tour.TourFullInfo;
import quvoncuz.dto.tour.TourShortInfo;
import quvoncuz.service.SavedTourService;
import quvoncuz.service.TourService;
import quvoncuz.util.SecurityUtil;

@RestController
@RequestMapping("/tours")
@RequiredArgsConstructor
public class TourController {

    private final TourService tourService;
    private final SavedTourService savedTourService;

    @PreAuthorize("permitAll()")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TourShortInfo>>> getAllActiveTour(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(tourService.getAllActiveTour(page, size)));
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/{tourId}")
    public ResponseEntity<ApiResponse<TourFullInfo>> getById(
            @PathVariable @Positive(message = "Id must be positive") long tourId) {
        return ResponseEntity.ok(ApiResponse.success(tourService.getById(tourId)));
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/favorite")
    public ResponseEntity<ApiResponse<Page<TourShortInfo>>> getSavedTourId(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(savedTourService.getAllSavedTours(userId, page, size)));
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<TourShortInfo>>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(tourService.search(query, page, size)));
    }
}
