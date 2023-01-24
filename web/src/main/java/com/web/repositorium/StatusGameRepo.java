package com.web.repositorium;

import com.web.enity.statusGame.StatusGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatusGameRepo extends JpaRepository<StatusGame, Long> {
    @Query(value = "SELECT MAX(id) FROM game_statuses", nativeQuery = true)
    Optional<Long> findMaxId();
}
