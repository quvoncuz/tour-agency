package quvoncuz.mapper;

import quvoncuz.dto.payment.PaymentDTO;
import quvoncuz.dto.payment.PaymentFullInfo;
import quvoncuz.dto.payment.PaymentRequestDTO;
import quvoncuz.dto.payment.PaymentShortInfo;
import quvoncuz.entities.PaymentEntity;
import quvoncuz.enums.PaymentStatus;

import java.time.LocalDateTime;

public class PaymentMapper {

    public static PaymentDTO toDTO(PaymentEntity payment) {
        return PaymentDTO.builder()
                .id(payment.getId())
                .userId(payment.getUserId())
                .bookingId(payment.getBookingId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .createdDate(payment.getCreatedAt())
                .build();
    }

    public static PaymentFullInfo toFullInfo(PaymentEntity entity) {
        return PaymentFullInfo.builder()
                .tourId(entity.getTourId())
                .bookingId(entity.getBookingId())
                .amount(entity.getAmount())
                .status(entity.getStatus())
                .createdDate(entity.getCreatedAt().toLocalDate())
                .build();
    }

    public static PaymentShortInfo toShortInfo(PaymentEntity entity) {
        return PaymentShortInfo.builder()
                .id(entity.getId())
                .tourId(entity.getTourId())
                .bookingId(entity.getBookingId())
                .status(entity.getStatus())
                .build();
    }

    public static PaymentEntity toEntity(PaymentRequestDTO dto, Long userId) {
        return PaymentEntity.builder()
                .id(null)
                .userId(userId)
                .tourId(dto.getTourId())
                .bookingId(dto.getBookingId())
                .amount(0L)
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }
}