package battelshipgame.control;

import battelshipgame.render.Render;
import battelshipgame.ships.Ship;
import battelshipgame.ships.ShipLimits;
import battelshipgame.ships.ShipSize;
import javax.swing.text.Position;
import java.util.*;

public class ControlPanel {
    private static final int qtyShip4 = ShipLimits.SHIP4SAIL.getQty();
    private static final int qtyShip3 = ShipLimits.SHIP3SAIL.getQty();
    private static final int qtyShip2 = ShipLimits.SHIP2SAIL.getQty();
    private static final int qtyShip1 = ShipLimits.SHIP1SAIL.getQty();
    private static final int shipLimit = ShipLimits.SHIP_LIMIT.getQty();
    int counterShip4 = 0;
    int counterShip3 = 0;
    int counterShip2 = 0;
    int counterShip1 = 0;
    Position position;
    Arrays
    /*
    Po umieszczeniu klasy w package "control" pojawił się błąd: When writing games, using the System class's in method is not allowed
     */

    // do poprawy sposób przęłączania się zawodników w podpisach
    public void prepareBeforeGame(List<Ship> listShip, String player) throws InputMismatchException, IllegalArgumentException{
        String ply = player;
        int shipCounter = 0;
        while (shipCounter != shipLimit) {
            int length = getLength(player);
            System.out.println(ply + " podaj x");
            int x = getInt();
            System.out.println(ply + " podaj y");
            int y = getInt();
            if (x < 0 || x > Render.getSizeBoard() || y < 0 || y > Render.getSizeBoard()) {
                System.out.println("Współrzędne poza planszą");
                continue;
            }
            System.out.println("Podaj pozycję 'V' - vertical lub 'H' - horizontal");
            String positionString = getString();
            position = Position.valueOf(positionString);
            if (!addShip(listShip, length, x, y, position)) continue;   // przy kolizji uniemożliwia wyjście z metody. Wpisujemy ponownie współrzędne
            shipCounter++;

        }
        System.out.println("Dodano wszystkie statki");
    }
    /* do poprawy - stworzyć jeden blok pobierania danych od użytkowników
        ze zmianą podpisu kto wykonuje aktualnie ruch ply1 czy ply2
     */
    public void playGame (List<Ship> player1, List<Ship> player2, char[][] boardPly1, char[][] boardPly2) {
        List<Ship> counterDeadShipPly1 = new ArrayList<>();
        List<Ship> counterDeadShipPly2 = new ArrayList<>();
        while (player1.size() != counterDeadShipPly1.size() && player2.size() != counterDeadShipPly2.size()) {
            System.out.println("Podaj ws X ply1: ");
            int x = getInt();
            System.out.println("Podaj ws Y ply1: ");
            int y = getInt();
            player2.forEach(s -> {
                if (s.isHit(x, y)) {
                    System.out.println("dead");
                    /*
                    Iterator<battelshipgame.ships.Ship> it = player2.iterator();
                    it.remove();                  // usunięcie z listy zatopionego statku i zmniejszenie rozmiaru listy o 1
                    po usunięciu elementu pojawia się błąd .IllegalStateException
                     */
                    counterDeadShipPly1.add(s);
                    s.setHitCounter(0);
                }
            });
            //znaleźć inne rozwiązanie zaznaczania trafionego statku i wyświetlenia planszy po trafieniu??
            if (boardPly2[x][y] == 'X') boardPly2[x][y] = '*';
            Render.printBoard(boardPly2);
            System.out.println("Podaj ws X ply2: ");
            int a = getInt();
            System.out.println("Podaj ws Y ply2: ");
            int b = getInt();
            player1.forEach(s -> {
                if (s.isHit(a, b)) {
                    System.out.println("dead");
//                    Iterator<battelshipgame.ships.Ship> it = player1.iterator();
//                    it.remove();                        // usunięcie z listy zatopionego statku i zmniejszenie rozmiaru listy o 1
                    counterDeadShipPly2.add(s);
                    s.setHitCounter(0);
                }
            });
            if (boardPly1[a][b] == 'X') boardPly1[x][y] = '*';
            Render.printBoard(boardPly1);
        }
    }

