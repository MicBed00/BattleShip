package com.web;


public class ShipFacade {
    private String length;
    private String coord;
    private String position;

    public ShipFacade() {
    }

    public ShipFacade(String length, String coord, String position) {
        this.length = length;
        this.coord = coord;
        this.position = position;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getCoord() {
        return coord;
    }

    public void setCoord(String coord) {
        this.coord = coord;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
