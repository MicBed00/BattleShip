package board;


import DataConfig.SizeBoard;
import com.fasterxml.jackson.annotation.JsonIgnore;
import control.Render;
import control.Shot;
import control.UI;
import exceptions.CollidingException;
import exceptions.OutOfBoundsException;
import exceptions.ShipLimitExceedException;
import exceptions.ShotSamePlaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import DataConfig.Position;
import ship.Ship;
import DataConfig.ShipLimits;
import DataConfig.ShipSize;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static DataConfig.ShipLimits.SHIP4SAIL;

public class Board {
    private final Logger log = LoggerFactory.getLogger(Board.class);
    private final UI user = new UI();
    private static final int qtyShip4 = SHIP4SAIL.getQty();
    private static final int qtyShip3 = ShipLimits.SHIP3SAIL.getQty();
    private static final int qtyShip2 = ShipLimits.SHIP2SAIL.getQty();
    private static final int qtyShip1 = ShipLimits.SHIP1SAIL.getQty();
    public static final int shipLimit = ShipLimits.SHIP_LIMIT.getQty();


    private List<Ship> ships = new ArrayList<>();
  //  @JsonSerialize(keyUsing = MapKeySerializer.class)
  // @JsonDeserialize(keyUsing = MapKeyDeserializer.class)
    private Map<Shot, Boolean> opponetsShots = new LinkedHashMap<>();
    private Ship hittedShip;
    private Boolean registerHit;

    public List<Ship> getShips() {
        return ships;
    }

    public Map<Shot, Boolean> getOpponetsShots() {
        return opponetsShots;
    }

    public Boolean getRegisterHit() {
        return registerHit;
    }

    public void setRegisterHit(Boolean registerHit) {
        this.registerHit = registerHit;
    }

    public boolean addShip(int length, int x, int y, Position position) throws ShipLimitExceedException, OutOfBoundsException {
        List<Ship> copyList = new ArrayList<>();
        int beforeAddNewShip = ships.size();
        if (checkIfOutOfBounds(length, x, y, position)) {
            log.warn("Object Ship is out of Board");
            throw new OutOfBoundsException(user.getBundle().getString("outOfBoundsShip"));
        }
        if (ships.isEmpty()) {
            ships.add(new Ship(length, x, y, position));
            counterShip(ships, length);
            return true;
        }
        ships.forEach(s -> {
            if (counterShip(ships, length)) {
                log.warn("The ship limit has reached");
                throw new ShipLimitExceedException(user.getBundle().getString("shipLimitExceed"));
            }
            if (!isColliding(s, length, x, y, position)) {
                copyList.add(new Ship(length, x, y, position));
            } else {
                log.debug("Collision length: {}, x: {}, y: {}, position: {}", length, x, y, position);
                throw new CollidingException(user.getBundle().getString("collidingException"));
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

            if (length == ShipSize.FOUR.getSize() && list.size() < qtyShip4) {
                return false;
            }
            if (length == ShipSize.THREE.getSize() && list.size() < qtyShip3) {
                return false;
            }
            if (length == ShipSize.TWO.getSize() && list.size() < qtyShip2) {
                return false;
            }
            if (length == ShipSize.ONE.getSize() && list.size() < qtyShip1) {
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
        registerHit = false;
        hittedShip = null;

       if(correctShot(shot)) {
           ships.forEach(ship -> {
               if (ship.checkIfHit(shot.getX(), shot.getY())) {
                   registerHit = true;
                   hittedShip = ship;
               }
           });
       }
       addShotToMap(shot, registerHit);
       printShoot(opponetsShots, ships, shot);
       return registerHit;
    }

    private boolean correctShot(Shot shot) {
        if (shotSamePlace(shot)) {
            log.warn("Shoot in the same place");
            throw new ShotSamePlaceException(user.getBundle().getString("shotSamePlaceException"));
        }
        return true;
    }

    private void addShotToMap(Shot shot, boolean registerHit) {
        opponetsShots.put(shot, registerHit);
    }

    private boolean shotSamePlace(Shot shot) {
        return opponetsShots.containsKey(shot);
    }

    private void printShoot(Map<Shot, Boolean> opponetsShots, List<Ship> list, Shot shot) throws ArrayIndexOutOfBoundsException {
        System.out.println(opponetsShots.get(shot) ? user.messageBundle("hit") : user.messageBundle("miss"));
        if(hittedShip != null) {
            if (hittedShip.checkIfDead()) {
                System.out.println(user.messageBundle("shipSunk") + hittedShip +" \n");
            }
        }
        Render.renderShots(opponetsShots);
        System.out.println("###################################################\n");
    }

    @JsonIgnore
    public AtomicBoolean getIsFinished() {
        AtomicBoolean isFinished = new AtomicBoolean(true);
        ships.forEach(ship -> {
                    if(!ship.checkIfDead())
                        isFinished.set(false);
                });
        return isFinished;
    }
//
//    public void setFinished(boolean finished) {
//        isFinished.set(finished);
//    }

    public int[] statisticsShot() {
        int numberOfshots = this.opponetsShots.size();
        int numberShotsHit = numberOfShotsHit(this.opponetsShots);
      return new int[] {numberOfshots, numberShotsHit};
    }

    private int numberOfShotsHit(Map<Shot, Boolean> opponetsShots) {
        Set<Shot> shots = opponetsShots.keySet();
        long counterAccurateShot = shots.stream()
                .filter(opponetsShots::get)
                .count();
        return (int)counterAccurateShot;
    }
}
