package com.web;

import board.Board;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ship.Ship;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class RenderBoard {

//    public boolean shouldRender(int x, int y) {
//        AtomicBoolean result = new AtomicBoolean(false);
//        Board board = new Board();
//        List<Ship> list = board.getShips();
//
//        list.forEach(s -> {
//            if(s.getXstart() == x && s.getYstart() == y)
//                result.set(true);
//        });
//
//      return result.get();
//
//    }
}
