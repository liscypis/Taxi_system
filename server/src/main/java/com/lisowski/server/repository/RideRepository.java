package com.lisowski.server.repository;

import com.lisowski.server.models.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {
    Optional<Ride> findByDriver_IdAndRideStatusOrRideStatus(Long idDriver, String onTheWay, String noApp);
}
