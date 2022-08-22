package ship;

import control.UI;

public class Coordinates {
    UI user = new UI();
    private int x;
    private int y;


    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = user.getInt();
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = user.getLength();
    }
}
