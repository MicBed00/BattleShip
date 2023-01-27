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
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import ship.Ship;

import java.util.List;

@RestController
@RequestMapping("json")
public class GameControllerJson {
    private final GameStatusService gameStatusService;
    private final GameStatusRepoService gameStatusRep;

    private final GameRepoService gameRepoService;
    @Autowired
    public GameControllerJson(GameStatusService gameStatusService,
                              GameStatusRepoService gameStatusRep,
                              GameRepoService gameRepoService)
    {
        this.gameStatusService = gameStatusService;
        this.gameStatusRep = gameStatusRep;
        this.gameRepoService = gameRepoService;
    }


    @PostMapping(value = "/addShip", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> addShiptoList(@RequestBody Ship ship) throws BattleShipException {
        List<Board> boardsList = gameStatusService.chooseBoardPlayer(ship);
        gameStatusRep.saveGameStatusToDataBase(boardsList, StatePreperationGame.IN_PROCCESS);

        return ResponseEntity.ok(gameStatusRep.getLastIdDataBase());
    }

    @GetMapping(value = "/listBoard/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> getListBoard(@PathVariable int id) {
        //TODO tutaj trzeba zawócić game status i edytować metodę getBoards()
        // userId-> tabela games (ostatnia gra Usera) -> gameId -> tabela games_statuses
        return ResponseEntity.ok(gameStatusRep.getSavedStateGame(id).getGameStatus().getBoardsStatus());
    }

    @GetMapping(value = "/game/boards/isFinished/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> getList(@PathVariable int userId) {
        //TODO update endpoint finished muszę dodać do URL parametr userId
        if(gameStatusService.checkIfAllShipsAreHitted())
            gameStatusRep.updateStatePreperationGame(userId, "FINISHED");
        return ResponseEntity.ok(gameStatusService.checkIfAllShipsAreHitted());
    }

    @GetMapping(value = "/game/boards/phaseGame/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StatePreperationGame> getPhaseGame(@PathVariable int id) {
        return ResponseEntity.ok(gameStatusRep.getSavedStateGame(id).getGameStatus().getState());
    }

    @PostMapping(value = "/game/boards", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> addShot(@RequestBody Shot shot) {
        List<Board> boardListAfterShot = gameStatusService.addShotAtShip(shot);
        gameStatusRep.saveGameStatusToDataBase(boardListAfterShot, StatePreperationGame.PREPARED);
        return ResponseEntity.ok(boardListAfterShot);
    }

    @PostMapping(value = "/setupBoard/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void setupBoardStatusOnServer(@PathVariable int userId) {
        //TODO czy tak się odtwarza stan po stronie serwera w przypadku wznowienia gry??
        gameStatusService.restoreGameStatusOnServer(userId);
    }

    @GetMapping(value = "/game/boards", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> getBoardsShots() {
        return ResponseEntity.ok(gameStatusService.getShips());
    }

    @GetMapping(value = "/setup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> getSetpu() {
        return ResponseEntity.ok(gameStatusService.getShipLimits());
    }

    @GetMapping(value="/lastGame", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> getLastShipId(@CurrentSecurityContext SecurityContext securityContext) {
        String userEmail = securityContext.getAuthentication().getName();
        return ResponseEntity.ok(  gameRepoService.checkIfLastGameExist(userEmail));
    }

    @Transactional
    @DeleteMapping(value = "/deleteShip/{idBoard}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> deleteLastShip(@PathVariable int idBoard, @PathVariable int id) {
        gameStatusRep.deleteLastShip(id);
        //TODO poprawa logiki usuwania statków z tablicy
        return ResponseEntity.ok(gameStatusService.deleteShip(idBoard));
    }

    @PostMapping(value = "/rejected/{userId}/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateStatusGame(@PathVariable String status, @PathVariable int userId) {
        //TODO update endpoint upadateStatusGame
        gameStatusRep.updateStatePreperationGame(userId, status);
        //TODO do sprawdzenia ta część
    }
    @GetMapping(value = "/newGame", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> newGame() {
        return ResponseEntity.ok(gameStatusRep.saveGameStatusToDataBase(gameStatusService.getBoardList(), StatePreperationGame.IN_PROCCESS));
    }


}
