package com.lisowski.server.repository;

import com.lisowski.server.models.EStatus;
import com.lisowski.server.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {
    Optional<Status> findByStatus(EStatus name);
}
