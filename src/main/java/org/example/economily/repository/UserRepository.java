package org.example.economily.repository;

import jakarta.validation.constraints.NotBlank;
import org.example.economily.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    @Query("select u from User u where u.email =:email ")
    Optional<User> findByEmail(@NotBlank(message = "email cannot be null or empty") String email);


    @Query(value = "SELECT * FROM users WHERE email = :email AND status != 'PENDING' ORDER BY created_at DESC LIMIT 1 ",nativeQuery = true)
    Optional<User> findByEmailPending(@NotBlank(message = "email cannot be null or empty") String email);
}
