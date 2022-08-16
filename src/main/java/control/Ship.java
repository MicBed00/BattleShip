package control;

public class Ship {
    private int length;
    private int xStart;
    private int yStart;
    private Position position;
    //private int hitCounter = 0; // -> boolean[length] -> hitCounter[1] = true
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


    int getXstart() {

        return xStart;
    }

    int getXend() {

        return position == Position.HORIZONTAL ? xStart - length + 1 : xStart;
    }

    int getYstart() {

        return yStart;
    }

    int getYend() {
        return position == Position.VERTICAL ? yStart + length - 1 : yStart;
    }

    public boolean isHit (int x, int y) {
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
            if(!hit)
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


    public boolean isColliding(int length, int x, int y, Position position) {
        if (this.xStart == x && this.yStart == y) {
            return true;
        }
        if (position == Position.HORIZONTAL) {
            if (this.yStart == y) {
                if (x - length < 0) return true;
                /* true to koliduje wstawiamy na prawo od istniejącego statku
                   false to koliduje wstawimy na lewo od istniejącego statku
                */
                return this.xStart < x ? this.xStart >= x - length : this.xStart - this.length <= x;
            }
        }
        if (position == Position.VERTICAL) {
            if (this.xStart == x) {
                if (y + length > SizeBoard.ROW.getSize()) return true;
                   /*true to koliduje. Wstaiamy poniżej istniejącym statkiem
                   true to koliduje. Wstawiamy nad istniejącego statku
                 */
                return this.yStart < y ? this.yStart + this.length >= y : this.yStart <= y + length;
            }
        }
        if (this.position != position) {// sprawdza czy statki nie przecinają
            if(Position.VERTICAL == this.position) {
                if (this.yStart <= y && this.yStart + this.length >= y && this.xStart < x) return this.xStart >= x - length;
            } else {
                if (this.xStart >= x && this.xStart - this.length <= x && this.yStart > y) return this.yStart <= y + length;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "typ: " + length + " masztowiec, pozycja:" +
                " x=" + xStart +
                ", y=" + yStart +
                ", position=" + position;
    }
}
