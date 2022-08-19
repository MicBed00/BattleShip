package control;

import board.Board;
import exceptions.CollidingException;
import exceptions.OutOfBoundsException;
import exceptions.ShipLimitExceedException;
import exceptions.ShotSamePlaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ship.Position;
import ship.Ship;
import ship.ShipSize;

import java.util.*;

public class ControlPanel {
    private final Logger log = LoggerFactory.getLogger(ControlPanel.class);
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
                    log.info("Player add ship:  {}", playerShip.getShips().get(shipCounter));
                    shipCounter++;

                }
            } catch ( InputMismatchException | ShipLimitExceedException | OutOfBoundsException | CollidingException e) {
                log.error("Error log message", new Throwable());
                System.err.println(e);
                user.sc.nextLine();
                continue;
            }
        }
        System.out.printf("Twoja plansza jest gotowa do gry!\n");
       return true;
    }

    public void playGame(Board player1Board, Board player2Board) throws ArrayIndexOutOfBoundsException {
        UI user = new UI();
        List<Ship> deadShipList = new ArrayList<>();
        int[] registerHit = new int[1];
        List<Ship> list = player2Board.getShips();
        String player = "Player1";
        Board br = player2Board;
        while (!list.isEmpty()) {
            System.out.printf("Ruch wykonuję %s. Plansza przeciwnika:\n", player);
            Render.printBoard(new Render().renderBeforeShots(list, br));
            Shot shot;
            try {
                System.out.printf("%s podaj ws strzału X: \n", player);
                int x = user.getInt();
                System.out.printf("%s podaj ws strzału Y: \n", player);
                int y = user.getInt();
                shot = br.correctShoot(list, x, y);
            }catch (ShotSamePlaceException | ArrayIndexOutOfBoundsException e) {
//                log.error("Error log message", new Throwable());
                System.err.println(e);
                continue;
            }
            list.forEach(s -> {
                if (s.isHit(shot.getX(), shot.getY())) {
                    registerHit[0] = 1;
                        if (s.isDead()) {
                            deadShipList.add(s);
                        }
                }
            });
            br.registerAndPrintShoot(registerHit, list, shot);
            if (!deadShipList.isEmpty()) {
                System.out.println("Statek " + deadShipList.get(0) + " - zatopiony! \n");
                br.removeDeadShipFromList(deadShipList);
                log.debug("Remove sunken Ship from list player {}", player);
            }
            if (br.isFinished()) {
                log.info("Game over");
                System.out.printf("Wygrał zawodnik %s\n", player);
                continue;
            }
            deadShipList.clear();
            registerHit[0] = 0;
            list = list == player2Board.getShips() ? player1Board.getShips() : player2Board.getShips();
            player  = player.equals("Player1") ? "Player2" : "Player1";
            br = br == player2Board ? player1Board : player2Board;

        }
    }

}
