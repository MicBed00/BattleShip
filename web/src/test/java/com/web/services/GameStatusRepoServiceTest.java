package com.web.service;

import board.Board;
import board.StateGame;
import com.web.enity.game.Game;
import com.web.enity.game.StatusGame;
import com.web.repositorium.GameRepo;
import com.web.repositorium.StatusGameRepo;
import dataConfig.Position;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import serialization.GameStatus;
import ship.Ship;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameStatusRepoServiceTest {
    @Mock
    private GameRepo gameRepo;
    @Mock
    private StatusGameRepo repoStatusGame;
    @Mock
    private GameStatusService gameStatusService;
    @Mock
    private UserService userService;

    private AutoCloseable autoCloseable;
    private GameStatusRepoService gameStatusRepoService;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        gameStatusRepoService = new GameStatusRepoService(gameRepo, gameStatusService
                                                          ,repoStatusGame, userService);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void saveGameStatusToDataBase() {
        //given
        List<Board> list = new ArrayList<>();
        list.add(new Board());
        list.add(new Board());
        int currentPlayer = 1;
        long gameId = 1;
        long ownerGame = 1;
        Game game = new Game(Timestamp.valueOf(LocalDateTime.now()), ownerGame);
        given(gameStatusService.getCurrentPlayer(gameId)).willReturn(currentPlayer);
        given(gameRepo.findById(gameId)).willReturn(Optional.of(game));
        //when
        gameStatusService.saveGameStatusToDataBase(list, StateGame.IN_PROCCESS, gameId);
        //then
        verify(gameStatusService, times(1)).getCurrentPlayer(gameId);
        verify(repoStatusGame, times(1)).save(any(StatusGame.class));
    }

    @Test
    void shouldDeleteLastShip() {
        //given
        int userId = 1;
        long gameId = 1;
        long owner = 1;
        int currentPly = 1;
        long idStatusGame = 1;
        Board boardWithShip = new Board();
        boardWithShip.getShips().add(new Ship(1,1,1, Position.VERTICAL));
        List<Board> boardList = List.of(boardWithShip, new Board());
        Game game = new Game(Timestamp.valueOf(LocalDateTime.now()), owner);
        StatusGame statusGame = new StatusGame(new GameStatus(boardList,currentPly,StateGame.IN_PROCCESS),game);
        given(gameRepo.findById(gameId)).willReturn(Optional.of(game));
        given(repoStatusGame.findMaxIdByGameId(gameId)).willReturn(1L);
        given(repoStatusGame.findById(idStatusGame)).willReturn(Optional.of(statusGame));
        given(gameStatusService.getCurrentPlayer(gameId)).willReturn(currentPly);

        //when
        gameStatusRepoService.deleteShip(userId, gameId);

        //then
        assertEquals(0, boardWithShip.getShips().size());
    }

    @Test
    void shouldThrowExceptionWhenLastShipDoesNotExist() {
        //given
        int index = 1;
        long gameId = 1;
        long owner = 1;
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        Game game = new Game(timestamp, owner);
        given(gameRepo.findById(gameId)).willReturn(Optional.of(game));

        //when
        //then
        NoSuchElementException e = assertThrows(NoSuchElementException.class, () -> {
            gameStatusRepoService.deleteShip(index, gameId);
        });
        assertEquals("User has not yet added the ship", e.getMessage());
        verify(repoStatusGame, never()).deleteLast(gameId);
    }

    @Test
    void shouldReturnSavedStateGame() {
        //given
        long userId = 1;
        Game game = new Game();
        game.setId(1);
        long idStatusGame = 2;
        StatusGame status = new StatusGame();
        given(userService.getLastUserGames(userId)).willReturn(game);
        given(repoStatusGame.findMaxIdByGameId(game.getId())).willReturn(idStatusGame);
        given(repoStatusGame.findById(idStatusGame)).willReturn(Optional.of(status));

        //when
        gameStatusRepoService.getSavedStateGame(userId);

        //then
        verify(userService).getLastUserGames(userId);
        verify(repoStatusGame).findMaxIdByGameId(game.getId());
        verify(repoStatusGame).findById(idStatusGame);
    }

    @Test
    void shouldThrowExceptionWhenStateGameIsNotSaved() {
        //given
        long userId = 1;
        Game game = new Game();
        game.setId(1);
        long idStatusGame = 2;
        StatusGame status = new StatusGame();
        given(userService.getLastUserGames(userId)).willReturn(game);
        given(repoStatusGame.findMaxIdByGameId(game.getId())).willReturn(idStatusGame);
        given(repoStatusGame.findById(idStatusGame)).willReturn(Optional.empty());

        //when
        //then
        NoSuchElementException e = assertThrows(NoSuchElementException.class, () -> {
            gameStatusRepoService.getSavedStateGame(userId);
        });
        assertEquals("User has not yet added the ship", e.getMessage());
        verify(userService).getLastUserGames(userId);
        verify(repoStatusGame).findMaxIdByGameId(game.getId());
        verify(repoStatusGame).findById(idStatusGame);
    }

    @Test
    void updateStatePreperationGame() {
        //given
        String state = "FINISHED";
        StatusGame savedGame = new StatusGame(new GameStatus(new ArrayList<>(),1, StateGame.IN_PROCCESS)
                                            , new Game());
        long userId = 1;
        Game game = new Game();
        game.setId(1);
        long idStatusGame = 2;
        given(userService.getLastUserGames(userId)).willReturn(game);
        given(repoStatusGame.findMaxIdByGameId(game.getId())).willReturn(idStatusGame);
        given(repoStatusGame.findById(idStatusGame)).willReturn(Optional.of(savedGame));

        //when
        gameStatusRepoService.updateStatePreperationGame(1, state);

        //then
        verify(repoStatusGame, times(1)).save(any(StatusGame.class));
    }
}