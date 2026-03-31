package quvoncuz.mapper;

import quvoncuz.dto.booking.BookingFullInfo;
import quvoncuz.dto.booking.BookingShortInfo;
import quvoncuz.dto.booking.CreateBookingRequestDTO;
import quvoncuz.entities.BookingEntity;
import quvoncuz.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BookingMapper {

    public static BookingFullInfo toFullInfo(BookingEntity entity) {
        return new BookingFullInfo(
                entity.getId(),
                entity.getTourId(),
                entity.getSeatsBooked(),
                entity.getPaidAmount(),
                entity.getTotalPrice(),
                entity.getStatus(),
                entity.getNote(),
                entity.getBookedAt()
        );
    }

    public static BookingShortInfo toShortInfo(BookingEntity entity){
        return new BookingShortInfo(
                entity.getId(),
                entity.getTourId(),
                entity.getSeatsBooked(),
                entity.getTotalPrice(),
                entity.getStatus()
        );
    }

    public static BookingEntity toEntity(CreateBookingRequestDTO dto, Long userId) {
        BookingEntity entity = new BookingEntity();
        entity.setUserId(userId);
        entity.setTourId(dto.getTourId());
        entity.setSeatsBooked(dto.getSeatsBooked());
        entity.setPaidAmount(BigDecimal.ZERO);
        entity.setNote(dto.getNote());
        entity.setStatus(BookingStatus.PENDING);
        entity.setBookedAt(LocalDateTime.now());
        return entity;
    }
}
