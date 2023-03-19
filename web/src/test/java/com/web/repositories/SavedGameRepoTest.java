package com.web.repositories;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("classpath:/init_fixtures.sql")
class SavedGameRepoTest {

    @Autowired
    StatusGameRepo statusGameRepo;
    @Autowired
    GameRepo gameRepo;

//    @DirtiesContext
//    @Transactional
//    @Test
//    void shouldFindMaxIdByGameId() {
//        //given
//        Game game = new Game();
//        gameRepo.save(game);
//        SavedGame savedGame = new SavedGame(new GameStatus(), game);
//        SavedGame save = statusGameRepo.save(savedGame);
//
//        //when
//        Long result = statusGameRepo.findMaxIdByGameId(3L);
//
//        //then
//        assertEquals(1,result);
//    }

    @DirtiesContext
    @Transactional
    @Test
    void shouldSaveStatusGame() {
        //given
        Game game = new Game();
        gameRepo.save(game);
        SavedGame savedGame = new SavedGame(new GameStatus(), game);

        //when
        SavedGame result = statusGameRepo.save(savedGame);

        //then
        assertNotNull(result);
    }

}