
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
        cp.prepareBeforeGame(player1Board);
        cp.prepareBeforeGame(player2Board);
        LOG.info("Game prepared");
        cp.playGame(player1Board, player2Board);
    }
}