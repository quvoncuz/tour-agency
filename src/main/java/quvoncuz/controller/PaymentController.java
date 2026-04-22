package quvoncuz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import quvoncuz.dto.payment.PaymentShortInfo;
import quvoncuz.service.PaymentService;

import java.util.List;

@Controller
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
            @RequestHeader(value = "X-User-Id") long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(paymentService.findAll(userId, page, size));
    }

    @GetMapping("/by-user")
    public ResponseEntity<Page<PaymentShortInfo>> findAllByUserId(
            @RequestHeader(value = "X-User-Id") long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(paymentService.findAllByUserId(userId, page, size));
    }

    @GetMapping("/by-tour")
    public ResponseEntity<Page<PaymentShortInfo>> findAllByTourId(
            @RequestParam long tourId,
            @RequestHeader(value = "X-User-Id") long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(paymentService.findAllByTourId(tourId, userId, page, size));
    }
}
