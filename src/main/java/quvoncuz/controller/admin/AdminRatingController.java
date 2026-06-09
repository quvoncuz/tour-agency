package quvoncuz.controller.admin;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.ApiResponse;
import quvoncuz.dto.rating.RatingShortInfo;
import quvoncuz.service.RatingService;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/ratings")
public class AdminRatingController {

    private final RatingService ratingService;

    @DeleteMapping("/{ratingId}")
    public ResponseEntity<Void> delete(
            @PathVariable @Positive(message = "Id must be positive") long ratingId) {
        ratingService.delete(ratingId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<RatingShortInfo>>> findByUserId(
            @RequestParam @Positive(message = "Id must be positive") long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(ratingService.findByUserId(userId, page, size)));
    }
}
