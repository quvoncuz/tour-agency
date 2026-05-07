package quvoncuz.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.rating.RatingFullInfo;
import quvoncuz.dto.rating.RatingRequestDTO;
import quvoncuz.dto.rating.RatingShortInfo;
import quvoncuz.dto.rating.UpdateRatingRequestDTO;
import quvoncuz.enums.RatingType;
import quvoncuz.service.RatingService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ratings")
public class RatingController {

    private final RatingService ratingService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<RatingFullInfo> create(
            @Valid @RequestBody RatingRequestDTO dto) {
        return ResponseEntity.ok(ratingService.create(dto));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{ratingId}")
    public ResponseEntity<RatingFullInfo> update(
            @PathVariable @Positive(message = "Id must be positive") long ratingId,
            @Valid @RequestBody UpdateRatingRequestDTO dto) {
        return ResponseEntity.ok(ratingService.update(ratingId, dto));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{ratingId}")
    public ResponseEntity<Boolean> delete(
            @PathVariable @Positive(message = "Id must be positive") long ratingId) {
        return ResponseEntity.ok(ratingService.delete(ratingId));
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/{sourceId}")
    public ResponseEntity<Page<RatingShortInfo>> findBySourceIdAndType(
            @PathVariable @Positive(message = "Id must be positive") long sourceId,
            @RequestParam(defaultValue = "AGENCY") RatingType type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "1") int size) {
        return ResponseEntity.ok(ratingService.findBySourceIdAndType(sourceId, type, page, size));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/by-user")
    public ResponseEntity<Page<RatingShortInfo>> findByUserId(
            @RequestParam @Positive(message = "Id must be positive") long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ratingService.findByUserId(userId, page, size));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/all")
    public ResponseEntity<Page<RatingShortInfo>> findOwnRatings(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ratingService.findOwnRatings(page, size));
    }
}
