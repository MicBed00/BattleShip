package com.web.springTest.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShootingPage extends BasePage {


    public ShootingPage( ) {
        super();
    }

    @FindBy(id = "buttonPly1")
    private List<WebElement> buttonsBoards1;

    @FindBy(id = "buttonPly2")
    private List<WebElement> buttonsBoards2;

    //zamiast try catch mogę skorzystać z metody WebWaiter i poczekaż az element się pojawi i dopiero klikac.
    public GameOver shots() {

        for(int i = 0; i < buttonsBoards2.size(); i ++) {
            try {
                buttonsBoards2.get(i).click();
            }
            catch(org.openqa.selenium.StaleElementReferenceException ex)
            {    buttonsBoards2.get(i).click();

            }
            try {
                buttonsBoards1.get(i).click();
            }
            catch(org.openqa.selenium.StaleElementReferenceException ex)
            {    buttonsBoards1.get(i).click();
            }
        }

        return new GameOver();
    }

}
