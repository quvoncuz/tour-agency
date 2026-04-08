package quvoncuz.service;

import org.springframework.transaction.annotation.Transactional;
import quvoncuz.dto.payment.TransactionRequestDTO;
import quvoncuz.dto.profile.ProfileFullInfo;

public interface TransactionService {
    @Transactional
    ProfileFullInfo topUp(TransactionRequestDTO dto, Long userId);
}
