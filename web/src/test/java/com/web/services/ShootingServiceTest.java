package com.web.services;

import board.Board;
import board.Shot;
import dataConfig.Position;
import exceptions.ShotSamePlaceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.ToStringExclude;
import ship.Ship;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ShootingServiceTest {

    @Mock
    private SavedGameService savedGameService;

    @InjectMocks
    ShootingService shootingService;

    @DirtiesContext
    @Test
    public void shouldReturnFalseWhenNoShipsAreHit() {
        //given
        long gameId = 1;
        Ship ship1 = new Ship(1, 1, 1, Position.VERTICAL);
        boolean[] hits1 = ship1.getHits();
        hits1[0] = false;
        Ship ship2 = new Ship(1, 1, 1, Position.VERTICAL);
        boolean[] hits2 = ship2.getHits();
        hits2[0] = false;

        Board board1 = new Board();
        board1.getShips().add(ship1);

        Board board2 = new Board();
        board2.getShips().add(ship2);

        List<Board> boardList = List.of(board1, board2);

        given(savedGameService.getBoardsList(gameId)).willReturn(boardList);

        //when
        Boolean result = shootingService.checkIfAllShipsAreHitted(gameId);

        //then
        assertFalse(result);
        verify(savedGameService).getBoardsList(gameId);
    }

    @DirtiesContext
    @Test
    public void shouldReturnTrueWhenShipsOnePlayerAreHittedAndSecondPlayerNot() {
        //given
        long gameId = 1;
        Ship ship1 = new Ship(1, 1, 1, Position.VERTICAL);
        boolean[] hits1 = ship1.getHits();
        hits1[0] = true;
        Ship ship2 = new Ship(1, 1, 1, Position.VERTICAL);
        boolean[] hits2 = ship2.getHits();
        hits2[0] = false;

        Board board1 = new Board();
        board1.getShips().add(ship1);

        Board board2 = new Board();
        board2.getShips().add(ship2);

        List<Board> boardList = List.of(board1, board2);

        given(savedGameService.getBoardsList(gameId)).willReturn(boardList);

        //when
        Boolean result = shootingService.checkIfAllShipsAreHitted(gameId);

        //then
        assertTrue(result);
        verify(savedGameService).getBoardsList(gameId);
    }

    @DirtiesContext
    @Test
    public void shouldReturnFalseWhenShipIsHittedPartly() {
        //given
        long gameId = 1;
        Ship ship1 = new Ship(3, 1, 1, Position.VERTICAL);
        boolean[] hits1 = ship1.getHits();
        hits1[0] = true;
        hits1[1] = false;
        hits1[2] = true;
        Ship ship2 = new Ship(3, 1, 1, Position.VERTICAL);
        boolean[] hits2 = ship2.getHits();
        hits2[0] = false;
        hits2[1] = false;
        hits2[2] = true;

        Board board1 = new Board();
        board1.getShips().add(ship1);

        Board board2 = new Board();
        board2.getShips().add(ship2);

        List<Board> boardList = List.of(board1, board2);

        given(savedGameService.getBoardsList(gameId)).willReturn(boardList);

        //when
        Boolean result = shootingService.checkIfAllShipsAreHitted(gameId);

        //then
        assertFalse(result);
        verify(savedGameService).getBoardsList(gameId);
    }

    @DirtiesContext
    @Test
    public void shouldReturnFalseWhenOnOneBoardLastOnlyOneNotHittedShip() {
        //given
        long gameId = 1;
        Ship ship1 = new Ship(3, 1, 1, Position.VERTICAL);
        boolean[] hits1 = ship1.getHits();
        hits1[0] = true;
        hits1[1] = false;
        hits1[2] = true;
        Ship ship2 = new Ship(3, 1, 1, Position.VERTICAL);
        Ship ship3 = new Ship(1, 6, 6, Position.VERTICAL);
        boolean[] hits2 = ship2.getHits();
        hits2[0] = false;
        hits2[1] = false;
        hits2[2] = true;
        boolean[] hits3 = ship3.getHits();
        hits3[0] = true;

        Board board1 = new Board();
        board1.getShips().add(ship1);

        Board board2 = new Board();
        board2.getShips().add(ship2);
        board2.getShips().add(ship3);

        List<Board> boardList = List.of(board1, board2);

        given(savedGameService.getBoardsList(gameId)).willReturn(boardList);

        //when
        Boolean result = shootingService.checkIfAllShipsAreHitted(gameId);

        //then
        assertFalse(result);
        verify(savedGameService).getBoardsList(gameId);
    }


    @DirtiesContext
    @Test
    public void shouldAddShotToSecondBoard() {
        //given
        Shot shot = new Shot();
        long gameId = 1;
        List<Board> boardList = List.of(new Board(), new Board());
        given(savedGameService.getBoardsList(gameId)).willReturn(boardList);

        //when
        List<Board> result = shootingService.addShotToBoard(shot, gameId);

        //then
        assertEquals(1, result.get(1).getOpponentShots().size());
    }
    @DirtiesContext
    @Test
    public void shouldAddShotToFirstBoard() {
        //given
        Shot shot = new Shot();
        long gameId = 1;
        Board board1 = new Board();
        board1.getOpponentShots().add(shot);
        List<Board> boardList = List.of(new Board(), board1);
        given(savedGameService.getBoardsList(gameId)).willReturn(boardList);

        //when
        List<Board> result = shootingService.addShotToBoard(shot, gameId);

        //then
        assertEquals(1, result.get(1).getOpponentShots().size());
        assertEquals(1, result.get(0).getOpponentShots().size());
    }

    @DirtiesContext
    @Test
    public void shouldThrowExceptionShotSamePlace() {
        //given
        Shot shot = new Shot();
        long gameId = 1;
        Board board1 = new Board();
        board1.getOpponentShots().add(shot);
        List<Board> boardList = List.of(board1, new Board());
        given(savedGameService.getBoardsList(gameId)).willReturn(boardList);

        //when
        //then
        assertThrows(ShotSamePlaceException.class, () -> {
            shootingService.addShotToBoard(shot, gameId);
        });


    }



}