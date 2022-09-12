package serialization;

import board.Board;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GameStatus {
    private List<Board> boardsStatus;

    private String curretnPlayer;

    public GameStatus() {
    }

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

    public void setBoardsStatus(List<Board> boardsStatus) {
        this.boardsStatus = boardsStatus;
    }

    public void setCurretnPlayer(String curretnPlayer) {
        this.curretnPlayer = curretnPlayer;
    }

    @Override
    public String toString() {
        return "GameStatus{" +
                "boardsStatus=" + boardsStatus +
                ", curretnPlayer='" + curretnPlayer + '\'' +
                '}';
    }
}
