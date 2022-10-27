package com.web.service;

import DataConfig.ShipLimits;
import board.Board;
import org.springframework.stereotype.Service;
import ship.Ship;

import java.util.*;

@Service
public class GameService {
    List<String> shipSize;
    List<Board> boardList;
    List<String> positionList;
    Board boardPlayer1;
    Board boardPlayer2;

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
    }

    public boolean checkIsOverTheLimitShip(int size){
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

}
