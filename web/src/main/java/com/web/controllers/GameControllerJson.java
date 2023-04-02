package com.web.controllers;

import board.Board;
import board.Shot;
import board.StateGame;
import com.web.enity.game.Game;
import com.web.enity.game.SavedGame;
import com.web.enity.user.User;
import com.web.services.*;
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
    private final SavedGameService savedGameService;
    private final GameRepoService gameRepoService;
    private final WaitingRoomService waitingRoomService;
    private final ShipDeploymentService shipDeploymentService;
    private final ShootingService shootingService;
    @Autowired
    public GameControllerJson(SavedGameService savedGameService,
                              GameRepoService gameRepoService,
                              WaitingRoomService waitingRoomService,
                              ShipDeploymentService shipDeploymentService,
                              ShootingService shootingService)
    {
        this.savedGameService = savedGameService;
        this.gameRepoService = gameRepoService;
        this.waitingRoomService = waitingRoomService;
        this.shipDeploymentService = shipDeploymentService;
        this.shootingService = shootingService;
    }

    @PostMapping(value = "/game/save/{userId}/{sizeBoard}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Integer> saveNewGame(@PathVariable long userId,
                                        @PathVariable int sizeBoard) {
        return ResponseEntity.ok(waitingRoomService.saveNewGame(userId, sizeBoard));
    }

    @GetMapping(value = "/check-state", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Long>> statusesGame() {
        return ResponseEntity.ok(waitingRoomService.checkIfOwnGameStatusHasChanged());
    }

    @GetMapping(value = "/opponent/{idGame}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> checkIfOpponentAppears(@PathVariable long idGame) {
        return ResponseEntity.ok(waitingRoomService.checkIfOpponentAppears(idGame));
    }

    @GetMapping(value = "/change-state/{userId}/{state}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> changeState(@PathVariable long userId, @PathVariable String state) {
        SavedGame savedGame = savedGameService.updateStatePreperationGame(userId, state);
        return ResponseEntity.ok((long) savedGame.getGame().getId());
    }

    @GetMapping(value = "/request/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> requestToJoinGame( @PathVariable long gameId) {
        Game game = gameRepoService.getGame(gameId);
        User user = game.getUsers().stream().toList().get(0);
        savedGameService.updateStatePreperationGame(user.getId(), "REQUESTING");
        return ResponseEntity.ok(gameId);
    }

    @PostMapping(value = "/addSecondPlayer/{userId}/{gameId}")
    public ResponseEntity<Integer> addSecondPlayerToGame(@PathVariable long userId,
                                                         @PathVariable long gameId) {
        waitingRoomService.addSecondPlayerToGame(userId, gameId);
        return ResponseEntity.ok(savedGameService.getStatusGame(gameId).getGameStatus().getBoardsStatus().get(0).getSizeBoard());
    }

    @PostMapping(value = "/addShip/{userId}/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> addShip(@RequestBody Ship ship, @PathVariable long userId, @PathVariable long gameId) throws BattleShipException {
        List<Board> boardsList =shipDeploymentService.addShipToList(ship, gameId, userId);
        return ResponseEntity.ok( savedGameService.saveGameStatusToDataBase(boardsList, StateGame.IN_PROCCESS, gameId));
    }


    @GetMapping(value = "/listBoard/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> listBoards(@PathVariable long gameId) {
        return ResponseEntity.ok(savedGameService.getBoardsList(gameId));
    }
    @GetMapping(value = "/owner/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> usersId(@PathVariable long gameId) {
        return ResponseEntity.ok(gameRepoService.getGame(gameId).getOwnerGame());
    }

    @GetMapping(value = "/games/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Game> games(@PathVariable long gameId) {
        return ResponseEntity.ok(gameRepoService.getGame(gameId));
    }

    @GetMapping(value = "/board/{gameId}/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Board> listBoards(@PathVariable long gameId, @PathVariable long userId) {
        return ResponseEntity.ok(shipDeploymentService.getBoard(gameId, userId));
    }



    @GetMapping(value = "/game/status-isFinished/{userId}/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> statusFinished(@PathVariable int userId, @PathVariable long gameId) {
        boolean isFinished = shootingService.checkIfAllShipsAreHitted(gameId);
        if(isFinished)
            savedGameService.updateStatePreperationGame(userId, "FINISHED");
        return ResponseEntity.ok(isFinished);
    }
    @GetMapping(value = "/status-game/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StateGame> statusesGame(@PathVariable long gameId) {
        SavedGame savedGame = savedGameService.getStatusGame(gameId);
        return ResponseEntity.ok(savedGame.getGameStatus().getState());
    }

    @PostMapping(value = "/game/boards/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> shots(@RequestBody Shot shot, @PathVariable long gameId) {
        List<Board> boardListAfterShot = shootingService.addShotAtShip(shot, gameId);
        savedGameService.saveGameStatusToDataBase(boardListAfterShot, StateGame.PREPARED, gameId);
        return ResponseEntity.ok(boardListAfterShot);
    }

    @GetMapping(value = "/game/boards/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> getBoardsShots(@PathVariable long gameId) {
        return ResponseEntity.ok(savedGameService.getBoardsList(gameId));
    }

    @GetMapping(value = "/setup", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> getSetup() {
        return ResponseEntity.ok(savedGameService.getShipLimits());
    }

    @GetMapping(value="/checkGames/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> checkUnfinishedGames(@PathVariable long userId) {
        return ResponseEntity.ok( gameRepoService.checksUnfinishedGames());
    }

    @GetMapping(value="/resume-game/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> unfinishedGames(@PathVariable long gameId) {
        return ResponseEntity.ok(savedGameService.getBoardsList(gameId));
    }
    @DeleteMapping(value = "/deleteShip/{userId}/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Board> deleteShips(@PathVariable long userId, @PathVariable long gameId) {
        shipDeploymentService.deleteShip(userId, gameId);
        return ResponseEntity.ok(shipDeploymentService.getBoard(gameId, userId));
    }
    @DeleteMapping(value = "/delete-game/{userId}/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteGames(@PathVariable long userId, @PathVariable long gameId) {
        waitingRoomService.deleteGames(userId, gameId);
        return ResponseEntity.ok(true);
    }

    @PostMapping(value = "/update-state/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateStatusGame(@RequestBody String status, @PathVariable long userId) {
            savedGameService.updateStatePreperationGame(userId, status);
    }

    @PostMapping(value = "/update-prepared/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void statusToPrepared(@RequestBody String status, @PathVariable long userId) {
            savedGameService.checkIfTwoPlayersArePreparedThenChangingState(status, userId);
    }


}
