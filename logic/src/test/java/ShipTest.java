import dataConfig.Position;
import board.Shot;
import org.junit.jupiter.api.Test;
import ship.Ship;

import static org.junit.jupiter.api.Assertions.*;

class ShipTest {

    @Test
    public void hittedShipOneMasted() {
        Ship ship = new Ship(1,1,1, Position.VERTICAL);
        Shot shot = new Shot(1, 1);

        assertTrue(ship.checkIfHit(shot.getX(), shot.getY()));
    }

    @Test
    public void hittedShipThreeMasted() {
        Ship ship = new Ship(3,5,5,Position.HORIZONTAL);
        Shot shot = new Shot(4,5);

        assertTrue(ship.checkIfHit(shot.getX(), shot.getY()));
    }
    @Test
    public void hittedShipFourMasted() {
        Ship ship = new Ship(4,9,0,Position.HORIZONTAL);
        Shot shot = new Shot(6,0);

        assertTrue(ship.checkIfHit(shot.getX(), shot.getY()));
    }

    @Test
    public void missedShipFourMasted() {
        Ship ship = new Ship(4,9,0,Position.HORIZONTAL);
        Shot shot = new Shot(9,2);

        assertFalse(ship.checkIfHit(shot.getX(), shot.getY()));
    }

    @Test
    public void sunkShipOneMasted() {
        Ship ship = new Ship(1,5,5,Position.HORIZONTAL);
        Shot shot = new Shot(5,5);
        ship.checkIfHit(shot.getX(), shot.getY());

        assertTrue(ship.checkIfDead());
    }

    @Test
    public void sunkShipThreeMasted() {
        Ship ship = new Ship(3,0,5,Position.VERTICAL);
        Shot shot = new Shot(0,5);
        Shot shot1 = new Shot(0,6);
        Shot shot2 = new Shot(0,7);
        ship.checkIfHit(shot.getX(), shot.getY());
        ship.checkIfHit(shot1.getX(), shot1.getY());
        ship.checkIfHit(shot2.getX(), shot2.getY());

        assertTrue(ship.checkIfDead());
    }

    @Test
    public void notSunkShipThreeMasted() {
        Ship ship = new Ship(3,0,5,Position.VERTICAL);
        Shot shot = new Shot(0,5);
        Shot shot1 = new Shot(0,6);
        Shot shot2 = new Shot(5,7);
        ship.checkIfHit(shot.getX(), shot.getY());
        ship.checkIfHit(shot1.getX(), shot1.getY());
        ship.checkIfHit(shot2.getX(), shot2.getY());

        assertFalse(ship.checkIfDead());
    }

    @Test
    public void notSunkShipFourMasted() {
        Ship ship = new Ship(4,5,5,Position.VERTICAL);
        Shot shot = new Shot(5,5);
        Shot shot1 = new Shot(5,6);
        Shot shot2 = new Shot(5,7);
        ship.checkIfHit(shot.getX(), shot.getY());
        ship.checkIfHit(shot1.getX(), shot1.getY());
        ship.checkIfHit(shot2.getX(), shot2.getY());

        assertFalse(ship.checkIfDead());
    }


}