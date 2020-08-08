package com.lisowski.server.DTO.response;

import com.lisowski.server.models.Ride;
import lombok.Data;

@Data
public class RideDetailsResponse {
    private Long idRide;
    private Long idDriver;
    private Long userDistance;
    private Long driverDistance;
    private Long userDuration;
    private Long driverDuration;
    private String userPolyline;
    private String driverPolyline;
    private String userLocation;
    private String userDestination;
    private float approxPrice;

    public RideDetailsResponse(Ride ride) {
        this.idRide = ride.getId();
        this.idDriver = ride.getDriver().getId();
        this.userPolyline = ride.getRideDetails().getUserPolyline();
        this.driverPolyline = ride.getRideDetails().getDriverPolyline();
        this.userLocation = ride.getRideDetails().getWaypoint();
        this.userDestination = ride.getRideDetails().getEndPoint();
        this.userDistance = ride.getRideDetails().getUserDistance();
        this.driverDistance = ride.getRideDetails().getDriverDistance();
    }
    public RideDetailsResponse() {

    }




}
