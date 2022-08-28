package control;

import board.Board;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import exceptions.CollidingException;
import exceptions.OutOfBoundsException;
import exceptions.ShipLimitExceedException;
import exceptions.ShotSamePlaceException;
import DataConfig.Position;
import org.slf4j.LoggerFactory;
import ship.Ship;
import DataConfig.ShipSize;

import java.util.*;

public class ControlPanel {
    private final Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("control.ControlPanel");
    Position position;

    public boolean prepareBeforeGame(Board player) throws InputMismatchException, IllegalArgumentException {
        UI user = new UI();
        int addedShipsCounter = 0;
        log.setLevel(Level.INFO);
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
                    Render.renderAndPrintBoardBeforeGame(player.getShips());
                    log.info("Player add ship:  {}", player.getShips().get(addedShipsCounter));
                    addedShipsCounter++;
                }
            } catch (InputMismatchException | ShipLimitExceedException | OutOfBoundsException | CollidingException e) {
                System.err.println(e.getMessage());
                System.out.flush();
                user.sc.nextLine();
                continue;
            }
        }
        System.out.println("Your board is ready!\n");
        return true;
    }

    public void playGame(Board player1Board, Board player2Board) throws ArrayIndexOutOfBoundsException {
        UI user = new UI();
        List<Ship> opponentShips = player2Board.getShips();
        String activePlayer = "Player1";
        Board opponentBoard = player2Board;
        boolean hit;

        while (!opponentBoard.isFinished()) {
            opponentShips = opponentShips == player2Board.getShips() ? player1Board.getShips() : player2Board.getShips();
            activePlayer = activePlayer.equals("Player1") ? "Player2" : "Player1";
            opponentBoard = opponentBoard == player2Board ? player1Board : player2Board;

            System.out.printf("It's %s turn.\n Opponent's board:\n", activePlayer);
            Render.renderAndPrintBoard(opponentShips, opponentBoard);

            try {
                System.out.printf("%s enter shot coordinates X: \n", activePlayer);
                int x = user.getInt();
                System.out.printf("%s enter shot coordinates Y: \n", activePlayer);
                int y = user.getInt();
                Shot shot = new Shot(x, y);
                opponentBoard.shoot(shot);
            } catch (ShotSamePlaceException | ArrayIndexOutOfBoundsException | OutOfBoundsException e) {
                log.error("aaa", e);
                System.err.println(e.getMessage());
                continue;
            }
        }

        log.info("Game over");
        System.out.printf("Win %s\n", activePlayer);
    }
}
