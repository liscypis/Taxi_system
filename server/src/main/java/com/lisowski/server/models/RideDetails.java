package com.lisowski.server.models;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;


@Data
@Entity
@Table(name = "ride_deteils")
public class RideDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Instant timeStart;
    private Instant timeEnd;
    private String startPoint;
    private String endPoint;
    private String polyline;
    private float price;

    @OneToOne(mappedBy = "rideDetails")
    private Ride ride;

}
