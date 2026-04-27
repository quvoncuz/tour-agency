package quvoncuz.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.click.ClickResponse;
import quvoncuz.dto.click.CompleteRequest;
import quvoncuz.dto.click.PrepareRequest;
import quvoncuz.dto.payment.PaymentRequestDTO;
import quvoncuz.dto.payment.PaymentResponse;
import quvoncuz.service.ClickService;

@RestController
@RequestMapping("/click")
@RequiredArgsConstructor
public class ClickController {

    private final ClickService clickService;

    @PostMapping("/pay")
    public ResponseEntity<PaymentResponse> pay(
            @Valid @RequestBody PaymentRequestDTO request) {
        return ResponseEntity.ok(clickService.generatePaymentUrl(request));
    }

    @PostMapping("/prepare")
    public ResponseEntity<ClickResponse> prepare(
            @ModelAttribute PrepareRequest request) {
        return ResponseEntity.ok(clickService.prepare(request));
    }

    @PostMapping("/complete")
    public ResponseEntity<ClickResponse> complete(
            @ModelAttribute CompleteRequest request) {
        return ResponseEntity.ok(clickService.complete(request));
    }
}
