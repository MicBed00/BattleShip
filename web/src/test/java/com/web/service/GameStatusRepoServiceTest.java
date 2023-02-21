package com.web.service;

import board.Board;
import board.StatePreperationGame;
import com.web.enity.game.StartGame;
import com.web.enity.game.StatusGame;
import com.web.repositorium.GameRepo;
import com.web.repositorium.StatusGameRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import serialization.GameStatus;

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
    private GameRepo repoStartGame;
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
        gameStatusRepoService = new GameStatusRepoService(repoStartGame, gameStatusService
                                                        , repoStatusGame, userService);
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
        given(gameStatusService.getCurrentPlayer()).willReturn(currentPlayer);
        long userId = 1;
        given(userService.getUserId()).willReturn(userId);
        StartGame startGame = new StartGame(Timestamp.valueOf(LocalDateTime.now()));
        startGame.setId(1);
        given(userService.getLastUserGames(userId)).willReturn(startGame);

        //when
        gameStatusRepoService.saveGameStatusToDataBase(list, StatePreperationGame.IN_PROCCESS);

        //then
        verify(gameStatusService, times(1)).getCurrentPlayer();
        verify(userService, times(1)).getUserId();
        verify(repoStatusGame, times(1)).save(any(StatusGame.class));
    }

    @Test
    void shouldDeleteLastShip() {
        //given
        int index = 1;
        long gameId = 1;
        given(repoStartGame.findMaxId()).willReturn(Optional.of(gameId));

        //when
        gameStatusRepoService.deleteLastShip(index);

        //then
        verify(repoStartGame).findMaxId();
        verify(repoStatusGame).deleteLast(gameId);
        verify(gameStatusService).deleteShipFromServer(index);
    }

    @Test
    void shouldThrowExceptionWhenLastShipDoesNotExist() {
        //given
        int index = 1;
        long gameId = 1;
        given(repoStartGame.findMaxId()).willReturn(Optional.empty());

        //when
        //then
        NoSuchElementException e = assertThrows(NoSuchElementException.class, () -> {
            gameStatusRepoService.deleteLastShip(index);
        });
        assertEquals("User has not yet added the ship", e.getMessage());
        verify(repoStatusGame, never()).deleteLast(gameId);
        verify(gameStatusService, never()).deleteShipFromServer(index);
    }

    @Test
    void shouldReturnSavedStateGame() {
        //given
        long userId = 1;
        StartGame game = new StartGame();
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
        StartGame game = new StartGame();
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
        StatusGame savedGame = new StatusGame(new GameStatus(new ArrayList<>(),1, StatePreperationGame.IN_PROCCESS)
                                            , new StartGame());
        long userId = 1;
        StartGame game = new StartGame();
        game.setId(1);
        long idStatusGame = 2;
        given(userService.getLastUserGames(userId)).willReturn(game);
        given(repoStatusGame.findMaxIdByGameId(game.getId())).willReturn(idStatusGame);
        given(repoStatusGame.findById(idStatusGame)).willReturn(Optional.of(savedGame));

        //when
        gameStatusRepoService.updateStatePreperationGame(1, state);

        //then
        //TODO jak sprawdzić czy nastąpił update statusu
        verify(repoStatusGame, times(1)).save(any(StatusGame.class));
    }
}