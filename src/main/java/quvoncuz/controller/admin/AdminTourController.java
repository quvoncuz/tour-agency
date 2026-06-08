package quvoncuz.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import quvoncuz.dto.ApiResponse;
import quvoncuz.dto.tour.TourShortInfo;
import quvoncuz.service.TourService;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/tours")
@RequiredArgsConstructor
public class AdminTourController {

    private final TourService tourService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TourShortInfo>>> getAllTourForAdmin(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(tourService.getAllTourForAdmin(page, size)));
    }
}
