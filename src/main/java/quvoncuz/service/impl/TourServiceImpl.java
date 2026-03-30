package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import quvoncuz.dto.tour.CreateTourRequestDTO;
import quvoncuz.dto.tour.TourFullInfo;
import quvoncuz.dto.tour.TourShortInfo;
import quvoncuz.dto.tour.UpdateTourRequestDTO;
import quvoncuz.entities.AgencyEntity;
import quvoncuz.entities.TourEntity;
import quvoncuz.enums.AgencyStatus;
import quvoncuz.exceptions.PermissionDeniedException;
import quvoncuz.mapper.TourMapper;
import quvoncuz.repository.AgencyRepository;
import quvoncuz.repository.ProfileRepository;
import quvoncuz.repository.TourRepository;
import quvoncuz.service.TourService;
import quvoncuz.utils.SecurityUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TourServiceImpl implements TourService {

    public final AgencyRepository agencyRepository;
    public final ProfileRepository profileRepository;
    private final TourRepository tourRepository;

    @Override
    public TourFullInfo createTour(CreateTourRequestDTO dto) {

        Long profileId = SecurityUtils.getCurrentUserId();
        AgencyEntity agency = agencyRepository.findByOwnerId(profileId);
        if (!agency.getStatus().equals(AgencyStatus.ACCEPTED)){
            throw new PermissionDeniedException("You don't have access to create tour!");
        }

        TourEntity tour = TourMapper.toEntity(dto);
        tour.setAgencyId(agency.getId());
        tourRepository.createOrUpdate(List.of(tour), true);
        return TourMapper.toFullInfo(tour);
    }

    @Override
    public TourFullInfo updateTour(UpdateTourRequestDTO dto) {
        Long ownerId = SecurityUtils.getCurrentUserId();
        Long agencyId = agencyRepository.getAllAgencies()
                .stream()
                .findFirst()
                .filter(agency -> agency.getOwnerId().equals(ownerId))
                .orElseThrow(() -> new PermissionDeniedException("You don't have access to update tour!")).getId();

        List<TourEntity> allTour = tourRepository.getAllTour();
        TourEntity tour = allTour.stream()
                .filter(t -> t.getId().equals(dto.getId()) && t.getAgencyId().equals(agencyId))
                .findFirst()
                .orElseThrow(() -> new PermissionDeniedException("You don't have access to update tour!"));

        tour.setTitle(dto.getTitle());
        tour.setDescription(dto.getDescription());
        tour.setDestination(dto.getDestination());
        tour.setPrice(dto.getPrice());
        tour.setDurationDays(dto.getDurationDays());
        tour.setMaxSeats(dto.getMaxSeats());
        tour.setStartDate(dto.getStartDate());
        tour.setEndDate(dto.getEndDate());

        tourRepository.createOrUpdate(allTour, false);
        return TourMapper.toFullInfo(tour);
    }

    @Override
    public boolean deleteTour(Long tourId) {
        Long ownerId = SecurityUtils.getCurrentUserId();
        Long agencyId = agencyRepository.getAllAgencies()
                .stream()
                .findFirst()
                .filter(agency -> agency.getOwnerId().equals(ownerId))
                .orElseThrow(() -> new PermissionDeniedException("You don't have access to delete tour!")).getId();
        TourEntity tour = tourRepository.getAllTour()
                .stream()
                .filter(t -> t.getId().equals(tourId) && t.getAgencyId().equals(agencyId))
                .findFirst()
                .orElseThrow(() -> new PermissionDeniedException("You don't have access to delete tour!"));
        return tourRepository.deleteById(tour.getId());
    }

    @Override
    public List<TourShortInfo> getAllTour() {
        return tourRepository.getAllTour()
                .stream()
                .map(TourMapper::toShortInfo)
                .toList();
    }

    @Override
    public TourFullInfo getById(Long id) {
        TourEntity tourById = tourRepository.getTourById(id);
        return TourMapper.toFullInfo(tourById);
    }

    @Override
    public List<TourShortInfo> search(String query) {
        return tourRepository.getAllTour()
                .stream()
                .filter(tour -> tour.getDestination().contains(query) || tour.getTitle().contains(query))
                .map(TourMapper::toShortInfo)
                .toList();
    }
}
