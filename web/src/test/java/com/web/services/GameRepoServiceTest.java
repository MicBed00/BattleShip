package com.web.services;

import board.Board;
import board.StateGame;
import com.web.enity.game.Game;
import com.web.enity.user.User;
import com.web.repositories.GameRepo;
import com.web.repositories.StatusGameRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Mock
    GameStatusService gameStatusService;

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
        gameStatusService.saveNewGame(userId);

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

        //when
        boolean result = gameRepoService.checksUnfinishedGames(userId);

        //then
        assertFalse(result);
        verify(gameStatusRepoService, times(1)).getUnfinishedUserGames();
    }
}