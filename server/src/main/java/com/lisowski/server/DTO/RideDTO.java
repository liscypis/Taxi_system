package com.lisowski.server.DTO;

import com.lisowski.server.models.Ride;
import lombok.Data;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Data
public class RideDTO {
    private Long idRide;
    private Long idUser;
    private Long idDriver;
    private Long userDistance;
    private Long driverDistance;
    private Instant timeStart;
    private Instant arrivalTime;
    private Instant endTime;
    private String userPolyline;
    private String driverPolyline;
    private String userLocation;
    private String userDestination;
    private Integer rating;
    private float price;
    private String userName;
    private String userSurname;
    private String userNumber;
    private String driverName;
    private String driverSurname;
    private String driverNumber;

    public RideDTO(Ride ride) {
        this.idRide = ride.getId();
        this.idDriver = ride.getDriver().getId();
        this.idUser = ride.getUser().getId();
        this.userPolyline = ride.getRideDetails().getUserPolyline();
        this.driverPolyline = ride.getRideDetails().getDriverPolyline();
        this.timeStart = ride.getRideDetails().getTimeStart().plus(2, ChronoUnit.HOURS);
        this.arrivalTime = ride.getRideDetails().getTimeArriveToUser().plus(2, ChronoUnit.HOURS);
        this.endTime = ride.getRideDetails().getTimeEnd().plus(2, ChronoUnit.HOURS);
        this.userLocation = ride.getRideDetails().getWaypoint();
        this.userDestination = ride.getRideDetails().getEndPoint();
        this.userDistance = ride.getRideDetails().getUserDistance();
        this.driverDistance = ride.getRideDetails().getDriverDistance();
        this.price = ride.getRideDetails().getPrice();
        this.rating = ride.getRideDetails().getRating();
        this.userName = ride.getUser().getName();
        this.userSurname = ride.getUser().getSurname();
        this.userNumber = ride.getUser().getPhoneNum();
        this.driverName = ride.getDriver().getName();
        this.driverSurname = ride.getDriver().getSurname();
        this.driverNumber = ride.getDriver().getPhoneNum();
    }
}
