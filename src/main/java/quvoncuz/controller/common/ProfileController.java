package quvoncuz.controller.common;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.ApiResponse;
import quvoncuz.dto.profile.ProfileDTO;
import quvoncuz.dto.profile.ProfileFullInfo;
import quvoncuz.dto.profile.UpdateProfileRequestDTO;
import quvoncuz.service.ProfileService;
import quvoncuz.util.SecurityUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<ProfileDTO>> getOwnProfile() {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(profileService.getProfileById(userId)));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<ProfileFullInfo>> update(
            @Valid @RequestBody UpdateProfileRequestDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(profileService.updateProfile(dto, userId)));
    }
}
