package board;

import DataConfig.SizeBoard;
import exceptions.OutOfBoundsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import DataConfig.ShipSize;

import java.text.MessageFormat;
import java.util.*;

public class UI {
    public Locale local = new Locale("en");
    ResourceBundle bundle = ResourceBundle.getBundle("Bundle", local);
   // public ResourceBundle bundle = ResourceBundle.getBundle("Bundle", local);
    private final Logger log = LoggerFactory.getLogger(UI.class);
    public Scanner sc;

    public ResourceBundle getBundle() {
        return bundle;
    }

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
            log.warn("emptyString");
            throw new InputMismatchException(messageBundle("inputMismatchException"));
        }
        return position;
    }

    public int getInt() throws InputMismatchException, OutOfBoundsException {
        this.sc = new Scanner(System.in);
        int num = sc.nextInt();
        if(num > SizeBoard.ROW.getSize()) {
            log.warn("dataOut");
            throw new OutOfBoundsException(messageBundle("outOfBounds"));
        }
        sc.nextLine();
        return num;
    }

    public int getLength() throws InputMismatchException, OutOfBoundsException {
        this.sc = new Scanner(System.in);
        int length = getInt();

        if (length < ShipSize.ONE.getSize() || length > ShipSize.FOUR.getSize()) {
            log.warn("dataOut");
            throw new OutOfBoundsException(bundle.getString("outOfBounds"));
        }
        return length;
    }

    public String getPosition() throws InputMismatchException, OutOfBoundsException {
        this.sc = new Scanner(System.in);
        String position = getStringOrFail();
        position = position.toUpperCase(Locale.ROOT);

        if (!position.equals("V") && !position.equals("H")) {
            throw new OutOfBoundsException(messageBundle("outOfBoundsPosition"));
        }
        return position.equals("V") ? "VERTICAL" : "HORIZONTAL";
    }
}
