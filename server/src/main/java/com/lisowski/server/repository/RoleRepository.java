package com.lisowski.server.repository;

import com.lisowski.server.models.enums.ERole;
import com.lisowski.server.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByRole(ERole name);

}
