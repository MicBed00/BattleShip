package com.web.repositorium;

import com.web.enity.StartGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
public interface RepoStartGame extends JpaRepository<StartGame,Long> {
    Optional<StartGame> findByDate(Timestamp date);

    @Query(value = "SELECT MAX(id) FROM start_game", nativeQuery = true)
    int findMaxId();
}
