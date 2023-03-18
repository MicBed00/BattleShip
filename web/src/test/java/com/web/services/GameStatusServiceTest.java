package com.web.services;

import board.Board;
import board.Shot;
import board.StateGame;
import com.web.enity.game.Game;
import com.web.enity.game.StatusGame;
import com.web.repositories.GameRepo;
import com.web.repositories.StatusGameRepo;
import dataConfig.Position;
import exceptions.OutOfBoundsException;

import exceptions.ShotSamePlaceException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import serialization.GameStatus;
import ship.Ship;

import java.util.*;

import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameStatusServiceTest {

    @Mock
    private GameStatusRepoService gameStatusRepoService;
    @Mock
    private GameRepoService gameRepoService;
    @Mock
    private UserService userService;
    @Mock
    private GameRepo gameRepo;
    @Mock
    private StatusGameRepo repoStatusGame;
    @InjectMocks
    private GameStatusService gameStatusService;
    private AutoCloseable autoCloseable;


    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        gameStatusService = new GameStatusService(gameStatusRepoService
                , gameRepoService
                , userService
                , gameRepo
                , repoStatusGame);
    }
    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    private void addAllShipsToBoards() {
        int userId = 1;
        gameStatusService.saveNewGame(userId);
        long gameId = 1;
        Ship ship1 = new Ship(1, 1, 1, Position.VERTICAL);
        gameStatusService.addShipToList(ship1, gameId, userId);
        Ship ship2 = new Ship(1, 9, 0, Position.VERTICAL);
        gameStatusService.addShipToList(ship2, gameId, userId);
        Ship ship3 = new Ship(1, 3, 3, Position.VERTICAL);
        gameStatusService.addShipToList(ship3, gameId, userId);
        Ship ship4 = new Ship(1, 5, 5, Position.VERTICAL);
        gameStatusService.addShipToList(ship4, gameId, userId);
        Ship ship5 = new Ship(2, 8, 6, Position.VERTICAL);
        gameStatusService.addShipToList(ship5, gameId, userId);
        Ship ship6 = new Ship(1, 1, 1, Position.VERTICAL);
        gameStatusService.addShipToList(ship6, gameId, userId);
        Ship ship7 = new Ship(1, 9, 0, Position.VERTICAL);
        gameStatusService.addShipToList(ship7, gameId, userId);
        Ship ship8 = new Ship(1, 3, 3, Position.VERTICAL);
        gameStatusService.addShipToList(ship8, gameId, userId);
        Ship ship9 = new Ship(1, 5, 5, Position.VERTICAL);
        gameStatusService.addShipToList(ship9, gameId, userId);
        Ship ship10 = new Ship(2, 8, 6, Position.VERTICAL);
        gameStatusService.addShipToList(ship10, gameId, userId);
    }

    private void shootDownAllShipsOnOneBoards() {
        long gameId = 1;
        gameStatusService.addShotAtShip(new Shot(1,1), gameId);
        gameStatusService.addShotAtShip(new Shot(2,2), gameId);
        gameStatusService.addShotAtShip(new Shot(9,0), gameId);
        gameStatusService.addShotAtShip(new Shot(2,3), gameId);
        gameStatusService.addShotAtShip(new Shot(3,3), gameId);
        gameStatusService.addShotAtShip(new Shot(2,4), gameId);
        gameStatusService.addShotAtShip(new Shot(5,5), gameId);
        gameStatusService.addShotAtShip(new Shot(2,6), gameId);
        gameStatusService.addShotAtShip(new Shot(8,6), gameId);
        gameStatusService.addShotAtShip(new Shot(2,7), gameId);
        gameStatusService.addShotAtShip(new Shot(8,7), gameId);
    }
    @DirtiesContext
    @Test
    public void shouldReturnShipLimitsOnBoard() {
        //then
        assertEquals(5, gameStatusService.getShipLimits());
    }
    @DirtiesContext
    @Test
    public void shouldReturnListSize4MastedShip() {
        //then
        assertEquals("4", gameStatusService.getShipSize().get(3));
    }
    @DirtiesContext
    @Test
    public void shouldReturnListSize3MastedShip() {
        //then
        assertEquals("3", gameStatusService.getShipSize().get(2));
    }
    @DirtiesContext
    @Test
    public void shouldReturnListSize2MastedShip() {
        //then
        assertEquals("2", gameStatusService.getShipSize().get(1));
    }
    @DirtiesContext
    @Test
    public void shouldReturnListSize1MastedShip() {
        MockitoAnnotations.openMocks(this);
        //then
        assertEquals("1", gameStatusService.getShipSize().get(0));
    }


    @Test
    public void shouldReturnVerticalOrientationShip() {
        //then
        assertEquals("HORIZONTAL", gameStatusService.getOrientation().get(0).toString());
    }

    @DirtiesContext
    @Test
    public void shouldAddShipToFirstList() {
        //given
        int currentPly = 1;
        Ship ship = new Ship(1, 1, 1, Position.VERTICAL);
        List<Board> boardList = new ArrayList<>();
        boardList.add(new Board());
        boardList.add(new Board());
        Game game = new Game();
        game.setOwnerGame(1L);
        GameStatus gameStatus = new GameStatus(boardList,currentPly,StateGame.IN_PROCCESS);
        StatusGame statusGame = new StatusGame(gameStatus,game);
        statusGame.setGameStatus(gameStatus);


        long gameId = 1;
        long userId = 1;

        given(gameStatusRepoService.getStatusGame(gameId)).willReturn(statusGame);
        given(gameRepoService.getGame(gameId)).willReturn(game);

        //when
        List<Board> boards = gameStatusService.addShipToList(ship, gameId, userId);
        int sizeList = boardList.get(0).getShips().size();

        //then
        verify(gameRepoService,times(1)).getGame(gameId);
        assertEquals(1, sizeList);
    }


    @DirtiesContext
    @Test
    public void exceptionShouldBeThrowIfShipIsOutOfBoundBoard() {
        //given
        int currentPly = 1;
        long gameId = 1;
        long userId = 1;
        List<Board> boardList = new ArrayList<>();
        boardList.add(new Board());
        boardList.add(new Board());
        Game game = new Game();
        game.setOwnerGame(1L);
        GameStatus gameStatus = new GameStatus(boardList,currentPly,StateGame.IN_PROCCESS);
        StatusGame statusGame = new StatusGame(gameStatus,game);
        statusGame.setGameStatus(gameStatus);
        given(gameStatusRepoService.getStatusGame(gameId)).willReturn(statusGame);
        given(gameRepoService.getGame(gameId)).willReturn(game);

        Ship ship = new Ship(2, 8, 9, Position.VERTICAL);
        //when
        //then
        assertThrows(OutOfBoundsException.class, () -> gameStatusService.addShipToList(ship,gameId, userId));
    }
