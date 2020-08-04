package com.lisowski.server.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "status")
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EStatus status;
}
