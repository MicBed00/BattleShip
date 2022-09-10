package serialization;

import board.Board;

import java.util.List;

public class GameStatus {
    private List<Board> boardsStatus;
    private String curretnPlayer;

    public GameStatus(List<Board> boardsStatus, String curretnPlayer) {
        this.boardsStatus = boardsStatus;
        this.curretnPlayer = curretnPlayer;
    }

    public List<Board> getBoardsStatus() {
        return boardsStatus;
    }

    public String getCurretnPlayer() {
        return curretnPlayer;
    }
}
