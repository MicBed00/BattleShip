package serialization;

import board.Board;
import board.StatePreperationGame;

import java.util.List;

public class GameStatus {
    private List<Board> boardsStatus;
    private int curretnPlayer;
    private StatePreperationGame state;

    public GameStatus() {
    }

    public GameStatus(List<Board> boardsStatus, int curretnPlayer, StatePreperationGame state ) {
        this.boardsStatus = boardsStatus;
        this.curretnPlayer = curretnPlayer;
        this.state = state;
    }

    public List<Board> getBoardsStatus() {
        return boardsStatus;
    }


    public int getCurretnPlayer() {
        return curretnPlayer;
    }

    public void setBoardsStatus(List<Board> boardsStatus) {
        this.boardsStatus = boardsStatus;
    }

    public void setCurretnPlayer(int curretnPlayer) {
        this.curretnPlayer = curretnPlayer;
    }

    public StatePreperationGame getState() {
        return state;
    }

    public void setState(StatePreperationGame state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "GameStatus{" +
                "boardsStatus=" + boardsStatus +
                ", curretnPlayer=" + curretnPlayer +
                ", state=" + state +
                '}';
    }
}
