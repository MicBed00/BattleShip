package control;

import DataConfig.SizeBoard;
import exceptions.OutOfBoundsException;
import main.MainGame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import DataConfig.ShipSize;

import java.text.MessageFormat;
import java.util.*;

public class UI {
    private final Locale locale = new Locale(MainGame.currentLocal);
    public ResourceBundle getBundle() {
        return bundle;
    }
    private final ResourceBundle bundle = ResourceBundle.getBundle("Bundle", locale);
    private final Logger log = LoggerFactory.getLogger(UI.class);
    Scanner sc;

    public String messageBundle(String key, Object... arguments) {
        return MessageFormat.format(getString(key), arguments);
    }

    private String getString(String key) {
        return bundle.getString(key);
    }

    public String getStringOrFail() throws InputMismatchException {
        this.sc = new Scanner(System.in);
        String position = sc.nextLine();
        position = position.toUpperCase(Locale.ROOT);

        if (position.isEmpty()) {
            log.warn(messageBundle("emptyString"));
            throw new InputMismatchException(messageBundle("inputMismatchException"));
        }
        return position;
    }

    public int getInt() throws InputMismatchException, OutOfBoundsException {
        this.sc = new Scanner(System.in);
        int num = sc.nextInt();
        if(num > SizeBoard.ROW.getSize()) {
            log.warn(messageBundle("dataOut"));
            throw new OutOfBoundsException(messageBundle("outOfBounds"));
        }
        sc.nextLine();
        return num;
    }

    public int getLength() throws InputMismatchException, OutOfBoundsException {
        this.sc = new Scanner(System.in);
        int length = getInt();

        if (length < ShipSize.ONE.getSize() || length > ShipSize.FOUR.getSize()) {
            log.warn(messageBundle("dataOut"));
            throw new OutOfBoundsException(bundle.getString("outOfBounds"));
        }
        return length;
    }

    public String getPosition() throws InputMismatchException, OutOfBoundsException {
        this.sc = new Scanner(System.in);
        String position = getStringOrFail();
        position = position.toUpperCase(Locale.ROOT);

        if (!position.equals("V") && !position.equals("H")) {
            log.warn("Incorrect position {}", position);
            throw new OutOfBoundsException(messageBundle("outOfBoundsPosition"));
        }
        return position.equals("V") ? "VERTICAL" : "HORIZONTAL";
    }
}
