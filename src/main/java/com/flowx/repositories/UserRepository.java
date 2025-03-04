package com.flowx.repositories;

import com.flowx.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username); // Find users by username
    Optional<User> findByEmail(String email); // Find users by email
}
