package quvoncuz.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.profile.ProfileDTO;
import quvoncuz.dto.profile.ProfileFullInfo;
import quvoncuz.dto.profile.UpdateProfileRequestDTO;
import quvoncuz.service.ProfileService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileService profileService;

    @DeleteMapping("/{userId}")
    public ResponseEntity<Boolean> deleteById(
            @PathVariable long userId,
            @RequestHeader(value = "X-User-Id") long adminId) {
        return ResponseEntity.ok(profileService.deleteById(userId, adminId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ProfileDTO> getProfileById(
            @PathVariable long userId,
            @RequestHeader(value = "X-User-Id") long adminId) {
        return ResponseEntity.ok(profileService.getProfileById(userId, adminId));
    }

    @PutMapping("/{profileId}")
    public ResponseEntity<ProfileFullInfo> update(@Valid @RequestBody UpdateProfileRequestDTO dto,
                                                  @PathVariable long profileId,
                                                  @RequestHeader(value = "X-User-Id") long userId){
        return ResponseEntity.ok(profileService.updateProfile(dto, profileId, userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProfileDTO>> getAllProfiles(
            @RequestHeader(value = "X-User-Id") long adminId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(profileService.getAllProfiles(adminId, page, size));
    }
}
