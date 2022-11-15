package com.web.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ShootingPage extends BasePage {

    public ShootingPage(WebDriver webDriver) {
        super(webDriver);
    }

    //miss shoot on board2
    @FindBy( xpath = "//*[@id=\"tableBoardPly2\"]/tbody/tr[1]/td[1]/button")

    private WebElement missShot1B2;

    @FindBy( xpath = "//*[@id=\"tableBoardPly2\"]/tbody/tr[10]/td[10]/button")
    private WebElement missShot2B2;

    @FindBy( xpath = "//*[@id=\"tableBoardPly2\"]/tbody/tr[9]/td[1]/button")
    private WebElement missShot3B2;

    @FindBy( xpath = "//*[@id=\"tableBoardPly2\"]/tbody/tr[1]/td[9]/button")
    private WebElement missShot4B2;

    @FindBy( xpath = "//*[@id=\"tableBoardPly2\"]/tbody/tr[1]/td[2]/button")
    private WebElement missShot5B2;

    @FindBy( xpath = "//*[@id=\"tableBoardPly2\"]/tbody/tr[6]/td[5]/button")
    private WebElement missShot6B2;



    //miss shoot on board1

    @FindBy( xpath = "//*[@id=\"tableBoardPly1\"]/tbody/tr[1]/td[1]/button")
    private WebElement missShot1B1;

    @FindBy( xpath = "//*[@id=\"tableBoardPly1\"]/tbody/tr[10]/td[10]/button")
    private WebElement missShot2B1;

    @FindBy( xpath = "//*[@id=\"tableBoardPly1\"]/tbody/tr[9]/td[1]/button")
    private WebElement missShot3B1;

    @FindBy( xpath = "//*[@id=\"tableBoardPly1\"]/tbody/tr[1]/td[9]/button")
    private WebElement missShot4B1;

    @FindBy( xpath = "//*[@id=\"tableBoardPly1\"]/tbody/tr[1]/td[2]/button")
    private WebElement missShot5B1;

    @FindBy( xpath = "//*[@id=\"tableBoardPly1\"]/tbody/tr[6]/td[5]/button")
    private WebElement missShot6B1;


    //hit shots

    @FindBy( xpath = "//*[@id=\"tableBoardPly2\"]/tbody/tr[5]/td[5]/button")
    private WebElement hitShot1B2;

    @FindBy( xpath = "//*[@id=\"tableBoardPly2\"]/tbody/tr[4]/td[5]/button")
    private WebElement hitShot2B2;

    @FindBy( xpath = "//*[@id=\"tableBoardPly2\"]/tbody/tr[3]/td[5]/button")
    private WebElement hitShot3B2;

    @FindBy( xpath = "//*[@id=\"tableBoardPly2\"]/tbody/tr[2]/td[5]/button")
    private WebElement hitShot4B2;

    @FindBy( xpath = "//*[@id=\"tableBoardPly2\"]/tbody/tr[2]/td[2]/button")
    private WebElement hitShot5B2;

    @FindBy( xpath = "//*[@id=\"tableBoardPly2\"]/tbody/tr[9]/td[9]/button")
    private WebElement hitShot6B2;

    @FindBy( xpath = "//*[@id=\"tableBoardPly2\"]/tbody/tr[8]/td[9]/button")
    private WebElement hitShot7B2;

    @FindBy( xpath = "//*[@id=\"tableBoardPly2\"]/tbody/tr[8]/td[3]/button")
    private WebElement hitShot8B2;

    @FindBy( xpath = "//*[@id=\"tableBoardPly2\"]/tbody/tr[6]/td[1]/button")
    private WebElement hitShot9B2;

    @FindBy( xpath = "//*[@id=\"tableBoardPly2\"]/tbody/tr[5]/td[1]/button")
    private WebElement hitShot10B2;

    @FindBy( xpath = "//*[@id=\"tableBoardPly2\"]/tbody/tr[4]/td[1]/button")
    private WebElement hitShot11B2;



    @FindBy( xpath = "//*[@id=\"tableBoardPly1\"]/tbody/tr[5]/td[5]/button")
    private WebElement hitShot1B1;

    @FindBy( xpath = "//*[@id=\"tableBoardPly1\"]/tbody/tr[2]/td[2]/button")
    private WebElement hitShot2B1;

    @FindBy( xpath = "//*[@id=\"tableBoardPly1\"]/tbody/tr[2]/td[3]/button")

    private WebElement hitShot3B1;
    @FindBy( xpath = "//*[@id=\"tableBoardPly1\"]/tbody/tr[2]/td[4]/button")
    private WebElement hitShot4B1;

    @FindBy( xpath = "//*[@id=\"tableBoardPly1\"]/tbody/tr[2]/td[5]/button")
    private WebElement hitShot5B1;

    @FindBy( xpath = "//*[@id=\"tableBoardPly1\"]/tbody/tr[9]/td[9]/button")
    private WebElement hitShot6B1;

    @FindBy( xpath = "//*[@id=\"tableBoardPly1\"]/tbody/tr[8]/td[9]/button")
    private WebElement hitShot7B1;

    @FindBy( xpath = "//*[@id=\"tableBoardPly1\"]/tbody/tr[7]/td[9]/button")
    private WebElement hitShot8B1;

    @FindBy( xpath = "//*[@id=\"tableBoardPly1\"]/tbody/tr[8]/td[3]/button")
    private WebElement hitShot9B1;

    @FindBy( xpath = "//*[@id=\"tableBoardPly1\"]/tbody/tr[7]/td[3]/button")
    private WebElement hitShot10B1;

    @FindBy( xpath = "//*[@id=\"tableBoardPly1\"]/tbody/tr[6]/td[1]/button")
    private WebElement hitShot11B1;


    public void missShot() {
        missShot1B2.click();
        missShot1B1.click();

        missShot2B2.click();
        missShot2B1.click();

        missShot3B2.click();
        missShot3B1.click();

        missShot4B2.click();
        missShot4B1.click();

        missShot5B2.click();
        missShot5B1.click();

        missShot6B2.click();
        missShot6B1.click();

    }

    public void hitShot() {
        hitShot1B2.click();
        hitShot1B1.click();

        hitShot2B2.click();
        hitShot2B1.click();

        hitShot3B2.click();
        hitShot3B1.click();

        hitShot4B2.click();
        hitShot4B1.click();

        hitShot5B2.click();
        hitShot5B1.click();

        hitShot6B2.click();
        hitShot6B1.click();

        hitShot7B2.click();
        hitShot7B1.click();

        hitShot8B2.click();
        hitShot8B1.click();

        hitShot9B2.click();
        hitShot9B1.click();

        hitShot10B2.click();
        hitShot10B1.click();

        hitShot11B2.click();
        hitShot11B1.click();
    }







}
