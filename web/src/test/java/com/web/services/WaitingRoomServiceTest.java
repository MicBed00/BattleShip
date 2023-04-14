package com.web.services;

import board.StateGame;
import com.web.configuration.GameSetups;
import com.web.configuration.GameSetupsDto;
import com.web.enity.game.Game;
import com.web.enity.game.SavedGame;
import com.web.enity.user.User;
import com.web.repositories.GameRepo;
import com.web.repositories.SavedGameRepo;
import dataConfig.Position;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import serialization.GameStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WaitingRoomServiceTest {

    @Mock
    UserService userService;

    @Mock
    GameRepo gameRepo;

    @Mock
    GameService gameService;

    @Mock
    SavedGameRepo savedGameRepo;

    @Mock
    SavedGameService savedGameService;

    @InjectMocks
    WaitingRoomService waitingRoomService;

    @DirtiesContext
    @Test
    public void shouldSaveNewGame() {
        //given
        long userId = 1;
        int sizeBoard = 10;
        List<Integer> shipsSize = List.of(1, 2, 3, 4);
        List<Position> positions = List.of(Position.HORIZONTAL, Position.VERTICAL);
        int shipNumLimit = 5;
        GameSetupsDto gsDto = new GameSetupsDto(shipsSize, positions, shipNumLimit);
        GameSetups gameSetups = new GameSetups();
        Game game = new Game(1);
        User user = new User();
        given(gameService.createGameSetups(gsDto.getShipSize()
                , gsDto.getOrientations()
                , gsDto.getShipLimit())).willReturn(gameSetups);
        given(gameService.createGame(userId, gameSetups)).willReturn(game);
        given(gameService.saveGame(game)).willReturn(game);
        given(userService.getLogInUser(userId)).willReturn(user);

        //when
        Integer result = waitingRoomService.saveNewGame(userId, sizeBoard, gsDto);

        //then
        verify(gameService).createGame(userId, gameSetups);
        verify(gameService).createGameSetups(gsDto.getShipSize()
                , gsDto.getOrientations()
                , gsDto.getShipLimit());
        verify(gameService).saveGame(game);
        verify(userService).getLogInUser(userId);
        assertEquals(1, result);
    }

    @DirtiesContext
    @Test
    public void whenStatusDoesntChangedShouldReturnEmptyList() {
        //given
        long userId = 1L;
        User user = new User();
        user.setGames(List.of(new Game(1)));
        given(userService.getUserId()).willReturn(userId);
        given(userService.getLogInUser(userId)).willReturn(user);

        //when
        List<Long> result = waitingRoomService.checkIfSavedGameStatusHasChanged();

        //then
        assertTrue(result.isEmpty());
    }

    @DirtiesContext
    @Test
    public void whenOpponentAppearsShouldReturnListSizeTwo() {
        //given
        long gameId = 1;
        Game game = new Game(1);
        given(gameRepo.findById(gameId)).willReturn(Optional.of(game));
        SavedGame savedGame = new SavedGame(new GameStatus(List.of(), StateGame.REQUESTING), game);
        given(savedGameService.getSavedGame(game.getId())).willReturn(savedGame);

        //when
        List<String> result = waitingRoomService.checkIfOpponentAppears(gameId);

        //then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("true", result.get(0));
        assertEquals(String.valueOf(gameId), result.get(1));

    }

    @DirtiesContext
    @Test
    public void whenOpponentDoesntAppearShouldReturnListSizeOne() {
        //given
        long gameId = 1;
        Game game = new Game(1);
        given(gameRepo.findById(gameId)).willReturn(Optional.of(game));
        SavedGame savedGame = new SavedGame(new GameStatus(List.of(), StateGame.WAITING), game);
        given(savedGameService.getSavedGame(game.getId())).willReturn(savedGame);

        //when
        List<String> result = waitingRoomService.checkIfOpponentAppears(gameId);

        //then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("false", result.get(0));
    }

    @DirtiesContext
    @Test
    public void shouldAddPlayerToGame() {
        //given
        long userId = 1;
        long gameId = 1;
        Game game = new Game(1);
        given(gameRepo.findById(gameId)).willReturn(Optional.of(game));
        User user = new User();
        given(userService.getLogInUser(userId)).willReturn(user);

        //when
        waitingRoomService.addSecondPlayerToGame(userId, gameId);

        //then
        verify(gameRepo, times(1)).findById(gameId);
        verify(userService, times(1)).getLogInUser(userId);
        verify(gameRepo, times(1)).save(game);
        assertEquals(game, user.getGames().get(0));
        assertEquals(user, game.getUsers().get(0));
    }

    @DirtiesContext
    @Test
    public void shouldThrowExceptionWhenGameDoesntExist() {
        //given
        long userId = 1;
        long gameId = 1;
        Game game = new Game(1);
        given(gameRepo.findById(gameId)).willThrow(NoSuchElementException.class);

        //when
        //then
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            waitingRoomService.addSecondPlayerToGame(userId, gameId);
        });
        verify(gameRepo, times(1)).findById(gameId);
        verifyNoInteractions(userService);
        verifyNoInteractions(gameRepo);
    }

    @DirtiesContext
    @Test
    public void shouldDeleteGame() {
        //given
        long userId = 1;
        long gameId = 1;
        Game game = new Game(1);
        List<Game> games = new ArrayList<>();
        games.add(game);
        given(gameService.getGame(gameId)).willReturn(game);
        User user = new User();
        user.setGames(games);
        List<User> users = new ArrayList<>();
        users.add(user);
        game.setUsers(users);
        given(userService.findUserById(userId)).willReturn(user);
        SavedGame savedGame = new SavedGame(new GameStatus(List.of(), StateGame.WAITING), game);
        given(savedGameService.getSavedGame(gameId)).willReturn(savedGame);
        int currentPly = 1;
        given(savedGameService.getCurrentPlayer(gameId)).willReturn(currentPly);

        //when
        waitingRoomService.deleteGame(userId, gameId);

        //then
        verify(gameService).getGame(gameId);
        verify(savedGameService).getCurrentPlayer(gameId);
        verify(savedGameRepo).save(any(SavedGame.class));
        assertEquals(0, games.size());
    }

    @DirtiesContext
    @Test
    public void shouldThrowExceptionIfUserTriesDeleteGameThatDoesntExist() {
        //given
        long userId = 1;
        long gameId = 1;
        Game game = new Game(1);
        List<Game> games = new ArrayList<>();
        games.add(game);
        given(gameService.getGame(gameId)).willReturn(game);
        User user = new User();
        List<User> users = new ArrayList<>();
        users.add(user);
        game.setUsers(users);
        given(userService.findUserById(userId)).willReturn(user);
        SavedGame savedGame = new SavedGame(new GameStatus(List.of(), StateGame.WAITING), game);
        given(savedGameService.getSavedGame(gameId)).willReturn(savedGame);

        //when
        //then
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            waitingRoomService.deleteGame(userId, gameId);
        });
        verify(gameService).getGame(gameId);
        assertTrue(exception.getMessage().contains("No game"));
    }

    @DirtiesContext
    @Test
    public void shouldReturnLastAddedGameId() {
        //given
        long userId = 1;
        User user = new User();
        user.setGames(List.of(new Game(3), new Game(1), new Game(5)));
        given(userService.getLogInUser(userId)).willReturn(user);

        //when
        Integer result = waitingRoomService.getLowestGameId(userId);

        //then
        verify(userService).getLogInUser(userId);
        assertEquals(1, (int) result);
    }

    @DirtiesContext
    @Test
    public void shouldThrowExceptionIfUserDontHaveGame() {
        //given
        long userId = 1;
        User user = new User();
        given(userService.getLogInUser(userId)).willReturn(user);

        //when
        //then
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            waitingRoomService.getLowestGameId(userId);
        });
        verify(userService).getLogInUser(userId);
        assertTrue(exception.getMessage().contains("No game"));
    }


}