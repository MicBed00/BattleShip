package com.web.springTest.tests;

import com.web.service.GameService;
import com.web.springTest.config.TestConfig;
import exceptions.CollidingException;
import exceptions.ShotSamePlaceException;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.concurrent.TimeUnit;

//import static com.web.springTest.config.WebDriverSingleton.getDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AddShipTest {

//    public static WebDriver webDriver;
//
//    @BeforeAll
//    public static void setUp() {
//        String baseURL = "http://localhost:8080/view/startGame";
//        webDriver = getDriver();
//        webDriver.manage().window().maximize();
//        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
//        webDriver.get(baseURL);
//    }
//    @AfterAll
//    public static void tearDown() {
//        webDriver.quit();
//    }

//    @Test
//    public void shouldAddShip() {
//        //given
//        WebElement firstClick = webDriver.findElement(By.id("9&7"));
//        WebElement secondClick = webDriver.findElement(By.id("9&7"));
//        //when
//        firstClick.click();
//        webDriver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
//        secondClick.click();
//        Alert alert = webDriver.switchTo().alert();
//
//        String exceptionText = webDriver.switchTo().alert().getText();
//        String expectedText = "exceptions.CollidingException: Collision with Ship{length=1, xStart=4, yStart=3, position=VERTICAL}! Press enter and re-enter the data";
//        //then
//        assertEquals(expectedText, exceptionText);
//    }

}
