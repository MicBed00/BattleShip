package com.web.services;

import board.Board;
import board.Shot;
import board.StateGame;
import com.web.enity.game.Game;
import com.web.enity.game.SavedGame;
import com.web.enity.user.User;
import com.web.repositories.GameRepo;
import com.web.repositories.StatusGameRepo;
import dataConfig.Position;
import exceptions.CollidingException;
import exceptions.OutOfBoundsException;

import exceptions.ShotSamePlaceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
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


    private SavedGame saveGame() {
        int currentPly = 1;
        List<Board> boardList = new ArrayList<>();
        boardList.add(new Board());
        boardList.add(new Board());
        Game game = new Game();
        game.setOwnerGame(1L);
        game.getUsers().add(new User());
        GameStatus gameStatus = new GameStatus(boardList,currentPly,StateGame.IN_PROCCESS);
        SavedGame savedGame = new SavedGame(gameStatus,game);
        savedGame.setGameStatus(gameStatus);
        return savedGame;
    }

    private SavedGame addAllShipsToBoards(SavedGame saveGame) {
        long gameId = 1;
        long userIdOwner = 1;
        long userIdSec = 2;
        SavedGame savedGame = saveGame;
        Game game = savedGame.getGame();
        given(gameStatusRepoService.getStatusGame(gameId)).willReturn(savedGame);
        given(gameRepoService.getGame(gameId)).willReturn(game);

        Ship ship1 = new Ship(1, 1, 1, Position.VERTICAL);
        gameStatusService.addShipToList(ship1, gameId, userIdOwner);
        Ship ship2 = new Ship(1, 9, 0, Position.VERTICAL);
        gameStatusService.addShipToList(ship2, gameId, userIdOwner);
        Ship ship3 = new Ship(1, 3, 3, Position.VERTICAL);
        gameStatusService.addShipToList(ship3, gameId, userIdOwner);
        Ship ship4 = new Ship(1, 5, 5, Position.VERTICAL);
        gameStatusService.addShipToList(ship4, gameId, userIdOwner);
        Ship ship5 = new Ship(2, 8, 6, Position.VERTICAL);
        gameStatusService.addShipToList(ship5, gameId, userIdOwner);
        Ship ship6 = new Ship(1, 1, 1, Position.VERTICAL);
        gameStatusService.addShipToList(ship6, gameId, userIdSec);
        Ship ship7 = new Ship(1, 9, 0, Position.VERTICAL);
        gameStatusService.addShipToList(ship7, gameId, userIdSec);
        Ship ship8 = new Ship(1, 3, 3, Position.VERTICAL);
        gameStatusService.addShipToList(ship8, gameId, userIdSec);
        Ship ship9 = new Ship(1, 5, 5, Position.VERTICAL);
        gameStatusService.addShipToList(ship9, gameId, userIdSec);
        Ship ship10 = new Ship(2, 8, 6, Position.VERTICAL);
        gameStatusService.addShipToList(ship10, gameId, userIdSec);

        return savedGame;
    }

    private void shootDownAllShipsOnOneBoards(SavedGame saveGame) {
        long gameId = 1;
        SavedGame savedGame = saveGame;
        given(gameStatusRepoService.getStatusGame(gameId)).willReturn(savedGame);
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
        long gameId = 1;
        long userId = 1;
        SavedGame savedGame = saveGame();
        Game game = savedGame.getGame();
        Ship ship = new Ship(2, 4, 4, Position.VERTICAL);
        given(gameStatusRepoService.getStatusGame(gameId)).willReturn(savedGame);
        given(gameRepoService.getGame(gameId)).willReturn(game);

        //when
        List<Board> boardList = gameStatusService.addShipToList(ship, gameId, userId);
        int sizeList = boardList.get(0).getShips().size();

        //then
        verify(gameRepoService,times(1)).getGame(gameId);
        assertEquals(1, sizeList);
    }


    @DirtiesContext
    @Test
    public void exceptionShouldBeThrowIfShipIsOutOfBoundBoard() {
        //given
        long gameId = 1;
        long userId = 1;
        SavedGame savedGame = saveGame();
        Game game = savedGame.getGame();
        Ship ship = new Ship(2, 8, 9, Position.VERTICAL);
        given(gameStatusRepoService.getStatusGame(gameId)).willReturn(savedGame);
        given(gameRepoService.getGame(gameId)).willReturn(game);

        //when
        //then
        assertThrows(OutOfBoundsException.class, () -> gameStatusService.addShipToList(ship,gameId, userId));
    }

    @DirtiesContext
    @Test
    public void exceptionShouldBeThrowIfAddTwoShipsTheSamePlaceOnBoard() {
        //given
        long gameId = 1;
        long userId = 1;
        SavedGame savedGame = saveGame();
        Game game = savedGame.getGame();
        Ship ship1 = new Ship(2, 1, 1, Position.VERTICAL);
        Ship ship2 = new Ship(2, 1, 1, Position.VERTICAL);
        given(gameStatusRepoService.getStatusGame(gameId)).willReturn(savedGame);
        given(gameRepoService.getGame(gameId)).willReturn(game);
        given(gameStatusRepoService.getStatusGame(gameId)).willReturn(savedGame);
        given(gameRepoService.getGame(gameId)).willReturn(game);

        //when
        gameStatusService.addShipToList(ship1,gameId, userId);

        //then
        assertThrows(CollidingException.class, () -> gameStatusService.addShipToList(ship2, gameId, userId));
    }

    @DirtiesContext
    @Test
    public void whenSecondPlayerShootsShotShouldBeAddedToFirstPlayerBoards() {
        //given
        long gameId = 1L;
        Shot shotFirstPlayer = new Shot(1, 1);
        SavedGame savedGame = saveGame();
        given(gameStatusRepoService.getStatusGame(gameId)).willReturn(savedGame);

        gameStatusService.addShotAtShip(shotFirstPlayer,gameId);

        //when
        Shot shotSecondPlayer = new Shot(1, 1);
        List<Board> boardList = gameStatusService.addShotAtShip(shotSecondPlayer, gameId);
        int opponentShots = boardList.get(0).getOpponentShots().size();
        //then
        assertEquals(1, opponentShots);
    }

    @DirtiesContext
    @Test
    public void exceptionShouldBeThrowIfPlayerShootsTheSamePlace() {
        //given
        long gameId = 1L;
        SavedGame savedGame = saveGame();
        given(gameStatusRepoService.getStatusGame(gameId)).willReturn(savedGame);
        Shot shotFirstPlayer = new Shot(1, 1);
        gameStatusService.addShotAtShip(shotFirstPlayer, gameId);
        Shot shotSecondPlayer = new Shot(3, 3);
        gameStatusService.addShotAtShip(shotSecondPlayer, gameId);
        Shot shotSamePlace = new Shot(1, 1);

        //when
        //then
        assertThrows(ShotSamePlaceException.class, () -> gameStatusService.addShotAtShip(shotSamePlace, gameId));
    }

    @DirtiesContext
    @Test
    public void shotTheSamePlaceShouldHasInvalidState() {
        //given
        long gameId = 1L;
        SavedGame savedGame = saveGame();
        given(gameStatusRepoService.getStatusGame(gameId)).willReturn(savedGame);

        Shot shotFirstPlayer = new Shot(1, 1);
        Shot shotSecondPlayer = new Shot(3, 3);
        gameStatusService.addShotAtShip(shotFirstPlayer, gameId);
        gameStatusService.addShotAtShip(shotSecondPlayer, gameId);

        //when
        try {
            Shot shotSamePlace = new Shot(1, 1);
            gameStatusService.addShotAtShip(shotSamePlace, gameId);
        }catch (ShotSamePlaceException e) {}
        List<Board> boardList = gameStatusService.getBoardList(1L);
        Set<Shot> opponentShots = boardList.get(1).getOpponentShots();

        //then
        assertTrue(opponentShots.contains(new Shot(Shot.State.INVALID,1, 1)));
    }

    @DirtiesContext
    @Test
    public void missedShotShouldBeHaveMissedState() {
        //given
        long gameId = 1L;
        SavedGame savedGame = saveGame();
        given(gameStatusRepoService.getStatusGame(gameId)).willReturn(savedGame);
        Shot shotFirstPlayer = new Shot(1, 1);
        //when
        List<Board> boardList = gameStatusService.addShotAtShip(shotFirstPlayer, gameId);
        Set<Shot> opponentShots = boardList.get(1).getOpponentShots();
        Shot.State shotState = opponentShots.stream()
                .iterator()
                .next()
                .getState();
        //then
        assertSame("MISSED", shotState.toString());
    }

    @DirtiesContext
    @Test
    public void hitShotShouldBeHaveHitState() {
        //given
        long gameId = 1;
        long userId = 1;
        SavedGame savedGame = saveGame();
        Game game = savedGame.getGame();
        given(gameStatusRepoService.getStatusGame(gameId)).willReturn(savedGame);
        given(gameRepoService.getGame(gameId)).willReturn(game);

        Ship ship = new Ship(1, 1, 1, Position.VERTICAL);
        gameStatusService.addShipToList(ship,gameId, userId);

        Shot shotFirstPlayer = new Shot(3,3);
        gameStatusService.addShotAtShip(shotFirstPlayer, gameId);

        //when
        Shot shot = new Shot(1, 1);
        List<Board> boardList = gameStatusService.addShotAtShip(shot, gameId);
        Set<Shot> opponentShots = boardList.get(0).getOpponentShots();
        Shot.State shotState = opponentShots.stream()
                .iterator()
                .next()
                .getState();
        //then
        assertSame("HIT", shotState.toString());
    }

    @DirtiesContext
    @Test
    public void shouldReturnFalseIfNotAllShipsAreHit() {
        //given
        long gameId = 1;
        SavedGame savedGame = saveGame();
        addAllShipsToBoards(savedGame);

        //then
        assertFalse(gameStatusService.checkIfAllShipsAreHitted(gameId));
    }

    @DirtiesContext
    @Test
    public void shouldReturnTrueIfAllShipsAreHitted() {
        //given
        long gameId = 1L;
        SavedGame savedGame = saveGame();
        SavedGame savedGameWithShips = addAllShipsToBoards(savedGame);
        shootDownAllShipsOnOneBoards(savedGameWithShips);
        //then
        assertTrue(gameStatusService.checkIfAllShipsAreHitted(gameId));
    }
    //TODO aktualnie metoda kasowanie statku zanajduję się w gamestatusreposervie
