package quvoncuz.controller.agency;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.ApiResponse;
import quvoncuz.dto.agency.AgencyFullInfo;
import quvoncuz.dto.agency.UpdateAgencyRequestDTO;
import quvoncuz.service.AgencyService;
import quvoncuz.util.SecurityUtil;

@RestController
@PreAuthorize("hasRole('AGENCY')")
@RequestMapping("/agency/agencies")
@RequiredArgsConstructor
public class AgencyAgencyController {

    private final AgencyService agencyService;

    @PutMapping("/{agencyId}")
    public ResponseEntity<ApiResponse<AgencyFullInfo>> update(
            @PathVariable @Positive(message = "Id must be positive") long agencyId,
            @Valid @RequestBody UpdateAgencyRequestDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(agencyService.update(agencyId, userId, dto)));
    }
}