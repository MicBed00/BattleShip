package control;

import exceptions.OutOfBoundsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ship.ShipSize;

import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

class UI {
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
      System.out.println("Wciśnij enter");
      throw new InputMismatchException("String nie może być pusty");
    }
    return position;
  }

  public int getInt() throws InputMismatchException{
    int num = sc.nextInt();
    sc.nextLine();
    return num;
  }
  public int getLength() throws InputMismatchException, OutOfBoundsException {
      int length = getInt();
      if (length < ShipSize.ONE.getSize() || length > ShipSize.FOUR.getSize()) {
       log.warn("Date out of range length: {}", length);
       System.out.println("Wciśnij enter");
       throw new OutOfBoundsException("Dane poza zakresem");
      }
      return length;
  }

  public String getPosition() throws InputMismatchException, OutOfBoundsException {
      String position = getStringOrFail();
      position = position.toUpperCase(Locale.ROOT);
      if (!position.equals("V") && !position.equals("H")) {
        log.warn("Incorrect position {}", position);
        System.out.println("Wciśnij enter");
        throw new OutOfBoundsException("Niepoprawna pozycja");
      }
      return position.equals("V") ? "VERTICAL" : "HORIZONTAL";
  }
}
