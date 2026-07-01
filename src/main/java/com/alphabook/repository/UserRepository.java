package com.alphabook.repository;

import com.alphabook.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Hoang Van Khoi
 * @date 4/22/2026
 */
public interface  UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
