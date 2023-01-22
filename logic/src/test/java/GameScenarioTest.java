import board.Board;
import dataConfig.Position;
import board.Shot;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameScenarioTest {
//
//  @Test
//  public void gameWorks() {
//    // symulacja dodawania statków przez p1
//    Board board1 = new Board();
//     assertTrue(board1.addShip(2, 2, 2, Position.VERTICAL));
//     assertTrue(board1.addShip(2, 8, 2, Position.HORIZONTAL));
//    //p2 strzela niecelnie
//    Shot shot1 = new Shot(9,0);
//    board1.shoot(shot1);
//    assertEquals(Shot.State.MISSED, shot1.getState());
//
//    Shot shot2 = new Shot(3,5);
//    board1.shoot(shot2);
//    assertEquals(Shot.State.MISSED, shot2.getState());
//
//    Shot shot3 = new Shot(5,8);
//    board1.shoot(shot3);
//    assertEquals(Shot.State.MISSED, shot3.getState());
//
//    Shot shot4 = new Shot(9,9);
//    board1.shoot(shot4);
//    assertEquals(Shot.State.MISSED, shot4.getState());
//
//    assertFalse(board1.getIsFinished().get());
//
//    //p2 strzela celnie i eliminuje statki z planszy
//    Shot shot5 = new Shot(2,2);
//    board1.shoot(shot5);
//    assertEquals(Shot.State.HIT, shot5.getState());
//
//    Shot shot6 = new Shot(2,3);
//    board1.shoot(shot6);
//    assertEquals(Shot.State.HIT, shot6.getState());
//
//    Shot shot7 = new Shot(8,2);
//    board1.shoot(shot7);
//    assertEquals(Shot.State.HIT, shot7.getState());
//
//    Shot shot8 = new Shot(7,2);
//    board1.shoot(shot8);
//    assertEquals(Shot.State.HIT, shot8.getState());
//
//    assertTrue(board1.getIsFinished().get());
//  }
}
