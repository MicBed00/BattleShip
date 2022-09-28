package board;

 public enum ShipSize {
    FOUR(4),
    THREE(3),
    TWO(2),
    ONE(1);

    int size;
    ShipSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
