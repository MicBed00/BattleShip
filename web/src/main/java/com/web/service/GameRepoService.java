package com.web.service;

import com.web.enity.game.StartGame;
import com.web.enity.user.User;
import com.web.repositorium.GameRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class GameRepoService {
    private final GameRepo gameRepo;

    private final UserService userService;
    @Autowired
    public GameRepoService(GameRepo gameRepo, UserService userService) {
        this.gameRepo = gameRepo;
        this.userService = userService;
    }


    public void saveNewGame() {
        User user = userService.getLogInUser();
        StartGame startGame = new StartGame(Timestamp.valueOf(LocalDateTime.now()), user);

        gameRepo.save(startGame);
    }

    public boolean checkIfLastGameExist(String userEmail) {
        User user = userService.getUser(userEmail);
        Optional<StartGame> byUserId = gameRepo.findByUserId(user.getId());
        return gameRepo.findByUserId(user.getId()) != null;
    }

}
