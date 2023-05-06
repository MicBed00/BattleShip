package com.web.services;

import board.Board;
import board.StateGame;
import com.web.gameSetups.GameSetups;
import com.web.gameSetups.GameSetupsDto;
import com.web.enity.game.Game;
import com.web.enity.game.SavedGame;
import com.web.enity.user.User;
import com.web.repositories.GameRepo;
import com.web.repositories.SavedGameRepo;
import dataConfig.Position;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import serialization.GameStatus;
import ship.Ship;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SavedGameServiceTest {
    @Mock
    private GameService gameService;
    @Mock
    private UserService userService;
    @Mock
    private GameRepo gameRepo;
    @Mock
    private SavedGameRepo repoSavedGame;
    @Mock
    private GameSetupsDto gameSetupsDto;

    private ShipDeploymentService deploymentService;
    @InjectMocks
    private SavedGameService savedGameService;

    @DirtiesContext
    @Test
    public void shouldReturnShipLimit() {
        //given
        int shipLimit = 4;
        given(gameSetupsDto.getShipLimit()).willReturn(shipLimit);

        //when
        int result = savedGameService.getShipLimits();

        //then
        assertEquals(result, shipLimit);
    }

    @DirtiesContext
    @Test
    public void shouldReturnAccuracyShots() {
        //given
        int totalShots = 10;
        int hitShot = 5;

        //when
        double result = savedGameService.getAccuracyShots(totalShots, hitShot);

        //then
        assertEquals(result, 50);
    }

    @DirtiesContext
    @Test
    public void shouldUpdateStatePreperationGame() {
        //given
        int userId = 1;
        String state = "WAITING";
        GameStatus gameStatus = new GameStatus(List.of(), 1, StateGame.NEW);
        Game game = new Game(1);
        Long idStatus = 1L;
        SavedGame savedGame = new SavedGame(gameStatus, game);
        given(userService.getLastUserGames(userId)).willReturn(game);
        given(repoSavedGame.findMaxIdByGameId(1L)).willReturn(idStatus);
        given(repoSavedGame.findById(idStatus)).willReturn(Optional.of(savedGame));
        given(repoSavedGame.save(savedGame)).willReturn(savedGame);
        //when
        SavedGame result = savedGameService.updateStatePreperationGame(userId, state);

        //then
        assertEquals(result.getGameStatus().getState(), StateGame.valueOf(state));
        verify(repoSavedGame).save(savedGame);
    }

    @DirtiesContext
    @Test
    void whenTwoPlayersArePreparedChangeState() {
        //given
        int userId = 1;
        List<Board> boardList = getBoardListWithShips();
        GameStatus gameStatus = new GameStatus(boardList, 1, StateGame.NEW);
        Game game = new Game(1);
        Long idStatus = 1L;
        SavedGame savedGame = new SavedGame(gameStatus, game);
        //TODO zapytaj jak się mockuje zagnieżdżone metody, kolejność wywoływania mocków jest istotna?
//        SavedGame savedGameWithShips = addAllShipsToBoards(savedGame);
        given(userService.getLastUserGames(userId)).willReturn(game);
        given(repoSavedGame.findMaxIdByGameId(1L)).willReturn(idStatus);
        given(repoSavedGame.findById(idStatus)).willReturn(Optional.of(savedGame));

        //when
        savedGameService.checkIfTwoPlayersArePreparedNextChangeState("PREPARED", 1L);

        //then
        assertEquals(savedGame.getGameStatus().getState(), StateGame.PREPARED);
        verify(repoSavedGame, times(1)).save(savedGame);
    }

    @DirtiesContext
    @Test
    void whenTwoPlayersAreNotPreparedDoesNotChangeState() {
        //given
        int userId = 1;
        List<Board> boardList = List.of(new Board(), new Board());
        GameStatus gameStatus = new GameStatus(boardList, 1, StateGame.NEW);
        Game game = new Game(1);
        Long idStatus = 1L;
        SavedGame savedGame = new SavedGame(gameStatus, game);
        //TODO zapytaj jak się mockuje zagnieżdżone metody, kolejność wywoływania mocków jest istotna?
//        SavedGame savedGameWithShips = addAllShipsToBoards(savedGame);
        given(userService.getLastUserGames(userId)).willReturn(game);
        given(repoSavedGame.findMaxIdByGameId(1L)).willReturn(idStatus);
        given(repoSavedGame.findById(idStatus)).willReturn(Optional.of(savedGame));

        //when
        savedGameService.checkIfTwoPlayersArePreparedNextChangeState("PREPARED", 1L);

        //then
        assertEquals(savedGame.getGameStatus().getState(), StateGame.NEW);
        verify(repoSavedGame, times(0)).save(savedGame);
    }


    @DirtiesContext
    @Test
    public void shouldReturnFirstPlayer() {
        //given
        long gameId = 1L;
        long ownerId = 1;
        int shipLimit = 5;
        GameSetups gameSetups = new GameSetups();
        List<Board> boardList = List.of(new Board(), new Board());
        GameStatus gameStatus = new GameStatus(boardList, 1, StateGame.NEW);
        Game game = new Game(Timestamp.valueOf(LocalDateTime.now()), ownerId, gameSetups);
        Long idStatus = 1L;
        SavedGame savedGame = new SavedGame(gameStatus, game);
        given(repoSavedGame.findMaxIdByGameId(gameId)).willReturn(idStatus);
        given(repoSavedGame.findById(idStatus)).willReturn(Optional.of(savedGame));
        given(gameSetupsDto.getShipLimit()).willReturn(shipLimit);

        //when
        int result = savedGameService.getCurrentPlayer(gameId);

        //then
        assertEquals(1, result);
        verify(gameSetupsDto, times(1)).getShipLimit();
    }

    @DirtiesContext
    @Test
    public void shouldReturnSecondPlayer() {
        //given
        long gameId = 1L;
        long ownerId = 1;
        int shipLimit = 5;
        GameSetups gameSetups = new GameSetups();
        List<Board> boardList = getListWithFirstBoardFullShips();
        GameStatus gameStatus = new GameStatus(boardList, 1, StateGame.NEW);
        Game game = new Game(Timestamp.valueOf(LocalDateTime.now()), ownerId, gameSetups);
        Long idStatus = 1L;
        SavedGame savedGame = new SavedGame(gameStatus, game);
        given(repoSavedGame.findMaxIdByGameId(gameId)).willReturn(idStatus);
        given(repoSavedGame.findById(idStatus)).willReturn(Optional.of(savedGame));
        given(gameSetupsDto.getShipLimit()).willReturn(shipLimit);

        //when
        int result = savedGameService.getCurrentPlayer(gameId);

        //then
        assertEquals(result, 2);
        verify(gameSetupsDto, times(2)).getShipLimit();
    }
    @DirtiesContext
    @Test
    public void ifBoardDoesntExistShouldThrowException() {
        //given
        long gameId = 1L;

        //when
        //then
        assertThrows(NoSuchElementException.class, () -> {
            savedGameService.getCurrentPlayer(gameId);
        });
        verify(gameSetupsDto, times(0)).getShipLimit();
    }
    @DirtiesContext
    @Test
    public void shouldSaveGame() {
        //given
        List<Board> boardList = List.of(new Board(), new Board());
        long gameId = 1L;
        long ownerId = 1;
        int shipLimit = 5;
        GameSetups gameSetups = new GameSetups();
        GameStatus gameStatus = new GameStatus(boardList, 1, StateGame.NEW);
        Game game = new Game(Timestamp.valueOf(LocalDateTime.now()), ownerId, gameSetups);
        Long idStatus = 1L;
        SavedGame savedGame = new SavedGame(gameStatus, game);
        given(repoSavedGame.findMaxIdByGameId(gameId)).willReturn(idStatus);
        given(repoSavedGame.findById(idStatus)).willReturn(Optional.of(savedGame));
        given(gameRepo.findById(gameId)).willReturn(Optional.of(game));
        given(repoSavedGame.save(any(SavedGame.class))).willReturn(savedGame);

        //when
        boolean result = savedGameService.saveGameStatus(boardList, StateGame.NEW, gameId);

        //then
        assertTrue(result);
        verify(gameRepo).findById(anyLong());
        verify(repoSavedGame, times(1)).save(any(SavedGame.class)); //argThat
    }

    @DirtiesContext
    @Test
    public void shouldReturnUnfinishedUserGame() {
        //given
        long userId = 1;
        User user = new User();
        Game game = new Game(1);
        game.setUsers(List.of(user, new User()));
        user.setGames(List.of(game));
        List<Board> boardList = List.of(new Board(), new Board());
        SavedGame savedGame = new SavedGame(new GameStatus(boardList, StateGame.NEW), game);
        given(userService.getUserId()).willReturn(userId);
        given(userService.getLogInUser(userId)).willReturn(user);
        given(repoSavedGame.findMaxIdByGameId(anyLong())).willReturn(anyLong());
        given((repoSavedGame.findById(1L))).willReturn(Optional.of(savedGame));

        //when
        List<Integer> result = savedGameService.getUnfinishedUserGames();

        //then
        verify(userService).getUserId();
        verify(userService).getLogInUser(anyLong());
        verify(repoSavedGame).findMaxIdByGameId(anyLong());
        verify(repoSavedGame).findById(anyLong());
        assertEquals(1L, result.size());
    }

    @DirtiesContext
    @Test
    public void shouldReturnSavedGame() {
        //given
        long gameId = 1L;
        long idSavedGame = 1L;
        SavedGame savedGame = new SavedGame();
        given(repoSavedGame.findMaxIdByGameId(gameId)).willReturn(gameId);
        given(repoSavedGame.findById(idSavedGame)).willReturn(Optional.of(savedGame));

        //when
        SavedGame result = savedGameService.getSavedGame(gameId);

        //then
        assertEquals(savedGame, result);
    }

    @DirtiesContext
    @Test
    public void ifGameDoesntExistShouldThrowException() {
        //given
        long gameId = 1L;
        given(repoSavedGame.findMaxIdByGameId(gameId)).willReturn(gameId);

        //when
        //then
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            savedGameService.getSavedGame(gameId);
        });
        assertTrue(exception.getMessage().contains("No saved game"));
    }

    private List<Board> getListWithFirstBoardFullShips() {
        List<Board> boardList = getBoardListWithShips();
        boardList.remove(1);
        boardList.add(new Board());
        return boardList;
    }


