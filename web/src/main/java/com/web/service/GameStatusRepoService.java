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

import java.util.*;

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
                                 UserService userService) {
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

    public List<Integer> getUnfinishedUserGames() {
        long loginUserId = userService.getLoginUserId();
        User logInUser = userService.getLogInUser(loginUserId);
        List<Game> games = logInUser.getGames();
        return games.stream()
                .map(game -> repoStatusGame.findMaxIdByGameId(game.getId()))
                .map(id -> repoStatusGame.findById(id).get())
                .filter(gs -> !gs.getGameStatus().getState().equals(StateGame.FINISHED))
                .filter(gs -> !gs.getGameStatus().getState().equals(StateGame.REJECTED))
                .filter(gs -> gs.getGame().getUsers().size() > 1)
                .map(gs -> gs.getGame().getId())
                .toList();
    }

    public List<Long> checkIfOwnGameStatusHasChanged() {
        List<Long> result = new ArrayList<>();
        Long loginUserId = userService.getLoginUserId();
        User logInUser = userService.getLogInUser(loginUserId);
        List<Game> games = logInUser.getGames();

        //na podstawie wszystkiech gier użytkownika, wyznaczam te, które mają status REQUESTING
        List<StatusGame> statusGames = getUnFinishedStatusGames(games);

        if (statusGames.isEmpty()) {
            return new ArrayList<>();
        } else {
            //Zwracam ostatnią grę z listy i wyciągam id tej gry, zwracam w endpoincie
            Game game = statusGames.stream().
                    skip(statusGames.size() - 1)
                    .findFirst().get().getGame();

            result.add(idOpponent(game, loginUserId));      //id przeciwnika
            result.add(Long.valueOf(game.getId()));

            return result;
        }
    }

    private Long idOpponent(Game game, Long loginUserId) {
        return game.getUsers().stream()
                .filter(u -> !u.getId().equals(loginUserId))
                .map(User::getId)
                .findAny().get();
    }

    private List<StatusGame> getUnFinishedStatusGames(List<Game> games) {
        return games.stream()
                .sorted(Comparator.comparing(Game::getDate))
                .map(game -> repoStatusGame.findMaxIdByGameId(game.getId()))
                .map(id -> repoStatusGame.findById(id).get())
                .filter(gs -> gs.getGameStatus().getState().equals(StateGame.REQUESTING)
                        && gs.getGame().getUsers().size() > 1)
                .toList();
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
        if (owner == userId) {
            lastShip = boards.get(0).getShips().size() - 1;
            List<Ship> ships = statusGame.getGameStatus().getBoardsStatus().get(0).getShips();
            ships.remove(lastShip);
        } else {
            lastShip = boards.get(1).getShips().size() - 1;
            statusGame.getGameStatus().getBoardsStatus().get(1).getShips().remove(lastShip);
        }
        int currentPlayer = gameStatusService.getCurrentPlayer(gameId);
        GameStatus gameStatus = new GameStatus(boards, currentPlayer, state);
        repoStatusGame.save(new StatusGame(gameStatus, game));
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

        if (ply1Ships == ShipLimits.SHIP_LIMIT.getQty()
                && ply2Ships == ShipLimits.SHIP_LIMIT.getQty()) {
            updateStatePreperationGame(userId, state);
        }
    }
}
