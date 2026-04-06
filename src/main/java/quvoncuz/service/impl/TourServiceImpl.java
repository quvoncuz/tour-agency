package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import quvoncuz.dto.tour.CreateTourRequestDTO;
import quvoncuz.dto.tour.TourFullInfo;
import quvoncuz.dto.tour.TourShortInfo;
import quvoncuz.dto.tour.UpdateTourRequestDTO;
import quvoncuz.entities.AgencyEntity;
import quvoncuz.entities.SavedTourEntity;
import quvoncuz.entities.TourEntity;
import quvoncuz.enums.AgencyStatus;
import quvoncuz.exceptions.DoNotMatchException;
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
    private final TourRepository tourRepository;
    private final SavedTourRepository savedTourRepository;

    @Override
    public TourFullInfo createTour(CreateTourRequestDTO dto, Long ownerId) {
        AgencyEntity agency = agencyRepository.findByOwnerId(ownerId).orElseThrow(() -> new NotFoundException("Agency not found"));

        if (!agency.getStatus().equals(AgencyStatus.ACCEPTED)) {
            throw new PermissionDeniedException("You don't have access to create tour!");
        }

        TourEntity tour = TourMapper.toEntity(dto);
        tour.setAgencyId(agency.getId());

        tourRepository.save(tour);
        return TourMapper.toFullInfo(tour);
    }

    @Override
    public TourFullInfo updateTour(Long tourId, UpdateTourRequestDTO dto, Long ownerId) {
        AgencyEntity agency = agencyRepository.findByOwnerId(ownerId).orElseThrow(() -> new NotFoundException("Agency not found"));
        TourEntity tour = tourRepository.findById(tourId).orElseThrow(() -> new NotFoundException("Tour not found"));

        if (!tour.getAgencyId().equals(agency.getId())) {
            throw new DoNotMatchException("You don't have permission");
        }

        tour.setTitle(dto.getTitle());
        tour.setDescription(dto.getDescription());
        tour.setDestination(dto.getDestination());
        tour.setPrice(dto.getPrice());
        tour.setDurationDays(dto.getDurationDays());
        tour.setMaxSeats(dto.getMaxSeats());
        tour.setStartDate(dto.getStartDate());
        tour.setEndDate(dto.getEndDate());

        tourRepository.save(tour);
        return TourMapper.toFullInfo(tour);
    }

    @Override
    public Boolean deleteTour(Long tourId, Long ownerId) {
        Long agencyId = agencyRepository.findByOwnerId(ownerId).orElseThrow(() -> new NotFoundException("Agency not found")).getId();
        return tourRepository.deleteByIdAndAgencyId(tourId, agencyId);
    }

    @Override
    public Page<TourShortInfo> getAllTour(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return tourRepository.findAll(pageRequest).map(TourMapper::toShortInfo);
    }

    @Override
    public Page<TourShortInfo> getAllActiveTour(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return tourRepository.findAll(pageRequest)
                .map(TourMapper::toShortInfo);
    }

    @Override
    public TourFullInfo getById(Long id) {
        TourEntity tourById = tourRepository.findById(id).orElseThrow(() -> new NotFoundException("Tour not found"));
        return TourMapper.toFullInfo(tourById);
    }

    @Override
    public Page<TourShortInfo> getAllSavedTours(Long userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        List<Long> allSavedTourIdByUserId = savedTourRepository.findAllByUserId(userId)
                .stream()
                .map(SavedTourEntity::getTourId)
                .toList();
        return tourRepository.findAllByIdIn(allSavedTourIdByUserId, pageRequest)
                .map(TourMapper::toShortInfo);
    }

    @Override
    public Page<TourShortInfo> search(String query, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return tourRepository.findAllByQuery("%" + query + "%", pageRequest)
                .map(TourMapper::toShortInfo);
    }
}
