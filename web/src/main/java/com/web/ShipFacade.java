package com.web;


public class ShipFacade {
    private String length;
    private String xstart;
    private String ystart;
    private String position;

    public ShipFacade() {
    }

    public ShipFacade(String length, String xStart, String yStart, String position) {
        this.length = length;
        this.xstart = xStart;
        this.ystart = yStart;
        this.position = position;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getXstart() {
        return xstart;
    }

    public void setXstart(String xstart) {
        this.xstart = xstart;
    }

    public String getYstart() {
        return ystart;
    }

    public void setYstart(String ystart) {
        this.ystart = ystart;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "ShipFacade{" +
                "length=" + length +
                ", xStart=" + xstart +
                ", yStart=" + ystart +
                ", position=" + position +
                '}';
    }
}
