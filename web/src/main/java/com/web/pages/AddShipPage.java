package com.web.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
public class AddShipPage extends  BasePage {


    public AddShipPage(WebDriver webDriver) {
        super(webDriver);

    }
    @FindBy(id = "len")
    private WebElement lengthSelect;

    @FindBy(id = "orientation")
    private WebElement orientationSelect;

    @FindBy(id = "4&4")
    private WebElement buttonOnBoard1;

    @FindBy(id = "1&2")
    private WebElement buttonOnBoard2;

    @FindBy(id = "8&8")
    private WebElement buttonOnBoard3;

    @FindBy(id = "7&2")
    private WebElement buttonOnBoard4;

    @FindBy(id = "5&0")
    private WebElement buttonOnBoard5;

    @FindBy(id = "accept")
    private WebElement acceptButton;



    public AddShipPage addShipBoard1(WebDriver webDriver) {
        buttonOnBoard1.click();
        new Select(lengthSelect).selectByValue("4");
        new Select(orientationSelect).selectByValue("VERTICAL");
        try {
            buttonOnBoard2.click();
        }
        catch(org.openqa.selenium.StaleElementReferenceException ex)
        {
            buttonOnBoard2.click();
        }
        new Select(lengthSelect).selectByValue("3");
        new Select(orientationSelect).selectByValue("HORIZONTAL");
        buttonOnBoard3.click();
        new Select(lengthSelect).selectByValue("2");
        new Select(orientationSelect).selectByValue("HORIZONTAL");
        buttonOnBoard4.click();
        buttonOnBoard5.click();
        acceptButton.click();

        return new AddShipPage(webDriver);
    }

    public StartGamePage addShipBoard2(WebDriver webDriver) {
        new Select(orientationSelect).selectByValue("HORIZONTAL");
        new Select(lengthSelect).selectByValue("4");
        buttonOnBoard1.click();
        buttonOnBoard2.click();
        new Select(lengthSelect).selectByValue("2");
        new Select(orientationSelect).selectByValue("HORIZONTAL");
        buttonOnBoard3.click();
        buttonOnBoard4.click();
        new Select(lengthSelect).selectByValue("3");
        new Select(orientationSelect).selectByValue("HORIZONTAL");
        buttonOnBoard5.click();
        acceptButton.click();

        return new StartGamePage(webDriver);
    }

}
