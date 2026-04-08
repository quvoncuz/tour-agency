package quvoncuz.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quvoncuz.dto.profile.ProfileFullInfo;
import quvoncuz.dto.transaction.TransactionRequestDTO;
import quvoncuz.dto.transaction.TransactionShortInfo;
import quvoncuz.service.TransactionService;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping({"", "/"})
    public ResponseEntity<ProfileFullInfo> topUp(@RequestBody TransactionRequestDTO dto,
                                                 @RequestHeader(value = "X-User-Id") Long userId) {
        return ResponseEntity.ok(transactionService.topUp(dto, userId));
    }

    @GetMapping({"/all", "/all/"})
    public ResponseEntity<Page<TransactionShortInfo>> findAll(@RequestHeader(value = "X-User-Id") Long loginId,
                                                              @RequestParam(defaultValue = "1") int page,
                                                              @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(transactionService.findAll(loginId, page, size));
    }

    @GetMapping("/all/{userId}")
    public ResponseEntity<Page<TransactionShortInfo>> findAllByUserId(@PathVariable Long userId,
                                                                      @RequestHeader(value = "X-User-Id") Long loginId,
                                                                      @RequestParam(defaultValue = "1") int page,
                                                                      @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(transactionService.findAllByUserId(userId, loginId, page, size));
    }
}
