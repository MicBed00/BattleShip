package com.web.service;

import dataConfig.Position;
import dataConfig.ShipLimits;
import board.Board;
import board.Shot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ship.Ship;

import java.util.*;

@Service
public class GameStatusService {
    private List<String> shipSize;
    private List<Board> boardList;
    private List<Position> positionList;
    private Board boardPlayer1;
    private Board boardPlayer2;
    private List<Set<Shot>> listSets;
    private Shot shot;

    private GameStatusRepoService gameStatusRepoService;

    @Autowired
    GameStatusService(@Lazy GameStatusRepoService gameStatusRepoService) {
        this.gameStatusRepoService = gameStatusRepoService;
        shipSize = new ArrayList<>();
        boardList = new ArrayList<>();
        positionList = new ArrayList<>();
        positionList.add(Position.VERTICAL);
        positionList.add(Position.HORIZONTAL);
        shipSize.add("1");
        shipSize.add("2");
        shipSize.add("3");
        shipSize.add("4");
        boardPlayer1 = new Board();
        boardPlayer2 = new Board();
        boardList.add(boardPlayer1);
        boardList.add(boardPlayer2);
        listSets = new ArrayList<>();
        listSets.add(boardPlayer1.getOpponentShots());
        listSets.add(boardPlayer2.getOpponentShots());
        shot = new Shot();
    }

    public int getShipLimits() {
        return ShipLimits.SHIP_LIMIT.getQty();
    }
    private boolean checkIsOverTheLimitShip(int size){
        return size < ShipLimits.SHIP_LIMIT.getQty();
    }

    public List<String> getShipSize() {
        return shipSize;
    }

    public List<Position> getOrientation() {
        return positionList;
    }


    public List<Board> getBoardList() {
        return boardList;
    }

    public List<Board> chooseBoardPlayer(Ship ship) {

        if(ship.getLength() > 0 && ship.getPosition() != null) {
            if(checkIsOverTheLimitShip(boardPlayer1.getShips().size())) {
                addShipToBoard(boardPlayer1, ship);
            }else if(checkIsOverTheLimitShip(boardPlayer2.getShips().size())) {
                addShipToBoard(boardPlayer2, ship);
            }
        }

        return getBoardList();
    }

    private void addShipToBoard(Board boardPlayer, Ship ship) {
        boardPlayer.addShip(ship.getLength(), ship.getXstart(),
                    ship.getYstart(), ship.getPosition());
    }

    public List<Board> addShotAtShip(Shot shot) {
        if(boardPlayer1.getOpponentShots().size() == boardPlayer2.getOpponentShots().size()) {
            boardPlayer2.shoot(shot);
        } else {
            boardPlayer1.shoot(shot);
        }
        return getBoardList();
    }

    public List<Board> getShips() {
        return getBoardList();
    }

    public Boolean checkIfAllShipsAreHitted() {
        return boardPlayer1.getIsFinished().get() || boardPlayer2.getIsFinished().get();
    }

    public int[] statisticsGame(Board board) {
        return board.statisticsShot();

    }

    public double getAccuracyShot(int totalShots, int hitShot) {
        return ((double) hitShot/totalShots) * 100;

    }

    public List<Board> deleteShip(int idBoard) {
        Board board  = boardList.get(idBoard);
        int lastIndex = board.getShips().size() - 1;
        board.getShips().remove(lastIndex);
        return boardList;
    }

    public int getCurrentPlayer(List<Board> boardsList) {
            if(checkIsOverTheLimitShip(boardPlayer1.getShips().size())) {

                return 1;
            }else if(checkIsOverTheLimitShip(boardPlayer2.getShips().size())) {
                return 2;
            }

        return 0;
    }
    public void restoreGameStatusOnServer(int userId) {
        List<Board> listBoard = gameStatusRepoService.getSavedStateGame(userId).getGameStatus().getBoardsStatus();
        boardPlayer1 = listBoard.get(0);
        boardPlayer2 = listBoard.get(1);
        //dlaczego muszę kopiować nowe wartości do list, jeśli przypisuje do referencji boardPlayer1 i 2 nowe obiekty
        //, a te refenracje są zapisane w liscie boardList
        boardList = new ArrayList<>(listBoard);
    }

}
