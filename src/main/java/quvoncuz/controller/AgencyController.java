package quvoncuz.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.agency.*;
import quvoncuz.service.AgencyService;

@RestController
@RequestMapping("/agencies")
@RequiredArgsConstructor
public class AgencyController {

    private final AgencyService agencyService;

    @PostMapping
    public ResponseEntity<AgencyDTO> applyForAgency(
            @Valid @RequestBody CreateAgencyRequestDTO dto) {
        return ResponseEntity.ok(agencyService.applyForAgency(dto));
    }

    @PostMapping("/approve")
    public ResponseEntity<Boolean> approveAgency(
            @Valid @RequestBody AgencyApproveRequestDTO dto) {
        return ResponseEntity.ok(agencyService.approveAgency(dto));
    }

    @GetMapping("/pending")
    public ResponseEntity<Page<AgencyShortInfo>> getPendingAgencies(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(agencyService.getPendingAgencies(page, size));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<AgencyFullInfo>> getAllAgencies(@RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(agencyService.getAllAgencies(page, size));
    }

    @PutMapping("/{agencyId}")
    public ResponseEntity<AgencyFullInfo> update(
            @PathVariable long agencyId,
            @Valid @RequestBody UpdateAgencyRequestDTO dto) {
        return ResponseEntity.ok(agencyService.update(agencyId, dto));
    }

    @DeleteMapping("/{agencyId}")
    public ResponseEntity<Boolean> deleteById(
            @PathVariable long agencyId) {
        return ResponseEntity.ok(agencyService.deleteById(agencyId));
    }

    @GetMapping("/{agencyId}")
    public ResponseEntity<AgencyDTO> findById(
            @PathVariable long agencyId) {
        return ResponseEntity.ok(agencyService.findByAgencyId(agencyId));
    }
}