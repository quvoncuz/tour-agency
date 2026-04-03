package quvoncuz.dto.agency;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AgencyShortInfo {
    private Long id;
    private String name;
    private String phone;
    private String city;
    private Double rating;
}
