package quvoncuz.controller.common;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import quvoncuz.dto.ApiResponse;
import quvoncuz.dto.payment.PaymentShortInfo;
import quvoncuz.service.PaymentService;
import quvoncuz.util.SecurityUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PaymentShortInfo>>> findAllByUserId(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        long userId = SecurityUtil.getCurrentUserId();
        return ResponseEntity.ok(ApiResponse.success(paymentService.findAllByUserId(userId, page, size)));
    }
}
