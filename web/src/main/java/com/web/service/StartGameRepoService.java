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


}
