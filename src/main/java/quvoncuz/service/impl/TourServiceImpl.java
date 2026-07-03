package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quvoncuz.dto.tour.*;
import quvoncuz.entities.AgencyEntity;
import quvoncuz.entities.BookingEntity;
import quvoncuz.entities.ProfileEntity;
import quvoncuz.entities.TourEntity;
import quvoncuz.enums.AgencyStatus;
import quvoncuz.enums.BookingStatus;
import quvoncuz.enums.EventType;
import quvoncuz.enums.TourStatus;
import quvoncuz.events.NotificationEvent;
import quvoncuz.events.StatisticsEvent;
import quvoncuz.exceptions.DoNotMatchException;
import quvoncuz.exceptions.NotFoundException;
import quvoncuz.exceptions.PermissionDeniedException;
import quvoncuz.mapper.TourMapper;
import quvoncuz.repository.AgencyRepository;
import quvoncuz.repository.BookingRepository;
import quvoncuz.repository.TourRepository;
import quvoncuz.service.AgencyService;
import quvoncuz.service.TourService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TourServiceImpl implements TourService {

    private final AgencyRepository agencyRepository;
    private final TourRepository tourRepository;
    private final AgencyService agencyService;
    private final BookingRepository bookingRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public TourFullInfo createTour(CreateTourRequestDTO dto, Long userId) {
        AgencyEntity agency = agencyRepository.findByOwnerId(userId)
                .orElseThrow(() -> new NotFoundException("Agency not found"));

        if (!agency.getStatus().equals(AgencyStatus.ACCEPTED)) {
            throw new PermissionDeniedException("You don't have permission!");
        }

        if (!agency.getVisible()) {
            throw new PermissionDeniedException("You can't create tour");
        }

        TourEntity tour = TourMapper.toEntity(dto);
        tour.setAgencyId(agency.getId());

        tour = tourRepository.save(tour);

        List<String> allUserEmailBookedByAgency = bookingRepository.findAllUserEmailBookedByAgency(tour.getAgencyId());

        applicationEventPublisher.publishEvent(
                NotificationEvent.builder()
                        .entityId(tour.getId())
                        .eventType(EventType.TOUR_CREATED)
                        .subjectName(tour.getTitle())
                        .mails(allUserEmailBookedByAgency)
                        .dateTime(LocalDateTime.now())
                        .build());

        applicationEventPublisher.publishEvent(
                StatisticsEvent.builder()
                        .entityId(tour.getId())
                        .superId(tour.getAgencyId())
                        .eventType(EventType.TOUR_CREATED)
                        .dateTime(LocalDateTime.now())
                        .build());

        return TourMapper.toFullInfo(tour);
    }

    @Override
    @Transactional
    public TourFullInfo updateTour(Long tourId, UpdateTourRequestDTO dto, Long userId) {

        AgencyEntity agency = agencyRepository.findByOwnerId(userId).orElseThrow(() -> new NotFoundException("Agency not found"));
        TourEntity tour = tourRepository.findById(tourId).orElseThrow(() -> new NotFoundException("Tour not found"));

        if (!agency.getVisible()) {
            throw new PermissionDeniedException("You can't create tour");
        }

        Long oldPrice = tour.getPrice();

        if (!tour.getAgencyId().equals(agency.getId())) {
            throw new PermissionDeniedException("You don't have permission");
        }

        tour.setTitle(dto.getTitle());
        tour.setImageUrl(dto.getImageUrl());
        tour.setDescription(dto.getDescription());
        tour.setDestination(dto.getDestination());
        tour.setDurationDays(dto.getDurationDays());
        tour.setPrice(dto.getPrice());
        tour.setMaxSeats(dto.getMaxSeats());
        tour.setStartDate(dto.getStartDate());
        tour.setEndDate(dto.getStartDate().plusDays(dto.getDurationDays()));

        tourRepository.save(tour);

        if (!oldPrice.equals(dto.getPrice())) {
            List<BookingEntity> bookings = bookingRepository.findAllByTourIdAndStatus(tourId, BookingStatus.PENDING);
            bookings.forEach(booking -> {
                Integer seatsBooked = booking.getSeatsBooked();
                booking.setTotalPrice(seatsBooked * dto.getPrice());
                booking.setStatus(BookingStatus.ON_UPDATE);
            });

            List<String> allEmailByTourIdAndStatus = bookingRepository.findAllEmailByTourIdAndStatus(tourId, BookingStatus.PENDING);

            bookingRepository.saveAll(bookings);

            applicationEventPublisher.publishEvent(
                    NotificationEvent.builder()
                            .entityId(tour.getId())
                            .eventType(EventType.TOUR_UPDATED)
                            .subjectName(tour.getTitle())
                            .mails(allEmailByTourIdAndStatus)
                            .dateTime(LocalDateTime.now())
                            .build());
        }

        return TourMapper.toFullInfo(tour);
    }

    @Override
    @Transactional
    public void cancelTour(Long tourId, CancelTourDTO reason, Long userId) {

        AgencyEntity agency = agencyService.findByOwnerId(userId)
                .orElseThrow(() -> new NotFoundException("Agency not found"));

        TourEntity tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new NotFoundException("Tour not found"));

        if (!agency.getId().equals(tour.getAgencyId())) {
            throw new DoNotMatchException("You don't have permission");
        }

        tour.setStatus(TourStatus.CANCELLED);

        List<BookingEntity> bookings = bookingRepository.findAllByTourId(tourId);
        bookings.forEach(booking -> booking.setStatus(BookingStatus.CANCELED));

        List<String> mails = bookings.stream()
                .map(BookingEntity::getUser)
                .filter(Objects::nonNull)
                .map(ProfileEntity::getEmail)
                .filter(Objects::nonNull)
                .toList();

        bookingRepository.saveAll(bookings);

        tourRepository.save(tour);

        applicationEventPublisher.publishEvent(
                NotificationEvent.builder()
                        .entityId(tour.getId())
                        .eventType(EventType.TOUR_CANCELED)
                        .subjectName(tour.getTitle())
                        .mails(mails)
                        .dateTime(LocalDateTime.now())
                        .build());
    }

    @Override
    @Transactional
    public void deleteTour(Long tourId, Long userId) {
        tourRepository.updateVisible(false, tourId, userId);
    }

    @Override
    public Page<TourShortInfo> getAllTourForAdmin(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);

        return tourRepository.findAll(pageRequest)
                .map(TourMapper::toShortInfo);
    }

    @Override
    public Page<TourShortInfo> getAllTourForAgency(Long userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);

        return tourRepository.findAllByAgencyId(userId, pageRequest)
                .map(TourMapper::toShortInfo);
    }

    @Override
    public Page<TourShortInfo> getAllActiveTour(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return tourRepository.findAllByStatusAndVisible(TourStatus.ACTIVE, true, pageRequest)
                .map(TourMapper::toShortInfo);
    }

    @Override
    @Transactional
    public TourFullInfo getById(Long id) {
        TourEntity tourById = tourRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Tour not found"));
        tourRepository.incrementViewCount(id);
        return TourMapper.toFullInfo(tourById);
    }

    @Override
    public Page<TourShortInfo> search(String query, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);

        return tourRepository.findAllByQuery("%" + query.trim().toLowerCase() + "%", pageRequest)
                .map(TourMapper::toShortInfo);
    }
}
