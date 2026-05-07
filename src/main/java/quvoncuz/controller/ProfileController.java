package quvoncuz.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.profile.ProfileDTO;
import quvoncuz.dto.profile.ProfileFullInfo;
import quvoncuz.dto.profile.UpdateProfileRequestDTO;
import quvoncuz.service.ProfileService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileService profileService;

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Boolean> deleteById(
            @PathVariable @Positive(message = "Id must be positive") long userId) {
        return ResponseEntity.ok(profileService.deleteById(userId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{userId}")
    public ResponseEntity<ProfileDTO> getProfileById(
            @PathVariable @Positive(message = "Id must be positive") long userId) {
        return ResponseEntity.ok(profileService.getProfileById(userId));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/{profileId}")
    public ResponseEntity<ProfileFullInfo> update(
            @Valid @RequestBody UpdateProfileRequestDTO dto,
            @PathVariable @Positive(message = "Id must be positive") long profileId) {
        return ResponseEntity.ok(profileService.updateProfile(dto, profileId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<Page<ProfileDTO>> getAllProfiles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(profileService.getAllProfiles(page, size));
    }
}
