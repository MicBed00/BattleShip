package serialization;

import board.Board;
import board.StatePreperationGame;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameStatus that = (GameStatus) o;
        return curretnPlayer == that.curretnPlayer && Objects.equals(boardsStatus, that.boardsStatus) && state == that.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardsStatus, curretnPlayer, state);
    }
}
