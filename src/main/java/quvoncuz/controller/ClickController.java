package quvoncuz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.click.ClickResponse;
import quvoncuz.dto.click.CompleteRequest;
import quvoncuz.dto.click.PrepareRequest;
import quvoncuz.dto.payment.PaymentRequestDTO;
import quvoncuz.dto.payment.PaymentResponse;
import quvoncuz.service.ClickService;

@Controller
@RequestMapping("/api/v1/click")
@RequiredArgsConstructor
public class ClickController {

    private final ClickService clickService;

    @PostMapping("/pay")
    public ResponseEntity<PaymentResponse> pay(@RequestBody PaymentRequestDTO request,
                                               @RequestHeader(value = "X-User-Id") Long userId) {
        return ResponseEntity.ok(clickService.generatePaymentUrl(request, userId));
    }

    @PostMapping("/prepare")
    public ResponseEntity<ClickResponse> prepare(@ModelAttribute PrepareRequest request) {
        return ResponseEntity.ok(clickService.prepare(request));
    }

    @PostMapping("/complete")
    public ResponseEntity<ClickResponse> complete(@ModelAttribute CompleteRequest request) {
        return ResponseEntity.ok(clickService.complete(request));
    }
}