//    @DirtiesContext
//    @Test
//    public void exceptionShouldBeThrowIfAddTwoShipsTheSamePlaceOnBoard() {
//        //given
//        long gameId = 1;
//        long userId = 1;
//        Ship ship1 = new Ship(2, 1, 1, Position.VERTICAL);
//        Ship ship2 = new Ship(2, 1, 1, Position.VERTICAL);
//        //when
//        gameStatusService.addShipToList(ship1);
//        //then
//        assertThrows(CollidingException.class, () -> gameStatusService.addShipToList(ship2));
//    }
//
//    @DirtiesContext
//    @Test
//    public void whenSecondPlayerShootsShotShouldBeAddedToFirstPlayerBoards() {
//        //given
//        Shot shotFirstPlayer = new Shot(1, 1);
//        gameStatusService.addShotAtShip(shotFirstPlayer);
//
//        //when
//        Shot shotSecondPlayer = new Shot(1, 1);
//        List<Board> boardList = gameStatusService.addShotAtShip(shotSecondPlayer);
//        int opponentShots = boardList.get(0).getOpponentShots().size();
//        //then
//        assertEquals(1, opponentShots);
//    }
//
//    @DirtiesContext
//    @Test
//    public void exceptionShouldBeThrowIfPlayerShootsTheSamePlace() {
//        //given
//        Shot shotFirstPlayer = new Shot(1, 1);
//        gameStatusService.addShotAtShip(shotFirstPlayer);
//        Shot shotSecondPlayer = new Shot(3, 3);
//        gameStatusService.addShotAtShip(shotSecondPlayer);
//        //when
//        Shot shotSamePlace = new Shot(1, 1);
//        //then
//        assertThrows(ShotSamePlaceException.class, () -> gameStatusService.addShotAtShip(shotSamePlace));
//    }
//
//    @DirtiesContext
//    @Test
//    public void shotTheSamePlaceShouldHasInvalidState() {
//        //given
//        Shot shotFirstPlayer = new Shot(1, 1);
//        gameStatusService.addShotAtShip(shotFirstPlayer);
//        Shot shotSecondPlayer = new Shot(3, 3);
//        gameStatusService.addShotAtShip(shotSecondPlayer);
//        //when
//        try {
//            Shot shotSamePlace = new Shot(1, 1);
//            gameStatusService.addShotAtShip(shotSamePlace);
//        }catch (ShotSamePlaceException e) {}
//        List<Board> boardList = gameStatusService.getBoardList(1L);
//        Set<Shot> opponentShots = boardList.get(1).getOpponentShots();
//        //then
//        assertTrue(opponentShots.contains(new Shot(Shot.State.INVALID,1, 1)));
//
//    }
//
//    @DirtiesContext
//    @Test
//    public void missedShotShouldBeHaveMissedState() {
//        //given
//        long gameId = 1L;
//        Shot shotFirstPlayer = new Shot(1, 1);
//        //when
//        List<Board> boardList = gameStatusService.addShotAtShip(shotFirstPlayer, gameId);
//        Set<Shot> opponentShots = boardList.get(1).getOpponentShots();
//        Shot.State shotState = opponentShots.stream()
//                .iterator()
//                .next()
//                .getState();
//        //then
//        assertSame("MISSED", shotState.toString());
//    }
//
//    @DirtiesContext
//    @Test
//    public void hitShotShouldBeHaveHitState() {
//        //given
//        Ship ship = new Ship(1, 1, 1, Position.VERTICAL);
//        gameStatusService.addShipToList(ship);
//        Shot shotFirstPlayer = new Shot(3,3);
//        gameStatusService.addShotAtShip(shotFirstPlayer);
//        //when
//        Shot shot = new Shot(1, 1);
//        List<Board> boardList = gameStatusService.addShotAtShip(shot);
//        Set<Shot> opponentShots = boardList.get(0).getOpponentShots();
//        Shot.State shotState = opponentShots.stream()
//                .iterator()
//                .next()
//                .getState();
//        //then
//        assertSame("HIT", shotState.toString());
//    }
//
//    @DirtiesContext
//    @Test
//    public void shouldReturnFalseIfNotAllShipsAreHit() {
//        //given
//        addAllShipsToBoards();
//        //then
//        assertFalse(gameStatusService.checkIfAllShipsAreHitted());
//    }
//
//    @DirtiesContext
//    @Test
//    public void shouldReturnTrueIfAllShipsAreHitted() {
//        //given
//        addAllShipsToBoards();
//        shootDownAllShipsOnOneBoards();
//        //then
//        assertTrue(gameStatusService.checkIfAllShipsAreHitted());
//    }
//    @DirtiesContext
//    @Test
//    public void shouldDeleteShipFromList() {
//        //given
//        gameStatusService.addShipToList(new Ship(1,1,1, Position.VERTICAL));
//
//        //when
//        List<Board> boardList = gameStatusService.deleteShipFromServer(0);
//        int sizeList = boardList.get(0).getShips().size();
//
//        //then
//        assertEquals(0, sizeList);
//    }
//
//    @DirtiesContext
//    @Test
//    public void shouldReturnSecondPlayer() {
//        //given
//        Ship ship1 = new Ship(1, 1, 1, Position.VERTICAL);
//        gameStatusService.addShipToList(ship1);
//        Ship ship2 = new Ship(1, 9, 0, Position.VERTICAL);
//        gameStatusService.addShipToList(ship2);
//        Ship ship3 = new Ship(1, 3, 3, Position.VERTICAL);
//        gameStatusService.addShipToList(ship3);
//        Ship ship4 = new Ship(1, 5, 5, Position.VERTICAL);
//        gameStatusService.addShipToList(ship4);
//        Ship ship5 = new Ship(2, 8, 6, Position.VERTICAL);
//        gameStatusService.addShipToList(ship5);
//
//        //when
//        List<Board> boardList = gameStatusService.getBoardList();
//        //then
//        assertEquals(2, gameStatusService.getCurrentPlayer());
//    }



}
