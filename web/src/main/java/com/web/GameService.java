package com.web;

import DataConfig.ShipLimits;
import board.Board;
import org.springframework.stereotype.Service;
import ship.Ship;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class GameService {
    List<String> shipSize;
    List<Board> boardList;
    List<String> positionList;
    Board boardPlayer1;
    Board boardPlayer2;
    RenderBoard renderBoard;
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
        renderBoard = new RenderBoard();
    }

    public boolean checkIsOverTheLimitShip(int size){
        return size < ShipLimits.SHIP_LIMIT.getQty();
    }

    public boolean checkIsEqualTheLimitShip(int size) {
        return size == ShipLimits.SHIP_LIMIT.getQty();
    }

    public List<String> getShipSize() {
        return shipSize;
    }

    public int getSizeQtyShips(int player) {
        return boardList.get(player).getShips().size();
    }
    public List<String> getOrientation() {
        return positionList;
    }
    public boolean shouldRender(int x, int y) {
        AtomicBoolean result = new AtomicBoolean(false);
        List<Ship> list;
       list =  boardPlayer1.getShips().size() < ShipLimits.SHIP_LIMIT.getQty() ?
            boardPlayer1.getShips() :  boardPlayer2.getShips();

        list.forEach(s -> {
            if(s.checkIfShipIsPreset(x, y)) {
                result.set(true);
            }
        });
        return result.get();
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
