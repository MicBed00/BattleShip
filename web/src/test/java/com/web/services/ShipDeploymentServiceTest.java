package com.web.services;

import board.Board;
import board.StateGame;
import com.web.enity.game.Game;
import com.web.enity.game.SavedGame;
import com.web.repositories.GameRepo;
import com.web.repositories.SavedGameRepo;
import dataConfig.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import serialization.GameStatus;
import ship.Ship;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipDeploymentServiceTest {
    @Mock
    private SavedGameService savedGameService;
    @Mock
    private GameService gameService;
    @Mock
    private GameRepo gameRepo;
    @Mock
    private SavedGameRepo savedGameRepo;


    @InjectMocks
    private ShipDeploymentService shipDeploymentService;

    //TODO czy muszę testować scenariusze wyrzucania wyjątków w sytuacji gdy wyjątek jest
    //rzucany przez inną metodę ze stosu wywołań???
    @DirtiesContext
    @Test
    public void shouldAddShipToTheOwnerBoard() {
        //given
        Ship ship = new Ship(1,1,1, Position.VERTICAL);
        long gameId = 1;
        long userId = 1;
        Long ownerId = 1L;
        List<Board> boardList = new ArrayList<>();
        boardList.add(new Board(10));
        boardList.add(new Board(10));
        Game game = new Game();
        game.setOwnerGame(ownerId);

        //when
        when(savedGameService.getBoardsList(gameId)).thenReturn(boardList);
        when(gameService.getGame(gameId)).thenReturn(game);

        List<Board> result = shipDeploymentService.addShipToList(ship, gameId, userId);

        //then
        verify(savedGameService).getBoardsList(gameId);
        verify(gameService).getGame(gameId);
        assertEquals(1, result.get(0).getShips().size());
        assertEquals(ship, result.get(0).getShips().get(0));
    }

    @DirtiesContext
    @Test
    public void shouldAddShipToTheOpponentBoard() {
        //given
        Ship ship = new Ship(1,1,1, Position.VERTICAL);
        long gameId = 1;
        long userId = 2;
        Long ownerId = 1L;
        List<Board> boardList = new ArrayList<>();
        boardList.add(new Board(10));
        boardList.add(new Board(10));
        Game game = new Game();
        game.setOwnerGame(ownerId);

        //when
        when(savedGameService.getBoardsList(gameId)).thenReturn(boardList);
        when(gameService.getGame(gameId)).thenReturn(game);

        List<Board> result = shipDeploymentService.addShipToList(ship, gameId, userId);

        //then
        verify(savedGameService).getBoardsList(gameId);
        verify(gameService).getGame(gameId);
        assertEquals(1, result.get(1).getShips().size());
        assertEquals(ship, result.get(1).getShips().get(0));
    }

    @DirtiesContext
    @Test
    public void shouldNotAddShipToAnyBoard() {
        //given
        Ship ship = new Ship(0,1,1, Position.VERTICAL);
        long gameId = 1;
        long userId = 2;
        Long ownerId = 1L;
        List<Board> boardList = new ArrayList<>();
        boardList.add(new Board(10));
        boardList.add(new Board(10));
        Game game = new Game();
        game.setOwnerGame(ownerId);

        //when
        when(savedGameService.getBoardsList(gameId)).thenReturn(boardList);
        when(gameService.getGame(gameId)).thenReturn(game);

        List<Board> result = shipDeploymentService.addShipToList(ship, gameId, userId);

        //then
        verify(savedGameService).getBoardsList(gameId);
        verify(gameService).getGame(gameId);
        assertEquals(0, result.get(1).getShips().size());
    }

    @DirtiesContext
    @Test
    public void shouldReturnOwnerGameBoard() {
        //given
        long gameId = 1;
        long userId = 1;
        long ownerId = 1;
        Board ownerBoard = new Board(10);
        ownerBoard.getShips().add(new Ship(4,1,1,Position.VERTICAL));
        Board board2 = new Board(15);
        board2.getShips().add(new Ship(1,1,1,Position.VERTICAL));
        List<Board> boardList = List.of(ownerBoard, board2);
        given(savedGameService.getBoardsList(gameId)).willReturn(boardList);
        Game game = new Game();
        game.setOwnerGame(ownerId);
        given(gameService.getGame(gameId)).willReturn(game);

        //when
        Board result = shipDeploymentService.getBoard(gameId, userId);

        //then
        assertEquals(result, ownerBoard);
        assertNotEquals(result, board2);
    }

    @DirtiesContext
    @Test
    public void shouldReturnSecondBoard() {
        //given
        long gameId = 1;
        long userId = 2;
        long ownerId = 1;
        Board ownerBoard = new Board(10);
        ownerBoard.getShips().add(new Ship(4,1,1,Position.VERTICAL));
        Board board2 = new Board(15);
        board2.getShips().add(new Ship(1,1,1,Position.VERTICAL));
        List<Board> boardList = List.of(ownerBoard, board2);
        given(savedGameService.getBoardsList(gameId)).willReturn(boardList);
        Game game = new Game();
        game.setOwnerGame(ownerId);
        given(gameService.getGame(gameId)).willReturn(game);

        //when
        Board result = shipDeploymentService.getBoard(gameId, userId);

        //then
        assertNotEquals(result, ownerBoard);
        assertEquals(result, board2);
    }

    @DirtiesContext
    @Test
    public void shouldDeleteShipOnFirstBoard() {
        //given
        long gameId = 1;
        long userId = 1;
        long ownerId = 1;
        int currentPly = 1;
        Board board1 = new Board(10);
        board1.getShips().add(new Ship(1,1,1,Position.VERTICAL));
        board1.getShips().add(new Ship(1,5,5,Position.VERTICAL));
        Board board2 = new Board(10);
        board2.getShips().add(new Ship(1,1,1,Position.VERTICAL));
        board2.getShips().add(new Ship(1,5,5,Position.VERTICAL));
        Game game = new Game();
        game.setOwnerGame(ownerId);
        given(gameRepo.findById(gameId)).willReturn(Optional.of(game));
        SavedGame savedGame = new SavedGame(new GameStatus(List.of(board1, board2), StateGame.NEW), game);
        given(savedGameService.getSavedGame(gameId)).willReturn(savedGame);
        given(savedGameService.getCurrentPlayer(gameId)).willReturn(currentPly);

        //when
        shipDeploymentService.deleteShip(userId, gameId);

        //then
        assertEquals(1, board1.getShips().size());
        assertEquals(2, board2.getShips().size());
        verify(savedGameService, times(1)).getCurrentPlayer(gameId);
        verify(savedGameRepo, times(1)).save(any(SavedGame.class));
    }

    @DirtiesContext
    @Test
    public void shouldDeleteShipOnSecondBoard() {
        //given
        long gameId = 1;
        long userId = 2;
        long ownerId = 1;
        int currentPly = 1;
        Board board = new Board(10);
        board.getShips().add(new Ship(1,1,1,Position.VERTICAL));
        board.getShips().add(new Ship(1,5,5,Position.VERTICAL));
        Board board2 = new Board(10);
        board2.getShips().add(new Ship(1,1,1,Position.VERTICAL));
        board2.getShips().add(new Ship(1,5,5,Position.VERTICAL));
        Game game = new Game();
        game.setOwnerGame(ownerId);
        given(gameRepo.findById(gameId)).willReturn(Optional.of(game));
        SavedGame savedGame = new SavedGame(new GameStatus(List.of(board, board2), StateGame.NEW), game);
        given(savedGameService.getSavedGame(gameId)).willReturn(savedGame);
        given(savedGameService.getCurrentPlayer(gameId)).willReturn(currentPly);

        //when
        shipDeploymentService.deleteShip(userId, gameId);

        //then
        assertEquals(1, board2.getShips().size());
        assertNotEquals(2, board2.getShips().size());
        verify(savedGameService, times(1)).getCurrentPlayer(gameId);
        verify(savedGameRepo, times(1)).save(any(SavedGame.class));
    }

    @DirtiesContext
    @Test
    public void shouldThrowExceptionWhenGameDoesntExist() {
        //given
        long gameId = 1;
        long userId = 2;

        //when
        //then
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            shipDeploymentService.deleteShip(userId, gameId);
        });
        assertTrue(exception.getMessage().contains("Can't find game"));
        verifyNoInteractions(savedGameRepo);
        verify(savedGameRepo, times(0)).save(any(SavedGame.class));
    }






}