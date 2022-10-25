package com.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ship.Ship;

import java.util.List;

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
//        model.addAttribute("ships", gameService.boardPlayer1.getShips());
        return "add_ship";
    }

    @PostMapping(value = "/addShip",
    produces = "application/json")
    @ResponseBody
    public List<Ship> addShiptoList(@ModelAttribute Ship ship) throws Exception{
        System.out.println(ship);
        return gameService.getList(ship);
    }


//
//    @GetMapping(value = "/addShip_success", produces = "application/json")
//    @ResponseBody
//    public List<Ship> shipList() {
//        return gameService.boardPlayer1.getShips();
//    }

    @GetMapping("/addedShip")
    public String boardAfterAddedShip(Model model) {
        model.addAttribute("game", gameService);
        return "addShip_success";
    }



}