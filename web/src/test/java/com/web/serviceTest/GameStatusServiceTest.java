package com.web.serviceTest;

import board.Board;
import board.Shot;
import com.web.service.GameStatusService;
import dataConfig.Position;
import exceptions.CollidingException;
import exceptions.OutOfBoundsException;
import exceptions.ShotSamePlaceException;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ship.Ship;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource(locations = "classpath:emptyUnitTest.properties")
public class GameStatusServiceTest {

    @Autowired
    GameStatusService gameStatusService;

    private void addAllShipsToBoards() {
        Ship ship1 = new Ship(1, 1, 1, Position.VERTICAL);
        gameStatusService.addShipToList(ship1);
        Ship ship2 = new Ship(1, 9, 0, Position.VERTICAL);
        gameStatusService.addShipToList(ship2);
        Ship ship3 = new Ship(1, 3, 3, Position.VERTICAL);
        gameStatusService.addShipToList(ship3);
        Ship ship4 = new Ship(1, 5, 5, Position.VERTICAL);
        gameStatusService.addShipToList(ship4);
        Ship ship5 = new Ship(2, 8, 6, Position.VERTICAL);
        gameStatusService.addShipToList(ship5);
        Ship ship6 = new Ship(1, 1, 1, Position.VERTICAL);
        gameStatusService.addShipToList(ship6);
        Ship ship7 = new Ship(1, 9, 0, Position.VERTICAL);
        gameStatusService.addShipToList(ship7);
        Ship ship8 = new Ship(1, 3, 3, Position.VERTICAL);
        gameStatusService.addShipToList(ship8);
        Ship ship9 = new Ship(1, 5, 5, Position.VERTICAL);
        gameStatusService.addShipToList(ship9);
        Ship ship10 = new Ship(2, 8, 6, Position.VERTICAL);
        gameStatusService.addShipToList(ship10);
    }

    private void shootDownAllShipsOnOneBoards() {
        gameStatusService.addShotAtShip(new Shot(1,1));
        gameStatusService.addShotAtShip(new Shot(2,2));
        gameStatusService.addShotAtShip(new Shot(9,0));
        gameStatusService.addShotAtShip(new Shot(2,3));
        gameStatusService.addShotAtShip(new Shot(3,3));
        gameStatusService.addShotAtShip(new Shot(2,4));
        gameStatusService.addShotAtShip(new Shot(5,5));
        gameStatusService.addShotAtShip(new Shot(2,6));
        gameStatusService.addShotAtShip(new Shot(8,6));
        gameStatusService.addShotAtShip(new Shot(2,7));
        gameStatusService.addShotAtShip(new Shot(8,7));
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
        //then
        assertEquals("1", gameStatusService.getShipSize().get(0));
    }

    @DirtiesContext
    @Test
    public void shouldReturnVerticalOrientationShip() {
        //then
        assertEquals("VERTICAL", gameStatusService.getOrientation().get(0).toString());
    }

    @DirtiesContext
    @Test
    public void shouldAddShipToFirstList() {
        //given
        Ship ship = new Ship(1, 1, 1, Position.VERTICAL);

        //when
        List<Board> boardList = gameStatusService.addShipToList(ship);
        int sizeList = boardList.get(0).getShips().size();

        //then
        assertEquals(1, sizeList);
    }
    @DirtiesContext
    @Test
    public void exceptionShouldBeThrowIfShipIsOutOfBoundBoard() {
        //given
        Ship ship = new Ship(2, 8, 9, Position.VERTICAL);
        //when
        //then
        assertThrows(OutOfBoundsException.class, () -> gameStatusService.addShipToList(ship));
    }
    @DirtiesContext
    @Test
    public void exceptionShouldBeThrowIfAddTwoShipsTheSamePlaceOnBoard() {
        //given
        Ship ship1 = new Ship(2, 1, 1, Position.VERTICAL);
        Ship ship2 = new Ship(2, 1, 1, Position.VERTICAL);
        //when
        gameStatusService.addShipToList(ship1);
        //then
        assertThrows(CollidingException.class, () -> gameStatusService.addShipToList(ship2));
    }
    @DirtiesContext
    @Test
    public void whenFirstListShipIsFullShouldAddShipToSecondListShip() {
        //given
        Ship ship1 = new Ship(1, 1, 1, Position.VERTICAL);
        gameStatusService.addShipToList(ship1);
        Ship ship2 = new Ship(1, 9, 0, Position.VERTICAL);
        gameStatusService.addShipToList(ship2);
        Ship ship3 = new Ship(1, 3, 3, Position.VERTICAL);
        gameStatusService.addShipToList(ship3);
        Ship ship4 = new Ship(1, 5, 5, Position.VERTICAL);
        gameStatusService.addShipToList(ship4);
        Ship ship5 = new Ship(2, 8, 6, Position.VERTICAL);
        gameStatusService.addShipToList(ship5);

        Ship ship6 = new Ship(2, 8, 6, Position.VERTICAL);;
        //when
        List<Board> boardList = gameStatusService.addShipToList(ship6);
        int sizeFirstList = boardList.get(0).getShips().size();
        int sizeSecondList = boardList.get(1).getShips().size();


        //then
        assertEquals(5, sizeFirstList);
        assertEquals(1, sizeSecondList);
    }

