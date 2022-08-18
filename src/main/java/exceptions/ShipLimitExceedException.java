package exceptions;

import exceptions.BattleShipException;

public class ShipLimitExceedException extends BattleShipException {
    public ShipLimitExceedException(String message) {
        super(message);
    }
}
