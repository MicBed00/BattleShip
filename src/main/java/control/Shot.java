package control;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

public class Shot {
    private int x;
    private int y;

    public Shot(int x, int y) {
        this.x = x;
        this.y = y;
    }
//   // @JsonCreator
    public Shot(String s) {
        String[] key = s.split(",");
        this.x = Integer.parseInt(key[0]);
        this.y = Integer.parseInt(key[1]);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Shot shot = (Shot) o;
        return x == shot.x && y == shot.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    @JsonValue
    public String toString() {
        return    x + "," + y;
    }
}
