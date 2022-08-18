package board;

public enum SizeBoard {
    ROW(10),
    COLUMNE(10);

    private int size;

    public int getSize() {
        return size;
    }

    SizeBoard(int size) {
        this.size = size;
    }
}
