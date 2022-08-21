package ship;

import board.SizeBoard;

public class Ship {
    private final int length;
    private final int xStart;
    private final int yStart;
    private final Position position;
    private boolean[] hits;
    // |-|-|-|-|
    // | |1| |1|
    // |-|-|-|-|


    public Ship(int length, int x, int y, Position position) {
        this.length = length;
        this.xStart = x;
        this.yStart = y;
        this.position = position;
        this.hits = new boolean[length];

    }

    public boolean[] getHits() {
        return hits;
    }


    public int getXstart() {

        return xStart;
    }

    public int getXend() {

        return position == Position.HORIZONTAL ? xStart - length + 1 : xStart;
    }

    public int getYstart() {

        return yStart;
    }

    public int getYend() {
        return position == Position.VERTICAL ? yStart + length - 1 : yStart;
    }

    public boolean isHit(int x, int y) {
        if (this.position == Position.HORIZONTAL) {
            if (getYstart() == y && getXstart() >= x && getXend() <= x) {
                hits[getXstart() - x] = true;
                return true;
            }
        }
        if (this.position == Position.VERTICAL) {
            if (getXstart() == x && getYstart() <= y && getYend() >= y) {
                hits[y - getYstart()] = true;
                return true;
            }
        }
        return false;
    }

    public boolean isDead() {
        for (boolean hit : hits) {
            if (!hit)
                return false;
        }
        return true;
    }

    public int getLength() {
        return length;
    }

    public Position getPosition() {
        return position;
    }


    @Override
    public String toString() {
        return "typ: " + length + " masztowiec, pozycja:" +
                " x=" + xStart +
                ", y=" + yStart +
                ", position=" + position;
    }
}
