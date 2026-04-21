package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quvoncuz.dto.tour.CreateTourRequestDTO;
import quvoncuz.dto.tour.TourFullInfo;
import quvoncuz.dto.tour.TourShortInfo;
import quvoncuz.dto.tour.UpdateTourRequestDTO;
import quvoncuz.entities.*;
import quvoncuz.enums.AgencyStatus;
import quvoncuz.enums.BookingStatus;
import quvoncuz.enums.Role;
import quvoncuz.exceptions.InvalidException;
import quvoncuz.exceptions.NotFoundException;
import quvoncuz.exceptions.PermissionDeniedException;
import quvoncuz.mapper.TourMapper;
import quvoncuz.repository.AgencyRepository;
import quvoncuz.repository.BookingRepository;
import quvoncuz.repository.SavedTourRepository;
import quvoncuz.repository.TourRepository;
import quvoncuz.service.AgencyService;
import quvoncuz.service.NotificationService;
import quvoncuz.service.ProfileService;
import quvoncuz.service.TourService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TourServiceImpl implements TourService {

    private final AgencyRepository agencyRepository;
    private final TourRepository tourRepository;
    private final SavedTourRepository savedTourRepository;
    private final AgencyService agencyService;
    private final BookingRepository bookingRepository;
    private final ProfileService profileService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public TourFullInfo createTour(CreateTourRequestDTO dto, Long ownerId) {
        AgencyEntity agency = agencyRepository.findByOwnerId(ownerId).orElseThrow(() -> new NotFoundException("Agency not found"));

        if (!agency.getStatus().equals(AgencyStatus.ACCEPTED)) {
            throw new PermissionDeniedException("You don't have permission!");
        }

        TourEntity tour = TourMapper.toEntity(dto);
        tour.setAgencyId(agency.getId());

        tour = tourRepository.save(tour);
        return TourMapper.toFullInfo(tour);
    }

    @Override
    @Transactional
    public TourFullInfo updateTour(Long tourId, UpdateTourRequestDTO dto, Long ownerId) {
        AgencyEntity agency = agencyRepository.findByOwnerId(ownerId).orElseThrow(() -> new NotFoundException("Agency not found"));
        TourEntity tour = tourRepository.findById(tourId).orElseThrow(() -> new NotFoundException("Tour not found"));

        if (!tour.getAgencyId().equals(agency.getId())) {
            throw new PermissionDeniedException("You don't have permission");
        }

        tour.setTitle(dto.getTitle());
        tour.setDescription(dto.getDescription());
        tour.setDestination(dto.getDestination());
        tour.setDurationDays(dto.getDurationDays());
        tour.setMaxSeats(dto.getMaxSeats());
        tour.setStartDate(dto.getStartDate());
        tour.setEndDate(dto.getEndDate());

        tourRepository.save(tour);
        return TourMapper.toFullInfo(tour);
    }

    @Override
    @Transactional
    public TourFullInfo updateTourPrice(Long tourId, Long newPrice, Long userId) {
        ProfileEntity profile = profileService.findById(userId);
        if (profile.getRole() != Role.AGENCY) {
            throw new PermissionDeniedException("You don't have permission");
        }
        TourEntity tour = tourRepository.findById(tourId).orElseThrow(() -> new NotFoundException("Tour not found"));
        if(!tour.getAgency().getOwner().getId().equals(userId)) {
            throw new PermissionDeniedException("You don't have permission");
        }
        Long old = tour.getPrice();
        if (newPrice.equals(tour.getPrice())) {
            throw new InvalidException("Change the value");
        }
        List<BookingEntity> bookings = bookingRepository.findAllByTourIdAndStatus(tourId, BookingStatus.PENDING);
        bookings.forEach(booking -> {
            Integer seatsBooked = booking.getSeatsBooked();
            booking.setTotalPrice(seatsBooked * newPrice);
            booking.setStatus(BookingStatus.ON_UPDATE);

            notificationService.sendNotificationForTourUpdate(booking.getUser().getEmail(), tour.getTitle(), old, tour.getPrice());
        });

        bookingRepository.saveAll(bookings);

        return TourMapper.toFullInfo(tour);
    }

    @Override
    @Transactional
    public Boolean deleteTour(Long tourId, Long ownerId) {
        Long agencyId = agencyService.findByOwnerId(ownerId)
                .orElseThrow(() -> new NotFoundException("Agency not found")).getId();
        tourRepository.deleteByIdAndAgencyId(tourId, agencyId);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TourShortInfo> getAllTour(int page, int size) {
        return tourRepository.findAll(page - 1, size)
                .stream()
                .map(TourMapper::toShortInfo)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TourShortInfo> getAllActiveTour(int page, int size) {
        return tourRepository.findAll(page - 1, size)
                .stream()
                .map(TourMapper::toShortInfo)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TourFullInfo getById(Long id) {
        TourEntity tourById = tourRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tour not found"));
        incrementViewCount(tourById);
        return TourMapper.toFullInfo(tourById);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TourShortInfo> getAllSavedTours(Long userId, int page, int size) {
        List<Long> allSavedTourIdByUserId = savedTourRepository.findAllByUserId(userId)
                .stream()
                .map(SavedTourEntity::getTourId)
                .toList();
        return tourRepository.findAllByIdIn(allSavedTourIdByUserId, page - 1, size)
                .stream()
                .map(TourMapper::toShortInfo)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TourShortInfo> search(String query, int page, int size) {
        return tourRepository.findAllByQuery("%" + query + "%", page - 1, size)
                .stream()
                .map(TourMapper::toShortInfo)
                .toList();
    }

    @Transactional
    public void incrementViewCount(TourEntity tour) {
        tour.setViewCount(tour.getViewCount() + 1);
        tourRepository.save(tour);
    }
}
