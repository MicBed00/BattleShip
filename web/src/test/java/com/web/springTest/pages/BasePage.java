package com.web.springTest.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static com.web.springTest.config.WebDriverSingleton.getDriver;

public class BasePage {

    public BasePage() {
        PageFactory.initElements(getDriver(), this);
        getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(15));
    }

}
