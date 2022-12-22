package com.web.controller;

import board.Board;
import board.Shot;
import com.web.service.GameService;
import exceptions.BattleShipException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ship.Ship;

import java.util.List;

@RestController
@RequestMapping("json")
public class GameControllerJson {
    private final GameService gameService;
    @Autowired
   GameControllerJson(GameService gameService) {
       this.gameService = gameService;
    }

    @PostMapping(value = "/addShip", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> addShiptoList(@RequestBody Ship ship) throws BattleShipException {
        System.out.println(ship);
    return ResponseEntity.ok(gameService.addShipToList(ship));
    }

    @GetMapping(value = "/game/boards/isFinished", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> getList() {
        return ResponseEntity.ok(gameService.returnStatusGame());
    }

    @PostMapping(value="/game/boards", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> addShot(@RequestBody Shot shot) {
        return ResponseEntity.ok(gameService.addShotAtShip(shot));
    }

    @GetMapping(value="/game/boards", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> getBoardsShots() {
        return ResponseEntity.ok(gameService.getShips());
    }

    @GetMapping(value = "/setup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> getSetpu() {
        return ResponseEntity.ok(gameService.getShipLimits());
    }

    @DeleteMapping(value= "/deleteShip/{idBoard}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> deleteLastShip(@PathVariable int idBoard) {
       return ResponseEntity.ok(gameService.deleteShip(idBoard));
    }


}
