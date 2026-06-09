package quvoncuz.controller.admin;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.ApiResponse;
import quvoncuz.dto.profile.ProfileDTO;
import quvoncuz.service.ProfileService;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/profiles")
public class AdminProfileController {

    private final ProfileService profileService;

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteById(
            @PathVariable @Positive(message = "Id must be positive") long userId) {
        profileService.deleteById(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<ProfileDTO>> getProfileById(
            @PathVariable @Positive(message = "Id must be positive") long userId) {
        return ResponseEntity.ok(ApiResponse
                .success(profileService.getProfileById(userId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProfileDTO>>> getAllProfiles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse
                .success(profileService.getAllProfiles(page, size)));
    }
}
