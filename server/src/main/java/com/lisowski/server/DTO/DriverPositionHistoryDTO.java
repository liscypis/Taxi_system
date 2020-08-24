package com.lisowski.server.DTO;

import com.lisowski.server.models.DriverPositionHistory;
import com.lisowski.server.repository.DriverPosHistRepository;
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

    public DriverPositionHistoryDTO(DriverPositionHistory pos) {
        this.id = pos.getId();
        this.data = pos.getData().plus(2, ChronoUnit.HOURS);
        this.location = pos.getLocation();
    }
}
