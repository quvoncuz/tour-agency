package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import quvoncuz.dto.rating.RatingRequestDTO;
import quvoncuz.dto.rating.UpdateRatingRequestDTO;
import quvoncuz.entities.BookingEntity;
import quvoncuz.entities.RatingEntity;
import quvoncuz.enums.BookingStatus;
import quvoncuz.enums.RatingType;
import quvoncuz.exceptions.DoNotMatchException;
import quvoncuz.exceptions.NotFoundException;
import quvoncuz.repository.BookingRepository;
import quvoncuz.repository.RatingRepository;
import quvoncuz.service.RatingService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final Logger logger = LoggerFactory.getLogger(RatingServiceImpl.class);

    private final RatingRepository ratingRepository;
    private final BookingRepository bookingRepository;

    @Override
    public void create(RatingRequestDTO dto, Long userId) {
        if (hasRated(userId, dto.getSourceId(), dto.getType())) {
            throw new IllegalStateException("You have already rated this item");
        }
        RatingEntity rating = new RatingEntity();
        rating.setUserId(userId);
        rating.setSourceId(dto.getSourceId());
        rating.setType(dto.getType());
        validateStars(dto.getStars());
        rating.setStars(dto.getStars());
        rating.setComment(dto.getComment());
        requireCompleteBooking(userId, dto.getSourceId(), dto.getType());
        ratingRepository.createOrReplace(List.of(rating), true);
        logger.info("User {} created a rating for source {} of type {}", userId, dto.getSourceId(), dto.getType());
    }

    @Override
    public void update(UpdateRatingRequestDTO dto, Long userId) {
        List<RatingEntity> allRating = ratingRepository.findAll();
        RatingEntity rating = allRating.stream().filter(r -> r.getUserId().equals(userId)
                        && r.getSourceId().equals(dto.getSourceId())
                        && r.getType() == dto.getType())
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Rating not found"));
        validateStars(dto.getStars());
        rating.setStars(dto.getStars());
        rating.setComment(dto.getComment());
        ratingRepository.createOrReplace(allRating, false);
        logger.info("User {} updated a rating for source {} of type {}", userId, dto.getSourceId(), dto.getType());
    }

    @Override
    public void delete(Long ratingId, Long userId) {
        List<RatingEntity> allRating = ratingRepository.findAll();
        allRating.stream().filter(r -> r.getId().equals(ratingId)
                        && r.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Rating not found"));
        ratingRepository.deleteById(ratingId);
        logger.info("User {} deleted a rating with id {}", userId, ratingId);
    }

    @Override
    public List<RatingEntity> findBySourceIdAndType(Long sourceId, RatingType type, int page, int size) {
        logger.info("Finding ratings for source {} of type {}, page {}, size {}", sourceId, type, page, size);
        return ratingRepository.findAll().stream()
                .filter(r -> r.getSourceId().equals(sourceId) && r.getType() == type)
                .skip((long) (page - 1) * size)
                .limit(size)
                .toList();
    }

    @Override
    public List<RatingEntity> findByUserId(Long userId) {
        logger.info("Finding ratings for user {}", userId);
        return ratingRepository.findAll().stream()
                .filter(r -> r.getUserId().equals(userId))
                .toList();
    }

    @Override
    public Optional<RatingEntity> findByUserIdAndSourceIdAndType(
            Long userId, Long sourceId, RatingType type) {
        logger.info("Finding rating for user {}, source {}, type {}", userId, sourceId, type);
        return ratingRepository.findAll().stream()
                .filter(r -> r.getUserId().equals(userId)
                        && r.getSourceId().equals(sourceId)
                        && r.getType() == type)
                .findFirst();
    }

    @Override
    public double getAverageStars(Long sourceId, RatingType type) {
        logger.info("Calculating average stars for source {} of type {}", sourceId, type);
        return ratingRepository.findAll().stream()
                .filter(r -> r.getSourceId().equals(sourceId) && r.getType() == type)
                .mapToInt(RatingEntity::getStars)
                .average()
                .orElse(0.0);
    }

    @Override
    public boolean hasRated(Long userId, Long sourceId, RatingType target) {
        logger.info("Checking if user {} has rated source {} of type {}", userId, sourceId, target);
        return existsByUserIdAndSourceIdAndType(userId, sourceId, target);
    }

    private void requireCompleteBooking(Long userId, Long sourceId, RatingType type) {
        List<BookingEntity> bookingsByUser = bookingRepository.findByUserId(userId);
        if (type == RatingType.TOUR) {
            BookingEntity booking = bookingsByUser
                    .stream()
                    .filter(b -> b.getId().equals(sourceId))
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
