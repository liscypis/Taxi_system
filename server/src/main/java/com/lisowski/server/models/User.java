package com.lisowski.server.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20)
    private String name;

    @Column(length = 40)
    private String surname;

    @Column(length = 20)
    private String userName;

    @Column(length = 150)
    private String password;

    @Column(length = 40)
    private String email;

    @Column(unique = true, length = 9, nullable = false)
    private String phoneNum;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "car_id", referencedColumnName = "id")
    private Car car;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinTable(name = "driver_status",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "status_id"))
    private Status status;

    @OneToMany(mappedBy = "user")
    private Set<Ride> ride;

    @OneToMany(mappedBy = "driver")
    private Set<Ride> rideDriver;


//    @JsonBackReference
    @OneToOne(mappedBy = "driver")
    private DriverPositionHistory driverPositionHistory;

}
