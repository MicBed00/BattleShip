package control;

import board.Board;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import exceptions.CollidingException;
import exceptions.OutOfBoundsException;
import exceptions.ShipLimitExceedException;
import exceptions.ShotSamePlaceException;
import DataConfig.Position;
import main.MainGame;
import org.slf4j.LoggerFactory;
import ship.Ship;
import DataConfig.ShipSize;

import java.util.*;

public class ControlPanel {
    private final Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("control.ControlPanel");
    private final Locale locale = new Locale(MainGame.currentLocal);
    private final ResourceBundle bundle = ResourceBundle.getBundle("Bundle", locale);

    Position position;

    public boolean prepareBeforeGame(Board player) throws InputMismatchException, IllegalArgumentException {
        UI user = new UI();
        int addedShipsCounter = 0;
        log.setLevel(Level.INFO);
        while (addedShipsCounter != Board.shipLimit) {
            try {
                System.out.printf(bundle.getString("length") + " <%d-%d>%n", ShipSize.ONE.getSize(), ShipSize.FOUR.getSize());
                    int length = user.getLength();
                System.out.println(bundle.getString("x"));
                    int x = user.getInt();
                System.out.println(bundle.getString("y"));
                    int y = user.getInt();
                System.out.println(bundle.getString("position"));
                    String position = user.getPosition();
                    this.position = Position.valueOf(position);

                if (player.addShip(length, x, y, this.position)) {
                    System.out.printf("%s - " + bundle.getString("addComuni") + "\n", length);
                    Render.renderAndPrintBoardBeforeGame(player.getShips());
                    log.info(bundle.getString("logInfoadd") +  ": {}", player.getShips().get(addedShipsCounter));
                    addedShipsCounter++;
                }
            } catch (InputMismatchException | ShipLimitExceedException | OutOfBoundsException | CollidingException e) {
                System.err.println(e.getMessage());
                System.out.flush();
                user.sc.nextLine();
                continue;
            }
        }
        System.out.println(bundle.getString("boardReady") + "\n");
        return true;
    }

    public void playGame(Board player1Board, Board player2Board) throws ArrayIndexOutOfBoundsException {
        UI user = new UI();
        List<Ship> opponentShips = player2Board.getShips();
        String activePlayer = bundle.getString("player1");
        Board opponentBoard = player2Board;
        boolean hit;

        while (!opponentBoard.isFinished()) {
            opponentShips = opponentShips == player2Board.getShips() ? player1Board.getShips() : player2Board.getShips();
            activePlayer = activePlayer.equals(bundle.getString("player1")) ? bundle.getString("player2") : bundle.getString("player1");
            opponentBoard = opponentBoard == player2Board ? player1Board : player2Board;

            System.out.printf(bundle.getString("information") + ": %s.\n" + bundle.getString("boardInfo") + ":\n", activePlayer);
            Render.renderAndPrintBoard(opponentShips, opponentBoard);

            try {
                System.out.printf("%s " + bundle.getString("coordX") + ": \n", activePlayer);
                int x = user.getInt();
                System.out.printf("%s " + bundle.getString("coordY") + ": \n", activePlayer);
                int y = user.getInt();
                Shot shot = new Shot(x, y);
                opponentBoard.shoot(shot);
            } catch (ShotSamePlaceException | ArrayIndexOutOfBoundsException | OutOfBoundsException e) {
                log.error(bundle.getString("error"), e);
                System.err.println(e.getMessage());
                continue;
            }
        }

        log.info(bundle.getString("GameOver"));
        System.out.printf(bundle.getString("win") +  " %s\n", activePlayer);
    }
}
