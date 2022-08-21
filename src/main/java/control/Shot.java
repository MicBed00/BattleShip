package control;

import java.util.Objects;

public class Shot {
    private final int x;
    private final int y;

    public Shot(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
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
}
