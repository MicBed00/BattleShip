package com.web.service;

import board.Board;
import board.StatePreperationGame;
import com.web.enity.game.StartGame;
import com.web.enity.game.StatusGame;
import com.web.enity.user.User;
import com.web.repositorium.GameRepo;
import com.web.repositorium.StatusGameRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import serialization.GameStatus;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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

    public boolean saveGameStatusToDataBase(List<Board> boardsList, StatePreperationGame state) {
        int currentPlayer = gameStatusService.getCurrentPlayer(boardsList);
        GameStatus gameStatus = new GameStatus(boardsList, currentPlayer, state);
        StartGame game = getActualGame();
        StatusGame statusGame = new StatusGame(gameStatus, game);

        StatusGame newStatusGame = repoStatusGame.save(statusGame);

        return statusGame.equals(newStatusGame);
    }

    private StartGame getActualGame() {
        Long game_id = repoStartGame.findMaxId().get();
        return repoStartGame.findById(game_id).get();
    }

    public Long getLastIdDataBase() {
        return repoStatusGame.findMaxId().orElse(0L);

    }
    @Transactional
    public void deleteLastShip(int idShip) {
        repoStartGame.deleteById((long)idShip);
    }


    public StatusGame getSavedStateGame(int userId) {
        Long gameId = repoStartGame.findMaxIdByUserId(userId).orElseThrow(
                () -> new NoSuchElementException("Brak gry dla Usera")
        );
        StatusGame savedStateGame = repoStatusGame.findMaxIdByGameId(gameId).orElseThrow(
                () -> new NoSuchElementException("Brak zapisanej gry")
        );
        return savedStateGame;
    }
    @Transactional
    public void updateStatePreperationGame(int userId, String state) {
        StatusGame savedStateGame = getSavedStateGame(userId);
        savedStateGame.getGameStatus().setState(StatePreperationGame.valueOf(state));
        repoStatusGame.save(savedStateGame);
    }

    public Long getGameId() {
       return repoStartGame.findMaxId().orElse(0L);
    }




}
