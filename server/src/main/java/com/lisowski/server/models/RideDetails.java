package com.lisowski.server.models;

import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.Instant;


@Data
@Entity
@Table(name = "ride_deteils")
public class RideDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @UpdateTimestamp
    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    private Instant timeStart;

    private Instant timeArriveToUser;
    private Instant timeEnd;
    private String startPoint;
    private String endPoint;
    @Column(length = 1500)
    private String userPolyline;
    @Column(length = 1500)
    private String driverPolyline;
    private float price;

    @OneToOne(mappedBy = "rideDetails")
    private Ride ride;

}
