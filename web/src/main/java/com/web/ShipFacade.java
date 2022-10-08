package com.web;

import DataConfig.Position;

public class ShipFacade {
    private  int length;
    private  int xStart;
    private  int yStart;
    private int position;

    public ShipFacade() {
    }

    public ShipFacade(int length, int xStart, int yStart, int position) {
        this.length = length;
        this.xStart = xStart;
        this.yStart = yStart;
        this.position = position;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getxStart() {
        return xStart;
    }

    public void setxStart(int xStart) {
        this.xStart = xStart;
    }

    public int getyStart() {
        return yStart;
    }

    public void setyStart(int yStart) {
        this.yStart = yStart;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "ShipFacade{" +
                "length=" + length +
                ", xStart=" + xStart +
                ", yStart=" + yStart +
                ", position=" + position +
                '}';
    }
}
