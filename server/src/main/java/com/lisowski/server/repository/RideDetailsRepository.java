package com.lisowski.server.repository;

import com.lisowski.server.models.RideDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RideDetailsRepository extends JpaRepository<RideDetails, Long> {
}
