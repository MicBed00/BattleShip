package com.web.controller;

import board.Board;
import board.Shot;
import com.web.service.GameService;
import com.web.ShipFacade;
import exceptions.BattleShipException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ship.Ship;

import java.util.List;
import java.util.Set;

@Controller
public class GameController {
    @Autowired
    GameService gameService;

    @GetMapping(value = "/startGame")
    public String startGame(Model model) {
        ShipFacade shipFacade = new ShipFacade();
        model.addAttribute("ship", shipFacade);
        model.addAttribute("shipSize", gameService.getShipSize());
        model.addAttribute("orientList", gameService.getOrientation());
        model.addAttribute("shipLimit", gameService.getShipLimits());

        return "add_ship";
    }

    @PostMapping(value = "/addShip", produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody // nie musimy używać tej adnotacji bo ResponseEntity zwraca jsona. Gdybym chciał zwrócić np pojo wtedy muszę użyć adnotacji @ResponseBody by zwracać josna
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
        return ResponseEntity.ok(gameService.addShotatShip(shot));
    }




}
