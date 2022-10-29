package com.web.service;

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
    private List<String> positionList;
    private Board boardPlayer1;
    private Board boardPlayer2;
    private List<Set<Shot>> listSets;
    private Shot shot;
//    Map<String, Integer> shipLimits = new TreeMap<>()
    GameService() {
        shipSize = new ArrayList<>();
        boardList = new ArrayList<>();
        positionList = new ArrayList<>();
        positionList.add("VERTICAL");
        positionList.add("HORIZONTAL");
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

    private boolean checkIsOverTheLimitShip(int size){
        return size < ShipLimits.SHIP_LIMIT.getQty();
    }

    public List<String> getShipSize() {
        return shipSize;
    }

    public List<String> getOrientation() {
        return positionList;
    }


    public List<Board> getBoardList() {
        return boardList;
    }

    public List<Ship> addShipToList(Ship ship) {
        if(checkIsOverTheLimitShip(boardPlayer1.getShips().size())) {
            boardPlayer1.addShip(ship.getLength(), ship.getXstart(),
                                ship.getYstart(), ship.getPosition());
            return boardPlayer1.getShips();
        }else if(checkIsOverTheLimitShip(boardPlayer2.getShips().size())) {
            boardPlayer2.addShip(ship.getLength(), ship.getXstart(),
                                ship.getYstart(), ship.getPosition());
            return boardPlayer2.getShips();
        }
        return null;
    }

    public List<Set<Shot>> addShotatShip(Shot shot) {

        if(boardPlayer1.getOpponentShots().size() == boardPlayer2.getOpponentShots().size()) {
            boardPlayer2.shoot(shot);
        } else {
            boardPlayer1.shoot(shot);
        }
        return listSets;
    }

}
