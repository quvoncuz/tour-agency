package quvoncuz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.tour.CreateTourRequestDTO;
import quvoncuz.dto.tour.TourFullInfo;
import quvoncuz.dto.tour.TourShortInfo;
import quvoncuz.dto.tour.UpdateTourRequestDTO;
import quvoncuz.service.TourService;

import java.util.List;

@Controller
@RequestMapping("/tour")
@RequiredArgsConstructor
public class TourController {

    private final TourService tourService;

    @PostMapping("/create")
    public ResponseEntity<TourFullInfo> createTour(@RequestBody CreateTourRequestDTO dto,
                                                   @RequestHeader(value = "X-User-Id") Long userId) {
        return ResponseEntity.ok(tourService.createTour(dto, userId));
    }

    @PutMapping("/{tourId}")
    public ResponseEntity<TourFullInfo> updateTour(@PathVariable Long tourId,
                                                   @RequestBody UpdateTourRequestDTO dto,
                                                   @RequestHeader(value = "X-User-Id") Long userId) {
        return ResponseEntity.ok(tourService.updateTour(tourId, dto, userId));
    }

    @DeleteMapping("/{tourId}")
    public ResponseEntity<Boolean> delete(@PathVariable Long tourId,
                                          @RequestHeader(value = "X-User-Id") Long ownerId) {
        return ResponseEntity.ok(tourService.deleteTour(tourId, ownerId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<TourShortInfo>> getAllTour() {
        return ResponseEntity.ok(tourService.getAllTour());
    }


    @GetMapping("/all-active")
    public ResponseEntity<List<TourShortInfo>> getAllActiveTour() {
        return ResponseEntity.ok(tourService.getAllActiveTour());
    }

    @GetMapping("/{tourId}")
    public ResponseEntity<TourFullInfo> getById(@PathVariable Long tourId) {
        return ResponseEntity.ok(tourService.getById(tourId));
    }

    @GetMapping("/saved")
    public ResponseEntity<List<TourShortInfo>> getSavedTourId(@RequestHeader(value = "X-User-Id") Long userId,
                                                              @RequestParam(defaultValue = "1") int page,
                                                              @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(tourService.getAllSavedTour(userId, page, size));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TourShortInfo>> search(@RequestParam String query) {
        return ResponseEntity.ok(tourService.search(query));
    }
}
