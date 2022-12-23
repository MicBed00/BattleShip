package com.web.controller;

import board.StatePreperationGame;
import com.web.RepoStartGame;
import com.web.StartGame;
import com.web.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.testcontainers.shaded.org.bouncycastle.util.Times;
import serialization.GameStatus;
import serialization.Saver;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequestMapping("view")
public class GameControllerView {
    private final GameService gameService;
    private final RepoStartGame repoStartGame;

    @Autowired
    GameControllerView(GameService gameService, RepoStartGame repoStartGame) {
        this.gameService = gameService;
        this.repoStartGame = repoStartGame;
    }


    @GetMapping(value= "welcomeView")
    public String welcome() {
        return "welcomeView";
    }

    @GetMapping(value = "/startGame")
    public String startGame(Model model) {
        model.addAttribute("shipSize", gameService.getShipSize());
        model.addAttribute("orientList", gameService.getOrientation());
        model.addAttribute("shipLimit", gameService.getShipLimits());
        //TODO stworzyć isntację GameStatus(w serwisie), encji StartGame (id, kolumna typu: jsonB -> do bazy danych)
//        repoStartGame.save(new StartGame());
//        repoStartGame.save(new StartGame(1, Timestamp.valueOf(LocalDateTime.now()),
//                            new GameStatus(gameService.getBoardList(), 1, StatePreperationGame.IN_PROCCESS)));
        return "add_ship";
    }

    @GetMapping("/added_Ship")
    public String addedShip() {
        return "addShip_success";
    }

    @GetMapping(value = "/game")
    public String getBoards() {
        return "game";
    }

    @GetMapping(value="/statistics")
    public String printStatistics(Model model) {
        model.addAttribute("player1numberShots", gameService.statisticsGame(gameService.getBoardList().get(0))[0]);
        model.addAttribute("player1HittedShots", gameService.statisticsGame(gameService.getBoardList().get(0))[1]);
        model.addAttribute("player2numberShots", gameService.statisticsGame(gameService.getBoardList().get(1))[0]);
        model.addAttribute("player2HittedShots", gameService.statisticsGame(gameService.getBoardList().get(1))[1]);
        double accuracyPly1 = gameService.getAccuracyShot(gameService.statisticsGame(gameService.getBoardList().get(0))[0],
                gameService.statisticsGame(gameService.getBoardList().get(0))[1]);
        double accuracyPly2 = gameService.getAccuracyShot(gameService.statisticsGame(gameService.getBoardList().get(1))[0],
                gameService.statisticsGame(gameService.getBoardList().get(1))[1]);
        model.addAttribute("accuracyPly1", accuracyPly1);
        model.addAttribute("accuracyPly2", accuracyPly2);
        return "gameOver";
    }


}
