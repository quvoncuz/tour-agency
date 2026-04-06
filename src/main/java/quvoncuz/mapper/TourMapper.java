package quvoncuz.mapper;

import quvoncuz.dto.tour.CreateTourRequestDTO;
import quvoncuz.dto.tour.TourFullInfo;
import quvoncuz.dto.tour.TourShortInfo;
import quvoncuz.entities.TourEntity;
import quvoncuz.enums.TourStatus;

import java.time.LocalDateTime;

public class TourMapper {

    public static TourEntity toEntity(CreateTourRequestDTO dto) {
        return TourEntity.builder()
                .id(null)
                .agencyId(null)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .destination(dto.getDestination())
                .price(dto.getPrice())
                .durationDays(dto.getDurationDays())
                .maxSeats(dto.getMaxSeats())
                .availableSeats(dto.getMaxSeats())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .isActive(true)
                .viewCount(0L)
                .rating(0.0)
                .status(TourStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static TourShortInfo toShortInfo(TourEntity entity) {
        return TourShortInfo.builder()
                .id(entity.getId())
                .agencyId(entity.getAgencyId())
                .title(entity.getTitle())
                .destination(entity.getDestination())
                .price(entity.getPrice())
                .durationDays(entity.getDurationDays())
                .maxSeats(entity.getMaxSeats())
                .startDate(entity.getStartDate())
                .viewCount(entity.getViewCount())
                .rating(entity.getRating())
                .status(entity.getStatus())
                .build();
    }

    public static TourFullInfo toFullInfo(TourEntity entity) {
        return TourFullInfo.builder()
                .id(entity.getId())
                .agencyId(entity.getAgencyId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .destination(entity.getDestination())
                .price(entity.getPrice())
                .durationDays(entity.getDurationDays())
                .maxSeats(entity.getMaxSeats())
                .availableSeats(entity.getAvailableSeats())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .viewCount(entity.getViewCount())
                .rating(entity.getRating())
                .status(entity.getStatus())
                .build();
    }
}