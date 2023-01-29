package com.web.service;

import board.Board;
import board.StatePreperationGame;
import com.web.enity.game.StartGame;
import com.web.enity.user.User;
import com.web.repositorium.GameRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

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


    public boolean saveNewGame(long userId, List<Board> boardList, StatePreperationGame stateGame) {
        User user = userService.getLogInUser(userId);
        StartGame startGame = new StartGame(Timestamp.valueOf(LocalDateTime.now()), user);
        gameRepo.save(startGame);
        return  gameRepo.save(startGame) != null && gameStatusRepoService.saveGameStatusToDataBase(boardList, stateGame);
    }

    public boolean checkIfLastGameExistAndStatusIsSaved(String userEmail) {
        User user = userService.getUser(userEmail);
        return gameRepo.existsByUserId(user.getId());
    }

}
