package main;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import control.ControlPanel;
import board.StatePreperationGame;
import board.UI;
import org.slf4j.LoggerFactory;
import serialization.GameStatus;
import serialization.Reader;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MainGame {
    // rzutujemy LoggerFactory na klasę Logger, ALE z biblioteki logback. Logger pochodzi również z logback. Dzięki temu rzutowaniu
    //uzyskujemy dostęp do metody .setLevel, która nie jest zaimplementowana w loggerze slf4j.
    private static final Logger LOG = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("main.MainGame");
    private static UI user = new UI();

    public static void main(String[] args) {
        Locale.setDefault(Locale.JAPAN);
        LocalDateTime dt = LocalDateTime.of(2022, 1, 1, 10, 15, 50, 595);
        String pattern = "dd-MMMM-yyyy HH:mm:ss.SSS";

        DateTimeFormatter defaultTime = DateTimeFormatter.ofPattern(pattern);
        DateTimeFormatter germanTime = DateTimeFormatter.ofPattern(pattern, Locale.GERMAN);

        //"battleship.log"
        LOG.info(user.messageBundle("start", new Date()));
        LOG.setLevel(Level.INFO);
        System.out.println(dt.format(defaultTime));
        System.out.println(dt.format(germanTime));
        ControlPanel cp = new ControlPanel();

        if((0 < args.length) && (args!= null)) {
            String argFile = args[0];
            GameStatus gameStatus = null;
            try {
                gameStatus = new Reader().readFromFile(argFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(Objects.requireNonNull(gameStatus).getState().equals(StatePreperationGame.IN_PROCCESS)) {
               GameStatus game =  cp.prepareBeforeGame(gameStatus);
               cp.playGame(game);
            }else {
                cp.playGame(gameStatus);
            }
        } else {
            LOG.setLevel(Level.DEBUG);
            GameStatus gameStatus = cp.prepareBeforeGame();
            LOG.info(user.messageBundle("gamePrepared"));
            cp.playGame(gameStatus);
        }
    }
}