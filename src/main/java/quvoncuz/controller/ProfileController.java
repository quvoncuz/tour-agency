package quvoncuz.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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

    @DeleteMapping("/{userId}")
    public ResponseEntity<Boolean> deleteById(
            @PathVariable long userId) {
        return ResponseEntity.ok(profileService.deleteById(userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ProfileDTO> getProfileById(
            @PathVariable long userId) {
        return ResponseEntity.ok(profileService.getProfileById(userId));
    }

    @PutMapping("/{profileId}")
    public ResponseEntity<ProfileFullInfo> update(@Valid @RequestBody UpdateProfileRequestDTO dto,
                                                  @PathVariable long profileId) {
        return ResponseEntity.ok(profileService.updateProfile(dto, profileId));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<ProfileDTO>> getAllProfiles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(profileService.getAllProfiles(page, size));
    }
}
