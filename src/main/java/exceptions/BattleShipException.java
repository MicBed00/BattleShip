package exceptions;

public abstract class BattleShipException extends RuntimeException {

    public BattleShipException(String message) {
        super(message);
    }
}
