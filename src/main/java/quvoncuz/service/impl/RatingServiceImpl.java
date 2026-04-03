package quvoncuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import quvoncuz.dto.rating.RatingFullInfo;
import quvoncuz.dto.rating.RatingRequestDTO;
import quvoncuz.dto.rating.RatingShortInfo;
import quvoncuz.dto.rating.UpdateRatingRequestDTO;
import quvoncuz.entities.AgencyEntity;
import quvoncuz.entities.BookingEntity;
import quvoncuz.entities.RatingEntity;
import quvoncuz.entities.TourEntity;
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
        ratingRepository.createOrReplace(List.of(rating), true);
        calculateAverageStars(dto.getSourceId(), dto.getType());
        logger.info("User {} created a rating for source {} of type {}", userId, dto.getSourceId(), dto.getType());
        return RatingMapper.toFullInfo(rating);
    }

    @Override
    public RatingFullInfo update(Long ratingId, UpdateRatingRequestDTO dto, Long userId) {
        List<RatingEntity> allRating = ratingRepository.findAll();
        RatingEntity rating = allRating.stream().filter(r -> r.getUserId().equals(userId)
                        && r.getId().equals(ratingId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Rating not found"));
        validateStars(dto.getStars());
        rating.setStars(dto.getStars());
        rating.setComment(dto.getComment());
        ratingRepository.createOrReplace(allRating, false);
        logger.info("User {} updated a rating for ratingId {}", userId, ratingId);
        return RatingMapper.toFullInfo(rating);
    }

    @Override
    public Boolean delete(Long ratingId, Long userId) {
        List<RatingEntity> allRating = ratingRepository.findAll();
        allRating.stream().filter(r -> r.getId().equals(ratingId)
                        && r.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Rating not found"));
        ratingRepository.deleteById(ratingId);
        logger.info("User {} deleted a rating with id {}", userId, ratingId);
        return true;
    }

    @Override
    public List<RatingShortInfo> findBySourceIdAndType(Long sourceId, RatingType type, int page, int size) {
        logger.info("Finding ratings for source {} of type {}, page {}, size {}", sourceId, type, page, size);
        return ratingRepository.findAll().stream()
                .filter(r -> r.getSourceId().equals(sourceId) && r.getType() == type)
                .skip((long) (page - 1) * size)
                .limit(size)
                .map(RatingMapper::toShortInfo)
                .toList();
    }

    @Override
    public List<RatingShortInfo> findByUserId(Long userId) {
        logger.info("Finding ratings for user {}", userId);
        return ratingRepository.findAll().stream()
                .filter(r -> r.getUserId().equals(userId))
                .map(RatingMapper::toShortInfo)
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

    public void calculateAverageStars(Long sourceId, RatingType type) {


        logger.info("Calculating average stars for source {} of type {}", sourceId, type);
        double averageRating = ratingRepository.findAll().stream()
                .filter(r -> r.getSourceId().equals(sourceId) && r.getType() == type)
                .mapToInt(RatingEntity::getStars)
                .average()
                .orElse(0.0);

        if (type == RatingType.AGENCY) {
            List<AgencyEntity> allAgencies = agencyRepository.getAllAgencies();
            AgencyEntity agency = allAgencies.stream()
                    .filter(a -> a.getId().equals(sourceId))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Agency not found"));
            agency.setRating(averageRating);
            agencyRepository.createOrUpdate(allAgencies, false);
        } else if (type == RatingType.TOUR) {
            List<TourEntity> allTours = tourRepository.findAll();
            TourEntity tour = allTours.stream()
                    .filter(t -> t.getId().equals(sourceId))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Tour not found"));
            tour.setRating(averageRating);
            tourRepository.createOrUpdate(allTours, false);
        }
    }

    public boolean hasRated(Long userId, Long sourceId, RatingType target) {
        logger.info("Checking if user {} has rated source {} of type {}", userId, sourceId, target);
        return existsByUserIdAndSourceIdAndType(userId, sourceId, target);
    }

    private void requireCompleteBooking(Long userId, Long sourceId, RatingType type) {
        List<BookingEntity> bookingsByUser = bookingRepository.findByUserId(userId);
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
