package serialization;

import board.Board;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.*;
import exceptions.NullObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Saver {
    private final String filePath = "target/gameStatus.json";
    private File file;
    private ObjectMapper objectMapper;

    public Saver() {
        this.file = new File(filePath);
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writerWithDefaultPrettyPrinter();
    }

    public void saveToFile(Board board1, Board board2, String currentPlayer) throws IOException, NullObject{
        List<Board> boardList = creatBoardsList(board1, board2);
        GameStatus gameStatus = new GameStatus(boardList, currentPlayer);
        if(gameStatus == null) {
            throw new NullObject("Object is null");
        } else {
            objectMapper.writeValue(file, gameStatus);
        }
    }

    private List<Board> creatBoardsList(Board board1, Board board2) {
        List<Board> list = new ArrayList<>();
        list.add(board1);
        list.add(board2);
        return list;
    }
}
