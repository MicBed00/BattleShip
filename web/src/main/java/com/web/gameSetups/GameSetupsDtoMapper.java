package com.web.gameSetups;

import org.springframework.stereotype.Service;

@Service
public class GameSetupsDtoMapper {

    public GameSetups mapper(GameSetupsDto dto) {
        GameSetups gameSetups = new GameSetups();
        gameSetups.setOrientations(dto.getOrientations());
        gameSetups.setShipSize(dto.getShipSize());
        gameSetups.setShipLimit(dto.getShipLimit());

        return gameSetups;
    }
}
