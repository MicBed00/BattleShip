package main;

import DataConfig.Position;
import board.Board;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import control.ControlPanel;
import org.junit.platform.commons.util.LruCache;
import org.slf4j.LoggerFactory;
import serialization.JsonFile;
import ship.Ship;


import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainGame {
    // rzutujemy LoggerFactory na klasę Logger, ALE z biblioteki logback. Logger pochodzi również z logback. Dzięki temu rzutowaniu
    //uzyskujemy dostęp do metody .setLevel, która nie jest zaimplementowana w loggerze slf4j.

    private static final Logger LOG = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("main.MainGame");
    public static final String currentLocal = "en";
    private static final Locale locale = new Locale(currentLocal);
    private static final ResourceBundle bundle = ResourceBundle.getBundle("Bundle", locale);

    public static void main(String[] args) {

        Locale.setDefault(Locale.JAPAN);
        LocalDateTime dt = LocalDateTime.of(2022, 1, 1, 10, 15, 50, 595);
        String pattern = "dd-MMMM-yyyy HH:mm:ss.SSS";

        DateTimeFormatter defaultTime = DateTimeFormatter.ofPattern(pattern);
        DateTimeFormatter germanTime = DateTimeFormatter.ofPattern(pattern, Locale.GERMAN);



        //"battleship.log"
        LOG.info(bundle.getString("start") + " {} ", LocalTime.now());
        LOG.setLevel(Level.INFO);
        System.out.println(dt.format(defaultTime));
        System.out.println(dt.format(germanTime));
        Board player1Board = new Board();
        Board player2Board = new Board();
        ControlPanel cp = new ControlPanel();
        JsonFile<Board> boardJson = null;
        try {
            boardJson = new JsonFile<>("target/BoardJson.json");
            boardJson.creatJson(player1Board);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // TODO
        LOG.setLevel(Level.DEBUG);
        try {
            cp.prepareBeforeGame(player1Board);

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            cp.prepareBeforeGame(player2Board);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOG.info(bundle.getString("gamePrepared"));
        cp.playGame(player1Board, player2Board);



    }
}