package com.web.repositorium;

import com.web.enity.game.StatusGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatusGameRepo extends JpaRepository<StatusGame, Long> {
    @Query(value="SELECT MAX(id) FROM game_statuses", nativeQuery = true)
    Optional<Long> findMaxId();
    /*
    TODO Błąd w StatusGameRepo
    Chciałem wykorzystać to @query zeby od razu zwróciło mi status_game, ale wyrzuca błąd The column name id was not found in this ResultSet
    @Query(value="SELECT status_game FROM game_statuses WHERE id = (SELECT MAX(gs.id) FROM game_statuses gs WHERE gs.game_id = :gameId)",
           nativeQuery = true)
     */
    @Query(value="SELECT MAX(gs.id) FROM game_statuses gs WHERE gs.game_id = :gameId", nativeQuery = true)
    Optional<Long> findMaxIdByGameId(@Param("gameId") long gameId);
}
