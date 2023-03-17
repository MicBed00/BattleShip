package com.web.repositories;

import com.web.enity.game.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepo extends JpaRepository<Game,Long> {
    //TODO błąd przy pobieraniu gry dla użytkownika
    @Query(value = "SELECT MAX(id) FROM games", nativeQuery = true)
    Optional<Long> findMaxId();

}
