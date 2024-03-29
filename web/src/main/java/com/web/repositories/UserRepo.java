package com.web.repositories;

import com.web.enity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

//    @Query(value = "SELECT * FROM users WHERE id = :userId ORDER BY id DESC LIMIT 1", nativeQuery = true)
//    StartGame findLatesGameForUser(@Param("userId") long userId);
}
