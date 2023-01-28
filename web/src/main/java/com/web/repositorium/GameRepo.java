package com.web.repositorium;

import com.web.enity.game.StartGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepo extends JpaRepository<StartGame,Long> {
    @Query(value = "SELECT MAX(id) FROM games", nativeQuery = true)
    Optional<Long> findMaxId();

    //szukam najwyższego id game dla konkretnego użytkownika.
    //TODO Optional jest konieczny?? request uderza do serwera tylko wtedy gdy User grał wcześniej
    @Query(value = "SELECT MAX(id) FROM games WHERE user_id = :userId", nativeQuery = true)
    Optional<Long> findMaxIdByUserId(@Param("userId")long userId);

    //TODO tu trzeba zapisać @Query typu exist https://www.baeldung.com/spring-data-exists-query
    /*przydałby się dodatkowy warunek w tym zapytaniu by zwracał tylko  w sytuacji gdy dana gra ma
    zapisany satus w tabeli game_statuses. Tzn. że znalezione game_id jest zarejstrowane w tabeli game_statuses
    bo w tej sytuacji, może być przypadek taki, że gracz ma grę w tabali games, ale nie ma zapisanego stanu
    gry w tabeli game_statuses i nie można wznowić tej gry bo nie ma z czego
    */
     /*SELECT COUNT(*) FROM game_statuses WHERE game_id = (
      "SELECT COUNT(*) FROM games WHERE user_id = :userId")

     "SELECT COUNT(*) FROM games g WHERE g.id = :userId AND g.id in
     (SELECT at.id FROM AnotherTable at GROUP BY at.id HAVING COUNT(at.id) > 0)")

      */
    @Query(value="SELECT COUNT(*) > 0 FROM game_statuses gs WHERE gs.game_id = (SELECT MAX(id) FROM games g WHERE user_id = :userId)",
            nativeQuery = true)
    boolean existsByUserId(@Param("userId") long userId);

}
