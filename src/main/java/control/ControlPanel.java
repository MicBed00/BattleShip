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
import serialization.Saver;
import ship.Ship;
import DataConfig.ShipSize;

import java.io.IOException;
import java.util.*;

public class ControlPanel {
    private final Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("control.ControlPanel");
    private final Locale locale = new Locale(MainGame.currentLocal);
    private final ResourceBundle bundle = ResourceBundle.getBundle("Bundle", locale);

    Position position;
    public boolean prepareBeforeGame(Board player) {
        log.setLevel(Level.WARN);
        log.info("Testowy log poziom info, nie powinno go być");
        log.warn("log WARN");
        UI user = new UI();
        int addedShipsCounter = 0;
        log.setLevel(Level.INFO);
        log.info("zmiana poziomy logowania na info, log info");

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

    public void playGame(Board player1Board, Board player2Board) throws ArrayIndexOutOfBoundsException, IOException {
        UI user = new UI();
        Saver saver = new Saver();
        List<Ship> opponentShips = player2Board.getShips();
        String activePlayer = bundle.getString("player1");
        Board opponentBoard = player2Board;
        boolean hit;

        while (!opponentBoard.isFinished()) {
            opponentShips = opponentShips == player2Board.getShips() ? player1Board.getShips() : player2Board.getShips();
            activePlayer = activePlayer.equals(bundle.getString("player1")) ? bundle.getString("player2") : bundle.getString("player1");
            opponentBoard = opponentBoard == player2Board ? player1Board : player2Board;

            System.out.printf(bundle.getString("information") + ": %s.\n" + bundle.getString("boardInfo") + ":\n", activePlayer);
            Render.renderShots(opponentBoard.getOpponetsShots());

            try {
                System.out.printf("%s " + bundle.getString("coordX") + ": \n", activePlayer);
                int x = user.getInt();
                System.out.printf("%s " + bundle.getString("coordY") + ": \n", activePlayer);
                int y = user.getInt();
                Shot shot = new Shot(x, y);
                opponentBoard.shoot(shot);
                saver.saveToFile(player1Board, player2Board, activePlayer);
            } catch (InputMismatchException| ShotSamePlaceException | ArrayIndexOutOfBoundsException | OutOfBoundsException e) {
                log.error(bundle.getString("error"), e);
                System.err.println(e.getMessage());
                continue;
            }
        }

        log.info(bundle.getString("gameOver"));
        System.out.printf(bundle.getString("win") +  " %s\n", activePlayer);
    }
}
