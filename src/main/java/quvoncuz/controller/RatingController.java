package quvoncuz.controller;

import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/v1/rating")
public class RatingController {

    private final RatingService ratingService;

    @PostMapping("/create")
    public ResponseEntity<RatingFullInfo> create(@RequestBody RatingRequestDTO dto,
                                                 @RequestHeader(value = "X-User-Id") Long userId) {
        return ResponseEntity.ok(ratingService.create(dto, userId));
    }

    @PutMapping("/{ratingId}")
    public ResponseEntity<RatingFullInfo> update(@PathVariable Long ratingId,
                                                 @RequestBody UpdateRatingRequestDTO dto,
                                                 @RequestHeader(value = "X-User-Id") Long userId) {
        return ResponseEntity.ok(ratingService.update(ratingId, dto, userId));
    }

    @DeleteMapping("/{ratingId}")
    public ResponseEntity<Boolean> delete(@PathVariable Long ratingId,
                                          @RequestHeader(value = "X-User-Id") Long userId) {
        return ResponseEntity.ok(ratingService.delete(ratingId, userId));
    }

    @GetMapping("/{sourceId}")
    public ResponseEntity<List<RatingShortInfo>> findBySourceIdAndType(@PathVariable Long sourceId,
                                                                       @RequestParam(defaultValue = "AGENCY") RatingType type,
                                                                       @RequestParam(defaultValue = "1") int page,
                                                                       @RequestParam(defaultValue = "1") int size) {
        return ResponseEntity.ok(ratingService.findBySourceIdAndType(sourceId, type, page, size));
    }

    @GetMapping("/")
    public ResponseEntity<List<RatingShortInfo>> findByUserId(@RequestParam Long userId,
                                                              @RequestParam(defaultValue = "1") int page,
                                                              @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ratingService.findByUserId(userId, page, size));
    }
}
