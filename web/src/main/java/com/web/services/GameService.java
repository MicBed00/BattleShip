package com.web.services;

import com.web.gameSetups.GameSetups;
import com.web.enity.game.Game;
import com.web.repositories.GameRepo;
import dataConfig.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class GameService {
    private final GameRepo gameRepo;
    private final UserService userService;
    private final SavedGameService savedGameService;
    private List<Integer> idGamesForView = new ArrayList<>();
    @Autowired
    public GameService(GameRepo gameRepo,
                       UserService userService,
                       SavedGameService savedGameService)
    {
        this.gameRepo = gameRepo;
        this.userService = userService;
        this.savedGameService = savedGameService;
    }

    public List<Integer> getIdGamesForView() {
        return idGamesForView;
    }
    public boolean checksUnfinishedGames() {
        return savedGameService.getUnfinishedUserGames().size() > 0;
    }
    public Game getGame(long gameId) {
        return gameRepo.findById(gameId).orElseThrow(()-> new NoSuchElementException("Game doesn't exist"));
    }
    public void addGame(Game game) {
        getIdGamesForView().add(game.getId());
    }

    public GameSetups createGameSetups(List<Integer> shipSize, List<Position> orientations, int shipLimit) {
        return new GameSetups(shipSize, orientations, shipLimit);
    }

    public Game createGame(long userId, GameSetups gameSetups) {
        return new Game(Timestamp.valueOf(LocalDateTime.now()), userId, gameSetups);
    }

    public Game saveGame(Game game) {
        return gameRepo.save(game);
    }

    public int getShipLimitNumber(long gameId) {
        Game game = gameRepo.findById(gameId).orElseGet(() -> {
            throw new NoSuchElementException("No game");
        });
    return game.getGameSetups().getShipLimit();
    }
}
