package com.lisowski.server.repository;

import com.lisowski.server.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String userName);
    Optional<User> findById(Long id);
    Optional<User> findByPhoneNum(String phoneNum);

    Boolean existsByPhoneNum(String phoneNum);
    Boolean existsByUserName(String userName);
    Boolean existsByEmail(String email);
}
