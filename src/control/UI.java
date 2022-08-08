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
    sc.close();
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
}
