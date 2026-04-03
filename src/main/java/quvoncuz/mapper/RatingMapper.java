package quvoncuz.mapper;

import quvoncuz.dto.rating.RatingFullInfo;
import quvoncuz.dto.rating.RatingShortInfo;
import quvoncuz.entities.RatingEntity;

public class RatingMapper {

    public static RatingFullInfo toFullInfo(RatingEntity entity) {
        return RatingFullInfo.builder()
                .userId(entity.getId())
                .sourceId(entity.getSourceId())
                .stars(entity.getStars())
                .comment(entity.getComment())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static RatingShortInfo toShortInfo(RatingEntity entity) {
        return RatingShortInfo.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .sourceId(entity.getSourceId())
                .stars(entity.getStars())
                .build();
    }

}
