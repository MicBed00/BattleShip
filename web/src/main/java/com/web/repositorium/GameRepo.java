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
    //TODO Optional jest konieczny?? ten request uderza do serwera tylko wtedy gdy User grał wcześniej
    @Query(value = "SELECT MAX(id) FROM games WHERE user_id = :userId", nativeQuery = true)
    Optional<Long> findMaxIdByUserId(@Param("userId")long userId);

    //TODO tu trzeba zapisać @Query typu exist https://www.baeldung.com/spring-data-exists-query
    Optional<StartGame> findByUserId(long userId);

}
