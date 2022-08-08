package control;

public class Ship {
    private int length;
    private int x;
    private int y;
    private Position position;
    private int hitCounter = 0; // -> boolean[length] -> hitCounter[1] = true
    // |-|-|-|-|
    // | |1| |1|
    // |-|-|-|-|


    public Ship(int length, int x, int y, Position position) {
        this.length = length;
        this.x = x;
        this.y = y;
        this.position = position;

    }

    int getXstart() {
        return x;
    }

    int getXend() {
        return position == Position.HORIZONTAL ? x + length : x;
    }

    int getYstart() {
        return y;
    }

    int getYend() {
        return position == Position.VERTICAL ? y + length : y;
    }

    public boolean isHit (int x, int y) {
        /*
        @Słowo "Miss!" wyświetla się po trafieniu. Kazdy obiekt zapisany w liscie iteruje po tej metodzie i zwraca Miss, gdy nie trafimy
        */
        if (this.position == Position.HORIZONTAL) {
            if (this.y == y && this.x >= x && this.x - this.length <= x) {
                hitCounter++;                                                       //zlicza trafione strzały przypadające na statek
                System.out.println("Hit!");
                return true;
            }
        }
        if (this.position == Position.VERTICAL) {
            if (this.x == x && this.y <= y && this.y + this.length >= y) {
                hitCounter++;
                System.out.println("Hit!");
                return true;
            }
        }
        return false;
    }

    public boolean isDead() {
        return this.hitCounter == this.length;
    }


    public void setHitCounter(int hitCounter) {
        this.hitCounter = hitCounter;
    }

    public int getLength() {
        return length;
    }

    public Position getPosition() {
        return position;
    }

    public int x() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isColliding(int length, int x, int y, Position position) {
        if (this.x == x && this.y == y) {
            return true;
        }
        if (position == Position.HORIZONTAL) {
            if (this.y == y) {
                if (x - length < 0) return true;
                /* true to koliduje wstawiamy na prawo od istniejącego statku
                   false to koliduje wstawimy na lewo od istniejącego statku
                */
                return this.x < x ? this.x >= x - length : this.x - this.length <= x;
            }
        }
        if (position == Position.VERTICAL) {
            if (this.x == x) {
                if (y + length > SizeBoard.ROW.getSize()) return true;
                   /*true to koliduje. Wstaiamy poniżej istniejącym statkiem
                   true to koliduje. Wstawiamy nad istniejącego statku
                 */
                return this.y < y ? this.y + this.length >= y : this.y <= y + length;
            }
        }
        if (this.position != position) {// sprawdza czy statki nie przecinają
            if(Position.VERTICAL == this.position) {
                if (this.y <= y && this.y + this.length >= y && this.x < x) return this.x >= x - length;
            } else {
                if (this.x >= x && this.x - this.length <= x && this.y > y) return this.y <= y + length;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "length=" + length +
                ", x=" + x +
                ", y=" + y +
                ", position=" + position;
    }
}
