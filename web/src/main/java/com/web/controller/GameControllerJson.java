package com.web.controller;

import board.Board;
import board.Shot;
import board.StateGame;
import com.web.enity.game.Game;
import com.web.enity.game.StatusGame;
import com.web.enity.user.User;
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

    @PostMapping(value = "/addShip/{userId}/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> addShiptoList(@RequestBody Ship ship, @PathVariable long userId, @PathVariable long gameId) throws BattleShipException {
        List<Board> boardsList = gameStatusService.addShipToList(ship, gameId, userId);
        return ResponseEntity.ok( gameStatusRepoService.saveGameStatusToDataBase(boardsList, StateGame.IN_PROCCESS, gameId));
    }

    @PostMapping(value = "/addSecondPlayer/{userId}/{gameId}")
    public ResponseEntity<Long> addSecondPlayerToGame(@PathVariable long userId,
                                                      @PathVariable long gameId) {
        gameRepoService.addSecondPlayerToGame(userId, gameId);
        return ResponseEntity.ok(gameId);
    }
    @GetMapping(value = "/listBoard/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> getListBoard(@PathVariable long gameId) {
        return ResponseEntity.ok(gameStatusService.getBoardList(gameId));
    }

    @GetMapping(value = "/board/{gameId}/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Board> getListBoard(@PathVariable long gameId, @PathVariable long userId) {
        return ResponseEntity.ok(gameStatusService.getBoard(gameId, userId));
    }
    @GetMapping(value = "/opponent/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> checkIfOpponentAppears(@PathVariable long userId) {
        return ResponseEntity.ok(gameStatusService.checkIfOpponentAppears(userId));
    }
    @GetMapping(value = "/approve/{userId}/{state}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> approveOpponent(@PathVariable long userId, @PathVariable String state) {
        StatusGame statusGame = gameStatusRepoService.updateStatePreperationGame(userId, state);
        return ResponseEntity.ok((long)statusGame.getGame().getId());
    }

    @GetMapping(value = "/game/boards/isFinished/{userId}/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> getList(@PathVariable int userId, @PathVariable long gameId) {
        if(gameStatusService.checkIfAllShipsAreHitted(gameId))
            gameStatusRepoService.updateStatePreperationGame(userId, "FINISHED");
        return ResponseEntity.ok(gameStatusService.checkIfAllShipsAreHitted(gameId));
    }

    @GetMapping(value = "/game/boards/phaseGame/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StateGame> getPhaseGame(@PathVariable int userId) {
        return ResponseEntity.ok(gameStatusRepoService.getSavedStateGame(userId).getGameStatus().getState());
    }
    @GetMapping(value = "/statusGame/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StateGame> getPhaseGame(@PathVariable long gameId) {
        StatusGame statusGame = gameStatusRepoService.getStatusGame(gameId);
        return ResponseEntity.ok(statusGame.getGameStatus().getState());
    }


    @GetMapping(value = "/request/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> requestToJoinGame( @PathVariable long gameId) {
        Game game = gameRepoService.getGame(gameId);
        User user = game.getUsers().stream().toList().get(0);
        gameStatusRepoService.updateStatePreperationGame(user.getId(), "REQUESTING");
        return ResponseEntity.ok(gameId);
    }

    @PostMapping(value = "/game/boards/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> addShot(@RequestBody Shot shot, @PathVariable long gameId) {
        List<Board> boardListAfterShot = gameStatusService.addShotAtShip(shot, gameId);
        gameStatusRepoService.saveGameStatusToDataBase(boardListAfterShot, StateGame.PREPARED, gameId);
        return ResponseEntity.ok(boardListAfterShot);
    }

    @GetMapping(value = "/game/boards/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> getBoardsShots(@PathVariable long gameId) {
        return ResponseEntity.ok(gameStatusService.getBoardList(gameId));
    }

    @GetMapping(value = "/setup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> getSetup() {
        return ResponseEntity.ok(gameStatusService.getShipLimits());
    }

    @GetMapping(value="/lastGame/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> getLastShipId(@PathVariable long userId) {
        return ResponseEntity.ok( gameRepoService.checkIfLastGameExistAndStatusIsSaved(userId));
    }

    @Transactional
    @DeleteMapping(value = "/deleteShip/{userId}/{gameId}/{indexBoard}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> deleteLastShip(@PathVariable int userId,
                                                      @PathVariable long gameId,
                                                      @PathVariable int indexBoard) {
        gameStatusRepoService.deleteLastShip(indexBoard, gameId);
        return ResponseEntity.ok(gameStatusRepoService.getSavedStateGame(userId).getGameStatus().getBoardsStatus());
    }

    @PostMapping(value = "/rejected/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateStatusGame(@RequestBody String status, @PathVariable long userId) {
        gameStatusRepoService.updateStatePreperationGame(userId, status);
    }

    @PostMapping(value = "/game/save/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> saveNewGame(@PathVariable long userId) {
        gameRepoService.saveNewGame(userId);
        //TODO zmiana wartości zwracanej przez responseentity
        return ResponseEntity.ok(true);
    }

}
