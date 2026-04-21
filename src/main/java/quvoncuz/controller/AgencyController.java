package quvoncuz.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.agency.*;
import quvoncuz.service.AgencyService;

import java.util.List;

@Controller
@RequestMapping("/agencies")
@RequiredArgsConstructor
public class AgencyController {

    private final AgencyService agencyService;

    @PostMapping
    public ResponseEntity<AgencyDTO> applyForAgency(
            @Valid @RequestBody CreateAgencyRequestDTO dto,
            @RequestHeader("X-User-Id") long userId) {
        return ResponseEntity.ok(agencyService.applyForAgency(dto, userId));
    }

    @PostMapping("/approve")
    public ResponseEntity<Boolean> approveAgency(
            @Valid @RequestBody AgencyApproveRequestDTO dto,
            @RequestHeader("X-User-Id") long userId) {
        return ResponseEntity.ok(agencyService.approveAgency(dto, userId));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<AgencyShortInfo>> getPendingAgencies(
            @RequestHeader("X-User-Id") long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(agencyService.getPendingAgencies(userId, page, size));
    }

    @GetMapping("/all")
    public ResponseEntity<List<AgencyFullInfo>> getAllAgencies(@RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(agencyService.getAllAgencies(page, size));
    }

    @PutMapping("/{agencyId}")
    public ResponseEntity<AgencyFullInfo> update(
            @PathVariable long agencyId,
            @Valid @RequestBody UpdateAgencyRequestDTO dto,
            @RequestHeader("X-User-Id") long userId) {
        return ResponseEntity.ok(agencyService.update(agencyId, dto, userId));
    }

    @DeleteMapping("/{agencyId}")
    public ResponseEntity<Boolean> deleteById(
            @PathVariable long agencyId,
            @RequestHeader("X-User-Id") long userId) {
        return ResponseEntity.ok(agencyService.deleteById(agencyId, userId));
    }

    @GetMapping("/{agencyId}")
    public ResponseEntity<AgencyDTO> findById(
            @PathVariable long agencyId) {
        return ResponseEntity.ok(agencyService.findByAgencyId(agencyId));
    }
}