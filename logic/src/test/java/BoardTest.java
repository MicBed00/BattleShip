import dataConfig.Position;
import board.Board;
import board.Shot;
import exceptions.CollidingException;
import exceptions.OutOfBoundsException;
import exceptions.ShipLimitExceedException;
import exceptions.ShotSamePlaceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    private int sizeBoard = 11;
    private Board board;

    @BeforeEach
    void createBoard() {
        board = new Board(sizeBoard);
    }
    @Test
    public void addFirstShip() {
        //given
        int length = 3;
        int x = 0;
        int y = 0;
        Position position = Position.VERTICAL;

        //when
        boolean firstShip = board.addShip(length, x, y, position);

        //then
        assertTrue(firstShip);
    }

    @Test
    public void addSeveralShip() {
        //given
        int length1 = 3;
        int x1 = 0;
        int y1 = 0;
        Position position1 = Position.VERTICAL;

        int length2 = 2;
        int x2 = 5;
        int y2 = 2;
        Position position2 = Position.HORIZONTAL;

        int length3 = 3;
        int x3 = 9;
        int y3 = 5;
        Position position3 = Position.VERTICAL;

        int length4 = 4;
        int x4 = 6;
        int y4 = 9;
        Position position4 = Position.HORIZONTAL;

        //when
        boolean firstShip1 = board.addShip(length1, x1, y1, position1);
        boolean firstShip2 = board.addShip(length2, x2, y2, position2);
        boolean firstShip3 = board.addShip(length3, x3, y3, position3);
        boolean firstShip4 = board.addShip(length4, x4, y4, position4);

        //then
        assertTrue(firstShip1);
        assertTrue(firstShip2);
        assertTrue(firstShip3);
        assertTrue(firstShip4);
    }

    @Test
    public void shouldThrowOutOfBoundsException() {
        assertThrows(OutOfBoundsException.class, () -> board.addShip(3, 22, 4, Position.VERTICAL));
    }

    @Test
    public void shouldThrowShipLimitExceedException() {
        //only one 4 - length
        board.addShip(4, 1, 1, Position.VERTICAL);      //first
        assertThrows(ShipLimitExceedException.class, () -> board.addShip(4, 8, 1, Position.VERTICAL)); //second
    }

    @Test
    public void shouldThrowCollidingExeption() {
        board.addShip(1, 1, 1, Position.VERTICAL);
        assertThrows(CollidingException.class, () -> board.addShip(1, 1, 1, Position.VERTICAL));
    }

    @Test
    public void addShipEachSize() {
        //given
        int length1 = 4;
        int x1 = 0;
        int y1 = 0;
        Position position1 = Position.VERTICAL;
        int length2 = 3;
        int x2 = 4;
        int y2 = 0;
        Position position2 = Position.VERTICAL;
        int length3 = 2;
        int x3 = 7;
        int y3 = 8;
        Position position3 = Position.HORIZONTAL;
        int length4 = 1;
        int x4 = 2;
        int y4 = 8;
        Position position4 = Position.VERTICAL;

        //when
        boolean firstShip1 = board.addShip(length1, x1, y1, position1);
        boolean firstShip2 = board.addShip(length2, x2, y2, position2);
        boolean firstShip3 = board.addShip(length3, x3, y3, position3);
        boolean firstShip4 = board.addShip(length4, x4, y4, position4);

        //then
        assertTrue(firstShip1);
        assertTrue(firstShip2);
        assertTrue(firstShip3);
        assertTrue(firstShip4);
    }

    @Test
    public void correctShotMiss() {
        //given
        Shot shot = new Shot(4, 5);
        board.shoot(shot);
        assertEquals(Shot.State.MISSED, shot.getState());
    }

    @Test
    public void correctShotHit() {
        //given
        Board board2 = new Board(sizeBoard);
        board2.addShip(2, 2, 2, Position.VERTICAL);
        Shot shot = new Shot(2, 3);
        board2.shoot(shot);
        assertEquals(Shot.State.HIT, shot.getState());
    }

    @Test
    public void shotSameHittedPlace() {
        //given
        Board board2 = new Board(sizeBoard);
        board2.addShip(2, 2, 2, Position.VERTICAL);
        Shot shot1 = new Shot(2,3);
        board2.shoot(shot1);
        Shot shot2 = new Shot(2,3);

        assertThrows(ShotSamePlaceException.class, () -> board2.shoot(shot2));
    }

    @Test
    public void shotSameMissedPlace() {
        //given
        Board board2 = new Board(sizeBoard);
        board2.addShip(2, 2, 2, Position.VERTICAL);
        Shot shot1 = new Shot(9,5);
        board2.shoot(shot1);
        Shot shot2 = new Shot(9,5);

        assertThrows(ShotSamePlaceException.class, () -> board2.shoot(shot2));
    }

    @Test
    public void checkIfFinishedTrue() {
        //ShipLimits = 2 ships
        board.addShip(1,1,1,Position.HORIZONTAL);
        board.addShip(1, 6,6,Position.HORIZONTAL);
        Shot shot1 = new Shot(1,1);
        Shot shot2 = new Shot(6, 6);
        board.shoot(shot1);
        board.shoot(shot2);

        assertTrue(board.getIsFinished().get());
    }

    @Test
    public void checkIfFinishedFalse() {
        //ShipLimits = 2 ships
        board.addShip(1,1,1,Position.HORIZONTAL);
        board.addShip(1, 6,6,Position.HORIZONTAL);
        Shot shot1 = new Shot(1,1);
        Shot shot2 = new Shot(7, 6);
        board.shoot(shot1);
        board.shoot(shot2);

        assertFalse(board.getIsFinished().get());
    }

    @Test
    public void shouldSetSizeBoard() {
        //given
        int size = 14;

        //when
        Board board = new Board(size);

        //then
        assertEquals(size, board.getSizeBoard());
    }
}
