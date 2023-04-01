package com.web.services;

import board.StateGame;
import com.web.enity.game.Game;
import com.web.enity.game.SavedGame;
import com.web.enity.user.User;
import com.web.repositories.GameRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import serialization.GameStatus;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class GameRepoService {
    private final GameRepo gameRepo;
//    private final GameStatusRepoService gameStatusRepoService;
    private final UserService userService;
    private final GameStatusService gameStatusService;

    private List<Integer> idGamesForView = new ArrayList<>();
    @Autowired
    public GameRepoService(GameRepo gameRepo,
                           UserService userService,
                           GameStatusService gameStatusService)
    {
        this.gameRepo = gameRepo;
        this.userService = userService;
        this.gameStatusService = gameStatusService;
    }

    public List<Integer> getIdGamesForView() {
        return idGamesForView;
    }

//    @Transactional
//    public void saveNewGame(long userId) {
//        User user = userService.getLogInUser(userId);
//        Game game = new Game(Timestamp.valueOf(LocalDateTime.now()), userId);
//        user.getGames().add(game);
//        game.getUsers().add(user);
//        gameRepo.save(game);
//        gameStatusService.saveNewStatusGame(new GameStatus(), game);
//    }

    public boolean checksUnfinishedGames() {
        return gameStatusService.getUnfinishedUserGames().size() > 0;
    }

//    public List<Integer> getGamesWatingForUser() {
//       return gameRepo.findAll().stream()
//                .filter(game -> game.getUsers().size() == 1)
//                .map(game ->gameStatusRepoService.getStatusGame(game.getId()))
//                .filter(gs -> gs.getGameStatus().getState().equals(StateGame.NEW))
//                .map(SavedGame::getGame)
//                .map(Game::getId)
//                .toList();
//    }

    @Transactional
    public Integer addSecondPlayerToGame(long userId, long gameId) {
        Game game = gameRepo.findById(gameId).orElseThrow(() -> new NoSuchElementException("Game doesn't exist"));
        User user = userService.getLogInUser(userId);
        user.getGames().add(game);
        game.getUsers().add(user);
        gameRepo.save(game);
        return game.getId();
    }

    public Game getGame(long gameId) {
        return gameRepo.findById(gameId).orElseThrow(()-> new NoSuchElementException("Game doesn't exist"));
    }
}
