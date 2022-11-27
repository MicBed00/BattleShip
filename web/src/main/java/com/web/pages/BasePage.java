package com.web.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class BasePage {

    public BasePage(WebDriver webDriver) {
        PageFactory.initElements(webDriver, this);
//        waitForVisibilityOfElement(pageContent);

    }

}
