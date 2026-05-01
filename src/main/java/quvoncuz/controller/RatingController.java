package quvoncuz.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<RatingFullInfo> create(
            @Valid @RequestBody RatingRequestDTO dto) {
        return ResponseEntity.ok(ratingService.create(dto));
    }

    @PutMapping("/{ratingId}")
    public ResponseEntity<RatingFullInfo> update(
            @PathVariable @Positive(message = "Id must be positive") long ratingId,
            @Valid @RequestBody UpdateRatingRequestDTO dto) {
        return ResponseEntity.ok(ratingService.update(ratingId, dto));
    }

    @DeleteMapping("/{ratingId}")
    public ResponseEntity<Boolean> delete(
            @PathVariable @Positive(message = "Id must be positive") long ratingId) {
        return ResponseEntity.ok(ratingService.delete(ratingId));
    }

    @GetMapping("/{sourceId}")
    public ResponseEntity<Page<RatingShortInfo>> findBySourceIdAndType(
            @PathVariable @Positive(message = "Id must be positive") long sourceId,
            @RequestParam(defaultValue = "AGENCY") RatingType type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "1") int size) {
        return ResponseEntity.ok(ratingService.findBySourceIdAndType(sourceId, type, page, size));
    }

    @GetMapping("/by-user")
    public ResponseEntity<Page<RatingShortInfo>> findByUserId(
            @RequestParam @Positive(message = "Id must be positive") long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ratingService.findByUserId(userId, page, size));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<RatingShortInfo>> findOwnRatings(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ratingService.findOwnRatings(page, size));
    }
}
