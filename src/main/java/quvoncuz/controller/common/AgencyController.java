package quvoncuz.controller.common;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.ApiResponse;
import quvoncuz.dto.agency.AgencyDTO;
import quvoncuz.dto.agency.CreateAgencyRequestDTO;
import quvoncuz.service.AgencyService;
import quvoncuz.util.SecurityUtil;

@RestController
@RequestMapping("/agencies")
@RequiredArgsConstructor
public class AgencyController {

    private final AgencyService agencyService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<ApiResponse<AgencyDTO>> applyForAgency(
            @Valid @RequestBody CreateAgencyRequestDTO dto) {
        Long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(agencyService.applyForAgency(dto, userId)));
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/{agencyId}")
    public ResponseEntity<ApiResponse<AgencyDTO>> findById(
            @PathVariable @Positive(message = "Id must be positive") long agencyId) {
        return ResponseEntity.ok(ApiResponse.success(agencyService.findByAgencyId(agencyId)));
    }
}