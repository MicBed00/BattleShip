package serialization;

import board.Board;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.*;
import exceptions.NullObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Saver {
    private final String filePath = "target/gameStatus.json";
    private ObjectMapper objectMapper;

    public Saver() throws IOException {
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writerWithDefaultPrettyPrinter();
    }

    public void saveToFile(Board board1, Board board2, String currentPlayer) throws IOException, NullObject{
        List<Board> boardList = creatBoardsList(board1, board2);
        GameStatus gameStatus = new GameStatus(boardList, currentPlayer);
        String str = null;
        if(board1 == null || board2 == null || currentPlayer == null) {
            throw new NullObject("Object is null or incomplete");
        } else {
           str = objectMapper.writeValueAsString(gameStatus);
        }
        writeStringtoFile(str);
    }

    private void writeStringtoFile(String file) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath));
        bufferedWriter.write(file);
        bufferedWriter.close();
    }

    private List<Board> creatBoardsList(Board board1, Board board2) {
        List<Board> list = new ArrayList<>();
        list.add(board1);
        list.add(board2);
        return list;
    }
}
