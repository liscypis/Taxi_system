package com.lisowski.server.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "cars")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, nullable = false)
    private String carModel;

    @Column(length = 30, nullable = false)
    private String carBrand;

    @Column(length = 30, nullable = false)
    private String color;

    @Column(unique = true, length = 7, nullable = false)
    private String registrationNumber;

    @OneToOne(mappedBy = "car")
    private User user;

}
