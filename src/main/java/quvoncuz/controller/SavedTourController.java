package quvoncuz.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.tour.SaveTourRequestDTO;
import quvoncuz.dto.tour.TourShortInfo;
import quvoncuz.service.SavedTourService;

@RestController
@RequestMapping("/saved-tours")
@RequiredArgsConstructor
public class SavedTourController {

    private final SavedTourService savedTourService;

    @PostMapping
    public ResponseEntity<Boolean> saveTour(
            @Valid @RequestBody SaveTourRequestDTO dto) {
        return ResponseEntity.ok(savedTourService.saveTour(dto));
    }

    @GetMapping
    public ResponseEntity<Page<TourShortInfo>> getAllSavedTours(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(savedTourService.getAllSavedTours(page, size));
    }
}