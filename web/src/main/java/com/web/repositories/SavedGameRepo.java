package com.web.repositories;

import com.web.enity.game.SavedGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SavedGameRepo extends JpaRepository<SavedGame, Long> {

    /*
    //TODO Błąd w StatusGameRepo
    Chciałem wykorzystać to @query zeby od razu zwróciło mi status_game, ale wyrzuca błąd The column name id was not found in this ResultSet
    @Query(value="SELECT status_game FROM game_statuses WHERE id = (SELECT MAX(gs.id) FROM game_statuses gs WHERE gs.game_id = :gameId)",
           nativeQuery = true)
     */
    //TODO zamist Long powinna zawracać optionala
    @Query(value="SELECT MAX(gs.id) FROM game_statuses gs WHERE gs.game_id = :gameId", nativeQuery = true)
    Long findMaxIdByGameId(@Param("gameId") long gameId);
}
