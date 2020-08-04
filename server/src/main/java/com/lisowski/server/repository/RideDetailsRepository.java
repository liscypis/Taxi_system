package com.lisowski.server.repository;

import com.lisowski.server.models.RideDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideDetailsRepository extends JpaRepository<RideDetails, Long> {
}
