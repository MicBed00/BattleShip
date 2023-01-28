package com.web.service;

import com.web.enity.game.StartGame;
import com.web.enity.user.User;
import com.web.repositorium.GameRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class GameRepoService {
    private final GameRepo gameRepo;

    private final UserService userService;
    @Autowired
    public GameRepoService(GameRepo gameRepo, UserService userService) {
        this.gameRepo = gameRepo;
        this.userService = userService;
    }


    public boolean saveNewGame(long userId) {
        User user = userService.getLogInUser(userId);
        StartGame startGame = new StartGame(Timestamp.valueOf(LocalDateTime.now()), user);
        return gameRepo.save(startGame) != null;
    }

    public boolean checkIfLastGameExistAndStatusIsSaved(String userEmail) {
        User user = userService.getUser(userEmail);
        return gameRepo.existsByUserId(user.getId());
    }

}
