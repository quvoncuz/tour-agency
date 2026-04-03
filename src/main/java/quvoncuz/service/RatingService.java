package quvoncuz.service;

import quvoncuz.dto.rating.RatingFullInfo;
import quvoncuz.dto.rating.RatingRequestDTO;
import quvoncuz.dto.rating.RatingShortInfo;
import quvoncuz.dto.rating.UpdateRatingRequestDTO;
import quvoncuz.entities.RatingEntity;
import quvoncuz.enums.RatingType;

import java.util.List;
import java.util.Optional;

public interface RatingService {

    RatingFullInfo create(RatingRequestDTO dto, Long userId);

    RatingFullInfo update(Long ratingId, UpdateRatingRequestDTO dto, Long userId);

    Boolean delete(Long ratingId, Long userId);

    List<RatingShortInfo> findBySourceIdAndType(Long sourceId, RatingType type, int page, int size);

    List<RatingShortInfo> findByUserId(Long userId);

    Optional<RatingEntity> findByUserIdAndSourceIdAndType(
            Long userId, Long sourceId, RatingType type);

    boolean hasRated(Long userId, Long sourceId, RatingType target);
}
