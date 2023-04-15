package com.web.services;

import board.StateGame;
import com.web.configuration.GameSetupsDto;
import com.web.enity.game.Game;
import com.web.enity.game.SavedGame;
import com.web.enity.user.User;
import com.web.repositories.GameRepo;
import com.web.repositories.SavedGameRepo;
import dataConfig.ShipLimits;
import board.Board;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import serialization.GameStatus;

import java.util.*;

@Service
public class SavedGameService {
    private GameService gameService;
    private GameRepo gameRepo;
    private SavedGameRepo repoSavedGame;
    private UserService userService;

    private GameSetupsDto gameSetupsDto;

    @Autowired
    SavedGameService(@Lazy GameService gameService,
                     UserService userService,
                     GameRepo gameRepo,
                     SavedGameRepo repoSavedGame,
                     GameSetupsDto gameSetupsDto) {

        this.gameRepo = gameRepo;
        this.repoSavedGame = repoSavedGame;
        this.gameService = gameService;
        this.userService = userService;
        this.gameSetupsDto = gameSetupsDto;
    }

    public int getShipLimits() {
        return gameSetupsDto.getShipLimit();
    }

    private boolean checkIsOverTheLimitShip(int size, long gameId) {
         return size < gameService.getShipLimitNumber(gameId);
    }

    public List<Board> getBoardsList(long gameId) {
        return getSavedGame(gameId).getGameStatus().getBoardsStatus();
    }

    public int[] statisticsGame(Board board) {
        return board.statisticsShot();
    }

    public double getAccuracyShots(int totalShots, int hitShot) {
        return ((double) hitShot / totalShots) * 100;
    }

    private SavedGame getSavedStateGame(long userId) {
        Game game = userService.getLastUserGames(userId);
        Long idStatusGame = repoSavedGame.findMaxIdByGameId(game.getId());
        return repoSavedGame.findById(idStatusGame).orElseThrow(() -> new NoSuchElementException("User has not yet added the ship"));
    }

    @Transactional
    public SavedGame updateStatePreperationGame(long userId, String state) {
        SavedGame savedStateGame = getSavedStateGame(userId);
        savedStateGame.getGameStatus().setState(StateGame.valueOf(state));
        return repoSavedGame.save(savedStateGame);
    }

    public void checkIfTwoPlayersArePreparedNextChangeState(String state, long userId) {
        SavedGame savedStateGame = getSavedStateGame(userId);
        List<Board> boardsStatus = savedStateGame.getGameStatus().getBoardsStatus();
        int ply1Ships = boardsStatus.get(0).getShips().size();
        int ply2Ships = boardsStatus.get(1).getShips().size();

        if (ply1Ships == ShipLimits.SHIP_LIMIT.getQty()
                && ply2Ships == ShipLimits.SHIP_LIMIT.getQty()) {
            updateStatePreperationGame(userId, state);
        }
    }

    public int getCurrentPlayer(long gameId) {
        List<Board> boardList = getBoardsList(gameId);
        if (checkIsOverTheLimitShip(boardList.get(0).getShips().size(), gameId)) {
            return 1;
        } else if (checkIsOverTheLimitShip(boardList.get(1).getShips().size(), gameId)) {
            return 2;
        } else {
            return 2; //w web nie jest istotne
        }

    }

    @Transactional
    public boolean saveGameStatus(List<Board> boardsList, StateGame state, long gameId) {
        int currentPlayer = getCurrentPlayer(gameId);
        GameStatus gameStatus = new GameStatus(boardsList, currentPlayer, state);
        Game game = gameRepo.findById(gameId).orElseThrow(
                () -> new NoSuchElementException("No game in database")
        );
        SavedGame savedGame = new SavedGame(gameStatus, game);

        return repoSavedGame.save(savedGame) != null;
    }

    public List<Integer> getUnfinishedUserGames() {
        long loginUserId = userService.getUserId();
        User logInUser = userService.getLogInUser(loginUserId);
        List<Game> games = logInUser.getGames();
        return games.stream()
                .map(game -> repoSavedGame.findMaxIdByGameId(game.getId()))
                .map(id -> repoSavedGame.findById(id).get())
                .filter(gs -> !gs.getGameStatus().getState().equals(StateGame.FINISHED))
                .filter(gs -> !gs.getGameStatus().getState().equals(StateGame.REJECTED))
                .filter(gs -> gs.getGame().getUsers().size() > 1)
                .map(gs -> gs.getGame().getId())
                .toList();
    }


    public SavedGame getSavedGame(long idGame) {
        Long idSavedGame = repoSavedGame.findMaxIdByGameId(idGame);
        return repoSavedGame.findById(idSavedGame).orElseThrow(() -> new NoSuchElementException("No saved game"));
    }
}