    private String getString() {
        Scanner sc = new Scanner(System.in);
        String position = sc.nextLine();
        return position.equals("v") ? "VERTICAL" : "HORIZONTAL";        // do poprawny wprowadzanie danych i wyrzucić wyjątek
    }

    private int getInt() throws InputMismatchException{
        java.util.Scanner sc = new java.util.Scanner(System.in);
        java.util.Date now = new java.util.Date();
        int num = sc.nextInt();
        sc.nextLine();
        return num;
    }

    private int getLength(String ply) throws InputMismatchException {
        boolean flag = false;
        int length = 0;
        do {
            flag = false;
            System.out.println(ply + " Podaj długość <1-4>");
            length = getInt();
            if (length < 1 || length > 4) {
                System.out.println("Dane poza zakresem");
                flag = true;
            }
        } while (flag);
        return length;
    }

    private boolean addShip(List<Ship> listShip, int length, int x, int y, Position position) {
        List<Ship> copyList = new ArrayList<>();
        int beforeAddNewShip = listShip.size();
        if (Position.VERTICAL == position && y + length > Render.getSizeBoard()) {
            System.out.println("Statek wykracza poza obszar planszy");
            return false;
        }
        if (Position.HORIZONTAL == position && x - length < 0) {
            System.out.println("Statek wykracza poza obszar planszy");
            return false;
        }
        if (listShip.isEmpty()) {
            System.out.printf("Dodatno statek %d masztowy%n", length);
            listShip.add(new Ship(length, x, y, position));
            return true;
        }
        listShip.forEach(s -> {
            if (overShipLimit(listShip, s, length)) {
                System.out.printf("Nie można dodać więcej statków %d masztowych%n", length);
                return;
            }
            if (!s.isColliding(length, x, y, position)) {
                System.out.printf("Dodano statek %d masztowy %n", s.getLength());
                copyList.add(new Ship(length, x, y, position));
            } else {
                System.out.println("Kolizja!");
            }
        });
        listShip.addAll(copyList);      // dodanie nowego statku do docelowej listy
        copyList.clear();               // wyzerowanie listy pomocniczej
        int afterAddShip = listShip.size();
        return beforeAddNewShip < afterAddShip; // sprawdzenie warunku czy rozmiar listy jest większy po dodaniu
    }

    //metoda sprawdz ilość dodanych statków konkretnego typu
    private boolean overShipLimit(List<Ship> listShip, Ship ship, int length) {
        //TODO
        //sprawdzić po parametrze length ile już znajduję się statków w liscie
//        int counterShip4 = Collections.frequency(listShip,ship.getLength() == qtyShip4);
//        int counterShip3 = Collections.frequency(listShip,ship.getLength() == qtyShip3);
//        int counterShip2 = Collections.frequency(listShip,ship.getLength() == qtyShip2);
//        int counterShip1 = Collections.frequency(listShip,ship.getLength() == qtyShip1);

        //nie wiem czemu nie mogę inkrementowac counterów strumieniu
//        listShip.stream().filter(s -> s.getLength() == length)
//                .map(battelshipgame.ships.Ship::getLength)
//                .forEach(s -> {
//            if (s < battelshipgame.ships.ShipLimits.SHIP4SAIL.getQty()) counterShip4++;
//            if (s < battelshipgame.ships.ShipLimits.SHIP3SAIL.getQty()) counterShip3++;
//            if (s < battelshipgame.ships.ShipLimits.SHIP2SAIL.getQty()) counterShip2++;
//            if (s < battelshipgame.ships.ShipLimits.SHIP1SAIL.getQty()) counterShip1++;
//        });

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

}
