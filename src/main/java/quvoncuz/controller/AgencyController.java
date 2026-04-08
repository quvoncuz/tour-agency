package quvoncuz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.agency.*;
import quvoncuz.service.AgencyService;

@RestController
@RequestMapping("/api/v1/agency")
@RequiredArgsConstructor
public class AgencyController {

    private final AgencyService agencyService;

    @PostMapping("/apply")
    public ResponseEntity<AgencyDTO> applyForAgency(
            @RequestBody CreateAgencyRequestDTO dto,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(agencyService.applyForAgency(dto, userId));
    }

    @PostMapping("/approve")
    public ResponseEntity<Boolean> approveAgency(
            @RequestBody AgencyApproveRequestDTO dto,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(agencyService.approveAgency(dto, userId));
    }

    @GetMapping("/pending")
    public ResponseEntity<Page<AgencyShortInfo>> getPendingAgencies(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(agencyService.getPendingAgencies(userId, page, size));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<AgencyFullInfo>> getAllAgencies(@RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(agencyService.getAllAgencies(page, size));
    }

    @PutMapping("/{agencyId}")
    public ResponseEntity<AgencyFullInfo> update(
            @PathVariable Long agencyId,
            @RequestBody UpdateAgencyRequestDTO dto,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(agencyService.update(agencyId, dto, userId));
    }

    @DeleteMapping("/{agencyId}")
    public ResponseEntity<Boolean> deleteById(
            @PathVariable Long agencyId,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(agencyService.deleteById(agencyId, userId));
    }

    @GetMapping("/{agencyId}")
    public ResponseEntity<AgencyDTO> findById(
            @PathVariable Long agencyId) {
        return ResponseEntity.ok(agencyService.findByAgencyId(agencyId));
    }
}