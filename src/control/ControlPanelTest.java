package control;

import control.ControlPanel;
import control.Position;
import control.Ship;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ControlPanelTest {

  @Test
  public void addsFirstShip() {
    ControlPanel controlPanel = new ControlPanel();
    ArrayList<Ship> shipList = new ArrayList<>();
    //assertTrue(controlPanel.addShip(shipList, 4, 0, 0, Position.VERTICAL));
    assertTrue(controlPanel.addShip(shipList, 4, 5, 0, control.Position.HORIZONTAL));
  }
}
