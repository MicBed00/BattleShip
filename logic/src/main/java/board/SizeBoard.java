package board;

public enum SizeBoard {
    MAX(20),
    MIN(7),
    DEFAULT(10);

    private int size;

    public int getSize() {
        return size;
    }

    SizeBoard(int size) {
        this.size = size;
    }
}
