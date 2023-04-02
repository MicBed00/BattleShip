package com.web.services;

import board.Board;
import board.Shot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.AccessType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShootingService {
    private SavedGameService savedGameService;

    @Autowired
    public ShootingService(SavedGameService savedGameService) {
        this.savedGameService = savedGameService;
    }

    public Boolean checkIfAllShipsAreHitted(long gameId) {
        List<Board> boardList = savedGameService.getBoardsList(gameId);
        return boardList.get(0).getIsFinished().get() || boardList.get(1).getIsFinished().get();
    }

    public List<Board> addShotAtShip(Shot shot, long gameId) {
        List<Board> boardList = savedGameService.getBoardsList(gameId);

        if (boardList.get(0).getOpponentShots().size() == boardList.get(1).getOpponentShots().size()) {
            boardList.get(1).shoot(shot);
        } else {
            boardList.get(0).shoot(shot);
        }
        return boardList;
    }
}
