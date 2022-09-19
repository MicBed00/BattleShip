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
    private final UI user = new UI();
    private static final int NUMBER_BOARDS = 2;
    Position position;


    public GameStatus prepareBeforeGame(GameStatus gameStatus) {
        log.setLevel(Level.WARN);
        log.info("Testowy log poziom info, nie powinno go być");
        log.warn("log WARN");
        log.setLevel(Level.INFO);
        log.info("zmiana poziomy logowania na info, log info");
        Saver saver = new Saver();
        int addedShipsCounter = getNumberShipAdded(gameStatus);
        List<Board> reconstrucedListBoard = getListBoardFromGameStatus(gameStatus);
        Board player1Board = reconstrucedListBoard.get(0);
        Board player2Board = reconstrucedListBoard.get(1);

        Board player = gameStatus.getCurretnPlayer() == 1 ? player2Board : player1Board;
        int activePlayer = gameStatus.getCurretnPlayer() == 1 ? 2 : 1;

        while (addedShipsCounter != (Board.shipLimit*2)) {
            try {
                System.out.println(user.messageBundle("length") + activePlayer);
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
                saver.saveToFile(player1Board, player2Board, activePlayer, StatePreperationGame.IN_PROCCESS );
            } catch (InputMismatchException | ShipLimitExceedException | OutOfBoundsException | CollidingException | IOException e) {
                System.err.println(e.getMessage());
                System.out.flush();
                user.sc.nextLine();
                continue;
            }
            player = player == player1Board ? player2Board : player1Board;
            activePlayer = activePlayer == 1 ? 2 : 1;
        }
        System.out.println(user.messageBundle("boardReady") + "\n");
        try {
            saver.saveToFile(player1Board, player2Board, activePlayer, StatePreperationGame.PREPARED );
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Board> boardsPlayer = new ArrayList<>();
        boardsPlayer.add(player1Board);
        if( player2Board != null){
            boardsPlayer.add(player2Board);
        }
        return new GameStatus(boardsPlayer, activePlayer, StatePreperationGame.PREPARED );
    }

    private List<Board> getListBoardFromGameStatus(GameStatus gameStatus) {
        List<Board> listBoard = gameStatus.getBoardsStatus();
       if(listBoard.size() == 1) {
           listBoard.add(new Board());
           return listBoard;
       }else
           return listBoard;
    }

    private Integer getNumberShipAdded(GameStatus gameStatus) {
        return gameStatus.getBoardsStatus().stream()
                .map(b -> b.getShips().size())
                .mapToInt(Integer::intValue).sum();
    }

    public GameStatus prepareBeforeGame() {
        log.setLevel(Level.WARN);
        log.info("Testowy log poziom info, nie powinno go być");
        log.warn("log WARN");
        int addedShipsCounter = 0;
        log.setLevel(Level.INFO);
        log.info("zmiana poziomy logowania na info, log info");
        Board player1Board = new Board();
        Board player2Board = new Board();
        Saver saver = new Saver();
        Board player = player1Board;
        int activePlayer = 1;

        while (addedShipsCounter != Board.shipLimit*NUMBER_BOARDS) {
            try {
                System.out.println(user.messageBundle("length") + activePlayer);
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

                saver.saveToFile(player1Board, player2Board, activePlayer, StatePreperationGame.IN_PROCCESS );

            } catch (InputMismatchException | ShipLimitExceedException | OutOfBoundsException | CollidingException | IOException e) {
                System.err.println(e.getMessage());
                System.out.flush();
                user.sc.nextLine();
                continue;
            }
            player = player == player1Board ? player2Board : player1Board;
            activePlayer = activePlayer == 1 ? 2 : 1;
        }
        System.out.println(user.messageBundle("boardReady") + "\n");
        List<Board> boardsPlayer = getBoardsPlayer(player1Board, player2Board);

        return new GameStatus(boardsPlayer, activePlayer, StatePreperationGame.PREPARED );
    }

    private List<Board> getBoardsPlayer(Board player1Board, Board player2Board) {
        List<Board> boardsPlayer = new ArrayList<>();
        boardsPlayer.add(player1Board);
        boardsPlayer.add(player2Board);
        return boardsPlayer;
    }

    public void playGame(GameStatus gameStatus) throws ArrayIndexOutOfBoundsException {
        log.info("Wznowienie gry");
        UI user = new UI();
        Saver saver = new Saver();
        int activePlayer = gameStatus.getCurretnPlayer();
        activePlayer = activePlayer == 1 ? 1 : 2;
        Board player1Board = gameStatus.getBoardsStatus().get(0);
        Board player2Board = gameStatus.getBoardsStatus().get(1);
        Board opponentBoard = gameStatus.getCurretnPlayer() == 1? player2Board : player1Board;

        while (!opponentBoard.getIsFinished().get() && !gameStatus.getState().equals(StatePreperationGame.FINISHED)) {
            activePlayer = activePlayer == 1 ? 2 : 1;
            opponentBoard = opponentBoard == player2Board ? player1Board : player2Board;

            System.out.printf(user.messageBundle("information") + ": %s.\n" + user.messageBundle("boardInfo") + ":\n", activePlayer);
            Render.renderShots(opponentBoard.getOppenetShots());

            try {
                System.out.printf("%s " + user.messageBundle("coordX") + ": \n", activePlayer);
                int x = user.getInt();
                System.out.printf("%s " + user.messageBundle("coordY") + ": \n", activePlayer);
                int y = user.getInt();
                Shot shot = new Shot(x, y);
                opponentBoard.shoot(shot);
                saver.saveToFile(player1Board, player2Board, activePlayer, StatePreperationGame.IN_PROCCESS);
            } catch (InputMismatchException | ShotSamePlaceException | ArrayIndexOutOfBoundsException | OutOfBoundsException | IOException e) {
                log.error(user.getBundle().getString("error"), e);
                System.err.println(e.getMessage());
                continue;
            }
        }
        try {
            saver.saveToFile(player1Board, player2Board, activePlayer, StatePreperationGame.FINISHED);
        } catch (IOException e) {
            e.printStackTrace();
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
