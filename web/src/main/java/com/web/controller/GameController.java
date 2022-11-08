package com.web.controller;

import board.Board;
import board.Shot;
import com.web.WebDriverLibrary;
import com.web.service.GameService;
import com.web.ShipFacade;
import exceptions.BattleShipException;
import org.openqa.selenium.WebDriver;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ship.Ship;

import java.util.List;

@Controller
public class GameController {
//    private WebDriver webDriver;
    private final GameService gameService;

   GameController(GameService gameService) {
       this.gameService = gameService;
//       this.webDriver = webDriver;
//       webDriver.get("http://localhost:8080");
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

    @PostMapping(value = "/addShip", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> addShiptoList(@ModelAttribute Ship ship) throws BattleShipException {
        System.out.println(ship);
    return ResponseEntity.ok(gameService.addShipToList(ship));
    }

    @GetMapping("/added_Ship")
    public String addedShip() {
        return "addShip_success";
    }

    @GetMapping(value = "/game")
    public String getBoards() {
        return "game";
    }

    @GetMapping(value = "/game/boards/isFinished", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> getList() {
        return ResponseEntity.ok(gameService.returnStatusGame());
    }

    @PostMapping(value="/game/boards", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> addShot(@ModelAttribute Shot shot) {
        return ResponseEntity.ok(gameService.addShotAtShip(shot));
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
