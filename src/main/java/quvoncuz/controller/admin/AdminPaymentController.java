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
import quvoncuz.dto.payment.PaymentShortInfo;
import quvoncuz.service.PaymentService;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin/payments")
public class AdminPaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PaymentShortInfo>>> findAll(
            @RequestParam(defaultValue = "false") boolean refund,
            @RequestParam(required = false) long userId,
            @RequestParam(required = false) long tourId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse
                .success(paymentService.findAll(refund, userId, tourId, page, size)));
    }
}
