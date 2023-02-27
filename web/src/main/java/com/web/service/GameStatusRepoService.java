package com.web.service;

import board.Board;
import board.StateGame;
import com.web.enity.game.Game;
import com.web.enity.game.StatusGame;
import com.web.repositorium.GameRepo;
import com.web.repositorium.StatusGameRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import serialization.GameStatus;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class GameStatusRepoService {

    private final GameRepo gameRepo;
    private final StatusGameRepo repoStatusGame;
    private final GameStatusService gameStatusService;
    private final UserService userService;

    @Autowired
    public GameStatusRepoService(GameRepo gameRepo,
                                 GameStatusService gameStatusService,
                                 StatusGameRepo repoStatusGame,
                                 UserService userService)
    {
        this.gameRepo = gameRepo;
        this.gameStatusService = gameStatusService;
        this.repoStatusGame = repoStatusGame;
        this.userService = userService;
    }

    @Transactional
    public boolean saveGameStatusToDataBase(List<Board> boardsList, StateGame state, long gameId) {
        int currentPlayer = gameStatusService.getCurrentPlayer(gameId);
        GameStatus gameStatus = new GameStatus(boardsList, currentPlayer, state);
        Game game = gameRepo.findById(gameId).get();
        StatusGame statusGame = new StatusGame(gameStatus, game);

        return repoStatusGame.save(statusGame) != null;
    }

    public void saveNewStatusGame(GameStatus gameStatus, Game game) {
        StatusGame statusGame = new StatusGame(gameStatus, game);
        repoStatusGame.save(statusGame);
    }

    @Transactional
    public void deleteLastShip(int indexBoard, long gameId) {
//        Long gameId = repoStartGame.findMaxId().orElseThrow(
//                () -> new NoSuchElementException("User has not yet added the ship")
//        );
        repoStatusGame.deleteLast(gameId);
    }

    public StatusGame getStatusGame(long idGame) {
        Long idStatusGame = repoStatusGame.findMaxIdByGameId(idGame);
        return repoStatusGame.findById(idStatusGame).orElseThrow(() -> new NoSuchElementException("User has not yet added the ship"));
    }


    public StatusGame getSavedStateGame(long userId) {
        Game game = userService.getLastUserGames(userId);
        Long idStatusGame = repoStatusGame.findMaxIdByGameId(game.getId());
        return repoStatusGame.findById(idStatusGame).orElseThrow(() -> new NoSuchElementException("User has not yet added the ship"));
    }
    @Transactional
    public StatusGame updateStatePreperationGame(long userId, String state) {
        StatusGame savedStateGame = getSavedStateGame(userId);
        savedStateGame.getGameStatus().setState(StateGame.valueOf(state));
        return repoStatusGame.save(savedStateGame);
    }

}
