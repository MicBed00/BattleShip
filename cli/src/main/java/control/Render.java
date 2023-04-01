package control;

import board.Board;
import dataConfig.Position;
import board.Shot;
import board.SizeBoard;
import ship.Ship;

import java.util.List;
import java.util.Set;

public class Render {
    public static void renderAndPrintBoardBeforeGame(List<Ship> listShip, int sizeBoard) {
        char[][] instertedShipsByPlayer = Render.renderShipBeforeGame(listShip, sizeBoard);
        Render.printBoard(instertedShipsByPlayer, sizeBoard);
    }

    private static char[][] renderShipBeforeGame(List<Ship> list, int sizeBoard) {
        char[][] board = new char[sizeBoard][sizeBoard];

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

    public static void renderShots(Set<Shot> oppentsShot, int sizeBoard) throws ArrayIndexOutOfBoundsException {
        char[][] shotsBoard = new char[sizeBoard][sizeBoard];

        oppentsShot.stream()
                .forEach(s -> {
                    if (s.getState().equals(Shot.State.HIT)) {
                        shotsBoard[s.getY()][s.getX()] = 'X';
                    } else {
                        shotsBoard[s.getY()][s.getX()] = 'O';
                    }
                });
        printBoard(shotsBoard, sizeBoard);
    }

    public static void printBoard(char[][] board, int sizeBoard) {
        for (int i = 0; i < sizeBoard; i++) {
            System.out.print("\t" + i);
        }
        System.out.println();
        for (int i = 0; i < sizeBoard; i++) {
            System.out.print(i + "\t");
            for (int j = 0; j < sizeBoard; j++) {
                System.out.print(board[i][j] + "\t");
            }
            System.out.println();
        }
        System.out.println("\n");
    }
}
