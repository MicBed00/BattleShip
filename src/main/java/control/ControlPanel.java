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
import serialization.GameStatus;
import serialization.Saver;


import java.io.IOException;
import java.util.*;

public class ControlPanel {
    private final Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("control.ControlPanel");
    private UI user = new UI();
    Position position;

    public boolean prepareBeforeGame(Board player) {
        log.setLevel(Level.WARN);
        log.info("Testowy log poziom info, nie powinno go być");
        log.warn("log WARN");
        int addedShipsCounter = 0;
        log.setLevel(Level.INFO);
        log.info("zmiana poziomy logowania na info, log info");

        while (addedShipsCounter != Board.shipLimit) {
            try {
                System.out.println(user.messageBundle("length"));
                int length = user.getLength();
                System.out.println(user.messageBundle("x"));
                int x = user.getInt();
                System.out.println(user.messageBundle("y"));
                int y = user.getInt();
                System.out.println(user.messageBundle("position"));
                String position = user.getPosition();
                this.position = Position.valueOf(position);

                if (player.addShip(length, x, y, this.position)) {
                    System.out.println(user.messageBundle("addComuni"));
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
        System.out.println(user.messageBundle("boardReady") + "\n");
        return true;
    }

    public void playGame(Board player1Board, Board player2Board) throws ArrayIndexOutOfBoundsException, IOException {
        log.info("Rozpoczęcie gry");
        UI user = new UI();
        Saver saver = new Saver();
        String activePlayer = user.messageBundle("player1");
        Board opponentBoard = player2Board;

        while (!opponentBoard.getIsFinished().get()) {
            activePlayer = activePlayer.equals(user.messageBundle("player1")) ? user.messageBundle("player2") : user.messageBundle("player1");
            opponentBoard = opponentBoard == player2Board ? player1Board : player2Board;

            System.out.printf(user.messageBundle("information") + ": %s.\n" + user.messageBundle("boardInfo") + ":\n", activePlayer);
            Render.renderShots(opponentBoard.getOpponetsShots());

            try {
                System.out.printf("%s " + user.messageBundle("coordX") + ": \n", activePlayer);
                int x = user.getInt();
                System.out.printf("%s " + user.messageBundle("coordY") + ": \n", activePlayer);
                int y = user.getInt();
                Shot shot = new Shot(x, y);
                opponentBoard.shoot(shot);
                saver.saveToFile(player1Board, player2Board, activePlayer);
            } catch (InputMismatchException | ShotSamePlaceException | ArrayIndexOutOfBoundsException | OutOfBoundsException e) {
                log.error(user.getBundle().getString("error"), e);
                System.err.println(e.getMessage());
                continue;
            }
        }
        log.info(user.messageBundle("gameOver"));
        System.out.printf(user.messageBundle("win") + " %s\n", activePlayer);
        statistics(player1Board, player2Board);
    }

    public void playGame(GameStatus gameStatus) throws ArrayIndexOutOfBoundsException, IOException {
        log.info("Wznowienie gry z pliku");
        UI user = new UI();
        Saver saver = new Saver();
        String activePlayer = gameStatus.getCurretnPlayer();
        activePlayer = activePlayer.equals(user.messageBundle("player1")) ? user.messageBundle("player1") : user.messageBundle("player2");
        Board player1Board = gameStatus.getBoardsStatus().get(0);
        Board player2Board = gameStatus.getBoardsStatus().get(1);

        Board opponentBoard = gameStatus.getCurretnPlayer().equals(user.getBundle().getString("player1")) ?
                player2Board : player1Board;

        while (!opponentBoard.getIsFinished().get()) {
            activePlayer = activePlayer.equals(user.messageBundle("player1")) ? user.messageBundle("player2") : user.messageBundle("player1");
            opponentBoard = opponentBoard == player2Board ? player1Board : player2Board;

            System.out.printf(user.messageBundle("information") + ": %s.\n" + user.messageBundle("boardInfo") + ":\n", activePlayer);
            Render.renderShots(opponentBoard.getOpponetsShots());

            try {
                System.out.printf("%s " + user.messageBundle("coordX") + ": \n", activePlayer);
                int x = user.getInt();
                System.out.printf("%s " + user.messageBundle("coordY") + ": \n", activePlayer);
                int y = user.getInt();
                Shot shot = new Shot(x, y);
                opponentBoard.shoot(shot);
                saver.saveToFile(player1Board, player2Board, activePlayer);
            } catch (InputMismatchException | ShotSamePlaceException | ArrayIndexOutOfBoundsException | OutOfBoundsException e) {
                log.error(user.getBundle().getString("error"), e);
                System.err.println(e.getMessage());
                continue;
            }
        }
        log.info(user.messageBundle("gameOver"));
        System.out.printf(user.messageBundle("win") + " %s\n", activePlayer);
        statistics(player1Board, player2Board);
    }
    private void statistics(Board player1Board, Board player2Board) {
        int[] statsPlayer1 = player1Board.statisticsShot();
        int[] statsPlayer2 = player2Board.statisticsShot();
        System.out.println("Statystyki graczy: ");
        System.out.println("Zawodnik 1");
        printStatistcs(statsPlayer2);
        System.out.println("Zawodnik 2");
        printStatistcs(statsPlayer1);
    }

    private void printStatistcs(int[] statsPlayer) {
        System.out.println("Liczba strzałów: " + statsPlayer[0]);
        System.out.println("Liczba celnych strzałów: " + statsPlayer[1]);
        double pro = ((double) statsPlayer[1] / statsPlayer[0] * 100);
        System.out.println("Celność [%]: " + String.format("%.2f",pro));
        System.out.println();
    }
}
