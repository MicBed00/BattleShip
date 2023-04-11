package serialization;

import board.Board;
import board.StateGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GameStatus {
    private List<Board> boardsStatus;
    private StateGame state;
    private int currentPlayer;

    public GameStatus() {
    }

    public GameStatus(List<Board> boardsStatus, StateGame state) {
        this.boardsStatus = boardsStatus;
        this.state = state;
    }

    public GameStatus(List<Board> boardsStatus, int currentPlayer, StateGame state) {
        this.boardsStatus = boardsStatus;
        this.currentPlayer = currentPlayer;
        this.state = state;
    }

    public List<Board> getBoardsStatus() {
        return boardsStatus;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setBoardsStatus(List<Board> boardsStatus) {
        this.boardsStatus = boardsStatus;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public StateGame getState() {
        return state;
    }

    public void setState(StateGame state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "GameStatus{" + "boardsStatus=" + boardsStatus + ", curretnPlayer=" + currentPlayer + ", state=" + state + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameStatus that = (GameStatus) o;
        return currentPlayer == that.currentPlayer && Objects.equals(boardsStatus, that.boardsStatus) && state == that.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardsStatus, currentPlayer, state);
    }
}
