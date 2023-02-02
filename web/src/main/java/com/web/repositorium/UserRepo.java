package com.web.repositorium;

import com.web.enity.game.StartGame;
import com.web.enity.user.User;
import org.hibernate.mapping.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    User findTopByOrderByIdDesc();
//
//    @Query("SELECT * FROM users WHERE id = :userId ORDER BY ")
//    StartGame findLatesGameForUser(@Param("userId") long userId);


}