//    private SavedGame saveGame() {
//        int currentPly = 1;
//        List<Board> boardList = new ArrayList<>();
//        boardList.add(new Board());
//        boardList.add(new Board());
//        Game game = new Game();
//        game.setOwnerGame(1L);
//        game.getUsers().add(new User());
//        GameStatus gameStatus = new GameStatus(boardList,currentPly,StateGame.IN_PROCCESS);
//        SavedGame savedGame = new SavedGame(gameStatus,game);
//        savedGame.setGameStatus(gameStatus);
//        return savedGame;
//    }
//
    private SavedGame addAllShipsToBoards(SavedGame saveGame) {
        long gameId = 1;
        long userIdOwner = 1;
        long userIdSec = 2;
        long idSavedGame = 1L;
        SavedGame savedGame = saveGame;
        SavedGameService savedGameService1 = Mockito.mock(SavedGameService.class, RETURNS_DEEP_STUBS);
        List<Board> boardsList = savedGame.getGameStatus().getBoardsStatus();
        Game game = savedGame.getGame();
        given(savedGameService1.getSavedGame(gameId)).willReturn(savedGame);
        given(repoSavedGame.findMaxIdByGameId(game.getId())).willReturn(idSavedGame);
        given(repoSavedGame.findById(idSavedGame)).willReturn(Optional.of(savedGame));
        given(savedGameService.getBoardsList(game.getId())).willReturn(boardsList);

        given(gameService.getGame(gameId)).willReturn(game);

        Ship ship1 = new Ship(1, 1, 1, Position.VERTICAL);
        deploymentService.addShipToList(ship1, gameId, userIdOwner);
        Ship ship2 = new Ship(1, 9, 0, Position.VERTICAL);
        deploymentService.addShipToList(ship2, gameId, userIdOwner);
        Ship ship3 = new Ship(1, 3, 3, Position.VERTICAL);
        deploymentService.addShipToList(ship3, gameId, userIdOwner);
        Ship ship4 = new Ship(1, 5, 5, Position.VERTICAL);
        deploymentService.addShipToList(ship4, gameId, userIdOwner);
        Ship ship5 = new Ship(2, 8, 6, Position.VERTICAL);
        deploymentService.addShipToList(ship5, gameId, userIdOwner);
        Ship ship6 = new Ship(1, 1, 1, Position.VERTICAL);
        deploymentService.addShipToList(ship6, gameId, userIdSec);
        Ship ship7 = new Ship(1, 9, 0, Position.VERTICAL);
        deploymentService.addShipToList(ship7, gameId, userIdSec);
        Ship ship8 = new Ship(1, 3, 3, Position.VERTICAL);
        deploymentService.addShipToList(ship8, gameId, userIdSec);
        Ship ship9 = new Ship(1, 5, 5, Position.VERTICAL);
        deploymentService.addShipToList(ship9, gameId, userIdSec);
        Ship ship10 = new Ship(2, 8, 6, Position.VERTICAL);
        deploymentService.addShipToList(ship10, gameId, userIdSec);

        return savedGame;
    }

    private List<Board> getBoardListWithShips() {
        Board board1 = new Board();
        Board board2 = new Board();
        List<Board> boardList = new ArrayList<>();
        boardList.add(board1);
        boardList.add(board2);

        Ship ship1 = new Ship(1, 1, 1, Position.VERTICAL);
        board1.getShips().add(ship1);
        Ship ship2 = new Ship(1, 9, 0, Position.VERTICAL);
        board1.getShips().add(ship2);
        Ship ship3 = new Ship(1, 3, 3, Position.VERTICAL);
        board1.getShips().add(ship3);
        Ship ship4 = new Ship(1, 5, 5, Position.VERTICAL);
        board1.getShips().add(ship4);
        Ship ship5 = new Ship(2, 8, 6, Position.VERTICAL);
        board1.getShips().add(ship5);
        Ship ship6 = new Ship(1, 1, 1, Position.VERTICAL);
        board2.getShips().add(ship6);
        Ship ship7 = new Ship(1, 9, 0, Position.VERTICAL);
        board2.getShips().add(ship7);
        Ship ship8 = new Ship(1, 3, 3, Position.VERTICAL);
        board2.getShips().add(ship8);
        Ship ship9 = new Ship(1, 5, 5, Position.VERTICAL);
        board2.getShips().add(ship9);
        Ship ship10 = new Ship(2, 8, 6, Position.VERTICAL);
        board2.getShips().add(ship10);

        return boardList;
    }
