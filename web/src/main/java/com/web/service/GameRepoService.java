package com.web.service;

import board.Board;
import board.StatePreperationGame;
import com.web.enity.statusGame.StartGame;
import com.web.enity.statusGame.StatusGame;
import com.web.repositorium.StartGameRepo;
import com.web.repositorium.StatusGameRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import serialization.GameStatus;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class GameRepoService {

    private final StartGameRepo repoStartGame;
    private final StatusGameRepo repoStatusGame;
    private final GameService gameService;

    @Autowired
    public GameRepoService(StartGameRepo repoStartGame, GameService gameService, StatusGameRepo repoStatusGame) {
        this.repoStartGame = repoStartGame;
        this.gameService = gameService;
        this.repoStatusGame = repoStatusGame;
    }

    public boolean saveStatusGameToDataBase(List<Board> boardsList, StatePreperationGame state) {
        int currentPlayer = gameService.getCurrentPlayer(boardsList);
        GameStatus gameStatus = new GameStatus(boardsList, currentPlayer, state);
        StartGame game = getActualGame();
        StatusGame statusGame = new StatusGame(gameStatus, game);

        StatusGame newStatusGame = repoStatusGame.save(statusGame);

        return statusGame.equals(newStatusGame);
    }

    private StartGame getActualGame() {
        long game_id = (long) repoStartGame.findMaxId();
        return repoStartGame.findById(game_id).get();
    }

    public boolean saveStartGame(StartGame startGame) {
        StartGame newGame = repoStartGame.save(startGame);
        return newGame.equals(startGame);

    }


    public int getLastIdDataBase() {
        return repoStatusGame.findMaxId();

    }
    @Transactional
    public void deleteLastShip(int idShip) {
        repoStartGame.deleteById((long)idShip);
    }


    public List<Board> getBoards(int id) {
        StatusGame statusGame = repoStatusGame.findById((long) id).get();
        return statusGame.getGameStatus().getBoardsStatus();
    }

    public StatePreperationGame getPhaseGame(int id) {
        StatusGame statusGame = repoStatusGame.findById((long) id).get();
        return statusGame.getGameStatus().getState();
    }
    @Transactional
    public void updateStatePreperationGame(String state) {
        long maxId = repoStatusGame.findMaxId();
        StatusGame statusGame = repoStatusGame.findById(maxId).get();
        statusGame.getGameStatus().setState(StatePreperationGame.valueOf(state));
        repoStatusGame.save(statusGame);
    }

    public int getGameId() {
        return repoStartGame.findMaxId();
    }

    public boolean saveNewGame() {
        //TODO nie wiem co zapisywać do tabeli start_game oprócz timestampa, dlatego teraz wrzucam poprostu stringa
        String game = "game/" + Timestamp.valueOf(LocalDateTime.now());
        StartGame startGame = new StartGame(Timestamp.valueOf(LocalDateTime.now()), game);

        return saveStartGame(startGame) &&
                saveStatusGameToDataBase(gameService.getBoardList(), StatePreperationGame.IN_PROCCESS);

    }
}
