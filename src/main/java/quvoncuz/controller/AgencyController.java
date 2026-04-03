package quvoncuz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.agency.*;
import quvoncuz.service.AgencyService;

import java.util.List;

@Controller
@RequestMapping("/agency")
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
    public ResponseEntity<List<AgencyShortInfo>> getPendingAgencies(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(agencyService.getPendingAgencies(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<AgencyFullInfo>> getAllAgencies() {
        return ResponseEntity.ok(agencyService.getAllAgencies());
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
        return ResponseEntity.ok(agencyService.findById(agencyId));
    }
}