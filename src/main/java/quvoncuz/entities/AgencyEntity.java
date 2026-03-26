package quvoncuz.entities;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AgencyEntity {

    private Long id;
    private Long ownerId;
    private String name;
    private String phone;
    private String email;
    private String description;
    private String city;
    private String address;
    private Boolean approved;
    private Double rating;
    private Integer visitedCount;
    private LocalDateTime createdDate;
}
