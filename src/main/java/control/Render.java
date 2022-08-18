package control;

import board.Board;
import board.SizeBoard;
import ship.Position;
import ship.Ship;

import java.util.List;

public class Render {
    private static final int ROW = SizeBoard.ROW.getSize();
    private static final int COLUMNE = SizeBoard.COLUMNE.getSize();
    private char[][] shootArr = new char[ROW][COLUMNE];
    public static int getSizeBoard() {      //metoda potrzebna do warunku wprowadzania współrzędnych w metodzie isColliding();
        return ROW;
    }


    public static char[][] renderShipBeforeGame(List<Ship> list) {
        char[][] board = new char[ROW][COLUMNE];
        list.forEach(s -> {
            if (s.getPosition() == Position.HORIZONTAL) {
                for (int i = 0; i < s.getLength(); i++) {
                        board[s.getYstart()][s.getXstart() - i] = 'X';        // statek buduję się od prawej do lewej strony od x(n), x(n-1)..0
                }
            }
            if (s.getPosition() == Position.VERTICAL) {
                for (int i = 0; i < s.getLength(); i++) {
                        board[s.getYstart() + i][s.getXstart()] = 'X';        // statek buduję się z góry na dół y(n),y(n+1)..
                }
            }
        });
        return board;
    }

    public char[][] renderShots(List<Ship> list, char[][] shotBoard, int x, int y) throws ArrayIndexOutOfBoundsException {
        char[][] shots = shotBoard;
        shots[y][x] = 'O';
        list.forEach(s -> {
            if (s.getPosition() == Position.HORIZONTAL) {
                boolean[] ifHit = s.getHits();
                for (int i = 0; i < s.getLength(); i++) {
                    if(ifHit[i]) {
                        shots[s.getYstart()][s.getXstart() - i] = '*';        // statek buduję się od prawej do lewej strony od x(n), x(n-1)..0
                    }
                }
            }
            if (s.getPosition() == Position.VERTICAL) {
                for (int i = 0; i < s.getLength(); i++) {
                    boolean[] ifHit = s.getHits();
                    if(ifHit[i]) {
                        shots[s.getYstart() + i][s.getXstart()] = '*';        // statek buduję się z góry na dół y(n),y(n+1)..
                    }
                }
            }
        });
        return shots;
    }

    public char[][] renderBeforeShots(List<Ship> list, Board board) throws ArrayIndexOutOfBoundsException {
        char[][] shots = board.getShotBoard();
        list.forEach(s -> {
            if (s.getPosition() == Position.HORIZONTAL) {
                boolean[] ifHit = s.getHits();
                for (int i = 0; i < s.getLength(); i++) {
                    if(ifHit[i]) {
                        shots[s.getYstart()][s.getXstart() - i] = '*';        // statek buduję się od prawej do lewej strony od x(n), x(n-1)..0
                    }
                }
            }
            if (s.getPosition() == Position.VERTICAL) {
                for (int i = 0; i < s.getLength(); i++) {
                    boolean[] ifHit = s.getHits();
                    if(ifHit[i]) {
                        shots[s.getYstart() + i][s.getXstart()] = '*';        // statek buduję się z góry na dół y(n),y(n+1)..
                    }
                }
            }
        });
        return shots;
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
