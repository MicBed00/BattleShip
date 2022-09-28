package board;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class UI {
    public Locale local = new Locale("en");
    private final ResourceBundle bundle = ResourceBundle.getBundle("Bundle", local);

    public ResourceBundle getBundle() {
        return bundle;
    }

    public String messageBundle(String key, Object... arguments) {
        return MessageFormat.format(getString(key), arguments);
    }

    private String getString(String key) {
        return bundle.getString(key);
    }

}
