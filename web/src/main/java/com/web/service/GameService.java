package com.web.service;

import DataConfig.Position;
import DataConfig.ShipLimits;
import board.Board;
import board.Shot;
import org.springframework.stereotype.Service;
import ship.Ship;

import java.util.*;

@Service
public class GameService {
    private List<String> shipSize;
    private List<Board> boardList;
    private List<Position> positionList;
    private Board boardPlayer1;
    private Board boardPlayer2;
    private List<Set<Shot>> listSets;
    private Shot shot;


    GameService() {
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

    public List<Board> addShipToList(Ship ship) {

        if(ship.getLength() > 0 && ship.getPosition() != null) {
            if(checkIsOverTheLimitShip(boardPlayer1.getShips().size())) {

                boardPlayer1.addShip(ship.getLength(), ship.getXstart(),
                        ship.getYstart(), ship.getPosition());

            }else if(checkIsOverTheLimitShip(boardPlayer2.getShips().size())) {
                boardPlayer2.addShip(ship.getLength(), ship.getXstart(),
                        ship.getYstart(), ship.getPosition());
            }
        }

        return getBoardList();
    }

    public List<Board> addShotAtShip(Shot shot) {
        System.out.println("rozmiar setów przed dodaniem strzałów: "+ boardPlayer1.getOpponentShots().size() + "  drugi " +
                boardPlayer2.getOpponentShots().size());
        if(boardPlayer1.getOpponentShots().size() == boardPlayer2.getOpponentShots().size()) {
            boardPlayer2.shoot(shot);
        } else {
            boardPlayer1.shoot(shot);
        }
        System.out.println("rozmiar setów POOOOO dodaniem strzałów: "+ boardPlayer1.getOpponentShots().size() + "  drugi " +
                boardPlayer2.getOpponentShots().size());
        return getBoardList();
    }

    public List<Board> getShips() {
        return getBoardList();
    }

    public Boolean returnStatusGame() {
        return boardPlayer1.getIsFinished().get() || boardPlayer2.getIsFinished().get();
    }

    public int[] statisticsGame(Board board) {
        return board.statisticsShot();

    }

    public double getAccuracyShot(int totalShots, int hitShot) {
        return ((double) hitShot/totalShots) * 100;

    }

}
