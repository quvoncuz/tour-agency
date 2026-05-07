package quvoncuz.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.tour.CreateTourRequestDTO;
import quvoncuz.dto.tour.TourFullInfo;
import quvoncuz.dto.tour.TourShortInfo;
import quvoncuz.dto.tour.UpdateTourRequestDTO;
import quvoncuz.service.TourService;

@RestController
@RequestMapping("/tours")
@RequiredArgsConstructor
public class TourController {

    private final TourService tourService;

    @PreAuthorize("hasRole('AGENCY')")
    @PostMapping
    public ResponseEntity<TourFullInfo> createTour(
            @Valid @RequestBody CreateTourRequestDTO dto) {
        return ResponseEntity.ok(tourService.createTour(dto));
    }

    @PreAuthorize("hasRole('AGENCY')")
    @PutMapping("/{tourId}")
    public ResponseEntity<TourFullInfo> updateTour(
            @PathVariable @Positive(message = "Id must be positive") long tourId,
            @Valid @RequestBody UpdateTourRequestDTO dto) {
        return ResponseEntity.ok(tourService.updateTour(tourId, dto));
    }

    @PreAuthorize("hasRole('AGENCY')")
    @PutMapping("/{tourId}/update")
    public ResponseEntity<TourFullInfo> updateTourPrice(
            @PathVariable @Positive(message = "Id must be positive") long tourId,
            @RequestBody @Positive(message = "Id must be positive") long price) {
        return ResponseEntity.ok(tourService.updateTourPrice(tourId, price));
    }

    @PreAuthorize("hasRole('AGENCY')")
    @DeleteMapping("/{tourId}")
    public ResponseEntity<Boolean> delete(
            @PathVariable @Positive(message = "Id must be positive") long tourId) {
        return ResponseEntity.ok(tourService.deleteTour(tourId));
    }

    @PreAuthorize("hasAnyRole('AGENCY', 'ADMIN')")
    @GetMapping("/all-by")
    public ResponseEntity<Page<TourShortInfo>> getAllTour(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(tourService.getAllTour(page, size));
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/all")
    public ResponseEntity<Page<TourShortInfo>> getAllActiveTour(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(tourService.getAllActiveTour(page, size));
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/{tourId}")
    public ResponseEntity<TourFullInfo> getById(
            @PathVariable @Positive(message = "Id must be positive") long tourId) {
        return ResponseEntity.ok(tourService.getById(tourId));
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/saved")
    public ResponseEntity<Page<TourShortInfo>> getSavedTourId(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(tourService.getAllSavedTours(page, size));
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/search")
    public ResponseEntity<Page<TourShortInfo>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(tourService.search(query, page, size));
    }
}
