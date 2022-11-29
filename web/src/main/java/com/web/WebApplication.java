package com.web;

import com.web.configuration.WebDriverConfig;
import com.web.pages.AddShipPage;
import com.web.pages.HomePage;
import com.web.pages.ShootingPage;
import com.web.pages.StartGamePage;
import org.openqa.selenium.WebDriver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class WebApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebApplication.class, args);

		WebDriverConfig driver = new WebDriverConfig();
		WebDriver webDriver = driver.getChromeDriver();
		webDriver.manage().window().maximize();
		webDriver.get("http://localhost:8080/view/welcomeView");
		
		HomePage homePage = new HomePage(webDriver);
		AddShipPage addShipPage = homePage.enterToGame(webDriver);
		AddShipPage addShipPage2 = addShipPage.addShipBoard1(webDriver);

		StartGamePage startGamePage = addShipPage2.addShipBoard2(webDriver);
		ShootingPage shootingPage = startGamePage.startGame(webDriver);

		shootingPage.shots();


	}

}
