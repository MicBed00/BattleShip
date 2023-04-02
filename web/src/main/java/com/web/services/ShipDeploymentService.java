package com.web.services;

import board.Board;
import board.StateGame;
import com.web.enity.game.Game;
import com.web.enity.game.SavedGame;
import com.web.repositories.GameRepo;
import com.web.repositories.SavedGameRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import serialization.GameStatus;
import ship.Ship;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ShipDeploymentService {
    private SavedGameService savedGameService;
    private GameRepoService gameRepoService;
    private GameRepo gameRepo;

    private SavedGameRepo savedGameRepo;

    @Autowired
    public ShipDeploymentService(SavedGameService savedGameService,
                                 GameRepoService gameRepoService,
                                 GameRepo gameRepo,
                                 SavedGameRepo savedGameRepo) {
        this.savedGameService = savedGameService;
        this.gameRepoService = gameRepoService;
        this.gameRepo = gameRepo;
        this.savedGameRepo = savedGameRepo;
    }

    @Transactional
    public List<Board> addShipToList(Ship ship, long gameId, long userId) {
        List<Board> boardList = savedGameService.getBoardsList(gameId);
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

    private void addShipToBoard(Board boardPlayer, Ship ship) {
        boardPlayer.addShip(ship.getLength(), ship.getXstart(),
                ship.getYstart(), ship.getPosition());
    }

    public Board getBoard(long gameId, long userId) {
        List<Board> boardList =savedGameService.getBoardsList(gameId);
        Game game = gameRepoService.getGame(gameId);
        Long owner = game.getOwnerGame();

        if (owner == userId) {
            return boardList.get(0);
        } else {
            return boardList.get(1);
        }
    }

    @Transactional
    public void deleteShip(long userId, long gameId) {
        Game game = gameRepo.findById(gameId).orElseThrow(
                () -> new NoSuchElementException("Can't find game")
        );
        Long owner = game.getOwnerGame();
        SavedGame savedGame = savedGameService.getStatusGame(gameId);

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
        int currentPlayer = savedGameService.getCurrentPlayer(gameId);
        GameStatus gameStatus = new GameStatus(boards, currentPlayer, state);
        savedGameRepo.save(new SavedGame(gameStatus, game));
    }

}
