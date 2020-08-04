package com.lisowski.server.models;

import lombok.Data;

import javax.persistence.*;
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

    @OneToOne(mappedBy = "user")
    private Ride ride;

    @OneToOne(mappedBy = "driver")
    private DriverPositionHistory driverPositionHistory;

}
