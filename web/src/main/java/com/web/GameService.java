package com.web;

import DataConfig.Position;
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
//        positionList = new ArrayList<Position>(Arrays.asList(Position.values()));
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

    public boolean checkIsEqualTheLimitShip(int size) {
        return size == ShipLimits.SHIP_LIMIT.getQty();
    }

//    public Ship convertToShip(ShipFacade shipFacade) {
//        String[] coorXY = shipFacade.getCoord().split(",");
//        int l = Integer.parseInt(shipFacade.getLength());
//        int x = Integer.parseInt(coorXY[0]);
//        int y = Integer.parseInt(coorXY[1]);
//        Position pos = Position.valueOf(shipFacade.getPosition());
//        return new Ship(l, x, y, pos);
//    }
}
