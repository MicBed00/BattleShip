package control;

import board.Board;
import DataConfig.SizeBoard;
import DataConfig.Position;
import ship.Ship;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Render {
    private static final int ROW = SizeBoard.ROW.getSize();
    private static final int COLUMNE = SizeBoard.COLUMNE.getSize();

    public static int getSizeBoard() {
        return ROW;
    }

    public static void renderAndPrintBoardBeforeGame(List<Ship> listShip) {
        char[][] instertedShipsByPlayer = Render.renderShipBeforeGame(listShip);
        Render.printBoard(instertedShipsByPlayer);
    }

    private static char[][] renderShipBeforeGame(List<Ship> list) {
        char[][] board = new char[ROW][COLUMNE];

        list.forEach(ship -> {
            if (ifHorizontal(ship)) {
                for (int i = 0; i < ship.getLength(); i++) {
                    board[ship.getYstart()][ship.getXstart() - i] = 'X';        // statek buduję się od prawej do lewej strony od x(n), x(n-1)..0
                }
            }
            if (ifVertical(ship)) {
                for (int i = 0; i < ship.getLength(); i++) {
                    board[ship.getYstart() + i][ship.getXstart()] = 'X';        // statek buduję się z góry na dół y(n),y(n+1)..
                }
            }
        });
        return board;
    }

    private static boolean ifHorizontal(Ship s) {
        return s.getPosition() == Position.HORIZONTAL;
    }

    private static boolean ifVertical(Ship ship) {
        return ship.getPosition() == Position.VERTICAL;
    }

    public static void renderShots(Set<Shot> oppentsShot) throws ArrayIndexOutOfBoundsException {
        char[][] shotsBoard = new char[ROW][COLUMNE];

        oppentsShot.stream()
                .forEach(s -> {
                    if (s.getState().equals(Shot.State.HIT)) {
                        shotsBoard[s.getY()][s.getX()] = 'X';
                    } else {
                        shotsBoard[s.getY()][s.getX()] = 'O';
                    }
                });

//        Iterator<Shot> iterator = oppentsShot.iterator();
//
//        for (Iterator<Shot> it = iterator; it.hasNext(); ) {
//            Shot s = it.next();
//            if (s.getState().equals(Shot.State.HIT)) {
//                shotsBoard[s.getY()][s.getX()] = 'X';
//            } else {
//                shotsBoard[s.getY()][s.getX()] = 'O';
//            }
//        }
        printBoard(shotsBoard);
    }

    public static void printBoard(char[][] board) {
        for (int i = 0; i < COLUMNE; i++) {
            System.out.print("\t" + i);
        }
        System.out.println();
        for (int i = 0; i < ROW; i++) {
            System.out.print(i + "\t");
            for (int j = 0; j < COLUMNE; j++) {
                System.out.print(board[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println("\n");
    }

}
