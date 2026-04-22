package quvoncuz.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.rating.RatingFullInfo;
import quvoncuz.dto.rating.RatingRequestDTO;
import quvoncuz.dto.rating.RatingShortInfo;
import quvoncuz.dto.rating.UpdateRatingRequestDTO;
import quvoncuz.enums.RatingType;
import quvoncuz.service.RatingService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/ratings")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<RatingFullInfo> create(
            @Valid @RequestBody RatingRequestDTO dto,
            @RequestHeader(value = "X-User-Id") long userId) {
        return ResponseEntity.ok(ratingService.create(dto, userId));
    }

    @PutMapping("/{ratingId}")
    public ResponseEntity<RatingFullInfo> update(
            @PathVariable long ratingId,
            @Valid @RequestBody UpdateRatingRequestDTO dto,
            @RequestHeader(value = "X-User-Id") long userId) {
        return ResponseEntity.ok(ratingService.update(ratingId, dto, userId));
    }

    @DeleteMapping("/{ratingId}")
    public ResponseEntity<Boolean> delete(
            @PathVariable long ratingId,
            @RequestHeader(value = "X-User-Id") long userId) {
        return ResponseEntity.ok(ratingService.delete(ratingId, userId));
    }

    @GetMapping("/{sourceId}")
    public ResponseEntity<Page<RatingShortInfo>> findBySourceIdAndType(
            @PathVariable long sourceId,
            @RequestParam(defaultValue = "AGENCY") RatingType type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "1") int size) {
        return ResponseEntity.ok(ratingService.findBySourceIdAndType(sourceId, type, page, size));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<RatingShortInfo>> findByUserId(
            @RequestParam long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ratingService.findByUserId(userId, page, size));
    }
}
