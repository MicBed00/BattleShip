package com.web.services;

import board.StateGame;
import com.web.enity.game.Game;
import com.web.enity.game.SavedGame;
import com.web.enity.user.User;
import com.web.repositories.GameRepo;
import com.web.repositories.SavedGameRepo;
import dataConfig.Position;
import dataConfig.ShipLimits;
import board.Board;
import board.Shot;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import serialization.GameStatus;
import ship.Ship;

import java.util.*;

@Service
public class SavedGameService {
    private List<String> shipSize;
    private List<Position> positionList;

    private GameRepoService gameRepoService;

    private GameRepo gameRepo;
    private SavedGameRepo repoSavedGame;

    private UserService userService;


    @Autowired
    SavedGameService(@Lazy GameRepoService gameRepoService,
                     UserService userService,
                     GameRepo gameRepo,
                     SavedGameRepo repoSavedGame) {

        this.gameRepo = gameRepo;
        this.repoSavedGame = repoSavedGame;
        this.gameRepoService = gameRepoService;
        this.userService = userService;
        positionList = new ArrayList<>();
        positionList.add(Position.HORIZONTAL);
        positionList.add(Position.VERTICAL);
        shipSize = new ArrayList<>();
        shipSize.add("1");
        shipSize.add("2");
        shipSize.add("3");
        shipSize.add("4");
    }

    public int getShipLimits() {
        return ShipLimits.SHIP_LIMIT.getQty();
    }

    private boolean checkIsOverTheLimitShip(int size) {
        return size < ShipLimits.SHIP_LIMIT.getQty();
    }

    public List<String> getShipSize() {
        return shipSize;
    }

    public List<Position> getOrientation() {
        return positionList;
    }

    public List<Board> getBoardsList(long gameId) {
        return getStatusGame(gameId).getGameStatus().getBoardsStatus();
    }

    public int[] statisticsGame(Board board) {
        return board.statisticsShot();
    }

    public double getAccuracyShot(int totalShots, int hitShot) {
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

    public void checkIfTwoPlayersArePreparedThenChangingState(String state, long userId) {
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
        if (checkIsOverTheLimitShip(boardList.get(0).getShips().size())) {
            return 1;
        } else if (checkIsOverTheLimitShip(boardList.get(1).getShips().size())) {
            return 2;
        }
        throw new NoSuchElementException("Can't set current player");
    }

    @Transactional
    public boolean saveGameStatusToDataBase(List<Board> boardsList, StateGame state, long gameId) {
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


    public SavedGame getStatusGame(long idGame) {
        Long idStatusGame = repoSavedGame.findMaxIdByGameId(idGame);
        return repoSavedGame.findById(idStatusGame).orElseThrow(() -> new NoSuchElementException("User has not yet added the ship"));
    }


}
