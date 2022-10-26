import DataConfig.Position;
import board.Board;
import board.Shot;
import board.StatePreperationGame;
import org.junit.jupiter.api.Test;
import serialization.GameStatus;
import serialization.Reader;
import serialization.Saver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class SaverTest {

//    @Test
//    public void serialiationStatusGameTest() throws IOException {
//        int currentPlayer = 1;
//        StatePreperationGame state = StatePreperationGame.FINISHED;
//        Board board1 = new Board();
//        Board board2 = new Board();
//        board1.addShip(2, 2, 2, Position.VERTICAL);
//        board1.addShip(2, 8, 2, Position.HORIZONTAL);
//
//        board2.addShip(1, 1, 1, Position.VERTICAL);
//        board2.addShip(2, 5, 5, Position.HORIZONTAL);
//        //strzela niecelnie
//        board1.shoot(new Shot(9, 0));
//        board1.shoot(new Shot(3, 5));
//        board1.shoot(new Shot(5, 8));
//        board1.shoot(new Shot(9, 9));
//
//        board2.shoot(new Shot(9, 0));
//        board2.shoot(new Shot(0, 0));
//        board2.shoot(new Shot(8, 8));
//        board2.shoot(new Shot(9, 9));
//
//        //strzela celnie i eliminuje statki z planszy
//        board1.shoot(new Shot(2, 2));
//        board1.shoot(new Shot(2, 3));
//        board1.shoot(new Shot(8, 2));
//
//        board2.shoot(new Shot(1, 1));
//        board2.shoot(new Shot(5, 5));
//        board2.shoot(new Shot(4, 5));
//
//        Saver saver = new Saver();
//
//        GameStatus gameStatus = getGameStatusBeforeSaveToFile(currentPlayer, state, board1, board2);
//        saver.saveToFile(board1, board2, currentPlayer, state);
//        GameStatus gameStatusAfterDeser = new Reader().readFromFile("target/gameStatus.json");
//
//        assertEquals(gameStatus, gameStatusAfterDeser);
//    }
//
//    private GameStatus getGameStatusBeforeSaveToFile(int currentPlayer, StatePreperationGame state,
//                                                     Board board1, Board board2) {
//        List<Board> boardList = new ArrayList<>();
//        boardList.add(board1);
//        boardList.add(board2);
//        return new GameStatus(boardList, currentPlayer, state);
//    }
}