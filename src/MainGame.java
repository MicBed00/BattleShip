
import java.util.ArrayList;
import java.util.List;


public class MainGame {
    public static void main(String[] args) {
        List<Ship> playerOne = new ArrayList<>();
        List<Ship> playerTwo = new ArrayList<>();
        ControlPanel cp = new ControlPanel();
        cp.prepareBeforeGame(playerOne, "player1");
        cp.prepareBeforeGame(playerTwo, "player2");
        Render rd = new Render();
        char[][] player1 = rd.renderAndPrint(playerOne);
        char[][] player2 = rd.renderAndPrint(playerTwo);
        cp.playGame(playerOne,playerTwo, player1, player2);
    }
}
