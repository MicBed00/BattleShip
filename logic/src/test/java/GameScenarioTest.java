import board.Board;
import DataConfig.Position;
import board.Shot;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameScenarioTest {

  @Test
  public void gameWorks() {
    // symulacja dodawania statków przez p1
    Board board1 = new Board();
     assertTrue(board1.addShip(2, 2, 2, Position.VERTICAL));
     assertTrue(board1.addShip(2, 8, 2, Position.HORIZONTAL));
    //p2 strzela niecelnie
    assertFalse(board1.shoot(new Shot(9,0)));
    assertFalse(board1.shoot(new Shot(3,5)));
    assertFalse(board1.shoot(new Shot(5,8)));
    assertFalse(board1.shoot(new Shot(9,9)));
    assertFalse(board1.getIsFinished().get());
    //p2 strzela celnie i eliminuje statki z planszy
    assertTrue(board1.shoot(new Shot(2,2)));
    assertTrue(board1.shoot(new Shot(2,3)));
    assertTrue(board1.shoot(new Shot(8,2)));
    assertTrue(board1.shoot(new Shot(7,2)));
    assertTrue(board1.getIsFinished().get());
  }
}
