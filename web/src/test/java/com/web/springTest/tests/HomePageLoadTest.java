package com.web.springTest.tests;

import com.web.springTest.config.TestConfig;
import com.web.springTest.pages.AddShipPage;
import com.web.springTest.pages.HomePage;
import com.web.springTest.pages.ShootingPage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.concurrent.TimeUnit;

//import static com.web.springTest.config.WebDriverSingleton.getDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HomePageLoadTest{
//    public static WebDriver webDriver;
//
//    @BeforeAll
//    public static void setUp() {
//        String baseURL = "http://localhost:8080/view/welcomeView";
//        webDriver = getDriver();
//        webDriver.manage().window().maximize();
//        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
//        webDriver.get(baseURL);
//    }
//
//    @Test
//    public void loadingHomePage() {
//        String expected = webDriver.findElement(By.id("title")).getText();
//
//        assertEquals(expected, "Battle Ship!");
//    }

//    @Test
//    public void shouldEnterToAddingShipPage() {
//        AddShipPage shootingPage = new HomePage().enterToGame();
//
//        assertEquals(shootingPage.getClass(). );
//    }

//    @AfterAll
//    public static void tearDown() {
//        webDriver.quit();
//    }
}
