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

import java.util.List;

@Service
@RequiredArgsConstructor
public class TourServiceImpl implements TourService {

    private final AgencyRepository agencyRepository;
    private final ProfileRepository profileRepository;
    private final TourRepository tourRepository;

    @Override
    public TourFullInfo createTour(CreateTourRequestDTO dto, Long ownerId) {
        AgencyEntity agency = agencyRepository.findByOwnerId(ownerId);
        if (!agency.getStatus().equals(AgencyStatus.ACCEPTED)) {
            throw new PermissionDeniedException("You don't have access to create tour!");
        }

        TourEntity tour = TourMapper.toEntity(dto);
        tour.setAgencyId(agency.getId());
        tourRepository.createOrUpdate(List.of(tour), true);
        return TourMapper.toFullInfo(tour);
    }

    @Override
    public TourFullInfo updateTour(UpdateTourRequestDTO dto, Long ownerId) {
        Long agencyId = agencyRepository.getAllAgencies()
                .stream()
                .filter(agency -> agency.getOwnerId().equals(ownerId)
                        && agency.getId().equals(dto.getId()))
                .findFirst()
                .orElseThrow(() -> new PermissionDeniedException("You don't have access to update tour!")).getId();

        List<TourEntity> allTour = tourRepository.findAll();
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
    public boolean deleteTour(Long tourId, Long ownerId) {
        Long agencyId = agencyRepository.getAllAgencies()
                .stream()
                .findFirst()
                .filter(agency -> agency.getOwnerId().equals(ownerId))
                .orElseThrow(() -> new PermissionDeniedException("You don't have access to delete tour!")).getId();
        TourEntity tour = tourRepository.findAll()
                .stream()
                .filter(t -> t.getId().equals(tourId) && t.getAgencyId().equals(agencyId))
                .findFirst()
                .orElseThrow(() -> new PermissionDeniedException("You don't have access to delete tour!"));
        return tourRepository.deleteById(tour.getId());
    }

    @Override
    public List<TourShortInfo> getAllTour() {
        return tourRepository.findAll()
                .stream()
                .map(TourMapper::toShortInfo)
                .toList();
    }

    @Override
    public List<TourShortInfo> getAllActiveTour() {
        return tourRepository.findAll()
                .stream()
                .filter(tour -> tour.getIsActive() && tour.getStartDate().isAfter(java.time.LocalDate.now()))
                .map(TourMapper::toShortInfo)
                .toList();
    }

    @Override
    public TourFullInfo getById(Long id) {
        TourEntity tourById = tourRepository.findById(id);
        return TourMapper.toFullInfo(tourById);
    }

    @Override
    public List<TourShortInfo> search(String query) {
        return tourRepository.findAll()
                .stream()
                .filter(tour -> tour.getDestination().contains(query) || tour.getTitle().contains(query))
                .map(TourMapper::toShortInfo)
                .toList();
    }
}
