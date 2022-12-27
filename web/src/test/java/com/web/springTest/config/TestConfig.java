package com.web.springTest.config;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.WebDriver;
import org.springframework.test.context.event.annotation.AfterTestMethod;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.concurrent.TimeUnit;

import static com.web.springTest.config.WebDriverSingleton.getDriver;


public class TestConfig {
    public static WebDriver webDriver;

    @BeforeAll
    public static void setUp() {
        String baseURL = "http://localhost:8080/view/welcomeView";

        webDriver = getDriver();
        webDriver.manage().window().maximize();
        webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        webDriver.get(baseURL);
    }
    @AfterAll
    public static void tearDown() {
        webDriver.quit();
    }

}
