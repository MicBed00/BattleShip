package com.web.services;

import board.Board;
import board.StateGame;
import com.web.enity.game.Game;
import com.web.enity.game.SavedGame;
import com.web.enity.user.User;
import com.web.repositories.GameRepo;
import com.web.repositories.StatusGameRepo;
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
    private final GameRepoService gameRepoService;
    private final StatusGameRepo repoStatusGame;
    private final GameStatusService gameStatusService;
    private final UserService userService;

    @Autowired
    public GameStatusRepoService(GameRepo gameRepo,
                                 GameStatusService gameStatusService,
                                 StatusGameRepo repoStatusGame,
                                 UserService userService,
                                 GameRepoService gameRepoService) {
        this.gameRepo = gameRepo;
        this.gameStatusService = gameStatusService;
        this.repoStatusGame = repoStatusGame;
        this.userService = userService;
        this.gameRepoService = gameRepoService;
    }

    @Transactional
    public boolean saveStatusGame(SavedGame savedGame) {
        return repoStatusGame.save(savedGame) != null;
    }

//    public void saveNewStatusGame(StatusGame statusGame) {
//        repoStatusGame.save(statusGame);
//    }

    public List<Integer> getUnfinishedUserGames() {
        long loginUserId = userService.getUserId();
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
        Long loginUserId = userService.getUserId();
        User logInUser = userService.getLogInUser(loginUserId);
        List<Game> games = logInUser.getGames();

        //na podstawie wszystkiech gier użytkownika, wyznaczam te, które mają status REQUESTING
        List<SavedGame> savedGames = getUnFinishedStatusGames(games);

        if (savedGames.isEmpty()) {
            return new ArrayList<>();
        } else {
            //Zwracam ostatnią grę z listy i wyciągam id tej gry, zwracam w endpointcie
            Game game = savedGames.stream().
                    skip(savedGames.size() - 1)
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

    //zwraca listę gier, które aktualnie są wyświetlane w widoku i mają status Requesting
     List<SavedGame> getUnFinishedStatusGames(List<Game> games) {
        return games.stream()
                .sorted(Comparator.comparing(Game::getDate))
                .filter(game -> gameRepoService.getIdGamesForView().contains(game.getId()))
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
        SavedGame savedGame = getStatusGame(gameId);

        List<Board> boards = savedGame.getGameStatus().getBoardsStatus();
        StateGame state = savedGame.getGameStatus().getState();
        int lastShip;
        if (owner == userId) {
            lastShip = boards.get(0).getShips().size() - 1;
            List<Ship> ships = savedGame.getGameStatus().getBoardsStatus().get(0).getShips();
            ships.remove(lastShip);
        } else {
            lastShip = boards.get(1).getShips().size() - 1;
            savedGame.getGameStatus().getBoardsStatus().get(1).getShips().remove(lastShip);
        }
        int currentPlayer = gameStatusService.getCurrentPlayer(gameId);
        GameStatus gameStatus = new GameStatus(boards, currentPlayer, state);
        repoStatusGame.save(new SavedGame(gameStatus, game));
    }

    @Transactional
    public void deleteGames(Long userId, Long gameId) {
        Game game = gameRepo.findById(gameId).orElseThrow(
                () -> new NoSuchElementException("Can't find game")
        );
        User user = userService.findUserById(userId);
        SavedGame savedGame = getStatusGame(gameId);
        StateGame state = savedGame.getGameStatus().getState();
        List<Board> boards = savedGame.getGameStatus().getBoardsStatus();

        List<Game> games = user.getGames();
        if(games.contains(game)) {
         games.remove(game);
            int currentPlayer = gameStatusService.getCurrentPlayer(gameId);
            GameStatus gameStatus = new GameStatus(boards, currentPlayer, state);
            repoStatusGame.save(new SavedGame(gameStatus, game));
        } else {
            throw new NoSuchElementException("No game");
        }
    }


    public SavedGame getStatusGame(long idGame) {
        Long idStatusGame = repoStatusGame.findMaxIdByGameId(idGame);
        return repoStatusGame.findById(idStatusGame).orElseThrow(() -> new NoSuchElementException("User has not yet added the ship"));
    }


    public SavedGame getSavedStateGame(long userId) {
        Game game = userService.getLastUserGames(userId);
        Long idStatusGame = repoStatusGame.findMaxIdByGameId(game.getId());
        return repoStatusGame.findById(idStatusGame).orElseThrow(() -> new NoSuchElementException("User has not yet added the ship"));
    }

    @Transactional
    public SavedGame updateStatePreperationGame(long userId, String state) {
        SavedGame savedStateGame = getSavedStateGame(userId);
        savedStateGame.getGameStatus().setState(StateGame.valueOf(state));
        return repoStatusGame.save(savedStateGame);
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

//    public List<SavedGame> getSavedStatesGames(List<Game> games) {
//        return games.stream()
//                .sorted(Game::getDate)
//                .map(game -> getStatusGame(game.getId()))
//                .
//        List<SavedGame> result = new ArrayList<>();
//        for (Game game : games) {
//           result.add(getStatusGame(game.getId()));
//        }
//        return result;
//    }
}
