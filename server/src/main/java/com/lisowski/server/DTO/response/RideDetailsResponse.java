package com.lisowski.server.DTO.response;

import lombok.Data;

@Data
public class RideDetailsResponse {
    private Long userDistance;
    private Long driverDistance;
    private Long userDuration;
    private Long driverDuration;
    private String userPolyline;
    private String driverPolyline;
    private float approxPrice;

}
