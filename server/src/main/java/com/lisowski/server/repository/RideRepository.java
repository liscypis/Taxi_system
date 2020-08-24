package com.lisowski.server.repository;

import com.lisowski.server.models.Ride;
import com.lisowski.server.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    Optional<List<Ride>> findByDriver_IdAndRideStatus(Long idDriver, String statusComplete);

    Optional<List<Ride>> findByUser_IdAndRideStatus(Long idDriver, String statusComplete);

    Optional<List<Ride>> findByRideStatusIn(List<String> notComplete);

    Optional<Ride> findByDriver_IdAndRideStatusIn(Long id, List<String> noAppOrToUser);

    Integer deleteAllByUser(User u);
    Integer deleteAllByDriver(User u);

}
