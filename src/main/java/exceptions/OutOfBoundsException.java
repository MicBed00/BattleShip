package exceptions;

import exceptions.BattleShipException;

public class OutOfBoundsException extends BattleShipException {
    public OutOfBoundsException(String message) {
        super(message);
    }

}
