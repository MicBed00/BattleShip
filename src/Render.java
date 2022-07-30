import java.util.List;

public class Render {
    private static final int ROW = 10;
    private static final int COLUMNE = 10;

    public static int getSizeBoard() {      //metoda potrzebna do warunku wprowadzania współrzędnych w metodzie isColliding();
        return ROW;
    }

    public char[][] renderAndPrint(List<Ship> list) {
        char[][] board = new char[ROW][COLUMNE];
        list.forEach(s -> {
            if (s.getPosition() == Position.HORIZONTAL) {
                for (int i = 0; i < s.getLength(); i++) {
                    board[s.getY()][s.getX() - i] = 'X';        // statek buduję się od prawej do lewej strony od x(n), x(n-1)..0
                }
            }
            if (s.getPosition() == Position.VERTICAL) {
                for (int i = 0; i < s.getLength(); i++) {
                    board[s.getY() + i][s.getX()] = 'X';        // statek buduję się z góry na dół y(n),y(n+1)..
                }
            }
        });
        printBoard(board);
        return board;
    }

    static void printBoard(char[][] board) {
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
