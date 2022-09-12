package main;

import board.Board;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import control.ControlPanel;
import org.slf4j.LoggerFactory;
import serialization.GameStatus;


import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainGame {
    // rzutujemy LoggerFactory na klasę Logger, ALE z biblioteki logback. Logger pochodzi również z logback. Dzięki temu rzutowaniu
    //uzyskujemy dostęp do metody .setLevel, która nie jest zaimplementowana w loggerze slf4j.

    private static final Logger LOG = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("main.MainGame");
    public static final String currentLocal = "pl";
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
        String argFile = args[0];
        System.out.println(argFile);
        ObjectMapper mapper = new ObjectMapper();
        GameStatus gameStatus = null;
        try {

            gameStatus = mapper.readValue(new File(argFile), GameStatus.class);
            System.out.println(gameStatus);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Board board1 = gameStatus.getBoardsStatus().get(0);
        Board board2 = gameStatus.getBoardsStatus().get(1);

//        LOG.setLevel(Level.DEBUG);
//        cp.prepareBeforeGame(player1Board);
//        cp.prepareBeforeGame(player2Board);
//        LOG.info(bundle.getString("gamePrepared"));
        //tutaj strzelić if i sprawdzać czy args jest puste czy nie, jesli nie to będzie plik json i trzeba go deserializować

        try {
            cp.playGame(board1, board2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}