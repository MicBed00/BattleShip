package com.web.repositorium;

import com.web.enity.game.StatusGame;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatusGameRepo extends JpaRepository<StatusGame, Long> {

    /*
    //TODO Błąd w StatusGameRepo
    Chciałem wykorzystać to @query zeby od razu zwróciło mi status_game, ale wyrzuca błąd The column name id was not found in this ResultSet
    @Query(value="SELECT status_game FROM game_statuses WHERE id = (SELECT MAX(gs.id) FROM game_statuses gs WHERE gs.game_id = :gameId)",
           nativeQuery = true)
     */
    //TODO zamist Long powinna zawracać optionala
    @Query(value="SELECT MAX(gs.id) FROM game_statuses gs WHERE gs.game_id = :gameId", nativeQuery = true)
    Long findMaxIdByGameId(@Param("gameId") long gameId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM game_statuses gs WHERE gs.id = (SELECT MAX(id) FROM game_statuses WHERE game_id = :gameId)", nativeQuery = true)
    void deleteLast(@Param("gameId") long gameId);
}