    @DirtiesContext
    @Test
    public void whenFirstPlayerShootsShotShouldBeAddedToSecondPlayerBoards() {
        //given
        Shot shot = new Shot(1, 1);
        //when
        List<Board> boardList = gameStatusService.addShotAtShip(shot);
        int opponentShots = boardList.get(1).getOpponentShots().size();
        //then
        assertEquals(1, opponentShots);
    }

    @DirtiesContext
    @Test
    public void whenSecondPlayerShootsShotShouldBeAddedToFirstPlayerBoards() {
        //given
        Shot shotFirstPlayer = new Shot(1, 1);
        gameStatusService.addShotAtShip(shotFirstPlayer);

        //when
        Shot shotSecondPlayer = new Shot(1, 1);
        List<Board> boardList = gameStatusService.addShotAtShip(shotSecondPlayer);
        int opponentShots = boardList.get(0).getOpponentShots().size();
        //then
        assertEquals(1, opponentShots);
    }

    @DirtiesContext
    @Test
    public void exceptionShouldBeThrowIfPlayerShootsTheSamePlace() {
        //given
        Shot shotFirstPlayer = new Shot(1, 1);
        gameStatusService.addShotAtShip(shotFirstPlayer);
        Shot shotSecondPlayer = new Shot(3, 3);
        gameStatusService.addShotAtShip(shotSecondPlayer);
        //when
        Shot shotSamePlace = new Shot(1, 1);
        //then
        assertThrows(ShotSamePlaceException.class, () -> gameStatusService.addShotAtShip(shotSamePlace));
    }

    @DirtiesContext
    @Test
    public void shotTheSamePlaceShouldHasInvalidState() {
        //given
        Shot shotFirstPlayer = new Shot(1, 1);
        gameStatusService.addShotAtShip(shotFirstPlayer);
        Shot shotSecondPlayer = new Shot(3, 3);
        gameStatusService.addShotAtShip(shotSecondPlayer);
        //when
        try {
            Shot shotSamePlace = new Shot(1, 1);
            gameStatusService.addShotAtShip(shotSamePlace);
        }catch (ShotSamePlaceException e) {}
        List<Board> boardList = gameStatusService.getBoardList();
        Set<Shot> opponentShots = boardList.get(1).getOpponentShots();
        //then
        assertTrue(opponentShots.contains(new Shot(Shot.State.INVALID,1, 1)));

    }

    @DirtiesContext
    @Test
    public void missedShotShouldBeHaveMissedState() {
        //given
        Shot shotFirstPlayer = new Shot(1, 1);
        //when
        List<Board> boardList = gameStatusService.addShotAtShip(shotFirstPlayer);
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
        Ship ship = new Ship(1, 1, 1, Position.VERTICAL);
        gameStatusService.addShipToList(ship);
        Shot shotFirstPlayer = new Shot(3,3);
        gameStatusService.addShotAtShip(shotFirstPlayer);
        //when
        Shot shot = new Shot(1, 1);
        List<Board> boardList = gameStatusService.addShotAtShip(shot);
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
        addAllShipsToBoards();
        //then
        assertFalse(gameStatusService.checkIfAllShipsAreHitted());
    }

    @DirtiesContext
    @Test
    public void shouldReturnTrueIfAllShipsAreHitted() {
        //given
        addAllShipsToBoards();
        shootDownAllShipsOnOneBoards();
        //then
        assertTrue(gameStatusService.checkIfAllShipsAreHitted());
    }
    @DirtiesContext
    @Test
    public void shouldDeleteShipFromList() {
        //given
        gameStatusService.addShipToList(new Ship(1,1,1, Position.VERTICAL));

        //when
        List<Board> boardList = gameStatusService.deleteShipFromServer(0);
        int sizeList = boardList.get(0).getShips().size();

        //then
        assertEquals(0, sizeList);
    }

    @DirtiesContext
    @Test
    public void shouldReturnSecondPlayer() {
        //given
        Ship ship1 = new Ship(1, 1, 1, Position.VERTICAL);
        gameStatusService.addShipToList(ship1);
        Ship ship2 = new Ship(1, 9, 0, Position.VERTICAL);
        gameStatusService.addShipToList(ship2);
        Ship ship3 = new Ship(1, 3, 3, Position.VERTICAL);
        gameStatusService.addShipToList(ship3);
        Ship ship4 = new Ship(1, 5, 5, Position.VERTICAL);
        gameStatusService.addShipToList(ship4);
        Ship ship5 = new Ship(2, 8, 6, Position.VERTICAL);
        gameStatusService.addShipToList(ship5);

        //when
        List<Board> boardList = gameStatusService.getBoardList();
        //then
        assertEquals(2, gameStatusService.getCurrentPlayer(boardList));
    }

    @DirtiesContext
    @Test
    public void shouldCleanBoards() {
        //given
        addAllShipsToBoards();
        shootDownAllShipsOnOneBoards();
        //when
        gameStatusService.resetGame();
        List<Board> boardList = gameStatusService.getBoardList();
        List<Ship> shipsPly1 = boardList.get(0).getShips();
        List<Ship> shipsPly2 = boardList.get(1).getShips();
        //then
        assertEquals(0, shipsPly1.size());
        assertEquals(0, shipsPly2.size());
    }


}
