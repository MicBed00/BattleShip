package com.web.controller;

import board.StatePreperationGame;
import com.web.repositorium.RepoStartGame;
import com.web.service.GameService;
import com.web.service.StartGameRepoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("view")
public class GameControllerView {
    private final GameService gameService;
    private final StartGameRepoService repoService;

    @Autowired
    GameControllerView(GameService gameService, StartGameRepoService repoService) {
        this.gameService = gameService;
        this.repoService = repoService;
    }


    @GetMapping(value= "welcomeView")
    public String welcome() {
        return "welcomeView";
    }

    @GetMapping(value = "/startGame")
    public String startGame() {
//        repoService.saveStatusGameToDataBase(gameService.getBoardList(), StatePreperationGame.IN_PROCCESS);
        return "redirect:/view/getParamGame";
    }

    @GetMapping(value = "/getParamGame")
    public String getParametersGame(Model model) {
        model.addAttribute("shipSize", gameService.getShipSize());
        model.addAttribute("orientList", gameService.getOrientation());
        model.addAttribute("shipLimit", gameService.getShipLimits());

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
