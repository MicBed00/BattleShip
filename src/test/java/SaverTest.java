import DataConfig.Position;
import board.Board;
import com.fasterxml.jackson.databind.SerializationFeature;
import control.Shot;
import org.junit.jupiter.api.Test;
import serialization.GameStatus;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class SaverTest {

    @Test
    void serialiationStatusGameTest() throws IOException {
        String currentPlayer = "Zwodnik 1";
        Board board1 = new Board();
        Board board2 = new Board();
        board1.addShip(2, 2, 2, Position.VERTICAL);
        board1.addShip(2, 8, 2, Position.HORIZONTAL);

        board2.addShip(1, 1, 1, Position.VERTICAL);
        board2.addShip(2, 5, 5, Position.HORIZONTAL);
        //p2 strzela niecelnie
        board1.shoot(new Shot(9, 0));
        board1.shoot(new Shot(3, 5));
        board1.shoot(new Shot(5, 8));
        board1.shoot(new Shot(9, 9));
        board1.getIsFinished();
        board2.shoot(new Shot(9, 0));
        board2.shoot(new Shot(0, 0));
        board2.shoot(new Shot(8, 8));
        board2.shoot(new Shot(9, 9));
        board2.getIsFinished();
        //p2 strzela celnie i eliminuje statki z planszy
        board1.shoot(new Shot(2, 2));
        board1.shoot(new Shot(2, 3));
        board1.shoot(new Shot(8, 2));
        board1.shoot(new Shot(7, 2));
        board1.getIsFinished();
        board2.shoot(new Shot(1, 1));
        board2.shoot(new Shot(5, 5));
        board2.shoot(new Shot(4, 5));
        board2.getIsFinished();

//        Saver saver = new Saver();
//        saver.saveToFile(board1, board2, currentPlayer);
        List<Board> boardList = new ArrayList<>();
        boardList.add(board1);
        boardList.add(board2);
        GameStatus gameStatus = new GameStatus(boardList, currentPlayer);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writerWithDefaultPrettyPrinter();
        String json = mapper.writeValueAsString(gameStatus);

    }
}