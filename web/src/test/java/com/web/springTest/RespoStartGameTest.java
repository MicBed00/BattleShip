package com.web.springTest;

import board.StatePreperationGame;
import com.web.repositorium.StartGameRepo;
import com.web.enity.statusGame.StartGame;
import com.web.service.GameService;
import org.junit.jupiter.api.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import serialization.GameStatus;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RespoStartGameTest {
//
//    @Autowired
//    GameService gameService;
//    @Autowired
//    StartGameRepo repository;
//
//    private StartGame startGame;
//
//    @Test
//    public void shouldReturnListElement() {
//        //given
//        GameStatus gameStatus = new GameStatus(gameService.getBoardList(), 1, StatePreperationGame.IN_PROCCESS);
//        startGame = new StartGame(Timestamp.valueOf(LocalDateTime.now()), gameStatus);
//        repository.save(startGame);
//
//        //when
//        List<StartGame> status = repository.findAll();
//
//        //then
//        assertThat(status.size(), equalTo(1));
//    }
//
//    @Test
//    public void shouldReturnTheSameGameStatus() {
//        //given
//        GameStatus gameStatus = new GameStatus(gameService.getBoardList(), 1, StatePreperationGame.IN_PROCCESS);
//        startGame = new StartGame(Timestamp.valueOf(LocalDateTime.now()), gameStatus);
//        repository.save(startGame);
//
//        //when
//        GameStatus gameStatusDataBase = repository.findAll().get(0).getGameStatus();
//
//        //then
//        assertThat(gameStatusDataBase, equalToObject(startGame.getGameStatus()));
//    }
//
//    @Test
//    public void removingElementFromDataBaseShouldDecreaseRepositoryList() {
//        //given
//        GameStatus gameStatus = new GameStatus(gameService.getBoardList(), 1, StatePreperationGame.IN_PROCCESS);
//        startGame = new StartGame(Timestamp.valueOf(LocalDateTime.now()), gameStatus);
//        repository.save(startGame);
//
//        //when
//        repository.delete(startGame);
//
//        //then
//        assertThat(repository.findAll(), hasSize(0));
//        assertThat(repository.findAll(), not(contains(startGame)));
//    }
//
//    @Test
//    public void shouldUpdateElementInDataBase() {
//        //given
//        GameStatus gameStatus = new GameStatus(gameService.getBoardList(), 1, StatePreperationGame.IN_PROCCESS);
//        startGame = new StartGame(Timestamp.valueOf(LocalDateTime.now()), gameStatus);
//        repository.save(startGame);
//
//        //when
//        StartGame startGameUpdated = null;
//        Optional<StartGame> optionalStartGame = repository.findById(1L);
//
//        if(optionalStartGame.isPresent())
//            startGameUpdated = optionalStartGame.get();
//
//        startGameUpdated.getGameStatus().setState(StatePreperationGame.FINISHED);
//        repository.save(startGameUpdated);
//
//        //then
//        assertThat(startGameUpdated.getGameStatus().getState(), equalTo(StatePreperationGame.FINISHED));
//    }

//    @Test
//    public void shouldFindElementByDate() {
//        //given
//        GameStatus gameStatus = new GameStatus(gameService.getBoardList(), 1, StatePreperationGame.IN_PROCCESS);
//        LocalDateTime date = LocalDateTime.now();
//        StartGame startGame = new StartGame(Timestamp.valueOf(date), gameStatus);
//
//        //when
//        repository.save(startGame);
//        StartGame startGameFindByDate = repository.findByDate(Timestamp.valueOf(date)).get();
//
//        //then
//        assertThat(repository.findByDate(Timestamp.valueOf(date)).get(), equalToObject(startGameFindByDate));
//    }

//    @Test
//    void shouldFindMaxIdInDataBase() {
//        //given
//        GameStatus gameStatus = new GameStatus(gameService.getBoardList(), 1, StatePreperationGame.IN_PROCCESS);
//        startGame = new StartGame(Timestamp.valueOf(LocalDateTime.now()), gameStatus);
//        repository.save(startGame);
//
//        //when
//        int maxId = 1;
//
//        //then
//        assertThat(repository.findMaxId(), equalTo(maxId));
//    }

}