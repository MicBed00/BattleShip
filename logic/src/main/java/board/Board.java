package board;

import DataConfig.Position;
import DataConfig.ShipLimits;
import com.fasterxml.jackson.annotation.JsonIgnore;
import exceptions.CollidingException;
import exceptions.OutOfBoundsException;
import exceptions.ShipLimitExceedException;
import exceptions.ShotSamePlaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ship.Ship;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Board {
    private final Logger log = LoggerFactory.getLogger(Board.class);
    private final UI user = new UI();
    private List<Ship> ships = new ArrayList<>();
    private Set<Shot> opponetShots = new HashSet<>();
    private Ship hittedShip;
    @JsonIgnore
    private Boolean registerHit;

    public List<Ship> getShips() {
        return ships;
    }

    public Set<Shot> getOpponetShots() {
        return opponetShots;
    }

    public boolean addShip(int length, int x, int y, Position position) throws ShipLimitExceedException, OutOfBoundsException {
        List<Ship> copyList = new ArrayList<>();
        int beforeAddNewShip = ships.size();
        if (checkIfOutOfBounds(length, x, y, position)) {
            log.warn("Object Ship is out of Board");
            throw new OutOfBoundsException(user.messageBundle("outOfBoundsShip"));
        }
        if (ships.isEmpty()) {
            ships.add(new Ship(length, x, y, position));
            counterShip(ships, length);
            return true;
        }
        ships.forEach(s -> {
            if (counterShip(ships, length)) {
                log.warn("The ship limit has reached");
                throw new ShipLimitExceedException(user.messageBundle("shipLimitExceed", length));
            }
            if (!isColliding(s, length, x, y, position)) {
                copyList.add(new Ship(length, x, y, position));
            } else {
                log.debug("Collision length: {}, x: {}, y: {}, position: {}", length, x, y, position);
                throw new CollidingException(user.messageBundle("collidingException", s.toString()));
            }
        });
        ships.addAll(copyList);
        copyList.clear();
        int afterAddShip = ships.size();
        return beforeAddNewShip < afterAddShip;
    }

    private boolean checkIfOutOfBounds(int length, int x, int y, Position position) {
        return Position.VERTICAL == position && y + length > Render.getSizeBoard()
                || Position.HORIZONTAL == position && (x + 1) - length < 0
                || x > Render.getSizeBoard()
                || y > Render.getSizeBoard();
    }

    private boolean counterShip(List<Ship> listShip, int length) {
        List<Ship> list = listShip.stream()
                .filter(s -> s.getLength() == length)
                .toList();

        if (length == ShipSize.FOUR.getSize() && list.size() < ShipLimits.SHIP4SAIL.getQty()) {
            return false;
        }
        if (length == ShipSize.THREE.getSize() && list.size() < ShipLimits.SHIP3SAIL.getQty()) {
            return false;
        }
        if (length == ShipSize.TWO.getSize() && list.size() < ShipLimits.SHIP2SAIL.getQty()) {
            return false;
        }
        if (length == ShipSize.ONE.getSize() && list.size() < ShipLimits.SHIP1SAIL.getQty()) {
            return false;
        }
        return true;
    }

    private boolean isColliding(Ship ship, int length, int x, int y, Position position) {
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

    public boolean shoot(Shot shot) throws ShotSamePlaceException {
        shot.setState(Shot.State.MISSED);
        registerHit = false;
        hittedShip = null;

        if (correctShot(shot)) {
            ships.forEach(ship -> {
                if (ship.checkIfHit(shot.getX(), shot.getY())) {
                    shot.setState(Shot.State.HIT);
                    registerHit = true;
                    hittedShip = ship;
                }
            });
        }
        opponetShots.add(shot);
        printShoot(opponetShots, shot);
        return registerHit;
    }

    private boolean correctShot(Shot shot) {
        if (shotSamePlace(shot)) {
            log.warn("Shoot in the same place");
            throw new ShotSamePlaceException(user.messageBundle("shotSamePlaceException"));
        }
        return true;
    }


    private boolean shotSamePlace(Shot shot) {
        return opponetShots.contains(shot);
    }

    private void printShoot(Set<Shot> shotList, Shot shot) throws ArrayIndexOutOfBoundsException {
        System.out.println(shot.getState().equals(Shot.State.HIT) ? user.messageBundle("hit") : user.messageBundle("miss"));
        if (hittedShip != null) {
            if (hittedShip.checkIfDead()) {
                System.out.println(user.messageBundle("shipSunk", hittedShip) + "\n");
            }
        }
        Render.renderShots(shotList);
        System.out.println("###################################################\n");
    }

    @JsonIgnore
    public AtomicBoolean getIsFinished() {
        AtomicBoolean isFinished = new AtomicBoolean(true);
        ships.forEach(ship -> {
            if (!ship.checkIfDead())
                isFinished.set(false);
        });
        return isFinished;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return Objects.equals(ships, board.ships) && Objects.equals(opponetShots, board.opponetShots);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ships, opponetShots);
    }

    public int[] statisticsShot() {
        int numberOfshots = this.opponetShots.size();
        int numberShotsHit = numberOfShotsHit(this.opponetShots);
        return new int[]{numberOfshots, numberShotsHit};
    }

    private int numberOfShotsHit(Set<Shot> shots) {
        long counterAccurateShot = shots.stream()
                .filter(s -> s.getState().equals(Shot.State.HIT))
                .count();
        return (int) counterAccurateShot;
    }
}
