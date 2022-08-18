import board.Board;
import ship.Position;
import ship.Ship;
import control.Shot;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class GameScenarioTest {

  @Test
  public void gameWorks() {
    // symulacja dodawania statków przez p1
    Board board1 = new Board();
    List<Ship> list = new ArrayList<>();
     assertTrue(board1.addShip(2, 2, 2, Position.VERTICAL));
     assertTrue(board1.addShip(2, 8, 2, Position.HORIZONTAL));
    // symulacja strzelania przez p2
    Board board2 = new Board();
    assertInstanceOf(Shot.class, board2.correctShoot(board1.getShips(), 9, 0));
    assertInstanceOf(Shot.class, board2.correctShoot(board1.getShips(), 3, 5));
    assertInstanceOf(Shot.class, board2.correctShoot(board1.getShips(), 5, 8));
    assertInstanceOf(Shot.class, board2.correctShoot(board1.getShips(), 9, 9));
    assertFalse(board1.isFinished());
    assertInstanceOf(Shot.class, board2.correctShoot(board1.getShips(), 2, 2));
    assertInstanceOf(Shot.class, board2.correctShoot(board1.getShips(), 2, 3));
    list.add(new Ship(2,2,2,Position.VERTICAL));
    board1.removeDeadShipFromList(list);
    list.clear();
    assertInstanceOf(Shot.class, board2.correctShoot(board1.getShips(), 8, 2));
    assertInstanceOf(Shot.class, board2.correctShoot(board1.getShips(), 7, 2));
    list.add(new Ship(2, 8, 2, Position.HORIZONTAL));
    board1.removeDeadShipFromList(list);
    list.clear();
    assertTrue(board1.isFinished());

//     assertFalse(board.shoot(5, 6)); lub assertThrowsException();
//     assertFalse(board.shoot(5, 7)); lub assertThrowsException();
//     assertFalse(board.shoot(5, 8)); lub assertThrowsException();
//     assertFalse(board.ifFinished());
//     assertTrue(board.shoot(0, 0));
//     assertTrue(board.shoot(0, 0));
//     assertTrue(board.shoot(0, 0));
//     assertTrue(board.shoot(0, 0));
//
//     assertTrue(board.ifFinished())
  }
}
