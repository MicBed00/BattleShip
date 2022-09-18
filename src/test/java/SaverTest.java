import DataConfig.Position;
import board.Board;
import com.fasterxml.jackson.databind.SerializationFeature;
import control.Shot;
import control.StatePreperationGame;
import org.junit.jupiter.api.Test;
import serialization.GameStatus;

import com.fasterxml.jackson.databind.ObjectMapper;
import serialization.Saver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class SaverTest {

    @Test
    void serialiationStatusGameTest() throws IOException {
        int currentPlayer = 1;
        StatePreperationGame state = StatePreperationGame.FINISHED;
        Board board1 = new Board();
        Board board2 = new Board();
        board1.addShip(2, 2, 2, Position.VERTICAL);
        board1.addShip(2, 8, 2, Position.HORIZONTAL);

        board2.addShip(1, 1, 1, Position.VERTICAL);
        board2.addShip(2, 5, 5, Position.HORIZONTAL);
        //strzela niecelnie
        board1.shoot(new Shot(9, 0));
        board1.shoot(new Shot(3, 5));
        board1.shoot(new Shot(5, 8));
        board1.shoot(new Shot(9, 9));

        board2.shoot(new Shot(9, 0));
        board2.shoot(new Shot(0, 0));
        board2.shoot(new Shot(8, 8));
        board2.shoot(new Shot(9, 9));

        //strzela celnie i eliminuje statki z planszy
        board1.shoot(new Shot(2, 2));
        board1.shoot(new Shot(2, 3));
        board1.shoot(new Shot(8, 2));

        board2.shoot(new Shot(1, 1));
        board2.shoot(new Shot(5, 5));
        board2.shoot(new Shot(4, 5));

        Saver saver = new Saver();
        saver.saveToFile(board1,board2,currentPlayer, state);


        File jsonFile = new File("target/gameStatus.json");
        File expectedFile = new File("src/test/java/expectedFileTest.json");
     testCreatJsonFile(currentPlayer, board1, board2, expectedFile, state);  //

        assertThat(jsonFile).hasSameTextualContentAs(expectedFile);
    }

    private void testCreatJsonFile(int currentPlayer, Board board1, Board board2, File expectedFile, StatePreperationGame state) throws IOException {
        List<Board> boardList = new ArrayList<>();
        boardList.add(board1);
        boardList.add(board2);
        GameStatus gameStatus = new GameStatus(boardList, currentPlayer, state);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writerWithDefaultPrettyPrinter();
        String json = mapper.writeValueAsString(gameStatus);

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(expectedFile));
        bufferedWriter.write(json);
        bufferedWriter.close();
    }
}