package control;

import DataConfig.SizeBoard;
import exceptions.OutOfBoundsException;
import main.MainGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import DataConfig.ShipSize;

import java.util.InputMismatchException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

public class UI {
    private final Locale locale = new Locale(MainGame.currentLocal);
    private final ResourceBundle bundle = ResourceBundle.getBundle("Bundle", locale);
    private final Logger log = LoggerFactory.getLogger(UI.class);
    Scanner sc;

    public UI() {
        this.sc = new Scanner(System.in);
    }

    public String getStringOrFail() throws InputMismatchException {
        String position = sc.nextLine();
        position = position.toUpperCase(Locale.ROOT);

        if (position.isEmpty()) {
            log.warn(bundle.getString("emptyString"));
            throw new InputMismatchException(bundle.getString("inputMismatchException"));
        }
        return position;
    }

    public int getInt() throws InputMismatchException, OutOfBoundsException {
        int num = sc.nextInt();
        if(num > SizeBoard.ROW.getSize()) {
            log.warn(bundle.getString("dataOut"));
            throw new OutOfBoundsException(bundle.getString("outOfBounds"));
        }
        sc.nextLine();
        return num;
    }

    public int getLength() throws InputMismatchException, OutOfBoundsException {
        int length = getInt();

        if (length < ShipSize.ONE.getSize() || length > ShipSize.FOUR.getSize()) {
            log.warn(bundle.getString("dataOut") + " {}", length);
            throw new OutOfBoundsException(bundle.getString("outOfBounds"));
        }
        return length;
    }

    public String getPosition() throws InputMismatchException, OutOfBoundsException {
        String position = getStringOrFail();
        position = position.toUpperCase(Locale.ROOT);

        if (!position.equals("V") && !position.equals("H")) {
            log.warn("Incorrect position {}", position);
            throw new OutOfBoundsException(bundle.getString("outOfBoundsPosition"));
        }
        return position.equals("V") ? "VERTICAL" : "HORIZONTAL";
    }
}
