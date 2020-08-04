package com.lisowski.server.repository;

import com.lisowski.server.models.DriverPositionHistory;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DriverPosHistRepository extends JpaRepository<DriverPositionHistory, Long> {
}
