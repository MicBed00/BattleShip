package com.web.repositorium;

import com.web.enity.game.StartGame;
import com.web.enity.game.StatusGame;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import serialization.GameStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql("classpath:/init_fixtures.sql")
class StatusGameRepoTest {

    @Autowired
    StatusGameRepo statusGameRepo;
    @Autowired
    GameRepo gameRepo;

    @DirtiesContext
    @Transactional
    @Test
    void shouldFindMaxIdByGameId() {
        //given
        StartGame startGame = new StartGame();
        gameRepo.save(startGame);
        StatusGame statusGame = new StatusGame(new GameStatus(), startGame);
        statusGameRepo.save(statusGame);

        //when
        Long result = statusGameRepo.findMaxIdByGameId(1L);

        //then
        assertEquals(1,result);
    }

    @DirtiesContext
    @Test
    void shouldDeleteLastStatusGameByGameId() {
        //given
        StartGame startGame = new StartGame();
        gameRepo.save(startGame);
        StatusGame statusGame1 = new StatusGame(new GameStatus(), startGame);
        StatusGame statusGame2 = new StatusGame(new GameStatus(), startGame);
        statusGameRepo.save(statusGame1);
        statusGameRepo.save(statusGame2);

        //when
        statusGameRepo.deleteLast(1L);

        //then
        assertEquals(1, statusGameRepo.findAll().size());
    }

    @DirtiesContext
    @Transactional
    @Test
    void shouldSaveStatusGame() {
        //given
        StartGame startGame = new StartGame();
        gameRepo.save(startGame);
        StatusGame statusGame = new StatusGame(new GameStatus(), startGame);

        //when
        StatusGame result = statusGameRepo.save(statusGame);

        //then
        assertNotNull(result);
    }

}