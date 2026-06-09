package quvoncuz.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.ApiResponse;
import quvoncuz.dto.agency.AgencyApproveRequestDTO;
import quvoncuz.dto.agency.AgencyShortInfo;
import quvoncuz.service.AgencyService;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/agencies")
@RequiredArgsConstructor
public class AdminAgencyController {

    private final AgencyService agencyService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> approveAgency(
            @Valid @RequestBody AgencyApproveRequestDTO dto) {
        agencyService.approveAgency(dto);
        return ResponseEntity.ok(ApiResponse.success(dto.getApprove() ? "Successfully accepted" : "Successfully rejected"));
    }

    @GetMapping // richardson maturity model
    public ResponseEntity<ApiResponse<Page<AgencyShortInfo>>> getAllAgencies(
            @RequestParam(defaultValue = "false") boolean pending,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse
                .success(agencyService.getAllAgencies(pending, page, size)));
    }

    @DeleteMapping("/{agencyId}")
    public ResponseEntity<Void> deleteById(
            @PathVariable @Positive(message = "Id must be positive") long agencyId) {
        agencyService.deleteById(agencyId);
        return ResponseEntity.noContent().build();
    }
}