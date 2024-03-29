package com.web.repositories;

import com.web.gameSetups.GameSetups;
import com.web.enity.game.Game;
import com.web.enity.game.SavedGame;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import serialization.GameStatus;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("classpath:/init_fixtures.sql")
class SavedGameRepoTest {

    @Autowired
    SavedGameRepo savedGameRepo;
    @Autowired
    GameRepo gameRepo;

    @DirtiesContext
    @Transactional
    @Test
    void shouldFindMaxIdByGameId() {
        //given
        Game game1 = new Game(Timestamp.valueOf(LocalDateTime.now()), 1L, new GameSetups());
        Game game2 = new Game(Timestamp.valueOf(LocalDateTime.now()), 1L, new GameSetups());
        gameRepo.save(game1);
        gameRepo.save(game2);
        SavedGame savedGame = new SavedGame(new GameStatus(), game1);
        SavedGame savedGame1 = new SavedGame(new GameStatus(), game2);
        SavedGame save1 = savedGameRepo.save(savedGame);
        SavedGame save2 = savedGameRepo.save(savedGame1);
        SavedGame save3 = savedGameRepo.save(savedGame);
        SavedGame save4 = savedGameRepo.save(savedGame1);
        SavedGame save5 = savedGameRepo.save(savedGame1);
        SavedGame save6 = savedGameRepo.save(savedGame);
        SavedGame save7 = savedGameRepo.save(savedGame);

        //when
        Long result = savedGameRepo.findMaxIdByGameId(1L);

        //then
        assertEquals(1,result);
    }

//    @PersistenceContext
//    private EntityManager entityManager;
//
//
//
//...
//
//
//        entityManager.flush();
//    entityManager.clear();

    @DirtiesContext
    @Transactional
    @Test
    void shouldSaveStatusGame() {
        //given
        Game game = new Game(Timestamp.valueOf(LocalDateTime.now()), 1L, new GameSetups());
        gameRepo.save(game);
        SavedGame savedGame = new SavedGame(new GameStatus(), game);

        //when
        SavedGame result = savedGameRepo.save(savedGame);

        //then
        assertNotNull(result);
    }

}