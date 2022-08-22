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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
  public List<Ship> getShips() {
    return ships;
  }

  public char[][] getShotBoard() {
    return shotBoard;
  }

  public boolean addShip(int length, int x, int y, Position position) throws ShipLimitExceedException, OutOfBoundsException {
    List<Ship> copyList = new ArrayList<>();
    int beforeAddNewShip = ships.size();                                           // rozmiar listy przed dodaniem nowego statku
    if (checkIfOutOfBounds(length, x, y, position)) {
      log.warn("Object Ship is out of Board");
      System.out.println("Wciśnij enter");
      throw new OutOfBoundsException("Statek wykracza poza obszar planszy");
    }
    if (ships.isEmpty()) {
      ships.add(new Ship(length, x, y, position));                                 // stworzenie obiektu ship.Ship i dodanie do pustej listy
      counterShip(ships,length);
      return true;
    }
    ships.forEach(s -> {
      if (counterShip(ships, length)) {
        log.warn("The ship limit has reached");
        System.out.println("Wciśnij enter");
        throw new ShipLimitExceedException("Limit statków " + length + " masztowych został osiągnięty");
      }
      if (!isColliding(s, length, x, y, position)) {
        copyList.add(new Ship(length, x, y, position));                             // stworzenie obiektu ship.Ship i dodanie do listy pomocniczej
      } else {
        log.debug("Collision length: {}, x: {}, y: {}, position: {}", length, x, y, position );
        System.out.println("Wciśnij enter");
        throw new CollidingException("Kolizja ze statkiem " + s.toString());
      }
    });
    ships.addAll(copyList);                                                              // dodanie nowego statku do docelowej listy
    copyList.clear();                                                                       // wyzerowanie listy pomocniczej
    int afterAddShip = ships.size();
    return beforeAddNewShip < afterAddShip;                                                 // sprawdzenie warunku czy rozmiar listy jest większy po dodaniu
  }

  private boolean checkIfOutOfBounds(int length, int x, int y, Position position) {
    if (Position.VERTICAL == position && y + length > Render.getSizeBoard()
            || Position.HORIZONTAL == position && (x + 1) - length < 0
            || x > Render.getSizeBoard()
            || y > Render.getSizeBoard()) {
      return true;
    }
    return false;
  }

  private boolean counterShip(List<Ship> listShip, int length) {
    //sprawdzić po parametrze length ile już znajduję się statków w liscie
        AtomicInteger counterShip4 = new AtomicInteger(0);
        AtomicInteger counterShip3 = new AtomicInteger(0);
        AtomicInteger counterShip2 = new AtomicInteger(0);
        AtomicInteger counterShip1 = new AtomicInteger(0);

  //  nie wiem czemu nie mogę inkrementowac counterów strumieniu
        listShip.stream().filter(s -> s.getLength() == length)
                .map(Ship::getLength)
                .forEach(s -> {
            if (s < ShipLimits.SHIP4SAIL.getQty()) counterShip4.getAndIncrement();
            if (s < ShipLimits.SHIP3SAIL.getQty()) counterShip3.getAndIncrement();
            if (s < ShipLimits.SHIP2SAIL.getQty()) counterShip2.getAndIncrement();
            if (s < ShipLimits.SHIP1SAIL.getQty()) counterShip1.getAndIncrement();
        });

//    List<Integer> list = listShip.stream()
//            .filter(s -> s.getLength() == length)       //sprawdzam po długości ile statków znajduję się na liście
//            .map(Ship::getLength)
//            .toList();
//    for (Ship s : listShip) {
//
//      if (length == ShipSize.FOUR.getSize() && list.size() < qtyShip4) {
//        counterShip4++;
//        return false;
//      }
//      if (length == ShipSize.THREE.getSize() && list.size() < qtyShip3) {
//        counterShip3++;
//        return false;
//      }
//      if (length == ShipSize.TWO.getSize() && list.size() < qtyShip2) {
//        counterShip2++;
//        return false;
//      }
//      if (length == ShipSize.ONE.getSize() && list.size() < qtyShip1) {
//        counterShip1++;
//        return false;
//      }
//    }
    return true;
  }
  public boolean isColliding(Ship ship, int length, int x, int y, Position position) {
    if (ship.getXstart() == x && ship.getYstart() == y) {
      return true;
    }
    if (position == Position.HORIZONTAL) {
      if (ship.getYstart() == y) {
        if (x - length < 0) return true;
                /* true to koliduje wstawiamy na prawo od istniejącego statku
                   false to koliduje wstawimy na lewo od istniejącego statku
                */
        return ship.getXstart() < x ? ship.getXstart() >= x - length : ship.getXstart() - ship.getLength() <= x;
      }
    }
    if (position == Position.VERTICAL) {
      if (ship.getXstart() == x) {
        if (y + length > SizeBoard.ROW.getSize()) return true;
                   /*true to koliduje. Wstaiamy poniżej istniejącym statkiem
                   true to koliduje. Wstawiamy nad istniejącego statku
                 */
        return ship.getYstart() < y ? ship.getYstart() + ship.getLength() >= y : ship.getYstart() <= y + length;
      }
    }
    if (ship.getPosition() != position) {// sprawdza czy statki nie przecinają
      if(Position.VERTICAL == ship.getPosition()) {
        if (ship.getYstart() <= y && ship.getYstart() + ship.getLength() >= y && ship.getXstart() < x)
          return ship.getXstart() >= x - length;
      } else {
        if (ship.getXstart() >= x && ship.getXstart() - ship.getLength() <= x && ship.getYstart() > y)
          return ship.getYstart() <= y + length;
      }
    }
    return false;
  }

  public Shot correctShoot(List<Ship> listPlayer, int x, int y) throws ShotSamePlaceException {
    listPlayer.forEach(s -> {
      if(shotSamePlace(s, x, y)) {
        log.warn("Shoot in the same place");
        throw new ShotSamePlaceException("Strzał w to samo miejsce!");
      }
    });
    return new Shot(x, y);
  }

  private boolean shotSamePlace(Ship ship, int x, int y) {
   boolean[] hit = ship.getHits();
   if (shotBoard[x][y] == 'O')
     return true;
   if(ship.getXstart() >= x && ship.getXend() <= x && ship.getYstart() == y) {
     return hit[ship.getXstart() - x];
   } else if(ship.getYstart() <= y && ship.getYend() >= y && ship.getXstart() == x) {
     return hit[y - ship.getYstart()];
   } else
     return false;

  }

  public void registerAndPrintShoot(int[] registerShot, List<Ship> list, Shot shot) {
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
