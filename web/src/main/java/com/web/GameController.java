package com.web;

import DataConfig.Position;
import DataConfig.ShipLimits;
import board.Board;
import board.SizeBoard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestHandler;
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

    @PostMapping("/addShip")
    public String creatNewShip(@ModelAttribute Ship ship) {
        System.out.println(ship);
        if(gameService.checkIsOverTheLimitShip(gameService.getSizeQtyShips(0))) {
            gameService.boardPlayer1.addShip(ship.getLength(), ship.getXstart(),
                                            ship.getYstart(), ship.getPosition());
//            if (gameService.checkIsEqualTheLimitShip(gameService.getSizeQtyShips(0))) {
//                return "redirect:/addedShip";
//
//           }
        }else if(gameService.checkIsOverTheLimitShip(gameService.getSizeQtyShips(1))) {
                gameService.boardPlayer2.addShip(ship.getLength(), ship.getXstart(),
                                                ship.getYstart(), ship.getPosition());
                if(gameService.checkIsEqualTheLimitShip(gameService.getSizeQtyShips(1))) {
                    return "redirect:/addedShip";
                }
        }
        return "redirect:/startGame";
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
