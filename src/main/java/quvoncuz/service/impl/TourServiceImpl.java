package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import quvoncuz.dto.tour.CreateTourRequestDTO;
import quvoncuz.dto.tour.TourFullInfo;
import quvoncuz.dto.tour.TourShortInfo;
import quvoncuz.dto.tour.UpdateTourRequestDTO;
import quvoncuz.entities.AgencyEntity;
import quvoncuz.entities.SavedTourEntity;
import quvoncuz.entities.TourEntity;
import quvoncuz.enums.AgencyStatus;
import quvoncuz.exceptions.NotFoundException;
import quvoncuz.exceptions.PermissionDeniedException;
import quvoncuz.mapper.TourMapper;
import quvoncuz.repository.AgencyRepository;
import quvoncuz.repository.ProfileRepository;
import quvoncuz.repository.SavedTourRepository;
import quvoncuz.repository.TourRepository;
import quvoncuz.service.TourService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TourServiceImpl implements TourService {

    private final AgencyRepository agencyRepository;
    private final ProfileRepository profileRepository;
    private final TourRepository tourRepository;
    private final SavedTourRepository savedTourRepository;

    @Override
    public TourFullInfo createTour(CreateTourRequestDTO dto, Long ownerId) {
        AgencyEntity agency = agencyRepository.getAllAgencies()
                .stream()
                .peek(a -> System.out.println("Checking agency: " + a.getId() + " with ownerId: " + a.getOwnerId()))
                .filter(a -> a.getId().equals(ownerId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Agency not found with id" + ownerId));
        if (!agency.getStatus().equals(AgencyStatus.ACCEPTED)) {
            throw new PermissionDeniedException("You don't have access to create tour!");
        }

        TourEntity tour = TourMapper.toEntity(dto);
        tour.setAgencyId(agency.getId());
        tour.setViewCount(0L);
        tour.setRating(0.0);
        tour.setAvailableSeats(dto.getMaxSeats());
        tourRepository.createOrUpdate(List.of(tour), true);
        return TourMapper.toFullInfo(tour);
    }

    @Override
    public TourFullInfo updateTour(Long tourId, UpdateTourRequestDTO dto, Long ownerId) {
        Long agencyId = agencyRepository.getAllAgencies()
                .stream()
                .filter(agency -> agency.getOwnerId().equals(ownerId))
                .findFirst()
                .orElseThrow(() -> new PermissionDeniedException("You don't have access to update tour!"))
                .getId();

        List<TourEntity> allTour = tourRepository.findAll();
        TourEntity tour = allTour.stream()
                .filter(t -> t.getId().equals(tourId) && t.getAgencyId().equals(agencyId))
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
    public Boolean deleteTour(Long tourId, Long ownerId) {
        Long agencyId = agencyRepository.getAllAgencies()
                .stream()
                .filter(agency -> agency.getOwnerId().equals(ownerId))
                .findFirst()
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
    public List<TourShortInfo> getAllSavedTour(Long userId, int page, int size) {
        List<Long> tourIds = savedTourRepository.findAllByUserId(userId)
                .stream()
                .filter(t -> t.getUserId().equals(userId))
                .mapToLong(SavedTourEntity::getTourId)
                .boxed()
                .toList();

        return getAllTour()
                .stream()
                .filter(t -> tourIds.contains(t.getId()))
                .skip((long) size * (page - 1))
                .limit(size)
                .toList();
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
