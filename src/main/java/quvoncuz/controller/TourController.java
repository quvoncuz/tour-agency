package quvoncuz.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.tour.CreateTourRequestDTO;
import quvoncuz.dto.tour.TourFullInfo;
import quvoncuz.dto.tour.TourShortInfo;
import quvoncuz.dto.tour.UpdateTourRequestDTO;
import quvoncuz.service.TourService;

@Controller
@RequestMapping("/tours")
@RequiredArgsConstructor
public class TourController {

    private final TourService tourService;

    @PostMapping
    public ResponseEntity<TourFullInfo> createTour(
            @Valid @RequestBody CreateTourRequestDTO dto,
            @RequestHeader(value = "X-User-Id") long userId) {
        return ResponseEntity.ok(tourService.createTour(dto, userId));
    }

    @PutMapping("/{tourId}")
    public ResponseEntity<TourFullInfo> updateTour(
            @PathVariable long tourId,
            @Valid @RequestBody UpdateTourRequestDTO dto,
            @RequestHeader(value = "X-User-Id") long userId) {
        return ResponseEntity.ok(tourService.updateTour(tourId, dto, userId));
    }

    @PutMapping("/{tourId}/update")
    public ResponseEntity<TourFullInfo> updateTourPrice(@PathVariable long tourId,
                                                        @RequestBody long price,
                                                        @RequestHeader(value = "X-User-Id") long userId) {
        return ResponseEntity.ok(tourService.updateTourPrice(tourId, price, userId));
    }

    @DeleteMapping("/{tourId}")
    public ResponseEntity<Boolean> delete(
            @PathVariable long tourId,
            @RequestHeader(value = "X-User-Id") long ownerId) {
        return ResponseEntity.ok(tourService.deleteTour(tourId, ownerId));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<TourShortInfo>> getAllTour(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(tourService.getAllTour(page, size));
    }


    @GetMapping("/all-active")
    public ResponseEntity<Page<TourShortInfo>> getAllActiveTour(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(tourService.getAllActiveTour(page, size));
    }

    @GetMapping("/{tourId}")
    public ResponseEntity<TourFullInfo> getById(
            @PathVariable long tourId) {
        return ResponseEntity.ok(tourService.getById(tourId));
    }

    @GetMapping("/saved")
    public ResponseEntity<Page<TourShortInfo>> getSavedTourId(
            @RequestHeader(value = "X-User-Id") long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(tourService.getAllSavedTours(userId, page, size));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<TourShortInfo>> search(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(tourService.search(query, page, size));
    }
}
