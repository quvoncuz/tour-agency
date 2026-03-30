package quvoncuz.mapper;

import quvoncuz.dto.agency.AgencyDTO;
import quvoncuz.dto.agency.AgencyFullInfo;
import quvoncuz.dto.agency.AgencyShortInfo;
import quvoncuz.entities.AgencyEntity;

public class AgencyMapper {

    public static AgencyShortInfo toShortInfo(AgencyEntity entity) {
        return new AgencyShortInfo(
                entity.getId(),
                entity.getName(),
                entity.getPhone(),
                entity.getCity(),
                entity.getRating()
        );
    }

    public static AgencyFullInfo toFullInfo(AgencyEntity entity) {
        return new AgencyFullInfo(
                entity.getId(),
                entity.getOwnerId(),
                entity.getName(),
                entity.getPhone(),
                entity.getEmail(),
                entity.getDescription(),
                entity.getCity(),
                entity.getAddress(),
                entity.getRating()
        );
    }

    public static AgencyDTO toDTO(AgencyEntity agency) {
        return new AgencyDTO(
                agency.getId(),
                agency.getOwnerId(),
                agency.getName(),
                agency.getPhone(),
                agency.getEmail(),
                agency.getDescription(),
                agency.getCity(),
                agency.getAddress(),
                agency.getApproved(),
                agency.getRating()
        );
    }
}
