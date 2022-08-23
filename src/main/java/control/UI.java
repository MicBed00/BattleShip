package control;

import DataConfig.SizeBoard;
import exceptions.OutOfBoundsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import DataConfig.ShipSize;

import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

public class UI {
    private final Logger log = LoggerFactory.getLogger(UI.class);
    Scanner sc;

    public UI() {
        this.sc = new Scanner(System.in);
    }

    public String getStringOrFail() throws InputMismatchException {
        String position = sc.nextLine();
        position = position.toUpperCase(Locale.ROOT);

        if (position.isEmpty()) {
            log.warn("Empty string");
            throw new InputMismatchException("String nie może być pusty. Wciśnij enter i wprowadź ponownie dane.");
        }
        return position;
    }

    public int getInt() throws InputMismatchException, OutOfBoundsException {
        int num = sc.nextInt();
        if(num > SizeBoard.ROW.getSize()) {
            log.warn("Date out of range");
            throw new OutOfBoundsException("Dane poza zakresem. Wciśnij enter i wprowadź ponownie dane.");
        }
        sc.nextLine();
        return num;
    }

    public int getLength() throws InputMismatchException, OutOfBoundsException {
        int length = getInt();

        if (length < ShipSize.ONE.getSize() || length > ShipSize.FOUR.getSize()) {
            log.warn("Date out of range length: {}", length);
            throw new OutOfBoundsException("Dane poza zakresem. Wciśnij enter i wprowadź ponownie dane.");
        }
        return length;
    }

    public String getPosition() throws InputMismatchException, OutOfBoundsException {
        String position = getStringOrFail();
        position = position.toUpperCase(Locale.ROOT);

        if (!position.equals("V") && !position.equals("H")) {
            log.warn("Incorrect position {}", position);
            throw new OutOfBoundsException("Niepoprawna pozycja. Wciśnij enter i wprowadź ponownie dane.");
        }
        return position.equals("V") ? "VERTICAL" : "HORIZONTAL";
    }
}