//    @DirtiesContext
//    @Test
//    public void shouldDeleteShipFromList() {
//        //given
//        long gameId = 1;
//        long userId = 1;
//        SavedGame savedGame = saveGame();
//        Game game = savedGame.getGame();
//        given(gameStatusRepoService.getStatusGame(gameId)).willReturn(savedGame);
//        given(gameRepoService.getGame(gameId)).willReturn(game);
//
//        gameStatusService.addShipToList(new Ship(1,1,1, Position.VERTICAL),gameId, userId);
//
//        //when
//        List<Board> boardList = gameStatusService.deleteShipFromServer(0);
//        int sizeList = boardList.get(0).getShips().size();
//
//        //then
//        assertEquals(0, sizeList);
//    }
//
    @DirtiesContext
    @Test
    public void shouldReturnSecondPlayer() {
        //given
        long gameId = 1L;
        long userId = 1;
        SavedGame savedGame = saveGame();
        Game game = savedGame.getGame();
        given(gameStatusRepoService.getStatusGame(gameId)).willReturn(savedGame);
        given(gameRepoService.getGame(gameId)).willReturn(game);

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

        //when
        int currentPlayer = gameStatusService.getCurrentPlayer(gameId);

        //then
        assertEquals(2, currentPlayer);
    }
}