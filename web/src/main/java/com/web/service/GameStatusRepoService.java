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
        int currentPlayer = gameStatusService.getCurrentPlayer(boardsList);
        GameStatus gameStatus = new GameStatus(boardsList, currentPlayer, state);
        StartGame game = userService.getLastUserGames(userService.getUserId());
        StatusGame statusGame = new StatusGame(gameStatus, game);

        return repoStatusGame.save(statusGame) != null;
    }

    @Transactional
    public void deleteLastShip(long userId, int indexBoad) {
        Long gameId = repoStartGame.findMaxId().orElseThrow(
                () -> new NoSuchElementException("Brak gry dla Usera")
        );
        repoStatusGame.deleteLast(gameId);
        //TODO do obsłużenia wyjątek, gdy
        gameStatusService.deleteShipFromServer(indexBoad);
    }


    public StatusGame getSavedStateGame(long userId) {
        //TODO pobieranie ostatniej gry dla Usera do sprawdzenia
        StartGame game = userService.getLastUserGames(userId);
        Long idStatusGame = repoStatusGame.findMaxIdByGameId(game.getId());
        return repoStatusGame.findById(idStatusGame).orElseThrow(() -> new NoSuchElementException("Brak zapisanego statusu gry"));
    }
    @Transactional
    public void updateStatePreperationGame(long userId, String state) {
        StatusGame savedStateGame = getSavedStateGame(userId);
        savedStateGame.getGameStatus().setState(StatePreperationGame.valueOf(state));
        repoStatusGame.save(savedStateGame);
    }


}
