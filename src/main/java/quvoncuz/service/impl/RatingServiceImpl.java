package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import quvoncuz.dto.rating.RatingFullInfo;
import quvoncuz.dto.rating.RatingRequestDTO;
import quvoncuz.dto.rating.RatingShortInfo;
import quvoncuz.dto.rating.UpdateRatingRequestDTO;
import quvoncuz.entities.BookingEntity;
import quvoncuz.entities.RatingEntity;
import quvoncuz.enums.BookingStatus;
import quvoncuz.enums.RatingType;
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

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final Logger logger = LoggerFactory.getLogger(RatingServiceImpl.class);

    private final RatingRepository ratingRepository;
    private final BookingRepository bookingRepository;
    private final AgencyRepository agencyRepository;
    private final TourRepository tourRepository;

    @Override
    public RatingFullInfo create(RatingRequestDTO dto, Long userId) {
        if (hasRated(userId, dto.getSourceId(), dto.getType())) {
            throw new IllegalStateException("You have already rated this item");
        }
        validateStars(dto.getStars());
        RatingEntity rating = RatingEntity.builder()
                .id(null)
                .userId(userId)
                .sourceId(dto.getSourceId())
                .type(dto.getType())
                .stars(dto.getStars())
                .comment(dto.getComment())
                .createdAt(LocalDateTime.now())
                .build();
        requireCompleteBooking(userId, dto.getSourceId(), dto.getType());
        ratingRepository.save(rating);
        calculateAverageStars(dto.getSourceId(), dto.getType());
        logger.info("User {} created a rating for source {} of type {}", userId, dto.getSourceId(), dto.getType());
        return RatingMapper.toFullInfo(rating);
    }

    @Override
    public RatingFullInfo update(Long ratingId, UpdateRatingRequestDTO dto, Long userId) {
        RatingEntity rating = ratingRepository.findById(ratingId).orElseThrow(() -> new NotFoundException("Rating not found"));
        if (!rating.getUserId().equals(userId)) {
            throw new DoNotMatchException("It's not your rating");
        }
        validateStars(dto.getStars());
        rating.setStars(dto.getStars());
        rating.setComment(dto.getComment());

        ratingRepository.save(rating);
        logger.info("User {} updated a rating for ratingId {}", userId, ratingId);
        return RatingMapper.toFullInfo(rating);
    }

    @Override
    public Boolean delete(Long ratingId, Long userId) {
        logger.info("User {} deleted a rating with id {}", userId, ratingId);
        return ratingRepository.deleteByIdAndUserId(ratingId, userId);
    }

    @Override
    public Page<RatingShortInfo> findBySourceIdAndType(Long sourceId, RatingType type, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        logger.info("Finding ratings for source {} of type {}, page {}, size {}", sourceId, type, page, size);
        return ratingRepository.findBySourceIdAndType(sourceId, type, pageRequest)
                .map(RatingMapper::toShortInfo);
    }

    @Override
    public Page<RatingShortInfo> findByUserId(Long userId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        logger.info("Finding ratings for user {}", userId);
        return ratingRepository.findAllByUserId(userId, pageRequest)
                .map(RatingMapper::toShortInfo);
    }

    @Override
    public Optional<RatingEntity> findByUserIdAndSourceIdAndType(
            Long userId, Long sourceId, RatingType type) {
        logger.info("Finding rating for user {}, source {}, type {}", userId, sourceId, type);
        return ratingRepository.findByUserIdAndSourceIdAndType(userId, sourceId, type);
    }

    public void calculateAverageStars(Long sourceId, RatingType type) {

        logger.info("Calculating average stars for source {} of type {}", sourceId, type);
        double averageRating = ratingRepository.findAllBySourceIdAndType(sourceId, type)
                .stream()
                .mapToInt(RatingEntity::getStars)
                .average()
                .orElse(0.0);

        if (type == RatingType.AGENCY) {
            agencyRepository.updateRating(averageRating, sourceId);
        } else if (type == RatingType.TOUR) {
            tourRepository.updateRating(averageRating, sourceId);
        }
    }

    public boolean hasRated(Long userId, Long sourceId, RatingType target) {
        logger.info("Checking if user {} has rated source {} of type {}", userId, sourceId, target);
        return existsByUserIdAndSourceIdAndType(userId, sourceId, target);
    }

    private void requireCompleteBooking(Long userId, Long sourceId, RatingType type) {
        List<BookingEntity> bookingsByUser = bookingRepository.findAllByUserId(userId);
        if (type == RatingType.TOUR) {
            BookingEntity booking = bookingsByUser
                    .stream()
                    .filter(b -> b.getTourId().equals(sourceId))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Booking not found"));

            if (booking.getStatus() != BookingStatus.CONFIRMED) {
                throw new IllegalStateException("You can only rate completed bookings");
            }
        } else if (type == RatingType.AGENCY) {
            boolean hasComplete = bookingsByUser
                    .stream()
                    .anyMatch(b -> b.getStatus() == BookingStatus.CONFIRMED);
            if (!hasComplete) {
                throw new IllegalStateException("You can only rate agencies if you have completed a booking");
            }
        }
    }

    private void validateStars(Integer stars) {
        if (stars == null || stars < 1 || stars > 5) {
            throw new DoNotMatchException("Stars must be between 1 and 5");
        }
    }

    private boolean existsByUserIdAndSourceIdAndType(
            Long userId, Long sourceId, RatingType target) {
        return findByUserIdAndSourceIdAndType(userId, sourceId, target).isPresent();
    }
}
