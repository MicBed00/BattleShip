package battelshipgame.ships;

import battelshipgame.ships.Position;

public class Ship {
    private int length;
    private int x;
    private int y;
    private Position position;
    private int hitCounter = 0;

    public Ship(int length, int x, int y, Position position) {
        this.length = length;
        this.x = x;
        this.y = y;
        this.position = position;

    }
    public boolean isHit (int x, int y) {
        /*
        Trzeba jeszcze sprawdzić czy trafienie  x i y nie zostało już wcześniej zarejestrowane.
        Dwa sposoby:
        1. Zapisaywać trafienie jako obiekt Shot do listHit i najpierw iterować po liście sprawdzając czy nie ma już trafienia
        2. Po trafieniu zmieniać symbol pola na planszy (np. * -> #) i porównywać symbole
        */
        if (this.position == Position.HORIZONTAL) {
            if (this.y == y && this.x >= x && this.x - this.length <= x) {
                hitCounter++;
                System.out.println("Hit!");
                return hitCounter == this.length;
            } else {
                System.out.println("Miss!");
            }
        }
        if (this.position == Position.VERTICAL) {
            if (this.x == x && this.y <= y && this.y + this.length >= y) {
                hitCounter++;
                System.out.println("Hit!");
                return hitCounter == this.length;
            } else {
                System.out.println("Miss!");
            }
        }
        return hitCounter == this.length;
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

    public int getX() {
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
                // true to koliduje wstawiamy na prawo od istniejącego statku
                //false to koliduje wstawimy na lewo od istniejącego statku
                return this.x < x ? this.x >= x - length : this.x - this.length <= x;
            }
        }
        if (position == Position.VERTICAL) {
            if (this.x == x)
                // if (y + length > SIZE_BOARD) return true;  // przekroczenie rozmiaru planszy - brak zadeklarowanego SIZE_BOARD
                // true to koliduje. Wstaiamy poniżej istniejącym statkiem
               // true to koliduje. Wstawiamy nad istniejącego statku
                return this.y < y ? this.y + this.length >= y : this.y <= y + length;
        }
        if (this.position != position) {            // sprawdza czy statki nie przecinają
            if (this.y <= y && this.y + this.length >= y && this.x < x) return this.x >= x - length;
            if (this.x >= x && this.x - this.length <= x && this.y > y) return this.y <= y + length;
        }
        return false;
    }

//    @Override
//    public String toString() {
//        return "length=" + length +
//                ", x=" + x +
//                ", y=" + y +
//                ", position=" + position +
//                '}';
//    }
}
