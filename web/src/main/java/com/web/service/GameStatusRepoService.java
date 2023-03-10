package com.web.service;

import board.Board;
import board.StateGame;
import com.web.enity.game.Game;
import com.web.enity.game.StatusGame;
import com.web.enity.user.User;
import com.web.repositorium.GameRepo;
import com.web.repositorium.StatusGameRepo;
import dataConfig.ShipLimits;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import serialization.GameStatus;
import ship.Ship;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.apache.catalina.security.SecurityUtil.remove;

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
        Game game = gameRepo.findById(gameId).orElseThrow(
                () -> new NoSuchElementException("Brak gry w bazie")
        );
        StatusGame statusGame = new StatusGame(gameStatus, game);

        return repoStatusGame.save(statusGame) != null;
    }

    public void saveNewStatusGame(GameStatus gameStatus, Game game) {
        StatusGame statusGame = new StatusGame(gameStatus, game);
        repoStatusGame.save(statusGame);
    }

    @Transactional
    public void deleteShip(long userId, long gameId) {
        Game game = gameRepo.findById(gameId).orElseThrow(
                () -> new NoSuchElementException("Can't find game")
        );
        Long owner = game.getOwnerGame();
        StatusGame statusGame = getStatusGame(gameId);

        List<Board> boards = statusGame.getGameStatus().getBoardsStatus();
        StateGame state = statusGame.getGameStatus().getState();
        int lastShip;
        if(owner == userId) {
            lastShip = boards.get(0).getShips().size() - 1;
            List<Ship> ships = statusGame.getGameStatus().getBoardsStatus().get(0).getShips();
            ships.remove(lastShip);
        } else {
            lastShip = boards.get(1).getShips().size() - 1;
            statusGame.getGameStatus().getBoardsStatus().get(1).getShips().remove(lastShip);
        }
        int currentPlayer = gameStatusService.getCurrentPlayer(gameId);
        GameStatus gameStatus = new GameStatus(boards, currentPlayer, state);
        repoStatusGame.save(new StatusGame(gameStatus,game));
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

    public void checkIfTwoPlayersArePreparedThenChangingState(String state, long userId) {
        StatusGame savedStateGame = getSavedStateGame(userId);
        List<Board> boardsStatus = savedStateGame.getGameStatus().getBoardsStatus();
        int ply1Ships = boardsStatus.get(0).getShips().size();
        int ply2Ships = boardsStatus.get(1).getShips().size();

        if(ply1Ships == ShipLimits.SHIP_LIMIT.getQty()
           && ply2Ships == ShipLimits.SHIP_LIMIT.getQty()) {
            updateStatePreperationGame(userId, state);
        }
    }
}
