package quvoncuz.service;

import quvoncuz.dto.rating.RatingRequestDTO;
import quvoncuz.dto.rating.UpdateRatingRequestDTO;
import quvoncuz.entities.RatingEntity;
import quvoncuz.enums.RatingType;

import java.util.List;
import java.util.Optional;

public interface RatingService {

    void create(RatingRequestDTO dto, Long userId);

    void update(UpdateRatingRequestDTO dto, Long userId);

    void delete(Long ratingId, Long userId);

    List<RatingEntity> findBySourceIdAndType(Long sourceId, RatingType type, int page, int size);

    List<RatingEntity> findByUserId(Long userId);

    Optional<RatingEntity> findByUserIdAndSourceIdAndType(
            Long userId, Long sourceId, RatingType type);

    double getAverageStars(Long sourceId, RatingType type);

    boolean hasRated(Long userId, Long sourceId, RatingType target);
}
