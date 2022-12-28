package com.web.controller;

import board.Board;
import board.Shot;
import board.StatePreperationGame;
import com.web.service.GameService;
import com.web.service.StartGameRepoService;
import exceptions.BattleShipException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import serialization.Saver;
import ship.Ship;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("json")
public class GameControllerJson {
    private final GameService gameService;
    private final StartGameRepoService repoService;

    @Autowired
    GameControllerJson(GameService gameService, StartGameRepoService repoService) {
        this.gameService = gameService;
        this.repoService = repoService;
    }

    @PostMapping(value = "/addShip", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> addShiptoList(@RequestBody Ship ship) throws BattleShipException {
        List<Board> boardsList = gameService.assigneBoardPlayer(ship);
        repoService.saveStatusGameToDataBase(boardsList);
        return ResponseEntity.ok(boardsList);
    }


    @GetMapping(value = "/game/boards/isFinished", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> getList() {
        return ResponseEntity.ok(gameService.returnStatusGame());
    }

    @PostMapping(value = "/game/boards", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> addShot(@RequestBody Shot shot) {
        return ResponseEntity.ok(gameService.addShotAtShip(shot));
    }

    @GetMapping(value = "/game/boards", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> getBoardsShots() {
        return ResponseEntity.ok(gameService.getShips());
    }

    @GetMapping(value = "/setup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> getSetpu() {
        return ResponseEntity.ok(gameService.getShipLimits());
    }

    @DeleteMapping(value = "/deleteShip/{idBoard}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> deleteLastShip(@PathVariable int idBoard) {

        return ResponseEntity.ok(gameService.deleteShip(idBoard));
    }


}
