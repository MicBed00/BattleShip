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

        return "add_ship";
    }

    @PostMapping("/addShip")
    public String creatNewShip(@ModelAttribute("shipfacade") ShipFacade shipFacade) {
        String[] coorXY = shipFacade.getCoord().split(",");
        int l = Integer.parseInt(shipFacade.getLength());
        int x = Integer.parseInt(coorXY[0]);
        int y = Integer.parseInt(coorXY[1]);
        Position pos = Position.valueOf(shipFacade.getPosition());
        System.out.println(new Ship(l,x,y,pos));
        if(gameService.checkIsOverTheLimitShip(gameService.getSizeQtyShips(0))) {
            gameService.boardPlayer1.addShip(l, x, y, pos);
            if (gameService.checkIsEqualTheLimitShip(gameService.getSizeQtyShips(0))) {
                return "redirect:/addedShip";
            }
                 }else if(gameService.checkIsOverTheLimitShip(gameService.getSizeQtyShips(1))) {
                gameService.boardPlayer2.addShip(l,x,y,pos);
                if(gameService.checkIsEqualTheLimitShip(gameService.getSizeQtyShips(1))) {
                    return "addedShip";
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
        model.addAttribute("ships", gameService.boardPlayer1.getShips());
      //  model.addAttribute("render", gameService);
        return "addShip_success";
    }

}
