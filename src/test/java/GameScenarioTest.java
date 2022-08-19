import board.Board;
import ship.Position;
import ship.Ship;
import control.Shot;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
public class GameScenarioTest {
/*
po każdym strzale zrobic pokolei każdą metodę z metody playgame() isHit, isDead itd
 */
  @Test
  public void gameWorks() {
    // symulacja dodawania statków przez p1
    Board board1 = new Board();
    List<Ship> list = new ArrayList<>();
     assertTrue(board1.addShip(2, 2, 2, Position.VERTICAL));
     assertTrue(board1.addShip(2, 8, 2, Position.HORIZONTAL));
    // symulacja strzelania przez p2
    Board board2 = new Board();
    //p2 strzela niecelnie
    assertInstanceOf(Shot.class, board2.correctShoot(9, 0));
    assertFalse(board1.getShips().get(0).isHit(9,0));
    assertFalse(board1.getShips().get(1).isHit(9,0));
    assertInstanceOf(Shot.class, board2.correctShoot( 3, 5));
    assertFalse(board1.getShips().get(0).isHit(3,5));
    assertFalse(board1.getShips().get(1).isHit(3,5));
    assertInstanceOf(Shot.class, board2.correctShoot( 5, 8));
    assertFalse(board1.getShips().get(0).isHit(5,8));
    assertFalse(board1.getShips().get(1).isHit(5,8));
    assertInstanceOf(Shot.class, board2.correctShoot(9, 9));
    assertFalse(board1.getShips().get(0).isHit(9,9));
    assertFalse(board1.getShips().get(1).isHit(9,9));
    assertFalse(board1.isFinished());
    //p2 strzela celnie i eliminuje statki z planszy
    assertInstanceOf(Shot.class, board2.correctShoot( 2, 2));
    assertTrue(board1.getShips().get(0).isHit(2,2));
    assertFalse(board1.getShips().get(1).isHit(2,2));
    assertInstanceOf(Shot.class, board2.correctShoot( 2, 3));
    assertTrue(board1.getShips().get(0).isHit(2,3));
    assertFalse(board1.getShips().get(1).isHit(2,2));
    assertTrue(list.add(new Ship(2,2,2,Position.VERTICAL)));
    assertTrue(board1.removeDeadShipFromList(list));
    list.clear();
    assertInstanceOf(Shot.class, board2.correctShoot( 8, 2));
//    assertFalse(board1.getShips().get(0).isHit(8,2));   // usunięty z listy po trafieniu
    assertTrue(board1.getShips().get(1).isHit(8,2));
    assertInstanceOf(Shot.class, board2.correctShoot( 7, 2));
//    assertFalse(board1.getShips().get(0).isHit(7,2));
    assertTrue(board1.getShips().get(1).isHit(7,2));
    assertTrue(list.add(new Ship(2, 8, 2, Position.HORIZONTAL)));
    assertTrue(board1.removeDeadShipFromList(list));
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
