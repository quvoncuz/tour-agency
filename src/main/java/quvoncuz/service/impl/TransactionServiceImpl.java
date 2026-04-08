package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quvoncuz.dto.payment.TransactionRequestDTO;
import quvoncuz.dto.profile.ProfileFullInfo;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.entities.TransactionEntity;
import quvoncuz.enums.TransactionStatus;
import quvoncuz.exceptions.NotFoundException;
import quvoncuz.mapper.ProfileMapper;
import quvoncuz.repository.ProfileRepository;
import quvoncuz.repository.TransactionRepository;
import quvoncuz.service.TransactionService;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final ProfileRepository profileRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    @Override
    public ProfileFullInfo topUp(TransactionRequestDTO dto, Long userId) {

        TransactionEntity fillBalance = TransactionEntity.builder()
                .userId(userId)
                .amount(dto.getBalance())
                .cardNumber(dto.getCardNumber())
                .status(TransactionStatus.PENDING)
                .build();

        ProfileEntity profile = profileRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        profile.setBalance(profile.getBalance() + dto.getBalance());

        transactionRepository.save(fillBalance);
        profileRepository.save(profile);
        return ProfileMapper.toFullInfo(profile);
    }

    /*
    @Transactional
    @Override
    public Map<String, String> topUp(TransactionRequestDTO dto, Long userId) {

        TransactionEntity fillBalance = TransactionEntity.builder()
                .userId(userId)
                .amount(dto.getBalance())
                .cardNumber(dto.getCardNumber())
                .status(TransactionStatus.PENDING)
                .build();

        transactionRepository.save(fillBalance);
        return Map.of("", "");
    }*/
}
