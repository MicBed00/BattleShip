package com.web.controller;

import board.Board;
import board.Shot;
import com.web.service.GameService;
import com.web.service.StartGameRepoService;
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
    private final StartGameRepoService repoService;

    @Autowired
    GameControllerJson(GameService gameService, StartGameRepoService repoService) {
        this.gameService = gameService;
        this.repoService = repoService;
    }

    @PostMapping(value = "/addShip", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> addShiptoList(@RequestBody Ship ship) throws BattleShipException {
        //TODO pobieranie z bazy statusu gry na podstawie ostatniego id(id wyznaczać w servisie czy przesyłane w request??)
        // dostanę entity z któego wyodrębnie informację typu lista statków itp

        List<Board> boardsList = gameService.chooseBoardPlayer(ship);
        repoService.saveStatusGameToDataBase(boardsList);

        return ResponseEntity.ok(repoService.getLastIdDataBase());
    }

//    /json/listBoard
    @GetMapping(value = "/listBoard/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> getListBoard(@PathVariable int id) {
        return ResponseEntity.ok(repoService.getListBoard(id));
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

    @GetMapping(value="/shipId", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> getLastShipId() {
        return ResponseEntity.ok(repoService.getLastIdDataBase());
    }

    @DeleteMapping(value = "/deleteShip/{idBoard}/{idShip}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> deleteLastShip(@PathVariable int idBoard, @PathVariable int idShip) {
        repoService.deleteLastShip(idShip);

        return ResponseEntity.ok(gameService.deleteShip(idBoard));
    }


}
