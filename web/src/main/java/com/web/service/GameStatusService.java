package com.web.service;

import board.StateGame;
import com.web.enity.game.Game;
import com.web.enity.game.StatusGame;
import com.web.enity.user.User;
import dataConfig.Position;
import dataConfig.ShipLimits;
import board.Board;
import board.Shot;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ship.Ship;

import java.util.*;

@Service
public class GameStatusService {
    private List<String> shipSize;
    private List<Position> positionList;
    private GameStatusRepoService gameStatusRepoService;
    private GameRepoService gameRepoService;

    private UserService userService;


    @Autowired
    GameStatusService(@Lazy GameStatusRepoService gameStatusRepoService,
                      @Lazy GameRepoService gameRepoService,
                      UserService userService) {
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
        Long idOwner = game.getIdOwner();

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
        Long owner = game.getIdOwner();

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
        //TODO tu wyciągnąc wszystkie gry usera i sprawdzić czy ma przeciwnika
        List<String> answer = new ArrayList<>();
        StatusGame savedStateGame = gameStatusRepoService.getSavedStateGame(userId);
        Game lastUserGames = userService.getLastUserGames(userId);
        if (savedStateGame.getGameStatus().getState().equals(StateGame.REQUESTING)) {
            answer.add("true");
            answer.add(String.valueOf(lastUserGames.getId()));
        } else {
            answer.add("false");
        }
        return answer;
    }


}
