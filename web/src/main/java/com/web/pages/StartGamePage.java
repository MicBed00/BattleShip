package com.web.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StartGamePage extends BasePage {


    public StartGamePage(WebDriver webDriver) {
        super(webDriver);
    }

    @FindBy(id = "startGame")
    private WebElement startGameButton;


    public ShootingPage startGame(WebDriver webDriver) {
        startGameButton.click();

        return new ShootingPage(webDriver);
    }
}
