package board;

import control.Render;
import control.Shot;
import exceptions.CollidingException;
import exceptions.OutOfBoundsException;
import exceptions.ShipLimitExceedException;
import exceptions.ShotSamePlaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ship.Position;
import ship.Ship;
import ship.ShipLimits;
import ship.ShipSize;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Board {
    private final Logger log = LoggerFactory.getLogger(Board.class);
    private static final int qtyShip4 = ShipLimits.SHIP4SAIL.getQty();
    private static final int qtyShip3 = ShipLimits.SHIP3SAIL.getQty();
    private static final int qtyShip2 = ShipLimits.SHIP2SAIL.getQty();
    private static final int qtyShip1 = ShipLimits.SHIP1SAIL.getQty();
    public static final int shipLimit = ShipLimits.SHIP_LIMIT.getQty();
    int counterShip4 = 0;
    int counterShip3 = 0;
    int counterShip2 = 0;
    int counterShip1 = 0;

    List<Ship> ships = new ArrayList<>();
    char[][] shotBoard = new char[Render.getSizeBoard()][Render.getSizeBoard()];
    Set<Shot> opponetsShots = new HashSet<Shot>();

    public List<Ship> getShips() {
        return ships;
    }

    public char[][] getShotBoard() {
        return shotBoard;
    }

    public boolean addShip(int length, int x, int y, Position position) throws ShipLimitExceedException, OutOfBoundsException {
        List<Ship> copyList = new ArrayList<>();
        int beforeAddNewShip = ships.size();                                                  // rozmiar listy przed dodaniem nowego statku

        if (checkIfOutOfBounds(length, x, y, position)) {
            log.warn("Object Ship is out of Board");
            throw new OutOfBoundsException("Statek wykracza poza obszar planszy. Wciśnij enter i wprowadź dane ponownie");
        }
        if (ships.isEmpty()) {
            ships.add(new Ship(length, x, y, position));
            counterShip(ships, length);
            return true;
        }
        ships.forEach(s -> {
            if (counterShip(ships, length)) {
                log.warn("The ship limit has reached");
                throw new ShipLimitExceedException("Limit statków " + length + " masztowych został osiągnięty. Wciśnij enter i wprowadź dane ponownie ");
            }
            if (!isColliding(s, length, x, y, position)) {
                copyList.add(new Ship(length, x, y, position));
            } else {
                log.debug("Collision length: {}, x: {}, y: {}, position: {}", length, x, y, position);
                throw new CollidingException("Kolizja ze statkiem " + s + " .Wciśnij enter i wprowadź dane ponownie");
            }
        });
        ships.addAll(copyList);
        copyList.clear();
        int afterAddShip = ships.size();
        return beforeAddNewShip < afterAddShip;
    }

    private boolean checkIfOutOfBounds(int length, int x, int y, Position position) {
        if (checkIfInBoard(length, x, y, position)) {
            return true;
        }
        return false;
    }

    private boolean checkIfInBoard(int length, int x, int y, Position position) {
        return Position.VERTICAL == position && y + length > Render.getSizeBoard()
                || Position.HORIZONTAL == position && (x + 1) - length < 0
                || x > Render.getSizeBoard()
                || y > Render.getSizeBoard();
    }

    private boolean counterShip(List<Ship> listShip, int length) {
        List<Integer> list = listShip.stream()
                .filter(s -> s.getLength() == length)
                .map(Ship::getLength)
                .toList();

        for (Ship s : listShip) {
            if (length == ShipSize.FOUR.getSize() && list.size() < qtyShip4) {
                counterShip4++;
                return false;
            }
            if (length == ShipSize.THREE.getSize() && list.size() < qtyShip3) {
                counterShip3++;
                return false;
            }
            if (length == ShipSize.TWO.getSize() && list.size() < qtyShip2) {
                counterShip2++;
                return false;
            }
            if (length == ShipSize.ONE.getSize() && list.size() < qtyShip1) {
                counterShip1++;
                return false;
            }
        }
        return true;
    }

    public boolean isColliding(Ship ship, int length, int x, int y, Position position) {
        if (ship.getXstart() == x && ship.getYstart() == y) {
            return true;
        }

        if (position == Position.HORIZONTAL) {
            if (ship.getYstart() == y) {
                if (x - length < 0) {
                    return true;
                }
                return ship.getXstart() < x ? ship.getXstart() >= x - length : ship.getXstart() - ship.getLength() <= x;
            }
        }

        if (position == Position.VERTICAL) {
            if (ship.getXstart() == x) {
                if (y + length > SizeBoard.ROW.getSize()) {
                    return true;
                }
                return ship.getYstart() < y ? ship.getYstart() + ship.getLength() >= y : ship.getYstart() <= y + length;
            }
        }

        if (ship.getPosition() != position) {                                 // sprawdza czy statki się nie przecinają
            if (Position.VERTICAL == ship.getPosition()) {
                if (ifYoverlapsOnTheShip(ship, x, y))
                    return ship.getXstart() >= x - length;
            } else {
                if (ifXoverlapOnTheShip(ship, x, y))
                    return ship.getYstart() <= y + length;
            }
        }
        return false;
    }

    private boolean ifYoverlapsOnTheShip(Ship ship, int x, int y) {
        return ship.getYstart() <= y && ship.getYstart() + ship.getLength() >= y && ship.getXstart() < x;
    }

    private boolean ifXoverlapOnTheShip(Ship ship, int x, int y) {
        return ship.getXstart() >= x && ship.getXstart() - ship.getLength() <= x && ship.getYstart() > y;
    }

    public Shot correctShoot(int x, int y) throws ShotSamePlaceException {
        Shot shot = new Shot(x, y);

        if (shotSamePlace(shot)) {
            log.warn("Shoot in the same place");
            throw new ShotSamePlaceException("Shoot in the same place!");
        };
        addShotToSet(shot);
        return new Shot(x, y);
    }

    private void addShotToSet(Shot shot) {
        opponetsShots.add(shot);
    }

    private boolean shotSamePlace(Shot shot) {
        return opponetsShots.contains(shot);
    }

    public void printShoot(int[] registerShot, List<Ship> list, Shot shot) throws ArrayIndexOutOfBoundsException {
        System.out.println(registerShot[0] == 1 ? "Hit!" : "Miss!");
        Render.printBoard(new Render().renderShots(list, shotBoard, shot.getX(), shot.getY()));
        System.out.println("###################################################\n");
    }

    public boolean removeDeadShipFromList(List<Ship> listShip) {
        return this.ships.removeAll(listShip);
    }

    public boolean isFinished() {
        return this.ships.isEmpty();
    }
}
