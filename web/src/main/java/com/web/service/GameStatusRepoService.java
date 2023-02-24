package com.web.service;

import board.Board;
import board.StatePreperationGame;
import com.web.enity.game.StartGame;
import com.web.enity.game.StatusGame;
import com.web.repositorium.GameRepo;
import com.web.repositorium.StatusGameRepo;
import jakarta.transaction.Transactional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import serialization.GameStatus;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class GameStatusRepoService {

    private final GameRepo repoStartGame;
    private final StatusGameRepo repoStatusGame;
    private final GameStatusService gameStatusService;
    private final UserService userService;

    @Autowired
    public GameStatusRepoService(GameRepo repoStartGame,
                                 GameStatusService gameStatusService,
                                 StatusGameRepo repoStatusGame,
                                 UserService userService)
    {
        this.repoStartGame = repoStartGame;
        this.gameStatusService = gameStatusService;
        this.repoStatusGame = repoStatusGame;
        this.userService = userService;
    }

    @Transactional
    public boolean saveGameStatusToDataBase(List<Board> boardsList, StatePreperationGame state) {
        int currentPlayer = gameStatusService.getCurrentPlayer();
        GameStatus gameStatus = new GameStatus(boardsList, currentPlayer, state);
        long userId = userService.getUserId();
        StartGame game = userService.getLastUserGames(userId);
        StatusGame statusGame = new StatusGame(gameStatus, game);

        return repoStatusGame.save(statusGame) != null;
    }

    @Transactional
    public void deleteLastShip(int indexBoard) {
        Long gameId = repoStartGame.findMaxId().orElseThrow(
                () -> new NoSuchElementException("User has not yet added the ship")
        );
        repoStatusGame.deleteLast(gameId);
        //TODO do obsłużenia wyjątek, gdy
        gameStatusService.deleteShipFromServer(indexBoard);
    }

    public StatusGame getStatusForGame(int idGame) {
        Long idStatusGame = repoStatusGame.findMaxIdByGameId(idGame);
        return repoStatusGame.findById(idStatusGame).orElseThrow(() -> new NoSuchElementException("User has not yet added the ship"));
    }

    public Integer getGameId(long statusId) {
        return repoStatusGame.findGameIdById(statusId).orElseThrow(() -> new NoSuchElementException("No games for status"));
    }

    public StatusGame getSavedStateGame(long userId) {
        StartGame game = userService.getLastUserGames(userId);
        Long idStatusGame = repoStatusGame.findMaxIdByGameId(game.getId());
        return repoStatusGame.findById(idStatusGame).orElseThrow(() -> new NoSuchElementException("User has not yet added the ship"));
    }
    @Transactional
    public StatusGame updateStatePreperationGame(long userId, String state) {
        StatusGame savedStateGame = getSavedStateGame(userId);
        savedStateGame.getGameStatus().setState(StatePreperationGame.valueOf(state));
        return repoStatusGame.save(savedStateGame);
    }


}
