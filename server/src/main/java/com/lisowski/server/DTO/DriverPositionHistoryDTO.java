package com.lisowski.server.DTO;

import lombok.Data;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Data
public class DriverPositionHistoryDTO {
    private Long id;
    private Instant data;
    private String location;
    private Long driverId;

    public DriverPositionHistoryDTO(Long id, Instant data, String location, Long driverId) {
        this.id = id;
        this.data = data.plus(2, ChronoUnit.HOURS);
        this.location = location;
        this.driverId = driverId;
    }
}
