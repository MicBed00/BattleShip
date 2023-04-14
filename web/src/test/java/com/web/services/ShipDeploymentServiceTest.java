package com.web.services;

import board.Board;
import com.web.enity.game.Game;
import com.web.repositories.GameRepo;
import com.web.repositories.SavedGameRepo;
import dataConfig.Position;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import ship.Ship;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipDeploymentServiceTest {
    @Mock
    private SavedGameService savedGameService;
    @Mock
    private GameService gameService;
    @Mock
    private GameRepo gameRepo;
    @Mock
    private SavedGameRepo savedGameRepo;

    @InjectMocks
    private ShipDeploymentService shipDeploymentService;

    //TODO czy muszę testować scenariusze wyrzucania wyjątków w sytuacji gdy wyjątek jest
    //rzucany przez inną metodę ze stosu wywołań???
    @DirtiesContext
    @Test
    public void shouldAddShipToTheOwnerBoard() {
        //given
        Ship ship = new Ship(1,1,1, Position.VERTICAL);
        long gameId = 1;
        long userId = 1;
        Long ownerId = 1L;
        List<Board> boardList = new ArrayList<>();
        boardList.add(new Board(10));
        boardList.add(new Board(10));
        Game game = new Game();
        game.setOwnerGame(ownerId);

        //when
        when(savedGameService.getBoardsList(gameId)).thenReturn(boardList);
        when(gameService.getGame(gameId)).thenReturn(game);

        List<Board> result = shipDeploymentService.addShipToList(ship, gameId, userId);

        //then
        verify(savedGameService).getBoardsList(gameId);
        verify(gameService).getGame(gameId);
        assertEquals(1, result.get(0).getShips().size());
        assertEquals(ship, result.get(0).getShips().get(0));
    }

    @DirtiesContext
    @Test
    public void shouldAddShipToTheOpponentBoard() {
        //given
        Ship ship = new Ship(1,1,1, Position.VERTICAL);
        long gameId = 1;
        long userId = 2;
        Long ownerId = 1L;
        List<Board> boardList = new ArrayList<>();
        boardList.add(new Board(10));
        boardList.add(new Board(10));
        Game game = new Game();
        game.setOwnerGame(ownerId);

        //when
        when(savedGameService.getBoardsList(gameId)).thenReturn(boardList);
        when(gameService.getGame(gameId)).thenReturn(game);

        List<Board> result = shipDeploymentService.addShipToList(ship, gameId, userId);

        //then
        verify(savedGameService).getBoardsList(gameId);
        verify(gameService).getGame(gameId);
        assertEquals(1, result.get(1).getShips().size());
        assertEquals(ship, result.get(1).getShips().get(0));
    }

    @DirtiesContext
    @Test
    public void shouldNotAddShipToAnyBoard() {
        //given
        Ship ship = new Ship(0,1,1, Position.VERTICAL);
        long gameId = 1;
        long userId = 2;
        Long ownerId = 1L;
        List<Board> boardList = new ArrayList<>();
        boardList.add(new Board(10));
        boardList.add(new Board(10));
        Game game = new Game();
        game.setOwnerGame(ownerId);

        //when
        when(savedGameService.getBoardsList(gameId)).thenReturn(boardList);
        when(gameService.getGame(gameId)).thenReturn(game);

        List<Board> result = shipDeploymentService.addShipToList(ship, gameId, userId);

        //then
        verify(savedGameService).getBoardsList(gameId);
        verify(gameService).getGame(gameId);
        assertEquals(0, result.get(1).getShips().size());
    }

}