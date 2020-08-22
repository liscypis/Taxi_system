package com.lisowski.server.repository;

import com.lisowski.server.models.Car;
import com.lisowski.server.models.enums.EStatus;
import com.lisowski.server.models.Role;
import com.lisowski.server.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);
    Optional<User> findById(Long id);
    Optional<User> findByPhoneNum(String phoneNum);
    Optional<User> findByEmail(String email);
    Optional<User> findByCarId(Long id);

    Boolean existsByPhoneNum(String phoneNum);
    Boolean existsByUserName(String userName);
    Boolean existsByEmail(String email);

    Optional<List<User>> findByRolesIn(Set<Role> roles);

    Optional<List<User>> findByCarAndRolesIn(Car car, Set<Role> roles);

    @Query("SELECT u.id from User as u WHERE u.status.status = :status ")
    Optional<List<Long>> findIdsByStatus(@Param("status")EStatus status);
}