//
//    private void shootDownAllShipsOnOneBoards(SavedGame saveGame) {
//        long gameId = 1;
//        SavedGame savedGame = saveGame;
////        given(getStatusGame(gameId)).willReturn(savedGame);
//        wa.addShotAtShip(new Shot(1,1), gameId);
//        wa.addShotAtShip(new Shot(2,2), gameId);
//        wa.addShotAtShip(new Shot(9,0), gameId);
//        wa.addShotAtShip(new Shot(2,3), gameId);
//        wa.addShotAtShip(new Shot(3,3), gameId);
//        wa.addShotAtShip(new Shot(2,4), gameId);
//        wa.addShotAtShip(new Shot(5,5), gameId);
//        wa.addShotAtShip(new Shot(2,6), gameId);
//        wa.addShotAtShip(new Shot(8,6), gameId);
//        wa.addShotAtShip(new Shot(2,7), gameId);
//        wa.addShotAtShip(new Shot(8,7), gameId);
//    }
//    @DirtiesContext
//    @Test
//    public void shouldReturnShipLimitsOnBoard() {
//        //then
//        assertEquals(5, savedGameService.getShipLimits());
//    }
//    @DirtiesContext
//    @Test
//    public void shouldReturnListSize4MastedShip() {
//        //then
//        assertEquals("4", savedGameService.getShipSize().get(3));
//    }
//    @DirtiesContext
//    @Test
//    public void shouldReturnListSize3MastedShip() {
//        //then
//        assertEquals("3", savedGameService.getShipSize().get(2));
//    }
//    @DirtiesContext
//    @Test
//    public void shouldReturnListSize2MastedShip() {
//        //then
//        assertEquals("2", savedGameService.getShipSize().get(1));
//    }
//    @DirtiesContext
//    @Test
//    public void shouldReturnListSize1MastedShip() {
//        MockitoAnnotations.openMocks(this);
//        //then
//        assertEquals("1", savedGameService.getShipSize().get(0));
//    }
//
//
//    @Test
//    public void shouldReturnVerticalOrientationShip() {
//        //then
//        assertEquals("HORIZONTAL", savedGameService.getOrientation().get(0).toString());
//    }
//
//    @DirtiesContext
//    @Test
//    public void shouldAddShipToFirstList() {
//        //given
//        long gameId = 1;
//        long userId = 1;
//        SavedGame savedGame = saveGame();
//        Game game = savedGame.getGame();
//        Ship ship = new Ship(2, 4, 4, Position.VERTICAL);
////        given(gameStatusRepoService.getStatusGame(gameId)).willReturn(savedGame);
//        given(gameService.getGame(gameId)).willReturn(game);
//
//        //when
//        List<Board> boardList = savedGameService.addShipToList(ship, gameId, userId);
//        int sizeList = boardList.get(0).getShips().size();
//
//        //then
//        verify(gameService,times(1)).getGame(gameId);
//        assertEquals(1, sizeList);
//    }
//
//
//    @DirtiesContext
//    @Test
//    public void exceptionShouldBeThrowIfShipIsOutOfBoundBoard() {
//        //given
//        long gameId = 1;
//        long userId = 1;
//        SavedGame savedGame = saveGame();
//        Game game = savedGame.getGame();
//        Ship ship = new Ship(2, 8, 9, Position.VERTICAL);
////        given(gameStatusRepoService.getStatusGame(gameId)).willReturn(savedGame);
//        given(gameService.getGame(gameId)).willReturn(game);
//
//        //when
//        //then
//        assertThrows(OutOfBoundsException.class, () -> savedGameService.addShipToList(ship,gameId, userId));
//    }
//
//    @DirtiesContext
//    @Test
//    public void exceptionShouldBeThrowIfAddTwoShipsTheSamePlaceOnBoard() {
//        //given
//        long gameId = 1;
//        long userId = 1;
//        SavedGame savedGame = saveGame();
//        Game game = savedGame.getGame();
//        Ship ship1 = new Ship(2, 1, 1, Position.VERTICAL);
//        Ship ship2 = new Ship(2, 1, 1, Position.VERTICAL);
////        given(gameStatusRepoService.getStatusGame(gameId)).willReturn(savedGame);
//        given(gameService.getGame(gameId)).willReturn(game);
////        given(gameStatusRepoService.getStatusGame(gameId)).willReturn(savedGame);
//        given(gameService.getGame(gameId)).willReturn(game);
//
//        //when
//        savedGameService.addShipToList(ship1,gameId, userId);
//
//        //then
//        assertThrows(CollidingException.class, () -> savedGameService.addShipToList(ship2, gameId, userId));
//    }
//
//    @DirtiesContext
//    @Test
//    public void whenSecondPlayerShootsShotShouldBeAddedToFirstPlayerBoards() {
//        //given
//        long gameId = 1L;
//        Shot shotFirstPlayer = new Shot(1, 1);
//        SavedGame savedGame = saveGame();
////        given(gameStatusRepoService.getStatusGame(gameId)).willReturn(savedGame);
//
//        savedGameService.addShotAtShip(shotFirstPlayer,gameId);
//
//        //when
//        Shot shotSecondPlayer = new Shot(1, 1);
//        List<Board> boardList = savedGameService.addShotAtShip(shotSecondPlayer, gameId);
//        int opponentShots = boardList.get(0).getOpponentShots().size();
//        //then
//        assertEquals(1, opponentShots);
//    }
//
//    @DirtiesContext
//    @Test
//    public void exceptionShouldBeThrowIfPlayerShootsTheSamePlace() {
//        //given
//        long gameId = 1L;
//        SavedGame savedGame = saveGame();
////        given(gameStatusRepoService.getStatusGame(gameId)).willReturn(savedGame);
//        Shot shotFirstPlayer = new Shot(1, 1);
//        savedGameService.addShotAtShip(shotFirstPlayer, gameId);
//        Shot shotSecondPlayer = new Shot(3, 3);
//        savedGameService.addShotAtShip(shotSecondPlayer, gameId);
//        Shot shotSamePlace = new Shot(1, 1);
//
//        //when
//        //then
//        assertThrows(ShotSamePlaceException.class, () -> savedGameService.addShotAtShip(shotSamePlace, gameId));
//    }
//
//    @DirtiesContext
//    @Test
//    public void shotTheSamePlaceShouldHasInvalidState() {
//        //given
//        long gameId = 1L;
//        SavedGame savedGame = saveGame();
////        given(gameStatusRepoService.getStatusGame(gameId)).willReturn(savedGame);
//
//        Shot shotFirstPlayer = new Shot(1, 1);
//        Shot shotSecondPlayer = new Shot(3, 3);
//        savedGameService.addShotAtShip(shotFirstPlayer, gameId);
//        savedGameService.addShotAtShip(shotSecondPlayer, gameId);
//
//        //when
//        try {
//            Shot shotSamePlace = new Shot(1, 1);
//            savedGameService.addShotAtShip(shotSamePlace, gameId);
//        }catch (ShotSamePlaceException e) {}
//        List<Board> boardList = savedGameService.getBoardsList(1L);
//        Set<Shot> opponentShots = boardList.get(1).getOpponentShots();
//
//        //then
//        assertTrue(opponentShots.contains(new Shot(Shot.State.INVALID,1, 1)));
//    }
//
//    @DirtiesContext
//    @Test
//    public void missedShotShouldBeHaveMissedState() {
//        //given
//        long gameId = 1L;
//        SavedGame savedGame = saveGame();
////        given(gameStatusRepoService.getStatusGame(gameId)).willReturn(savedGame);
//        Shot shotFirstPlayer = new Shot(1, 1);
//        //when
//        List<Board> boardList = savedGameService.addShotAtShip(shotFirstPlayer, gameId);
//        Set<Shot> opponentShots = boardList.get(1).getOpponentShots();
//        Shot.State shotState = opponentShots.stream()
//                .iterator()
//                .next()
//                .getState();
//        //then
//        assertSame("MISSED", shotState.toString());
//    }
//
//    @DirtiesContext
//    @Test
//    public void hitShotShouldBeHaveHitState() {
//        //given
//        long gameId = 1;
//        long userId = 1;
//        SavedGame savedGame = saveGame();
//        Game game = savedGame.getGame();
////        given(gameStatusRepoService.getStatusGame(gameId)).willReturn(savedGame);
//        given(gameService.getGame(gameId)).willReturn(game);
//
//        Ship ship = new Ship(1, 1, 1, Position.VERTICAL);
//        savedGameService.addShipToList(ship,gameId, userId);
//
//        Shot shotFirstPlayer = new Shot(3,3);
//        savedGameService.addShotAtShip(shotFirstPlayer, gameId);
//
//        //when
//        Shot shot = new Shot(1, 1);
//        List<Board> boardList = savedGameService.addShotAtShip(shot, gameId);
//        Set<Shot> opponentShots = boardList.get(0).getOpponentShots();
//        Shot.State shotState = opponentShots.stream()
//                .iterator()
//                .next()
//                .getState();
//        //then
//        assertSame("HIT", shotState.toString());
//    }
//
//    @DirtiesContext
//    @Test
//    public void shouldReturnFalseIfNotAllShipsAreHit() {
//        //given
//        long gameId = 1;
//        SavedGame savedGame = saveGame();
//        addAllShipsToBoards(savedGame);
//
//        //then
//        assertFalse(savedGameService.checkIfAllShipsAreHitted(gameId));
//    }
//
//    @DirtiesContext
//    @Test
//    public void shouldReturnTrueIfAllShipsAreHitted() {
//        //given
//        long gameId = 1L;
//        SavedGame savedGame = saveGame();
//        SavedGame savedGameWithShips = addAllShipsToBoards(savedGame);
//        shootDownAllShipsOnOneBoards(savedGameWithShips);
//        //then
//        assertTrue(savedGameService.checkIfAllShipsAreHitted(gameId));
//    }
//    //TODO aktualnie metoda kasowanie statku zanajduję się w gamestatusreposervie
////    @DirtiesContext
////    @Test
////    public void shouldDeleteShipFromList() {
////        //given
////        long gameId = 1;
////        long userId = 1;
////        SavedGame savedGame = saveGame();
////        Game game = savedGame.getGame();
////        given(gameStatusRepoService.getStatusGame(gameId)).willReturn(savedGame);
////        given(gameRepoService.getGame(gameId)).willReturn(game);
////
////        gameStatusService.addShipToList(new Ship(1,1,1, Position.VERTICAL),gameId, userId);
////
////        //when
////        List<Board> boardList = gameStatusService.deleteShipFromServer(0);
////        int sizeList = boardList.get(0).getShips().size();
////
////        //then
////        assertEquals(0, sizeList);
////    }
////
//    @DirtiesContext
//    @Test
//    public void shouldReturnSecondPlayer() {
//        //given
//        long gameId = 1L;
//        long userId = 1;
//        SavedGame savedGame = saveGame();
//        Game game = savedGame.getGame();
////        given(gameStatusRepoService.getStatusGame(gameId)).willReturn(savedGame);
//        given(gameService.getGame(gameId)).willReturn(game);
//
//        Ship ship1 = new Ship(1, 1, 1, Position.VERTICAL);
//        savedGameService.addShipToList(ship1, gameId, userId);
//        Ship ship2 = new Ship(1, 9, 0, Position.VERTICAL);
//        savedGameService.addShipToList(ship2, gameId, userId);
//        Ship ship3 = new Ship(1, 3, 3, Position.VERTICAL);
//        savedGameService.addShipToList(ship3, gameId, userId);
//        Ship ship4 = new Ship(1, 5, 5, Position.VERTICAL);
//        savedGameService.addShipToList(ship4, gameId, userId);
//        Ship ship5 = new Ship(2, 8, 6, Position.VERTICAL);
//        savedGameService.addShipToList(ship5, gameId, userId);
//
//        //when
//        int currentPlayer = savedGameService.getCurrentPlayer(gameId);
//
//        //then
//        assertEquals(2, currentPlayer);
//    }
}
