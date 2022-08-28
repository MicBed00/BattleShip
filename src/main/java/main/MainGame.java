package main;

import board.Board;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import control.ControlPanel;
import org.junit.platform.commons.util.LruCache;
import org.slf4j.LoggerFactory;


import java.time.LocalTime;

public class MainGame {
    // rzutujemy LoggerFactory na klasę Logger, ALE z biblioteki logback. Logger pochodzi również z logback. Dzięki temu rzutowaniu
    //uzyskujemy dostęp do metody .setLevel, która nie jest zaimplementowana w loggerze slf4j.

    private static final Logger LOG = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("main.MainGame");

    public static void main(String[] args) {
        //"battleship.log"
        LOG.info("Game starts at {} ", LocalTime.now());
        LOG.setLevel(Level.INFO);
        Board player1Board = new Board();
        Board player2Board = new Board();
        ControlPanel cp = new ControlPanel();
        // TODO
        LOG.setLevel(Level.DEBUG);
        cp.prepareBeforeGame(player1Board);
        cp.prepareBeforeGame(player2Board);
        LOG.info("Game prepared");
        cp.playGame(player1Board, player2Board);
    }
}