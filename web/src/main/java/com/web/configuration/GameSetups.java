package com.web.configuration;

import dataConfig.Position;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

public class GameSetups {
    private List<Integer> shipSize;
    private List<Position> orientations;
    private  int shipLimit;

    public GameSetups() {
    }

    public GameSetups(List<Integer> shipSize, List<Position> orientations, int shipLimit) {
        this.shipSize = shipSize;
        this.orientations = orientations;
        this.shipLimit = shipLimit;
    }

    public void setShipSize(List<Integer> shipSize) {
        this.shipSize = shipSize;
    }

    public void setOrientations(List<Position> orientations) {
        this.orientations = orientations;
    }
    public List<Integer> getShipSize() {
        return shipSize;
    }

    public List<Position> getOrientations() {
        return orientations;
    }

    public int getShipLimit() {
        return shipLimit;
    }

    public void setShipLimit(int shipLimit) {
        this.shipLimit = shipLimit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameSetups that = (GameSetups) o;
        return shipLimit == that.shipLimit && Objects.equals(shipSize, that.shipSize) && Objects.equals(orientations, that.orientations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shipSize, orientations, shipLimit);
    }


}
