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

    @Column(unique = true, length = 20)
    private String userName;

    @Column(length = 150)
    private String password;

    @Column(unique = true, length = 40)
    private String email;

    @Column(unique = true, length = 9, nullable = false)
    private String phoneNum;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

}
