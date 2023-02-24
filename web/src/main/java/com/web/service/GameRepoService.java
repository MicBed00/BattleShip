package com.web.service;

import com.web.enity.game.StartGame;
import com.web.enity.user.User;
import com.web.repositorium.GameRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public boolean saveNewGame(long userId) {
        User user = userService.getLogInUser(userId);
        StartGame startGame = new StartGame(Timestamp.valueOf(LocalDateTime.now()));
        user.getGames().add(startGame);
        //TODO tu był problem bo zapisywałem drugi raz obiekt User wykorzystywny w transakcji??
        //userService.saveUser(user); //zapis usera po dodaniu do jego Setu gry
        startGame.getUsers().add(user);
//        return  gameRepo.save(startGame) != null && gameStatusRepoService.saveGameStatusToDataBase(boardList, stateGame);
        return  gameRepo.save(startGame) != null;
    }

    public boolean checkIfLastGameExistAndStatusIsSaved(long userId) {
        return !userService.getLogInUser(userId).getGames().isEmpty();
    }

    public List<Integer> getGamesWatingForUser() {
       return gameRepo.findAll().stream()
                .filter(game -> game.getUsers().size() == 1)
                .map(StartGame::getId)
                .toList();
    }
    @Transactional
    public boolean addSecondPlayerToGame(long userId, long gameId) {
        StartGame startGame = gameRepo.findById(gameId).orElseThrow(() -> new NoSuchElementException("Game doesn't exist"));
        User logInUser = userService.getLogInUser(userId);
        logInUser.getGames().add(startGame);
        startGame.getUsers().add(logInUser);

        return gameRepo.save(startGame) != null;
    }
}
