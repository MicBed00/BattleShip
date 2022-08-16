package control;

import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

class UI {

  Scanner sc;

  public UI() {
    this.sc = new Scanner(System.in);
  }


  public String getStringOrFail() throws InputMismatchException {
    String position = sc.nextLine();
    position = position.toUpperCase(Locale.ROOT);
    if (position.isEmpty()) {
      throw new RuntimeException("String nie może być pusty");
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
       throw new OutOfBoundsException("Dane poza zakresem");
      }
      return length;
  }

  public String getPosition() throws InputMismatchException, OutOfBoundsException {
      String position = getStringOrFail();
      position = position.toUpperCase(Locale.ROOT);
      if (!position.equals("V") && !position.equals("H")) {
        throw new OutOfBoundsException("Niepoprawna pozycja");
      }
      return position.equals("V") ? "VERTICAL" : "HORIZONTAL";
  }
}
