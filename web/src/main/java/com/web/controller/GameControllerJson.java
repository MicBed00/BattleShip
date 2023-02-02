package com.web.controller;

import board.Board;
import board.Shot;
import board.StatePreperationGame;
import com.web.service.GameRepoService;
import com.web.service.GameStatusRepoService;
import com.web.service.GameStatusService;
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
    private final GameStatusService gameStatusService;
    private final GameStatusRepoService gameStatusRepoService;
    private final GameRepoService gameRepoService;
    @Autowired
    public GameControllerJson(GameStatusService gameStatusService,
                              GameStatusRepoService gameStatusRepoService,
                              GameRepoService gameRepoService)
    {
        this.gameStatusService = gameStatusService;
        this.gameStatusRepoService = gameStatusRepoService;
        this.gameRepoService = gameRepoService;
    }


    @PostMapping(value = "/addShip", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> addShiptoList(@RequestBody Ship ship) throws BattleShipException {
        List<Board> boardsList = gameStatusService.chooseBoardPlayer(ship);
        return ResponseEntity.ok( gameStatusRepoService.saveGameStatusToDataBase(boardsList, StatePreperationGame.IN_PROCCESS));
    }

    @GetMapping(value = "/listBoard/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> getListBoard(@PathVariable int userId) {
        return ResponseEntity.ok(gameStatusRepoService.getSavedStateGame(userId).getGameStatus().getBoardsStatus());
    }


    @GetMapping(value = "/game/boards/isFinished/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> getList(@PathVariable int userId) {
        //TODO update endpoint finished muszę dodać do URL parametr userId
        if(gameStatusService.checkIfAllShipsAreHitted())
            gameStatusRepoService.updateStatePreperationGame(userId, "FINISHED");
        return ResponseEntity.ok(gameStatusService.checkIfAllShipsAreHitted());
    }

    @GetMapping(value = "/game/boards/phaseGame/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StatePreperationGame> getPhaseGame(@PathVariable int id) {
//        StatePreperationGame state = gameStatusRepoService.getSavedStateGame(id).getGameStatus().getState();
        return ResponseEntity.ok(gameStatusRepoService.getSavedStateGame(id).getGameStatus().getState());
    }

    @PostMapping(value = "/game/boards", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> addShot(@RequestBody Shot shot) {
        List<Board> boardListAfterShot = gameStatusService.addShotAtShip(shot);
        gameStatusRepoService.saveGameStatusToDataBase(boardListAfterShot, StatePreperationGame.PREPARED);
        return ResponseEntity.ok(boardListAfterShot);
    }

    @PostMapping(value = "/setupBoard/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void setupBoardStatusOnServer(@PathVariable long userId) {
        //TODO czy tak się odtwarza stan po stronie serwera w przypadku wznowienia gry??
        gameStatusService.restoreStatusGameFromDataBase(userId);
    }

    @GetMapping(value = "/game/boards", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> getBoardsShots() {
        return ResponseEntity.ok(gameStatusService.getShips());
    }

    @GetMapping(value = "/setup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> getSetpu() {
        return ResponseEntity.ok(gameStatusService.getShipLimits());
    }

    @GetMapping(value="/lastGame/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> getLastShipId(@PathVariable long userId) {
        return ResponseEntity.ok( gameRepoService.checkIfLastGameExistAndStatusIsSaved(userId));
    }

    @Transactional
    @DeleteMapping(value = "/deleteShip/{userId}/{indexBoard}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> deleteLastShip(@PathVariable int userId, @PathVariable int indexBoard) {
        gameStatusRepoService.deleteLastShip(userId, indexBoard);
        return ResponseEntity.ok(gameStatusRepoService.getSavedStateGame(userId).getGameStatus().getBoardsStatus());
    }

    @PostMapping(value = "/rejected/{userId}/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateStatusGame(@PathVariable String status, @PathVariable int userId) {
        gameStatusRepoService.updateStatePreperationGame(userId, status);
    }

    @PostMapping(value = "/game/save/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> saveNewGame(@PathVariable long userId) {
        return ResponseEntity.ok(gameRepoService.saveNewGame(userId, gameStatusService.getBoardList(), StatePreperationGame.IN_PROCCESS));
    }

}
