package quvoncuz.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/v1/click")
@RequiredArgsConstructor
public class ClickController {

    private final ClickService clickService;

    @PostMapping("/pay")
    @Operation(summary = "Making redirect URL for Payment")
    public ResponseEntity<PaymentResponse> pay(@RequestBody PaymentRequestDTO request,
                                               @RequestHeader(value = "X-User-Id") Long userId) {
        try {
            return ResponseEntity.ok(clickService.generatePaymentUrl(request, userId));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/prepare")
    @Operation(summary = "Prepare API")
    public ResponseEntity<ClickResponse> prepare(@ModelAttribute PrepareRequest request) throws JsonProcessingException {
        return ResponseEntity.ok(clickService.prepare(request));
    }

    @PostMapping("/complete")
    @Operation(summary = "Complete API")
    public ResponseEntity<ClickResponse> complete(@ModelAttribute CompleteRequest request) throws JsonProcessingException {
        return ResponseEntity.ok(clickService.complete(request));
    }
}
