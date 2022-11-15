package com.web.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;


public class HomePage extends BasePage{


    public HomePage(WebDriver webDriver) {
        super(webDriver);
    }


    @FindBy(id = "startButton")
    public WebElement startClickHereGameButton;


    public AddShipPage enterToGame(WebDriver webDriver) {
      startClickHereGameButton.click();

      return new AddShipPage(webDriver);
    }

}
