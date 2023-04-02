package com.web.services;

import com.web.enity.game.Game;
import com.web.enity.user.User;
import com.web.repositories.GameRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class GameRepoService {
    private final GameRepo gameRepo;
    private final UserService userService;
    private final SavedGameService savedGameService;

    private List<Integer> idGamesForView = new ArrayList<>();
    @Autowired
    public GameRepoService(GameRepo gameRepo,
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
}
