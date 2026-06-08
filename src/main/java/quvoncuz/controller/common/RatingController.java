package quvoncuz.controller.common;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.ApiResponse;
import quvoncuz.dto.rating.RatingFullInfo;
import quvoncuz.dto.rating.RatingRequestDTO;
import quvoncuz.dto.rating.RatingShortInfo;
import quvoncuz.dto.rating.UpdateRatingRequestDTO;
import quvoncuz.enums.RatingType;
import quvoncuz.service.RatingService;
import quvoncuz.util.SecurityUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ratings")
public class RatingController {

    private final RatingService ratingService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<ApiResponse<RatingFullInfo>> create(
            @Valid @RequestBody RatingRequestDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(ratingService.create(dto, userId)));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{ratingId}")
    public ResponseEntity<ApiResponse<RatingFullInfo>> update(
            @PathVariable @Positive(message = "Id must be positive") long ratingId,
            @Valid @RequestBody UpdateRatingRequestDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(ratingService.update(ratingId, dto, userId)));
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/{ratingId}")
    public ResponseEntity<Void> delete(
            @PathVariable @Positive(message = "Id must be positive") long ratingId) {
        Long userId = SecurityUtil.getCurrentUserId();
        ratingService.deleteOwnRating(ratingId, userId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/{sourceId}")
    public ResponseEntity<ApiResponse<Page<RatingShortInfo>>> findBySourceIdAndType(
            @PathVariable @Positive(message = "Id must be positive") long sourceId,
            @RequestParam(defaultValue = "AGENCY") RatingType type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(ratingService.findBySourceIdAndType(sourceId, type, page, size)));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<RatingShortInfo>>> findOwnRatings(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(ratingService.findOwnRatings(userId, page, size)));
    }
}
