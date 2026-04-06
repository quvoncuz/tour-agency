package quvoncuz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.payment.PaymentFullInfo;
import quvoncuz.dto.payment.PaymentRequestDTO;
import quvoncuz.dto.payment.PaymentShortInfo;
import quvoncuz.service.PaymentService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<PaymentFullInfo> processPayment(@RequestBody PaymentRequestDTO dto,
                                                          @RequestHeader(value = "X-User-Id") Long userId) {
        return ResponseEntity.ok(paymentService.processPayment(dto, userId));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<PaymentShortInfo>> findAll(@RequestHeader(value = "X-User-Id") Long userId,
                                                          @RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(paymentService.findAll(userId, page, size));
    }

    @GetMapping("/by-user")
    public ResponseEntity<Page<PaymentShortInfo>> findAllByUserId(@RequestHeader(value = "X-User-Id") Long userId,
                                                                  @RequestParam(defaultValue = "1") int page,
                                                                  @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(paymentService.findAllByUserId(userId, page, size));
    }

    @GetMapping("/by-tour")
    public ResponseEntity<Page<PaymentShortInfo>> findAllByTourId(@RequestParam Long tourId,
                                                                  @RequestHeader(value = "X-User-Id") Long userId,
                                                                  @RequestParam(defaultValue = "1") int page,
                                                                  @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(paymentService.findAllByTourId(tourId, userId, page, size));
    }
}
