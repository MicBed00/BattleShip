package control;

import board.Board;
import DataConfig.SizeBoard;
import DataConfig.Position;
import ship.Ship;

import java.util.List;

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
    public static void renderAndPrintBoard(List<Ship> opponentShips, Board opponentBoard) {
        char[][] renderBoardBeforeShot = new Render().renderBeforeShots(opponentShips, opponentBoard);
        printBoard(renderBoardBeforeShot);
    }

    private char[][] renderBeforeShots(List<Ship> list, Board board) throws ArrayIndexOutOfBoundsException {
        char[][] shots = board.getShotBoard();
        list.forEach(s -> {
            if (s.getPosition() == Position.HORIZONTAL) {
                boolean[] ifHit = s.getHits();
                for (int i = 0; i < s.getLength(); i++) {
                    if (ifHit[i]) {
                        shots[s.getYstart()][s.getXstart() - i] = '*';
                    }
                }
            }
            if (s.getPosition() == Position.VERTICAL) {
                for (int i = 0; i < s.getLength(); i++) {
                    boolean[] ifHit = s.getHits();
                    if (ifHit[i]) {
                        shots[s.getYstart() + i][s.getXstart()] = '*';
                    }
                }
            }
        });
        return shots;
    }

    public char[][] renderShots(List<Ship> list, char[][] shotBoard, Shot shot) throws ArrayIndexOutOfBoundsException {
        char[][] shots = shotBoard;
        shots[shot.getY()][shot.getX()] = 'O';
        printShots(list, shots);
        return shots;
    }

    private void printShots(List<Ship> list, char[][] shots) {
        list.forEach(s -> {
            if (s.getPosition() == Position.HORIZONTAL) {
                boolean[] ifHit = s.getHits();
                for (int i = 0; i < s.getLength(); i++) {
                    if (ifHit[i]) {
                        shots[s.getYstart()][s.getXstart() - i] = '*';
                    }
                }
            }
            if (s.getPosition() == Position.VERTICAL) {
                for (int i = 0; i < s.getLength(); i++) {
                    boolean[] ifHit = s.getHits();
                    if (ifHit[i]) {
                        shots[s.getYstart() + i][s.getXstart()] = '*';
                    }
                }
            }
        });
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
