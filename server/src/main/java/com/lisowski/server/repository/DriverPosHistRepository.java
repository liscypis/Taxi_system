package com.lisowski.server.repository;

import com.lisowski.server.DTO.DriverPositionHistoryDTO;
import com.lisowski.server.models.DriverPositionHistory;
import com.lisowski.server.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverPosHistRepository extends JpaRepository<DriverPositionHistory, Long> {

    @Query("SELECT new com.lisowski.server.DTO.DriverPositionHistoryDTO(d.id, d.data, d.location, d.driver.id) " +
            "FROM DriverPositionHistory AS d " +
            "WHERE d.id IN (SELECT MAX(d.id) FROM d WHERE d.driver.id in :ids GROUP BY d.driver.id ) ")
    List<DriverPositionHistoryDTO> findLastPositions(@Param("ids") List<Long> ids);

    List<DriverPositionHistory> findAllByDriver_Id(Long id);

    void deleteAllByDriver(User u);
}
