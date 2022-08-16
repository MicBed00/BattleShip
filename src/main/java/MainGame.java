
import control.Board;
import control.ControlPanel;
import control.Render;
import control.Ship;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


public class MainGame {

    private static Logger logger = Logger.getLogger("BattleShip");

    public static void main(String[] args) {
        //"battleship.log"
//        logger.info("Game starts...");
//        logger.finest("Game starts at: " + System.currentTimeMillis());
//        List<Ship>  playerOne = new ArrayList<>();
//        List<Ship>  playerTwo = new ArrayList<>();
        Board player1Board = new Board();
        Board player2Board = new Board();
        ControlPanel cp = new ControlPanel();
        // TODO
        if(cp.prepareBeforeGame(player1Board)) {
            System.out.println("Twoja plansza jest gotowa do gry!\n");
        }
        if(cp.prepareBeforeGame(player2Board)) {
            System.out.printf("Twoja plansza jest gotowa do gry!\n");
        }
     //   logger.info("Game prepared...");
//        Render rd = new Render();
//        char[][] player1 = rd.renderAndPrint(playerOne);
//        char[][] player2 = rd.renderAndPrint(playerTwo);
      //  logger.info("Game in progress...");
        cp.playGame(player1Board, player2Board);
    }
}
