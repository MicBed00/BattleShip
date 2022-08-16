package control;

import java.util.ArrayList;
import java.util.List;


public class Board {
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
    if (checkIfOutOfBounds(length, x, y, position))
      return false;
    if (ships.isEmpty()) {
      ships.add(new Ship(length, x, y, position));                                 // stworzenie obiektu control.Ship i dodanie do pustej listy
      return true;
    }
    ships.forEach(s -> {
      if (overShipLimit(ships, s, length)) {
        throw new ShipLimitExceedException("Limit statków " + length + " masztowych został osiągnięty");
      }
      if (!s.isColliding(length, x, y, position)) {
        copyList.add(new Ship(length, x, y, position));                             // stworzenie obiektu control.Ship i dodanie do listy pomocniczej
      } else {
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
            || Position.HORIZONTAL == position && x - length < 0) {
      throw new OutOfBoundsException("Statek wykracza poza obszar planszy");
    }
    return false;
  }

  private boolean overShipLimit(List<Ship> listShip, Ship ship, int length) {
    //sprawdzić po parametrze length ile już znajduję się statków w liscie
//        int counterShip4 = Collections.frequency(listShip,ship.getLength() == qtyShip4);
//        int counterShip3 = Collections.frequency(listShip,ship.getLength() == qtyShip3);
//        int counterShip2 = Collections.frequency(listShip,ship.getLength() == qtyShip2);
//        int counterShip1 = Collections.frequency(listShip,ship.getLength() == qtyShip1);

    //nie wiem czemu nie mogę inkrementowac counterów strumieniu
//        listShip.stream().filter(s -> s.getLength() == length)
//                .map(Ship::getLength)
//                .forEach(s -> {
//            if (s < ShipLimits.SHIP4SAIL.getQty()) counterShip4++;
//            if (s < ShipLimits.SHIP3SAIL.getQty()) counterShip3++;
//            if (s < ShipLimits.SHIP2SAIL.getQty()) counterShip2++;
//            if (s < ShipLimits.SHIP1SAIL.getQty()) counterShip1++;
//        });

    List<Integer> list = listShip.stream()
            .filter(s -> s.getLength() == length)       //sprawdzam po długości ile statków znajduję się na liście
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

  public static Shot correctShoot(List<Ship> listPlayer) throws shotSamePlaceException {
    UI user = new UI();
    System.out.printf("Podaj ws X %s\n", listPlayer);
    int x = user.getInt();
    System.out.printf("Podaj ws Y %s\n", listPlayer);
    int y = user.getInt();
    listPlayer.forEach(s -> {
      if(shotSamePlace(s, x, y))
        throw new shotSamePlaceException("Strzał w to samo miejsce!");
    });
    return new Shot(x, y);
  }

  private static boolean shotSamePlace(Ship ship, int x, int y) {
   boolean[] hit = ship.getHits();

   if(ship.getXstart() >= x && ship.getXend() <= x && ship.getYstart() == y) {
     return hit[ship.getXstart() - x];
   } else if(ship.getYstart() <= y && ship.getYend() >= y && ship.getXstart() == x) {
     return hit[y - ship.getYstart()];
   } else
     return false;
  }

  public void registerShoot(int[] registerShot, List<Ship> list, Shot shot) {
    System.out.println(registerShot[0] == 1 ? "Hit!" : "Miss!");
    Render.printBoard(new Render().renderShots(list, shotBoard, shot.getX(), shot.getY()));
    System.out.println("###################################################\n");
  }


  public boolean isFinished() {
    return ships.isEmpty();
  }
}
