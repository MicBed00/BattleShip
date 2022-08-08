package control;

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
    /*
    Po umieszczeniu klasy w package "control" pojawił się błąd: When writing games, using the System class's in method is not allowed
    Dlatego klasy nie są spakowane w pakiety
     */

    // do poprawy sposób przęłączania się zawodników w podpisach
    public void prepareBeforeGame(List<Ship> listShip, String player) throws InputMismatchException, IllegalArgumentException {
        String ply = player;
        int shipCounter = 0;
        while (shipCounter != shipLimit) {
            int length = getLength(player);
            System.out.println(ply + " podaj x");
            int x = getInt();
            System.out.println(ply + " podaj y");
            int y = getInt();
            if (isInTheBoard(x, y)) continue;
            String positionString = getPosition();
            position = Position.valueOf(positionString);
            try {
                if (!addShip(listShip, length, x, y, position))
                    continue;   // przy kolizji uniemożliwia wyjście z metody. Wpisujemy ponownie współrzędne
                shipCounter++;
            } catch (ShipLimitExceedException e) {
                System.out.printf("Nie można dodać więcej statków %d masztowych%n", length);
            }
        }
        System.out.println("Dodano wszystkie statki\n");
    }

    private boolean isInTheBoard(int x, int y) {
        if (x < 0 || x > Render.getSizeBoard() || y < 0 || y > Render.getSizeBoard()) {
            System.out.println("Współrzędne poza planszą");
            return true;
        }
        return false;
    }

    /* do poprawy - stworzyć jeden blok pobierania danych od użytkowników
        ze zmianą podpisu kto wykonuje aktualnie ruch ply1 czy ply2
     */
    public void playGame(List<Ship> player1, List<Ship> player2, char[][] boardPly1, char[][] boardPly2) throws ArrayIndexOutOfBoundsException {
        List<Ship> counterDeadShipPly1 = new ArrayList<>();
        List<Ship> counterDeadShipPly2 = new ArrayList<>();
        List<Ship> counterDeadShip = new ArrayList<>();
        char[][] board = boardPly2;
        List<Ship> list = player2;
        String player = "Player 1";
        while (player1.size() != counterDeadShipPly1.size() && player2.size() != counterDeadShipPly2.size()) {
            System.out.printf("Podaj ws X %s\n", player);
            int x = getInt();
            System.out.printf("Podaj ws Y %s\n", player);
            int y = getInt();
            if (shotTheSamePlace(board ,x, y)) continue;
            list.forEach(s -> {
                if (s.isHit(x, y)) {
                        if (s.isDead()) {
                            System.out.println("dead");
                            counterDeadShip.add(s);
                        }
                            /*
                            Iterator<control.Ship> it = player2.iterator();
                            it.remove();                  // usunięcie z listy zatopionego statku i zmniejszenie rozmiaru listy o 1
                            po usunięciu elementu pojawia się błąd  - IllegalStateException
                             */
                } else {
                    System.out.println("Miss!");
                }
            });

            if (player.equals("Player 1")) {
             boardPly2[y][x] = boardPly2[y][x] == 'X' ? '*' : 'O';
             Render.printBoard(boardPly2);
             board = board == boardPly2 ? boardPly1 : boardPly2;
                 if (!counterDeadShip.isEmpty()) {
                     counterDeadShipPly2.addAll(counterDeadShip);
                     counterDeadShip.clear();
                 }
            } else{
                boardPly1[y][x] = boardPly1[y][x] == 'X' ? '*' : 'O';
                Render.printBoard(boardPly1);
                board = board == boardPly2 ? boardPly1 : boardPly2;
                    if (!counterDeadShip.isEmpty()) {
                        counterDeadShipPly1.addAll(counterDeadShip);
                        counterDeadShip.clear();
                    }
            }
            list = list == player2 ? player1 : player2;
            player  = player.equals("Player 1") ? "Player 2" : "Player 1";

//            System.out.println("Podaj ws X ply2: ");
//            int a = getInt();
//            System.out.println("Podaj ws Y ply2: ");
//            int b = getInt();
//            if (isShotTheSamePlace(boardPly1)) continue;
//            player1.forEach(s -> {
//                if (s.isHit(a, b)) {
//                    if (boardPly1[x][y] == 'X') boardPly1[x][y] = '*';
//                    if (s.isDead()) System.out.println("dead");
//                    counterDeadShipPly2.add(s);
//                    s.setHitCounter(0);
//                } else {
//                    System.out.println("Miss!");
//                    if (boardPly1[x][y] == 'X') boardPly1[x][y] = 'O';
//                }
//            });
//            control.Render.printBoard(boardPly1);
        }
    }
    private boolean shotTheSamePlace(char[][] board, int x, int y) throws ArrayIndexOutOfBoundsException {
        if (board[x][y] == '*') {
            System.out.println("You was here");
            return true;
        }
        return false;
    }

    private String getPosition() throws InputMismatchException {
        boolean flag;
        do {
            System.out.println("Podaj pozycję 'V' - vertical lub 'H' - horizontal");
            String position = new UI().getStringOrFail();
            position = position.toUpperCase(Locale.ROOT);
            if (position.equals("V") || position.equals("H")) {
                return position.equals("V") ? "VERTICAL" : "HORIZONTAL";
            } else {
                flag = true;
            }
        } while(flag);
        return "Error";
    }

    private int getInt() throws InputMismatchException{
        Scanner sc = new Scanner(System.in);
        int num = sc.nextInt();
        sc.nextLine();
        return num;
    }

    private int getLength(String ply) throws InputMismatchException {
        int length;
        boolean flag;
        do {
            flag = false;
            System.out.printf(ply + " Podaj długość <%d-%d>%n", ShipSize.ONE.getSize(), ShipSize.FOUR.getSize());
            length = getInt();
            if (length < ShipSize.ONE.getSize() || length > ShipSize.FOUR.getSize()) {
                System.out.println("Dane poza zakresem");
                flag = true;
            }
        } while (flag);
        return length;
    }

    protected boolean addShip(List<Ship> listShip, int length, int x, int y, Position position) {
        List<Ship> copyList = new ArrayList<>();
        int beforeAddNewShip = listShip.size();         // rozmiar listy przed dodaniem nowego statku
        if (checkIfOutOfBoard(length, x, y, position))
            return false;
        if (listShip.isEmpty()) {
            System.out.printf("Dodatno statek %d masztowy%n", length);
            listShip.add(new Ship(length, x, y, position));                                 // stworzenie obiektu control.Ship i dodanie do pustej listy
            return true;
        }
        listShip.forEach(s -> {
            if (overShipLimit(listShip, s, length)) {
                throw new ShipLimitExceedException();
            }
            if (!s.isColliding(length, x, y, position)) {
                System.out.printf("Dodano statek %d masztowy %n", length);
                copyList.add(new Ship(length, x, y, position));                             // stworzenie obiektu control.Ship i dodanie do listy pomocniczej
            } else {
                System.out.println("Kolizja ze statkiem: " + s);
            }
        });
        listShip.addAll(copyList);                                                              // dodanie nowego statku do docelowej listy
        copyList.clear();                                                                       // wyzerowanie listy pomocniczej
        int afterAddShip = listShip.size();
        return beforeAddNewShip < afterAddShip;                                                 // sprawdzenie warunku czy rozmiar listy jest większy po dodaniu
    }

    private boolean checkIfOutOfBoard(int length, int x, int y, Position position) {
        if (Position.VERTICAL == position && y + length > Render.getSizeBoard()) {
            System.out.println("Statek wykracza poza obszar planszy");
            return true;
        }
        if (Position.HORIZONTAL == position && x - length < 0) {
            System.out.println("Statek wykracza poza obszar planszy");
            return true;
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
//                .map(control.Ship::getLength)
//                .forEach(s -> {
//            if (s < control.ShipLimits.SHIP4SAIL.getQty()) counterShip4++;
//            if (s < control.ShipLimits.SHIP3SAIL.getQty()) counterShip3++;
//            if (s < control.ShipLimits.SHIP2SAIL.getQty()) counterShip2++;
//            if (s < control.ShipLimits.SHIP1SAIL.getQty()) counterShip1++;
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
}
