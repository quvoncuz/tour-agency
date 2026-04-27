package quvoncuz.service;

import org.springframework.data.domain.Page;
import quvoncuz.dto.rating.RatingFullInfo;
import quvoncuz.dto.rating.RatingRequestDTO;
import quvoncuz.dto.rating.RatingShortInfo;
import quvoncuz.dto.rating.UpdateRatingRequestDTO;
import quvoncuz.entities.RatingEntity;
import quvoncuz.enums.RatingType;

import java.util.Optional;

public interface RatingService {

    RatingFullInfo create(RatingRequestDTO dto);

    RatingFullInfo update(Long ratingId, UpdateRatingRequestDTO dto);

    Boolean delete(Long ratingId);

    Page<RatingShortInfo> findBySourceIdAndType(Long sourceId, RatingType type, int page, int size);

    Page<RatingShortInfo> findByUserId(Long userId, int page, int size);

    Optional<RatingEntity> findByUserIdAndSourceIdAndType(
            Long userId, Long sourceId, RatingType type);

    boolean hasRated(Long userId, Long sourceId, RatingType target);
}
