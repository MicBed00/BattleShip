package com.web.springTest;

import board.StatePreperationGame;
import com.web.RepoStartGame;
import com.web.StartGame;
import com.web.service.GameService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import serialization.GameStatus;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RespoStartGameTest {

    @Autowired
    GameService gameService;
    @Autowired
    RepoStartGame repository;


    @Test
    public void startGameTableShouldBeEmpty() {
        List<StartGame> status = repository.findAll();

        assertThat(status, empty());
    }

    @Test
    public void shouldSaveStartGameStatus() {
        //given
        GameStatus gameStatus = new GameStatus(gameService.getBoardList(), 1, StatePreperationGame.IN_PROCCESS);
        StartGame startGame = new StartGame(Timestamp.valueOf(LocalDateTime.now()), gameStatus);
        repository.save(startGame);

        //when
        List<StartGame> status = repository.findAll();

        //then
        assertThat(status.size(), equalTo(1));
    }



}