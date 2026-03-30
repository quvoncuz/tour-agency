package quvoncuz.mapper;

import quvoncuz.dto.tour.CreateTourRequestDTO;
import quvoncuz.dto.tour.TourFullInfo;
import quvoncuz.dto.tour.TourShortInfo;
import quvoncuz.entities.TourEntity;
import quvoncuz.enums.TourStatus;

public class TourMapper {

    public static TourEntity toEntity(CreateTourRequestDTO dto){
        TourEntity tour = new TourEntity();
        tour.setTitle(dto.getTitle());
        tour.setDescription(dto.getDescription());
        tour.setDestination(dto.getDestination());
        tour.setPrice(dto.getPrice());
        tour.setDurationDays(dto.getDurationDays());
        tour.setMaxSeats(dto.getMaxSeats());
        tour.setStartDate(dto.getStartDate());
        tour.setEndDate(dto.getEndDate());
        tour.setStatus(TourStatus.ACTIVE);
        return tour;
    }

    public static TourShortInfo toShortInfo(TourEntity entity){
        TourShortInfo shortInfo = new TourShortInfo();
        shortInfo.setId(entity.getId());
        shortInfo.setAgencyId(entity.getAgencyId());
        shortInfo.setTitle(entity.getTitle());
        shortInfo.setDestination(entity.getDestination());
        shortInfo.setPrice(entity.getPrice());
        shortInfo.setDurationDays(entity.getDurationDays());
        shortInfo.setMaxSeats(entity.getMaxSeats());
        shortInfo.setStartDate(entity.getStartDate());
        shortInfo.setViewCount(entity.getViewCount());
        shortInfo.setRating(entity.getRating());
        shortInfo.setStatus(entity.getStatus());
        return shortInfo;
    }

    public static TourFullInfo toFullInfo(TourEntity entity){
        TourFullInfo fullInfo = new TourFullInfo();
        fullInfo.setId(entity.getId());
        fullInfo.setAgencyId(entity.getAgencyId());
        fullInfo.setTitle(entity.getTitle());
        fullInfo.setDescription(entity.getDescription());
        fullInfo.setDestination(entity.getDestination());
        fullInfo.setPrice(entity.getPrice());
        fullInfo.setDurationDays(entity.getDurationDays());
        fullInfo.setMaxSeats(entity.getMaxSeats());
        fullInfo.setAvailableSeats(entity.getAvailableSeats());
        fullInfo.setStartDate(entity.getStartDate());
        fullInfo.setEndDate(entity.getEndDate());
        fullInfo.setViewCount(entity.getViewCount());
        fullInfo.setRating(entity.getRating());
        fullInfo.setStatus(entity.getStatus());
        return fullInfo;
    }

}
