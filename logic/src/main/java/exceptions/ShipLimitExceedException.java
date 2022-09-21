package exceptions;

public class ShipLimitExceedException extends BattleShipException {

    public ShipLimitExceedException(String message) {
        super(message);
    }
}
