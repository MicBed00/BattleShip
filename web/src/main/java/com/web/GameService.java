package com.web;

import board.Board;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GameService {

    List<Board> boardList = new ArrayList<>();
    Board boardPlayer1;
    Board boardPlayer2;

    GameService() {
        boardPlayer1 = new Board();
        boardPlayer2 = new Board();
        boardList.add(boardPlayer1);
        boardList.add(boardPlayer2);
    }

}
