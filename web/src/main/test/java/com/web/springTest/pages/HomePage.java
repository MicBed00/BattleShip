package com.web.springTest.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.stereotype.Component;

@Component
public class HomePage extends BasePage{

    public HomePage() {
        super();
    }

    @FindBy(id = "startButton")
    private WebElement startClickHereGameButton;


    public AddShipPage enterToGame() {
      startClickHereGameButton.click();

      return new AddShipPage();
    }

}
