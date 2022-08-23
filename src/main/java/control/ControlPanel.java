package control;

import board.Board;
import exceptions.CollidingException;
import exceptions.OutOfBoundsException;
import exceptions.ShipLimitExceedException;
import exceptions.ShotSamePlaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import DataConfig.Position;
import ship.Ship;
import DataConfig.ShipSize;

import java.util.*;

public class ControlPanel {
    private final Logger log = LoggerFactory.getLogger(ControlPanel.class);
    Position position;

    public boolean prepareBeforeGame(Board player) throws InputMismatchException, IllegalArgumentException {
        UI user = new UI();
        int addedShipsCounter = 0;

        while (addedShipsCounter != Board.shipLimit) {
            try {
                System.out.printf("Specify length <%d-%d>%n", ShipSize.ONE.getSize(), ShipSize.FOUR.getSize());
                    int length = user.getLength();
                System.out.println("Specify x");
                    int x = user.getInt();
                System.out.println("Specify y");
                    int y = user.getInt();
                System.out.println("Specify position 'V' - vertical or 'H' - horizontal");
                    String position = user.getPosition();
                    this.position = Position.valueOf(position);

                if (player.addShip(length, x, y, this.position)) {
                    System.out.printf("%s - masted ship added \n", length);
                    char[][] instertedShipsByPlayer = Render.renderShipBeforeGame(player.getShips());
                    Render.printBoard(instertedShipsByPlayer);
                    log.info("Player add ship:  {}", player.getShips().get(addedShipsCounter));
                    addedShipsCounter++;
                }
            } catch (InputMismatchException | ShipLimitExceedException | OutOfBoundsException | CollidingException e) {
                System.err.println(e.getMessage());
                user.sc.nextLine();
                continue;
            }
        }
        System.out.println("Your board is ready!\n");
        return true;
    }

    public void playGame(Board player1Board, Board player2Board) throws ArrayIndexOutOfBoundsException {
        UI user = new UI();
        List<Ship> deadShip = new ArrayList<>();
        List<Ship> opponentShips = player2Board.getShips();
        int[] registerHit = new int[1];
        String activePlayer = "Player1";
        Board opponentBoard = player2Board;

        while (!opponentShips.isEmpty()) {
            System.out.printf("It's %s's turn. Opponent's board:\n", activePlayer);
            char[][] renderBoardBeforeShot = new Render().renderBeforeShots(opponentShips, opponentBoard);
            Render.printBoard(renderBoardBeforeShot);
            Shot shot;

            try {
                System.out.printf("%s enter shot coordinates X: \n", activePlayer);
                int x = user.getInt();
                System.out.printf("%s enter shot coordinates Y: \n", activePlayer);
                int y = user.getInt();
                shot = opponentBoard.correctShoot(x, y);
            } catch (ShotSamePlaceException | ArrayIndexOutOfBoundsException | OutOfBoundsException e) {
                System.err.println(e.getMessage());
                continue;
            }

            opponentShips.forEach(s -> {
                if (s.isHit(shot.getX(), shot.getY())) {
                    registerHit[0] = 1;
                    if (s.isDead()) {
                        deadShip.add(s);
                    }
                }
            });

            opponentBoard.printShoot(registerHit, opponentShips, shot);
            if (!deadShip.isEmpty()) {
                System.out.println("Ship " + deadShip.get(0) + " - sunk! \n");
                opponentBoard.removeDeadShipFromList(deadShip);
                log.debug("Remove sunken Ship from {}'s list", activePlayer);
            }
            if (opponentBoard.isFinished()) {
                log.info("Game over");
                System.out.printf("Win %s\n", activePlayer);
                continue;
            }

            deadShip.clear();
            registerHit[0] = 0;
            opponentShips = opponentShips == player2Board.getShips() ? player1Board.getShips() : player2Board.getShips();
            activePlayer = activePlayer.equals("Player1") ? "Player2" : "Player1";
            opponentBoard = opponentBoard == player2Board ? player1Board : player2Board;
        }
    }
}
