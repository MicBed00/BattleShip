package com.web.service;

import board.Board;
import board.StateGame;
import com.web.enity.game.Game;
import com.web.enity.game.StatusGame;
import com.web.repositorium.GameRepo;
import com.web.repositorium.StatusGameRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
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
    @Mock
    private GameRepo gameRepo;
    private AutoCloseable autoCloseable;
    private GameStatusRepoService gameStatusRepoService;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        gameStatusRepoService = new GameStatusRepoService(repoStartGame, gameStatusService
                                                          ,repoStatusGame, userService);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

//    @Test
//    void saveGameStatusToDataBase() {
//        //given
//        List<Board> list = new ArrayList<>();
//        list.add(new Board());
//        list.add(new Board());
//        int currentPlayer = 1;
//        long gameId = 1;
//        given(gameStatusService.getCurrentPlayer(gameId)).willReturn(currentPlayer);
//        long userId = 1;
//        given(userService.getUserId()).willReturn(userId);
//        Game game = new Game(Timestamp.valueOf(LocalDateTime.now()));
//        game.setId(1);
//        gameRepo.save(game);
//        given(gameRepo.findById(gameId).orElseThrow(
//                () -> new NoSuchElementException("Brak gry w bazie")
//        )).willReturn(game);
//
//        //when
//        gameStatusRepoService.saveGameStatusToDataBase(list, StateGame.IN_PROCCESS, gameId);
//
//        //then
//        verify(gameStatusService, times(1)).getCurrentPlayer(gameId);
//        verify(userService, times(1)).getUserId();
//        verify(repoStatusGame, times(1)).save(any(StatusGame.class));
//    }

    @Test
    void shouldDeleteLastShip() {
        //given
        int index = 1;
        long gameId = 1;
//        given(repoStartGame.findMaxId()).willReturn(Optional.of(gameId));

        //when
        gameStatusRepoService.deleteShip(index, gameId);

        //then
//        verify(repoStartGame).findMaxId();
        verify(repoStatusGame).deleteLast(gameId);
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

        //theb
        verify(repoStatusGame, times(1)).save(any(StatusGame.class));
    }
}