package quvoncuz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.tour.SaveTourRequestDTO;
import quvoncuz.dto.tour.TourShortInfo;
import quvoncuz.service.SavedTourService;

import java.util.List;

@Controller
@RequestMapping("/saved-tour")
@RequiredArgsConstructor
public class SavedTourController {

    private final SavedTourService savedTourService;

    @PostMapping
    public ResponseEntity<Boolean> saveTour(
            @RequestBody SaveTourRequestDTO dto,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(savedTourService.saveTour(dto, userId));
    }

    @GetMapping
    public ResponseEntity<List<TourShortInfo>> getAllSavedTours(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(savedTourService.getAllSavedTours(userId));
    }
}