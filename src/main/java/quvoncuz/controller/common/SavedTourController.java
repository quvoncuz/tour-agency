package quvoncuz.controller.common;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.ApiResponse;
import quvoncuz.dto.tour.SaveTourRequestDTO;
import quvoncuz.dto.tour.TourShortInfo;
import quvoncuz.service.SavedTourService;
import quvoncuz.util.SecurityUtil;

@RestController
@RequestMapping("/saved-tours")
@RequiredArgsConstructor
public class SavedTourController {

    private final SavedTourService savedTourService;

    @PreAuthorize("permitAll()")
    @PostMapping
    public ResponseEntity<Void> saveTour(
            @Valid @RequestBody SaveTourRequestDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        savedTourService.saveTour(dto, userId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("permitAll()")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<TourShortInfo>>> getAllSavedTours(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(savedTourService.getAllSavedTours(userId, page, size)));
    }
}