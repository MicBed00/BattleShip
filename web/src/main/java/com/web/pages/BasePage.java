package com.web.pages;

import com.web.WebApplication;
import com.web.libraries.WebDriverLibrary;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

//import static com.web.webDriver.WebDriverLibrary.getChromeDriver;


public class BasePage {



    public BasePage(WebDriver webDriver) {
        PageFactory.initElements(webDriver, this);
//        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//        waitForVisibilityOfElement(pageContent);

    }

//    @FindBy()
//    pageContent

}
