package quvoncuz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.payment.TransactionRequestDTO;
import quvoncuz.dto.profile.ProfileFullInfo;
import quvoncuz.service.TransactionService;

@RestController
@RequestMapping("/fill")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("")
    public ResponseEntity<ProfileFullInfo> topUp(@RequestBody TransactionRequestDTO dto,
                                                       @RequestHeader(value = "X-User-Id") Long userId){
        return ResponseEntity.ok(transactionService.topUp(dto, userId));
    }
}
