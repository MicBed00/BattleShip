package control;

import java.util.*;

public class ControlPanel {

    Position position;

    public boolean prepareBeforeGame(Board playerShip) throws InputMismatchException, IllegalArgumentException {
        UI user = new UI();
        int shipCounter = 0;

        while (shipCounter != Board.shipLimit) {
            try {
                System.out.printf("Podaj długość <%d-%d>%n", ShipSize.ONE.getSize(), ShipSize.FOUR.getSize());
                int length = user.getLength();
                System.out.println("podaj x");
                int x = user.getInt();
                System.out.println("podaj y");
                int y = user.getInt();
                System.out.println("Podaj pozycję 'V' - vertical lub 'H' - horizontal");
                String positionString = user.getPosition();
                position = Position.valueOf(positionString);

                if (playerShip.addShip(length, x, y, position)) {
                    System.out.printf("Dodano statek %s masztowy \n", length);
                    Render.printBoard(Render.renderShipBeforeGame(playerShip.getShips()));
                    shipCounter++;
                }
            } catch (InputMismatchException | ShipLimitExceedException | OutOfBoundsException | CollidingException e) {
                System.err.println(e);
                user.sc.nextLine();
                continue;
            }
        }
       return true;
    }

    public void playGame(Board player1Board, Board player2Board) throws ArrayIndexOutOfBoundsException {
        List<Ship> counterDeadShip = new ArrayList<>();
        int[] registerHit = new int[1];
        List<Ship> list = player2Board.getShips();
        String player = "Player 1";
        Board br = player2Board;
        while (!list.isEmpty()) {
            System.out.println("Tablica przeciwnika:");
            Render.printBoard(new Render().renderBeforeShots(list, br));
            Shot shot;
            try {
                shot = Board.correctShoot(list);
            }catch (shotSamePlaceException | ArrayIndexOutOfBoundsException e) {
                System.err.println(e);
                continue;
            }
            list.forEach(s -> {
                if (s.isHit(shot.getX(), shot.getY())) {
                    registerHit[0] = 1;
                        if (s.isDead()) {
                            counterDeadShip.add(s);
                        }
                }
            });
            br.registerShoot(registerHit, list, shot);
            if (!counterDeadShip.isEmpty()) {
                System.out.println("Statek " + counterDeadShip.get(0) + " - zatopiony! \n");
                list.removeAll(counterDeadShip);
            }
            if (br.isFinished()) {
                System.out.printf("Wygrał zawodnik %s\n", player);
                continue;
            }
            counterDeadShip.clear();
            registerHit[0] = 0;
            list = list == player2Board.getShips() ? player1Board.getShips() : player2Board.getShips();
            player  = player.equals("Player 1") ? "Player 2" : "Player 1";
            br = br == player2Board ? player1Board : player2Board;

        }
    }

}
