package com.web.services;

import com.web.enity.game.Game;
import com.web.enity.user.User;
import com.web.repositories.GameRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import serialization.GameStatus;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class GameRepoService {
    private final GameRepo gameRepo;
    private final GameStatusRepoService gameStatusRepoService;
    private final UserService userService;
    @Autowired
    public GameRepoService(GameRepo gameRepo,
                           UserService userService,
                           GameStatusRepoService gameStatusRepoService
                           ) {
        this.gameRepo = gameRepo;
        this.userService = userService;
        this.gameStatusRepoService = gameStatusRepoService;
    }



    @Transactional
    public void saveNewGame(long userId) {
        User user = userService.getLogInUser(userId);
        Game game = new Game(Timestamp.valueOf(LocalDateTime.now()), userId);
        user.getGames().add(game);
        game.getUsers().add(user);
        gameRepo.save(game);
        gameStatusRepoService.saveNewStatusGame(new GameStatus(), game);
    }

    public boolean checksUnfinishedGames(long userId) {
        return gameStatusSe.getUnfinishedUserGames().size() > 0;
    }

    public List<Integer> getGamesWatingForUser() {
       return gameRepo.findAll().stream()
                .filter(game -> game.getUsers().size() == 1)
                .map(Game::getId)
                .toList();
    }
    @Transactional
    public Integer addSecondPlayerToGame(long userId, long gameId) {
        Game game = gameRepo.findById(gameId).orElseThrow(() -> new NoSuchElementException("Game doesn't exist"));
        User logInUser = userService.getLogInUser(userId);
        logInUser.getGames().add(game);
        game.getUsers().add(logInUser);
        gameRepo.save(game);
        return game.getId();
    }

    public Game getGame(long gameId) {
        return gameRepo.findById(gameId).orElseThrow(()-> new NoSuchElementException("Game doesn't exist"));
    }
}
