package ship;


import DataConfig.Position;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import main.MainGame;

import java.beans.ConstructorProperties;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class Ship {
    private static final Locale locale = new Locale(MainGame.currentLocal);
    private static final ResourceBundle bundle = ResourceBundle.getBundle("Bundle", locale);
    private  int length;
    private  int xStart;
    private  int yStart;
    private Position position;
    private boolean[] hits;
    // |-|-|-|-|
    // | |1| |1|
    // |-|-|-|-|


   public Ship() {

   }
    public Ship(int length, int x, int y, Position position) {
        this.length = length;
        this.xStart = x;
        this.yStart = y;
        this.position = position;
        this.hits = new boolean[length];
    }

    @JsonSetter("xstart")           //w trakcie deserializacji nie rozpoznawało mi nazwy pola
    public void setxStart(int xStart) {
        this.xStart = xStart;
    }
    @JsonSetter("ystart")
    public void setyStart(int yStart) {
        this.yStart = yStart;
    }

    public int getXstart() {
        return xStart;
    }
    @JsonIgnore
    public int getXend() {
        return position == Position.HORIZONTAL ? xStart - length + 1 : xStart;
    }

    public int getYstart() {
        return yStart;
    }
    @JsonIgnore
    public int getYend() {
        return position == Position.VERTICAL ? yStart + length - 1 : yStart;
    }

    public boolean[] getHits() {
        return hits;
    }

    public void setHits(boolean[] hits) {
        this.hits = hits;
    }

    public boolean checkIfHit(int x, int y) {
        if (this.position == Position.HORIZONTAL) {
            if (ifTheShipHit(x, y)) {
                hits[getXstart() - x] = true;
                return true;
            }
        }

        if (this.position == Position.VERTICAL) {
            if (ifTheShipHit(x, y)) {
                hits[y - getYstart()] = true;
                return true;
            }
        }
        return false;
    }

    private boolean ifTheShipHit(int x, int y) {
        return getYstart() == y && getXstart() >= x && getXend() <= x
                || getXstart() == x && getYstart() <= y && getYend() >= y;
    }

    public boolean checkIfDead() {
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
        return "type" + length + " masztowy, położenie" +
                " x=" + xStart +
                ", y=" + yStart +
                "position" + "=" + position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ship ship = (Ship) o;
        return length == ship.length && xStart == ship.xStart && yStart == ship.yStart && position == ship.position && Arrays.equals(hits, ship.hits);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(length, xStart, yStart, position);
        result = 31 * result + Arrays.hashCode(hits);
        return result;
    }
}
