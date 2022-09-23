package serialization;

import board.Board;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.*;
import board.StatePreperationGame;
import exceptions.NullObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Saver {
    private final ObjectMapper objectMapper;

    public Saver() {
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writerWithDefaultPrettyPrinter();
    }


    public void saveToFile(Board board1, Board board2, int currentPlayer, StatePreperationGame state) throws IOException, NullObject{
        List<Board> boardList = creatBoardsList(board1, board2);
        GameStatus gameStatus = new GameStatus(boardList, currentPlayer, state);
        String str = objectMapper.writeValueAsString(gameStatus);

        writeStringtoFile(str);
    }

    private void writeStringtoFile(String file) throws IOException {
        String filePath = "target/gameStatus.json";
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath));
        bufferedWriter.write(file);
        bufferedWriter.close();
    }

    private List<Board> creatBoardsList(Board board1, Board board2) {
        List<Board> list = new ArrayList<>();
        if(board2.getShips().size() > 0)
        list.add(board1);
        if(board2.getShips().size() > 0)
        list.add(board2);
        return list;
    }
}
