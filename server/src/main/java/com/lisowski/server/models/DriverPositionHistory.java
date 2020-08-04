package com.lisowski.server.models;

import lombok.Data;

import javax.annotation.processing.Generated;
import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
@Table(name = "driver_position_history")
public class DriverPositionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Instant data;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "driver_id", referencedColumnName = "id")
    private User driver;


}
