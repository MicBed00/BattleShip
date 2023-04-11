package com.web.configuration;

import dataConfig.Position;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GameSetupsDto {
    private List<Integer> shipSize;
    private List<Position> orientations;
    private int shipLimit;

    public GameSetupsDto() {
    }

    public GameSetupsDto(List<Integer> shipSize, List<Position> orientations, int shipLimit) {
        this.shipSize = shipSize;
        this.orientations = orientations;
        this.shipLimit = shipLimit;
    }

    public List<Integer> getShipSize() {
        return shipSize;
    }

    public List<Position> getOrientations() {
        return orientations;
    }

    public void setShipSize(List<Integer> shipSize) {
        this.shipSize = shipSize;
    }

    public void setOrientations(List<Position> orientations) {
        this.orientations = orientations;
    }

    public int getShipLimit() {
        return shipLimit;
    }

    public void setShipLimit(int shipLimit) {
        this.shipLimit = shipLimit;
    }
}
