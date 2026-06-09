package quvoncuz.service;

import org.springframework.data.domain.Page;
import quvoncuz.dto.rating.RatingFullInfo;
import quvoncuz.dto.rating.RatingRequestDTO;
import quvoncuz.dto.rating.RatingShortInfo;
import quvoncuz.dto.rating.UpdateRatingRequestDTO;
import quvoncuz.enums.RatingType;

public interface RatingService {

    RatingFullInfo create(RatingRequestDTO dto, Long userId);

    RatingFullInfo update(Long ratingId, UpdateRatingRequestDTO dto, Long userId);

    void delete(Long ratingId);

    void deleteOwnRating(Long ratingId, Long userId);

    Page<RatingShortInfo> findBySourceIdAndType(Long sourceId, RatingType type, int page, int size);

    Page<RatingShortInfo> findByUserId(Long userId, int page, int size);

    Page<RatingShortInfo> findOwnRatings(Long userId, int page, int size);

    boolean hasRated(Long userId, Long sourceId, RatingType target);
}
