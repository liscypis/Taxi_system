package com.lisowski.server.repository;

import com.lisowski.server.models.Ride;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RideRepository extends JpaRepository<Ride, Long> {
}
