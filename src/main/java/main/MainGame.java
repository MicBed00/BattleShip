package main;

import board.Board;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import control.ControlPanel;
import control.UI;
import org.slf4j.LoggerFactory;
import serialization.GameStatus;
import serialization.Reader;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class MainGame {
    // rzutujemy LoggerFactory na klasę Logger, ALE z biblioteki logback. Logger pochodzi również z logback. Dzięki temu rzutowaniu
    //uzyskujemy dostęp do metody .setLevel, która nie jest zaimplementowana w loggerze slf4j.

    private static final Logger LOG = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("main.MainGame");
    public static final String currentLocal = "pl";
    private static UI user = new UI();

    public static void main(String[] args) {

        Locale.setDefault(Locale.JAPAN);
        LocalDateTime dt = LocalDateTime.of(2022, 1, 1, 10, 15, 50, 595);
        String pattern = "dd-MMMM-yyyy HH:mm:ss.SSS";

        DateTimeFormatter defaultTime = DateTimeFormatter.ofPattern(pattern);
        DateTimeFormatter germanTime = DateTimeFormatter.ofPattern(pattern, Locale.GERMAN);

        //"battleship.log"
        LOG.info(user.messageBundle("start"));
        LOG.setLevel(Level.INFO);
        System.out.println(dt.format(defaultTime));
        System.out.println(dt.format(germanTime));
        Board player1Board = null;
        Board player2Board = null;
        ControlPanel cp = new ControlPanel();

        if((0 < args.length) && (args!= null)) {
            String argFile = args[0];
            GameStatus gameStatus = null;
            try {
                gameStatus = new Reader().readFromFile(argFile);

                cp.playGame(gameStatus);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            player1Board = new Board();
            player2Board = new Board();

            LOG.setLevel(Level.DEBUG);
            cp.prepareBeforeGame(player1Board);
            cp.prepareBeforeGame(player2Board);
            LOG.info(user.messageBundle("gamePrepared"));

            try {
                cp.playGame(player1Board, player2Board);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}