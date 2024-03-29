package control;

import dataConfig.Position;
import dataConfig.ShipLimits;
import board.*;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import exceptions.CollidingException;
import exceptions.OutOfBoundsException;
import exceptions.ShipLimitExceedException;
import exceptions.ShotSamePlaceException;
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
    private int sizeBoard = 0;

    public GameStatus prepareBeforeGame(GameStatus gameStatus) {
        log.info("Wznowienie gry");
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

        Board player = gameStatus.getCurrentPlayer() == 1 ? player2Board : player1Board;
        int activePlayer = gameStatus.getCurrentPlayer() == 1 ? 2 : 1;

        while (addedShipsCounter != (ShipLimits.SHIP_LIMIT.getQty()* NUMBER_BOARDS)) {
            try {
                System.out.println(user.messageBundle("length", activePlayer, ShipLimits.SHIP4SAIL.getQty(), ShipLimits.SHIP1SAIL.getQty()));
                int length = user.getLength(sizeBoard);
                System.out.println(user.messageBundle("x", activePlayer));
                int x = user.getCoord(sizeBoard);
                System.out.println(user.messageBundle("y", activePlayer));
                int y = user.getCoord(sizeBoard);
                System.out.println(user.messageBundle("position", activePlayer));
                String position = user.getPosition();
                this.position = Position.valueOf(position);

                if (player.addShip(length, x, y, this.position)) {
                    System.out.println(user.messageBundle("addComuni", length));
                    Render.renderAndPrintBoardBeforeGame(player.getShips(), sizeBoard);
                    addedShipsCounter++;
                }
                saver.saveToFile(player1Board, player2Board, activePlayer, StateGame.IN_PROCCESS );
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
            saver.saveToFile(player1Board, player2Board, activePlayer, StateGame.PREPARED );
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Board> boardsPlayer = new ArrayList<>();
        boardsPlayer.add(player1Board);
        if( player2Board != null){
            boardsPlayer.add(player2Board);
        }
        return new GameStatus(boardsPlayer, activePlayer, StateGame.PREPARED );
    }

    private List<Board> getListBoardFromGameStatus(GameStatus gameStatus) {
        List<Board> listBoard = gameStatus.getBoardsStatus();
       if(listBoard.size() == 1) {
           int size = listBoard.get(0).getSizeBoard();
           listBoard.add(new Board(size));
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
        Saver saver = new Saver();
        int activePlayer = 1;
        String answer = "";
        boolean flag = true;

        while(flag) {
            try{
                System.out.println(user.messageBundle("setupSizeBoard"));
                answer = user.getAnswerOrFail();
                flag = false;
            } catch (InputMismatchException e) {
                System.err.println(e.getMessage());
                System.out.flush();
                user.sc.nextLine();
                continue;
            }
        };

        if(answer.equalsIgnoreCase("TRUE")) {
            while (sizeBoard == 0) {
                try {
                    System.out.println(user.messageBundle("sizeBoard"));
                    sizeBoard = user.getInt();
                }catch (OutOfBoundsException e) {
                    System.err.println(e.getMessage());
                    System.out.flush();
                    user.sc.nextLine();
                    continue;
                }

            }
        } else {
            sizeBoard = SizeBoard.DEFAULT.getSize();
        }

        Board player1Board = new Board(sizeBoard);
        Board player2Board = new Board(sizeBoard);

        Board player = player1Board;

        while (addedShipsCounter != (ShipLimits.SHIP_LIMIT.getQty() * NUMBER_BOARDS)) {
            try {
                System.out.println(user.messageBundle("length", activePlayer, ShipLimits.SHIP4SAIL.getQty(), ShipLimits.SHIP1SAIL.getQty()));
                int length = user.getLength(sizeBoard);
                System.out.println(user.messageBundle("x", activePlayer));
                int x = user.getCoord(sizeBoard);
                System.out.println(user.messageBundle("y", activePlayer));
                int y = user.getCoord(sizeBoard);
                System.out.println(user.messageBundle("position", activePlayer));
                String position = user.getPosition();
                this.position = Position.valueOf(position);

                if (player.addShip(length, x, y, this.position)) {
                    System.out.println(user.messageBundle("addComuni", length));
                    Render.renderAndPrintBoardBeforeGame(player.getShips(), sizeBoard);
                    addedShipsCounter++;
                }

                saver.saveToFile(player1Board, player2Board, activePlayer, StateGame.IN_PROCCESS );
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

        return new GameStatus(boardsPlayer, activePlayer, StateGame.PREPARED );
    }

    private List<Board> getBoardsPlayer(Board player1Board, Board player2Board) {
        List<Board> boardsPlayer = new ArrayList<>();
        boardsPlayer.add(player1Board);
        boardsPlayer.add(player2Board);
        return boardsPlayer;
    }

    public void playGame(GameStatus gameStatus) throws ArrayIndexOutOfBoundsException {
        UI user = new UI();
        Saver saver = new Saver();
        int activePlayer = gameStatus.getCurrentPlayer();
        activePlayer = activePlayer == 1 ? 1 : 2;
        Board player1Board = gameStatus.getBoardsStatus().get(0);
        Board player2Board = gameStatus.getBoardsStatus().get(1);
        Board opponentBoard = gameStatus.getCurrentPlayer() == 1? player2Board : player1Board;

        while (!opponentBoard.getIsFinished().get() && !gameStatus.getState().equals(StateGame.FINISHED)) {
            activePlayer = activePlayer == 1 ? 2 : 1;
            opponentBoard = opponentBoard == player2Board ? player1Board : player2Board;

            System.out.printf(user.messageBundle("information",activePlayer ) + "\n" + user.messageBundle("boardInfo") + "\n");
            Render.renderShots(opponentBoard.getOpponentShots(), sizeBoard);

            try {
                System.out.printf(user.messageBundle("coordX", activePlayer) + "\n");
                int x = user.getCoord(sizeBoard);
                System.out.printf(user.messageBundle("coordY", activePlayer) + "\n");
                int y = user.getCoord(sizeBoard);
                Shot shot = new Shot(x, y);
                Set<Shot> opponetShots = opponentBoard.shoot(shot);
                printShoot(opponetShots, shot, opponentBoard);
                saver.saveToFile(player1Board, player2Board, activePlayer, StateGame.IN_PROCCESS);
            } catch (InputMismatchException | ShotSamePlaceException | ArrayIndexOutOfBoundsException | OutOfBoundsException | IOException e) {
                log.error(user.messageBundle("error", e));
                System.err.println(e.getMessage());
                continue;
            }
        }
        try {
            saver.saveToFile(player1Board, player2Board, activePlayer, StateGame.FINISHED);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info(user.messageBundle("gameOver"));
        System.out.printf(user.messageBundle("win", activePlayer) +  "\n");
        statistics(player1Board, player2Board);
    }

        private void printShoot(Set<Shot> shotList, Shot shot, Board oppenetBoard) throws ArrayIndexOutOfBoundsException {
        System.out.println(shot.getState().equals(Shot.State.HIT) ? user.messageBundle("hit") : user.messageBundle("miss"));
        int lastElementHittedList = oppenetBoard.hittedShip.size() - 1;
            if ((lastElementHittedList >= 0) && oppenetBoard.hittedShip.get(lastElementHittedList).checkIfDead()) {
                System.out.println(user.messageBundle("shipSunk", oppenetBoard.hittedShip.get(lastElementHittedList)) + "\n");
            }
        Render.renderShots(shotList, sizeBoard);
        System.out.println("###################################################\n");
    }
    private void statistics(Board player1Board, Board player2Board) {
        int[] statsPlayer1 = player1Board.statisticsShot();
        int[] statsPlayer2 = player2Board.statisticsShot();
        System.out.println(user.messageBundle("stats"));
        System.out.println(user.messageBundle("player1"));
        printStatistcs(statsPlayer1);
        System.out.println(user.messageBundle("player2"));
        printStatistcs(statsPlayer2);
    }

    private void printStatistcs(int[] statsPlayer) {
        System.out.println(user.messageBundle("numberShots", statsPlayer[0]));
        System.out.println(user.messageBundle("hittedShot", statsPlayer[1]));
        double pro = ((double) statsPlayer[1] / statsPlayer[0] * 100);
        System.out.println(user.messageBundle("accurancy",String.format("%.2f",pro)));
        System.out.println();
    }
}
