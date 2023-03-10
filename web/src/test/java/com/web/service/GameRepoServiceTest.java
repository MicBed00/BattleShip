package com.web.service;

import board.Board;
import board.StateGame;
import com.web.enity.game.Game;
import com.web.enity.game.StatusGame;
import com.web.enity.user.User;
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
import org.springframework.context.annotation.Bean;
import serialization.GameStatus;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class GameRepoServiceTest {

    @Mock
    private GameRepo gameRepo;
    @Mock
    private GameStatusRepoService gameStatusRepoService;
    @Mock
    private UserService userService;

    @Mock
    StatusGameRepo repoStatusGame;

    private AutoCloseable autoCloseable;
    private GameRepoService gameRepoService;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        gameRepoService = new GameRepoService(gameRepo, userService, gameStatusRepoService);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void shouldSaveNewGame() {
        //given
        long userId = 1;
        List<Board> boardList = new ArrayList<>();
        StateGame state = StateGame.IN_PROCCESS;
        long owner = 1;
        long gameId = 1;
        User user = new User();
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        Game startGame = new Game(timestamp, owner);
        startGame.setId(1);
        when(userService.getLogInUser(userId)).thenReturn(user);
        doReturn(startGame).when(gameRepo).save(Mockito.any(Game.class));

        //when
        gameRepoService.saveNewGame(userId);

        //then
        verify(userService, times(1)).getLogInUser(userId);
//        verify(gameRepo,times(1)).save(new Game(timestamp, owner));
    }

    @Test
    void shouldReturnFalseForUserWithoutGames() {
        //given
        long userId = 1L;
        User user = new User();
        List<Game> games = user.getGames();
        when(userService.getLogInUser(userId)).thenReturn(user);

        //when
        boolean result = gameRepoService.checkIfLastGameExistAndStatusIsSaved(userId);

        //then
        assertFalse(result);
        verify(userService, times(1)).getLogInUser(userId);
    }

    @Test
    void shouldReturnTrueForUserWithGame() {
        //given
        long userId = 1L;
        long owner = 1;
        User user = new User();
        user.getGames().add(new Game(Timestamp.valueOf(LocalDateTime.now()), owner));
        when(userService.getLogInUser(userId)).thenReturn(user);

        //when
        boolean result = gameRepoService.checkIfLastGameExistAndStatusIsSaved(userId);

        //then
        assertTrue(result);
        verify(userService, times(1)).getLogInUser(userId);
    }
}