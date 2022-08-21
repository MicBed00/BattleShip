import board.Board;
import control.*;
import exceptions.CollidingException;
import exceptions.OutOfBoundsException;
import exceptions.ShipLimitExceedException;
import exceptions.ShotSamePlaceException;
import org.junit.jupiter.api.Test;
import ship.Position;
import ship.Ship;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {


    @Test
    public void addFirstShip() {
        //given
        Board board = new Board();
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
        Board board = new Board();
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
        Board board = new Board();
        assertThrows(OutOfBoundsException.class, () -> board.addShip(3, 22, 4, Position.VERTICAL));
    }

    @Test
    public void shouldThrowShipLimitExceedException() {
        Board board = new Board();
        board.addShip(4, 1, 1, Position.VERTICAL);
        assertThrows(ShipLimitExceedException.class, () -> board.addShip(4, 8, 1, Position.VERTICAL));
    }

    @Test
    public void shouldThrowCollidingExeption() {
        Board board = new Board();
        board.addShip(1, 1, 1, Position.VERTICAL);
        assertThrows(CollidingException.class, () -> board.addShip(1, 1, 1, Position.VERTICAL));
    }

    @Test
    public void overShipLimit() {
        //given
        Board board = new Board();
        int length1 = 4;
        int x1 = 0;
        int y1 = 0;
        Position position1 = Position.VERTICAL;
        board.addShip(length1, x1, y1, position1);
        int length2 = 4;
        int x2 = 8;
        int y2 = 2;
        Position position2 = Position.HORIZONTAL;
        //then
        assertThrows(ShipLimitExceedException.class, () -> board.addShip(length2, x2, y2, position2));
    }

    @Test
    public void addShipEachSize() {
        //given
        Board board = new Board();
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
        Board board = new Board();
        List<Ship> list = new ArrayList<>();
        int x = 4;
        int y = 5;
        //when
        Shot shot = board.correctShoot(x, y);
        //then
        assertInstanceOf(Shot.class, shot);
    }

    @Test
    public void correctShotHit() {
        //given
        Board board1 = new Board();
        Board board2 = new Board();
        board2.addShip(2, 2, 2, Position.VERTICAL);
        int x = 4;
        int y = 5;
        //when
        Shot shot = board1.correctShoot(x, y);
        //then
        assertInstanceOf(Shot.class, shot);
    }

    @Test
    public void shotSamePlace() {
        //given
        Board board1 = new Board();
        Board board2 = new Board();
        board2.addShip(2, 2, 2, Position.VERTICAL);
        int x1 = 4;
        int y1 = 5;
        //when
        Shot correctShot = board1.correctShoot(x1, y1);

        //then
        assertThrows(ShotSamePlaceException.class, () -> board1.correctShoot(x1, y1));
    }
}
