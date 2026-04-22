package quvoncuz.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.tour.SaveTourRequestDTO;
import quvoncuz.dto.tour.TourShortInfo;
import quvoncuz.service.SavedTourService;

import java.util.List;

@Controller
@RequestMapping("/saved-tours")
@RequiredArgsConstructor
public class SavedTourController {

    private final SavedTourService savedTourService;

    @PostMapping
    public ResponseEntity<Boolean> saveTour(
            @Valid @RequestBody SaveTourRequestDTO dto,
            @RequestHeader("X-User-Id") long userId) {
        return ResponseEntity.ok(savedTourService.saveTour(dto, userId));
    }

    @GetMapping
    public ResponseEntity<Page<TourShortInfo>> getAllSavedTours(
            @RequestHeader("X-User-Id") long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(savedTourService.getAllSavedTours(userId, page, size));
    }
}