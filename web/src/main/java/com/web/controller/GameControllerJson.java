package com.web.controller;

import board.Board;
import board.Shot;
import board.StatePreperationGame;
import com.web.service.GameRepoService;
import com.web.service.GameService;
import exceptions.BattleShipException;
import jakarta.transaction.Transactional;
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
    private final GameRepoService repoService;

    @Autowired
    GameControllerJson(GameService gameService, GameRepoService repoService) {
        this.gameService = gameService;
        this.repoService = repoService;
    }

    @PostMapping(value = "/addShip", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> addShiptoList(@RequestBody Ship ship) throws BattleShipException {
        List<Board> boardsList = gameService.chooseBoardPlayer(ship);
        repoService.saveStatusGameToDataBase(boardsList, StatePreperationGame.IN_PROCCESS);

        return ResponseEntity.ok(repoService.getLastIdDataBase());
    }

    @GetMapping(value = "/listBoard/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> getListBoard(@PathVariable int id) {
        return ResponseEntity.ok(repoService.getBoards(id));
    }

    @GetMapping(value = "/game/boards/isFinished", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> getList() {
        if(gameService.checkIfAllShipsAreHitted())
            repoService.updateStatePreperationGame("FINISHED");
        return ResponseEntity.ok(gameService.checkIfAllShipsAreHitted());
    }

    @GetMapping(value = "/game/boards/phaseGame/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StatePreperationGame> getPhaseGame(@PathVariable int id) {
        return ResponseEntity.ok(repoService.getPhaseGame(id));
    }

    @PostMapping(value = "/game/boards", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> addShot(@RequestBody Shot shot) {
        List<Board> boardListAfterShot = gameService.addShotAtShip(shot);
        repoService.saveStatusGameToDataBase(boardListAfterShot, StatePreperationGame.PREPARED);
        return ResponseEntity.ok(boardListAfterShot);
    }

    @PostMapping(value = "/setupBoard/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void setupBoardStatusOnServer(@PathVariable int id) {
        gameService.restoreGameStatusOnServer(id);
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

    @Transactional
    @DeleteMapping(value = "/deleteShip/{idBoard}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> deleteLastShip(@PathVariable int idBoard, @PathVariable int id) {
        repoService.deleteLastShip(id);

        return ResponseEntity.ok(gameService.deleteShip(idBoard));
    }

    @PostMapping(value = "/rejected/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateStatusGame(@PathVariable String status) {
        repoService.updateStatePreperationGame(status);
        //TODO do sprawdzenia ta część
//        return ResponseEntity.ok(repoService.saveNewGame());
    }
    @GetMapping(value = "/newGame", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> newGame() {
        return ResponseEntity.ok(repoService.saveNewGame());
    }


}
