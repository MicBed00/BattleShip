package com.web.repositorium;

import com.web.enity.statusGame.StartGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StartGameRepo extends JpaRepository<StartGame,Long> {
    @Query(value = "SELECT MAX(id) FROM start_game", nativeQuery = true)
    int findMaxId();
}
