package quvoncuz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.payment.PaymentShortInfo;
import quvoncuz.service.PaymentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping({"/all/refund", "/all/refund/"})
    public ResponseEntity<Page<PaymentShortInfo>> findAllByRefund(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(paymentService.findAllByRefund(page, size));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<PaymentShortInfo>> findAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(paymentService.findAll(page, size));
    }

    @GetMapping("/by-user")
    public ResponseEntity<Page<PaymentShortInfo>> findAllByUserId(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(paymentService.findAllByUserId(page, size));
    }

    @GetMapping("/by-tour")
    public ResponseEntity<Page<PaymentShortInfo>> findAllByTourId(
            @RequestParam long tourId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(paymentService.findAllByTourId(tourId, page, size));
    }
}
