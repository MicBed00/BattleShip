package com.web.repositorium;

import com.web.enity.statusGame.StartGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepo extends JpaRepository<StartGame,Long> {
    @Query(value = "SELECT MAX(id) FROM games", nativeQuery = true)
    Optional<Long> findMaxId();
}
