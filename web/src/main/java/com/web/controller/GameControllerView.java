package com.web.controller;

import com.web.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("view")
public class GameControllerView {
    private final GameService gameService;

    @Autowired
    GameControllerView(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping(value="welcomeView")
    public String welcome() {
        return "welcomeView";
    }

    @GetMapping(value = "/startGame")
    public String startGame(Model model) {
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
        return "/statisticsGame";
    }


}
