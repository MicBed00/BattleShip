package com.web.controllers;

import board.Board;
import board.Shot;
import board.StateGame;
import com.web.enity.game.Game;
import com.web.enity.game.SavedGame;
import com.web.enity.user.User;
import com.web.services.GameRepoService;
import com.web.services.GameStatusRepoService;
import com.web.services.GameStatusService;
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
    public ResponseEntity<Boolean> addShip(@RequestBody Ship ship, @PathVariable long userId, @PathVariable long gameId) throws BattleShipException {
        List<Board> boardsList = gameStatusService.addShipToList(ship, gameId, userId);
        return ResponseEntity.ok( gameStatusService.saveGameStatusToDataBase(boardsList, StateGame.IN_PROCCESS, gameId));
    }

    @PostMapping(value = "/addSecondPlayer/{userId}/{gameId}")
    public ResponseEntity<Integer> addSecondPlayerToGame(@PathVariable long userId,
                                                      @PathVariable long gameId) {
        gameRepoService.addSecondPlayerToGame(userId, gameId);
        return ResponseEntity.ok(gameStatusRepoService.getStatusGame(gameId).getGameStatus().getBoardsStatus().get(0).getSizeBoard());
    }
    @GetMapping(value = "/listBoard/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> listBoards(@PathVariable long gameId) {
        return ResponseEntity.ok(gameStatusService.getBoardList(gameId));
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
        return ResponseEntity.ok(gameStatusService.getBoard(gameId, userId));
    }
    @GetMapping(value = "/opponent/{idGame}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<String>> checkIfOpponentAppears(@PathVariable long idGame) {
        return ResponseEntity.ok(gameStatusService.checkIfOpponentAppears(idGame));
    }
    @GetMapping(value = "/change-state/{userId}/{state}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> changeState(@PathVariable long userId, @PathVariable String state) {
        SavedGame savedGame = gameStatusRepoService.updateStatePreperationGame(userId, state);
        return ResponseEntity.ok((long) savedGame.getGame().getId());
    }

    @GetMapping(value = "/game/status-isFinished/{userId}/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> statusFinished(@PathVariable int userId, @PathVariable long gameId) {
        boolean isFinished = gameStatusService.checkIfAllShipsAreHitted(gameId);
        if(isFinished)
            gameStatusRepoService.updateStatePreperationGame(userId, "FINISHED");
        return ResponseEntity.ok(isFinished);
    }
    @GetMapping(value = "/status-game/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StateGame> statusesGame(@PathVariable long gameId) {
        SavedGame savedGame = gameStatusRepoService.getStatusGame(gameId);
        return ResponseEntity.ok(savedGame.getGameStatus().getState());
    }
    @GetMapping(value = "/check-state", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Long>> statusesGame() {
        return ResponseEntity.ok(gameStatusRepoService.checkIfOwnGameStatusHasChanged());
    }

    @GetMapping(value = "/request/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> requestToJoinGame( @PathVariable long gameId) {
        Game game = gameRepoService.getGame(gameId);
        User user = game.getUsers().stream().toList().get(0);
        gameStatusRepoService.updateStatePreperationGame(user.getId(), "REQUESTING");
        return ResponseEntity.ok(gameId);
    }

    @PostMapping(value = "/game/boards/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> shots(@RequestBody Shot shot, @PathVariable long gameId) {
        List<Board> boardListAfterShot = gameStatusService.addShotAtShip(shot, gameId);
        gameStatusService.saveGameStatusToDataBase(boardListAfterShot, StateGame.PREPARED, gameId);
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

    @GetMapping(value="/checkGames/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> checkUnfinishedGames(@PathVariable long userId) {
        return ResponseEntity.ok( gameRepoService.checksUnfinishedGames());
    }
    @GetMapping(value="/resume-game/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Board>> unfinishedGames(@PathVariable long gameId) {
        return ResponseEntity.ok(gameStatusService.getBoardList(gameId));
    }
    @DeleteMapping(value = "/deleteShip/{userId}/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Board> deleteShips(@PathVariable long userId, @PathVariable long gameId) {
        gameStatusRepoService.deleteShip(userId, gameId);
        return ResponseEntity.ok(gameStatusService.getBoard(gameId, userId));
    }
    @DeleteMapping(value = "/delete-game/{userId}/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> deleteGames(@PathVariable long userId, @PathVariable long gameId) {
        gameStatusRepoService.deleteGames(userId, gameId);
        return ResponseEntity.ok(true);
    }

    @PostMapping(value = "/update-state/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void updateStatusGame(@RequestBody String status, @PathVariable long userId) {
            gameStatusRepoService.updateStatePreperationGame(userId, status);
    }

    @PostMapping(value = "/update-prepared/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void statusToPrepared(@RequestBody String status, @PathVariable long userId) {
            gameStatusRepoService.checkIfTwoPlayersArePreparedThenChangingState(status, userId);
    }

    @PostMapping(value = "/game/save/{userId}/{sizeBoard}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Integer> saveNewGame(@PathVariable long userId,
                                        @PathVariable int sizeBoard) {
        return ResponseEntity.ok(gameStatusService.saveNewGame(userId, sizeBoard));
    }

}
