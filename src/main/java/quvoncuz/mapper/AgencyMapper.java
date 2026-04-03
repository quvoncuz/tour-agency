package quvoncuz.mapper;

import quvoncuz.dto.agency.AgencyDTO;
import quvoncuz.dto.agency.AgencyFullInfo;
import quvoncuz.dto.agency.AgencyShortInfo;
import quvoncuz.entities.AgencyEntity;

public class AgencyMapper {

    public static AgencyShortInfo toShortInfo(AgencyEntity entity) {
        return AgencyShortInfo.builder()
                .id(entity.getId())
                .name(entity.getName())
                .phone(entity.getPhone())
                .city(entity.getCity())
                .rating(entity.getRating())
                .build();
    }

    public static AgencyFullInfo toFullInfo(AgencyEntity entity) {
        return AgencyFullInfo.builder()
                .id(entity.getId())
                .ownerId(entity.getOwnerId())
                .name(entity.getName())
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .description(entity.getDescription())
                .city(entity.getCity())
                .address(entity.getAddress())
                .rating(entity.getRating())
                .build();
    }

    public static AgencyDTO toDTO(AgencyEntity agency) {
        return AgencyDTO.builder()
                .id(agency.getId())
                .ownerId(agency.getOwnerId())
                .name(agency.getName())
                .phone(agency.getPhone())
                .email(agency.getEmail())
                .description(agency.getDescription())
                .city(agency.getCity())
                .address(agency.getAddress())
                .approved(agency.getApproved())
                .rating(agency.getRating())
                .build();
    }
}