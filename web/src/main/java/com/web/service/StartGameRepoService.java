package com.web.service;

import board.Board;
import board.StatePreperationGame;
import com.web.enity.StartGame;
import com.web.repositorium.RepoStartGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import serialization.GameStatus;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StartGameRepoService {

    private RepoStartGame repoStartGame;
    private GameService gameService;

    @Autowired
    public StartGameRepoService(RepoStartGame repoStartGame, GameService gameService) {
        this.repoStartGame = repoStartGame;
        this.gameService = gameService;
    }

    public void saveStatusGameToDataBase(List<Board> boardsList) {
        int currentPlayer = gameService.getCurrentPlayer(boardsList);
        GameStatus gameStatus = new GameStatus(boardsList, currentPlayer, StatePreperationGame.IN_PROCCESS);
        StartGame startGame = new StartGame(Timestamp.valueOf(LocalDateTime.now()), gameStatus);

        repoStartGame.save(startGame);
    }


    public int getLastIdDataBase() {

        return repoStartGame.findMaxId();
    }

    public void deleteLastShip(int idShip) {
        repoStartGame.deleteById((long)idShip);
    }

    public List<Board> getAllShips(int id) {
        StartGame statusGame = repoStartGame.findById((long) id).get();
        List<Board> boardsStatus = statusGame.getGameStatus().getBoardsStatus();

        return boardsStatus;

    }

    public List<Board> getListBoard(int id) {
        StartGame statusGame = repoStartGame.findById((long) id).get();

        return statusGame.getGameStatus().getBoardsStatus();
    }
}
