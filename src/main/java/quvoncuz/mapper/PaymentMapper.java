package quvoncuz.mapper;

import quvoncuz.dto.payment.PaymentDTO;
import quvoncuz.dto.payment.PaymentFullInfo;
import quvoncuz.dto.payment.PaymentRequestDTO;
import quvoncuz.dto.payment.PaymentShortInfo;
import quvoncuz.entities.PaymentEntity;
import quvoncuz.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PaymentMapper {

    public static PaymentDTO toDTO(PaymentEntity payment) {
        return new PaymentDTO(
                payment.getId(),
                payment.getUserId(),
                payment.getBookingId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }

    public static PaymentFullInfo toFullInfo(PaymentEntity entity){
        return new PaymentFullInfo(
                entity.getTourId(),
                entity.getBookingId(),
                entity.getAmount(),
                entity.getStatus(),
                LocalDate.from(entity.getCreatedAt())
        );
    }

    public static PaymentShortInfo toShortInfo(PaymentEntity entity){
        return new PaymentShortInfo(
                entity.getId(),
                entity.getTourId(),
                entity.getBookingId(),
                entity.getStatus()
        );
    }

    public static PaymentEntity toEntity(PaymentRequestDTO dto, Long userId){
        return new PaymentEntity(
                null,
                userId,
                dto.getTourId(),
                dto.getBookingId(),
                BigDecimal.ONE,
                PaymentStatus.PENDING,
                LocalDateTime.now()
        );
    }

}
