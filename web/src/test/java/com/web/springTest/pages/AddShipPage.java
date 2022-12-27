package com.web.springTest.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Component;

@Component
public class AddShipPage extends  BasePage {


    public AddShipPage() {
        super();
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



    public AddShipPage addShipBoard1() {
        try {
            buttonOnBoard1.click();
        }
        catch(org.openqa.selenium.StaleElementReferenceException ex)
        {
            buttonOnBoard1.click();
        }
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
        try {
            buttonOnBoard3.click();
        }
        catch(org.openqa.selenium.StaleElementReferenceException ex)
        {
            buttonOnBoard3.click();
        }
        new Select(lengthSelect).selectByValue("2");
        new Select(orientationSelect).selectByValue("HORIZONTAL");
        try {
            buttonOnBoard4.click();
        }
        catch(org.openqa.selenium.StaleElementReferenceException ex)
        {
            buttonOnBoard4.click();
        }
        try {
            buttonOnBoard5.click();
        }
        catch(org.openqa.selenium.StaleElementReferenceException ex)
        {
            buttonOnBoard5.click();
        }
        acceptButton.click();

        return new AddShipPage();
    }

    public StartGamePage addShipBoard2() {
        try {
            new Select(orientationSelect).selectByValue("HORIZONTAL");
            new Select(lengthSelect).selectByValue("4");
            buttonOnBoard1.click();
        }
        catch(org.openqa.selenium.StaleElementReferenceException ex)
        {
            new Select(orientationSelect).selectByValue("HORIZONTAL");
            new Select(lengthSelect).selectByValue("4");
            buttonOnBoard1.click();
        }
        try {
            buttonOnBoard2.click();
        }
        catch(org.openqa.selenium.StaleElementReferenceException ex)
        {
            buttonOnBoard2.click();
        }
        try {
            new Select(lengthSelect).selectByValue("2");
            new Select(orientationSelect).selectByValue("HORIZONTAL");
            buttonOnBoard3.click();
        }
        catch(org.openqa.selenium.StaleElementReferenceException ex)
        {
            new Select(lengthSelect).selectByValue("2");
            new Select(orientationSelect).selectByValue("HORIZONTAL");
            buttonOnBoard3.click();
        }
        try {
            buttonOnBoard4.click();
        }catch(org.openqa.selenium.StaleElementReferenceException ex) {
            buttonOnBoard4.click();
        }
        try {
            new Select(lengthSelect).selectByValue("3");
            new Select(orientationSelect).selectByValue("HORIZONTAL");
            buttonOnBoard5.click();
        }catch(org.openqa.selenium.StaleElementReferenceException ex) {
            new Select(lengthSelect).selectByValue("3");
            new Select(orientationSelect).selectByValue("HORIZONTAL");
            buttonOnBoard5.click();
        }
        acceptButton.click();

        return new StartGamePage();
    }

}
