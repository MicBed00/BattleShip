public enum ShipLimits {
    SHIP_LIMIT(2),
    SHIP4SAIL(1),
    SHIP3SAIL(2),
    SHIP2SAIL(3),
    SHIP1SAIL(4);

    private int quantity;

    ShipLimits(int quantity) {
        this.quantity = quantity;
    }

    public int getQty() {
        return quantity;
    }
}
