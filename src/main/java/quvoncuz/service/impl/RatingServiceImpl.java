package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quvoncuz.dto.rating.RatingFullInfo;
import quvoncuz.dto.rating.RatingRequestDTO;
import quvoncuz.dto.rating.RatingShortInfo;
import quvoncuz.dto.rating.UpdateRatingRequestDTO;
import quvoncuz.entities.BookingEntity;
import quvoncuz.entities.RatingEntity;
import quvoncuz.enums.BookingStatus;
import quvoncuz.enums.RatingType;
import quvoncuz.exceptions.AlreadyExistsException;
import quvoncuz.exceptions.DoNotMatchException;
import quvoncuz.exceptions.NotFoundException;
import quvoncuz.mapper.RatingMapper;
import quvoncuz.repository.AgencyRepository;
import quvoncuz.repository.BookingRepository;
import quvoncuz.repository.RatingRepository;
import quvoncuz.repository.TourRepository;
import quvoncuz.service.RatingService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final BookingRepository bookingRepository;
    private final AgencyRepository agencyRepository;
    private final TourRepository tourRepository;

    @Override
    @Transactional
    public RatingFullInfo create(RatingRequestDTO dto, Long userId) {

        if (hasRated(userId, dto.getSourceId(), dto.getType())) {
            throw new AlreadyExistsException("You have already rated this item");
        }

        if (dto.getType() == RatingType.AGENCY) {
            if (!agencyRepository.existsById(dto.getSourceId())) {
                throw new NotFoundException("Agency not found");
            }
        } else {
            if (!tourRepository.existsById(dto.getSourceId())) {
                throw new NotFoundException("Tour not found");
            }
        }

        requireCompleteBooking(userId, dto.getSourceId(), dto.getType());

        RatingEntity rating = RatingEntity.builder()
                .userId(userId)
                .sourceId(dto.getSourceId())
                .type(dto.getType())
                .stars(dto.getStars())
                .comment(dto.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        ratingRepository.save(rating);
        calculateAverageStars(dto.getSourceId(), dto.getType());
        log.info("User {} created a rating for source {} of type {}", userId, dto.getSourceId(), dto.getType());
        return RatingMapper.toFullInfo(rating);
    }

    @Override
    @Transactional
    public RatingFullInfo update(Long ratingId, UpdateRatingRequestDTO dto, Long userId) {
        RatingEntity rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new NotFoundException("Rating not found"));
        if (!rating.getUserId().equals(userId)) {
            throw new DoNotMatchException("You don't have permission");
        }
        rating.setStars(dto.getStars());
        rating.setComment(dto.getComment());

        ratingRepository.save(rating);
        log.info("User {} updated a rating for ratingId {}", userId, ratingId);
        return RatingMapper.toFullInfo(rating);
    }

    @Override
    @Transactional
    public void delete(Long ratingId) {
        log.info("Admin deleted a rating with id {}", ratingId);
        ratingRepository.deleteById(ratingId);
    }

    @Override
    public void deleteOwnRating(Long ratingId, Long userId) {
        ratingRepository.deleteByIdAndUserId(ratingId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RatingShortInfo> findBySourceIdAndType(Long sourceId, RatingType type, int page, int size) {
        log.info("Finding ratings for source {} of type {}", sourceId, type);

        PageRequest pageRequest = PageRequest.of(page - 1, size);

        return ratingRepository.findAllBySourceIdAndType(sourceId, type, pageRequest)
                .map(RatingMapper::toShortInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RatingShortInfo> findByUserId(Long userId, int page, int size) {
        log.info("Admin request ratings for user {}", userId);

        PageRequest pageRequest = PageRequest.of(page - 1, size);

        return ratingRepository.findAllByUserId(userId, pageRequest)
                .map(RatingMapper::toShortInfo);
    }

    @Override
    public Page<RatingShortInfo> findOwnRatings(Long userId, int page, int size) {

        log.info("Finding ratings for user {}", userId);

        PageRequest pageRequest = PageRequest.of(page - 1, size);

        return ratingRepository.findAllByUserId(userId, pageRequest)
                .map(RatingMapper::toShortInfo);
    }

    public void calculateAverageStars(Long sourceId, RatingType type) {

        log.info("Calculating average stars for source {} of type {}", sourceId, type);
        Double averageRating = ratingRepository.calculateAverageStars(sourceId, type);
        if (type == RatingType.AGENCY) {
            agencyRepository.updateRating(averageRating, sourceId);
        } else if (type == RatingType.TOUR) {
            tourRepository.updateRating(averageRating, sourceId);
        }
    }

    @Transactional(readOnly = true)
    public boolean hasRated(Long userId, Long sourceId, RatingType target) {
        log.info("Checking if user {} has rated source {} of type {}", userId, sourceId, target);
        return findByUserIdAndSourceIdAndType(userId, sourceId, target).isPresent();
    }

    private Optional<RatingEntity> findByUserIdAndSourceIdAndType(
            Long userId, Long sourceId, RatingType type) {
        log.info("Finding rating for user {}, source {}, type {}", userId, sourceId, type);
        return ratingRepository.findByUserIdAndSourceIdAndType(userId, sourceId, type);
    }

    private void requireCompleteBooking(Long userId, Long sourceId, RatingType type) {

        if (type == RatingType.TOUR) {
            bookingRepository.findAllByUserIdAndTourIdAndStatus(userId, sourceId, BookingStatus.CONFIRMED)
                    .orElseThrow(() -> new DoNotMatchException("You can only rate completed bookings"));
        } else if (type == RatingType.AGENCY) {
            List<BookingEntity> bookingsByUser = bookingRepository.findAllByUserIdAndTour_AgencyId(userId, sourceId);
            boolean hasComplete = bookingsByUser
                    .stream()
                    .anyMatch(b -> b.getStatus() == BookingStatus.CONFIRMED);
            if (!hasComplete) {
                throw new DoNotMatchException("You can only rate agencies if you have completed a booking");
            }
        }
    }
}
