package com.web.services;

import board.StateGame;
import com.web.enity.game.Game;
import com.web.enity.game.SavedGame;
import com.web.enity.user.User;
import com.web.repositories.GameRepo;
import com.web.repositories.StatusGameRepo;
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

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class GameStatusService {
    private List<String> shipSize;
    private List<Position> positionList;
    private GameStatusRepoService gameStatusRepoService;
    private GameRepoService gameRepoService;

    private GameRepo gameRepo;
    private StatusGameRepo repoStatusGame;

    private UserService userService;


    @Autowired
    GameStatusService(@Lazy GameStatusRepoService gameStatusRepoService,
                      @Lazy GameRepoService gameRepoService,
                      UserService userService,
                      GameRepo gameRepo,
                      StatusGameRepo repoStatusGame) {

        this.gameRepo = gameRepo;
        this.repoStatusGame = repoStatusGame;
        this.gameRepoService = gameRepoService;
        this.gameStatusRepoService = gameStatusRepoService;
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

    @Transactional
    public List<Board> addShipToList(Ship ship, long gameId, long userId) {
        List<Board> boardList = getBoardList(gameId);
        Game game = gameRepoService.getGame(gameId);
        Long idOwner = game.getOwnerGame();

        if (ship.getLength() > 0 && ship.getPosition() != null) {
            if (idOwner == userId) {
                addShipToBoard(boardList.get(0), ship);
            } else {
                addShipToBoard(boardList.get(1), ship);
            }
        }
        return boardList;
    }

    public List<Board> getBoardList(long gameId) {
        return gameStatusRepoService.getStatusGame(gameId).getGameStatus().getBoardsStatus();
    }

    public Board getBoard(long gameId, long userId) {
        List<Board> boardList = getBoardList(gameId);
        Game game = gameRepoService.getGame(gameId);
        Long owner = game.getOwnerGame();

        if (owner == userId) {
            return boardList.get(0);
        } else {
            return boardList.get(1);
        }
    }

    private void addShipToBoard(Board boardPlayer, Ship ship) {
        boardPlayer.addShip(ship.getLength(), ship.getXstart(),
                ship.getYstart(), ship.getPosition());
    }

    public List<Board> addShotAtShip(Shot shot, long gameId) {
        List<Board> boardList = getBoardList(gameId);

        if (boardList.get(0).getOpponentShots().size() == boardList.get(1).getOpponentShots().size()) {
            boardList.get(1).shoot(shot);
        } else {
            boardList.get(0).shoot(shot);
        }
        return boardList;
    }

    public Boolean checkIfAllShipsAreHitted(long gameId) {
        List<Board> boardList = getBoardList(gameId);
        return boardList.get(0).getIsFinished().get() || boardList.get(1).getIsFinished().get();
    }

    public int[] statisticsGame(Board board) {
        return board.statisticsShot();
    }

    public double getAccuracyShot(int totalShots, int hitShot) {
        return ((double) hitShot / totalShots) * 100;

    }

    public int getCurrentPlayer(long gameId) {
        List<Board> boardList = getBoardList(gameId);
        if (checkIsOverTheLimitShip(boardList.get(0).getShips().size())) {
            return 1;
        } else if (checkIsOverTheLimitShip(boardList.get(1).getShips().size())) {
            return 2;
        }
        //TODO w tym miejscu wyrzucić wyjątek zamist tego zera
        return 0;
    }

    public List<String> checkIfOpponentAppears(long userId) {
        List<String> answer = new ArrayList<>();
        SavedGame savedStateGame = gameStatusRepoService.getSavedStateGame(userId);
        Game lastUserGames = userService.getLastUserGames(userId);
        if (savedStateGame.getGameStatus().getState().equals(StateGame.REQUESTING)) {
            answer.add("true");
            answer.add(String.valueOf(lastUserGames.getId()));
        } else {
            answer.add("false");
        }
        return answer;
    }

    @Transactional
    public boolean saveGameStatusToDataBase(List<Board> boardsList, StateGame state, long gameId) {
        int currentPlayer = getCurrentPlayer(gameId);
        GameStatus gameStatus = new GameStatus(boardsList, currentPlayer, state);
        Game game = gameRepo.findById(gameId).orElseThrow(
                () -> new NoSuchElementException("Brak gry w bazie")
        );
        SavedGame savedGame = new SavedGame(gameStatus, game);

        return gameStatusRepoService.saveStatusGame(savedGame);
    }

    @Transactional
    public void saveNewStatusGame(GameStatus gameStatus, Game game) {
        SavedGame savedGame = new SavedGame(gameStatus, game);
        repoStatusGame.save(savedGame);
    }

    @Transactional
    public void saveNewGame(long userId) {
        User user = userService.getLogInUser(userId);
        Game game = new Game(Timestamp.valueOf(LocalDateTime.now()), userId);
        user.getGames().add(game);
        game.getUsers().add(user);
        //TODO czy w tym miejscu zapisywac z wykorzystaniem repo czy lepiej przez jakiś serwis?
        gameRepo.save(game);
        saveNewStatusGame(new GameStatus(), game);
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







}
