package quvoncuz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.profile.ProfileDTO;
import quvoncuz.service.ProfileService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    @DeleteMapping("/{userId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable Long userId,
                                              @RequestHeader(value = "X-User-Id") Long adminId) {
        return ResponseEntity.ok(profileService.deleteById(userId, adminId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ProfileDTO> getProfileById(@PathVariable Long userId,
                                                     @RequestHeader(value = "X-User-Id") Long adminId) {
        return ResponseEntity.ok(profileService.getProfileById(userId, adminId));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<ProfileDTO>> getAllProfiles(@RequestHeader(value = "X-User-Id") Long adminId,
                                                           @RequestParam(defaultValue = "1") int page,
                                                           @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(profileService.getAllProfiles(adminId, page, size));
    }
}
