package quvoncuz.controller.agency;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.ApiResponse;
import quvoncuz.dto.tour.*;
import quvoncuz.service.TourService;
import quvoncuz.util.SecurityUtil;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('AGENCY')")
@RequestMapping("/agency/tours")
public class AgencyTourController {

    private final TourService tourService;

    @PostMapping
    public ResponseEntity<ApiResponse<TourFullInfo>> createTour(
            @Valid @RequestBody CreateTourRequestDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(tourService.createTour(dto, userId)));
    }

    @PutMapping("/{tourId}")
    public ResponseEntity<ApiResponse<TourFullInfo>> updateTour(
            @PathVariable @Positive(message = "Id must be positive") long tourId,
            @Valid @RequestBody UpdateTourRequestDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(tourService.updateTour(tourId, dto, userId)));
    }

    @DeleteMapping("/{tourId}")
    public ResponseEntity<Void> delete(
            @PathVariable @Positive(message = "Id must be positive") long tourId) {
        Long userId = SecurityUtil.getCurrentUserId();
        tourService.deleteTour(tourId, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> cancelTour(
            @PathVariable long id,
            @RequestBody @Valid CancelTourDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        tourService.cancelTour(id, dto, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TourShortInfo>>> getAllTour(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(tourService.getAllTourForAgency(userId, page, size)));
    }
}
