package quvoncuz.mapper;

import quvoncuz.dto.booking.BookingFullInfo;
import quvoncuz.dto.booking.BookingShortInfo;
import quvoncuz.dto.booking.CreateBookingRequestDTO;
import quvoncuz.entities.BookingEntity;
import quvoncuz.enums.BookingStatus;

import java.time.LocalDateTime;

public class BookingMapper {

    public static BookingFullInfo toFullInfo(BookingEntity entity) {
        return BookingFullInfo.builder()
                .id(entity.getId())
                .tourId(entity.getTourId())
                .seatsBooked(entity.getSeatsBooked())
                .paidAmount(entity.getPaidAmount())
                .totalPrice(entity.getTotalPrice())
                .status(entity.getStatus())
                .note(entity.getNote())
                .bookedAt(entity.getBookedAt())
                .build();
    }

    public static BookingShortInfo toShortInfo(BookingEntity entity) {
        return BookingShortInfo.builder()
                .id(entity.getId())
                .tourId(entity.getTourId())
                .seatsBooked(entity.getSeatsBooked())
                .totalPrice(entity.getTotalPrice())
                .status(entity.getStatus())
                .build();
    }

    public static BookingEntity toEntity(CreateBookingRequestDTO dto, Long userId) {
        return BookingEntity.builder()
                .id(null)
                .userId(userId)
                .tourId(dto.getTourId())
                .seatsBooked(dto.getSeatsBooked())
                .paidAmount(0L)
                .totalPrice(0L)
                .status(BookingStatus.PENDING)
                .note(dto.getNote())
                .bookedAt(LocalDateTime.now())
                .build();
    }
}