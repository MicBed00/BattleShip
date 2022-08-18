
import board.Board;
import control.ControlPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;

public class MainGame {

    private static final Logger LOG = LoggerFactory.getLogger(MainGame.class);

    public static void main(String[] args) {
        //"battleship.log"
        LOG.info("Game starts at {} ", LocalTime.now());
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
        LOG.info("Game prepared");
        cp.playGame(player1Board, player2Board);
//        logger.info("Game in progress...");
    }
}