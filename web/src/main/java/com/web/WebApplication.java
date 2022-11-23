package com.web;

import com.web.configuration.WebDriverConfig;
import com.web.pages.AddShipPage;
import com.web.pages.HomePage;
import com.web.pages.ShootingPage;
import com.web.pages.StartGamePage;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//import static com.web.webDriver.WebDriverLibrary.getChromeDriver;


@SpringBootApplication
public class WebApplication {

//	@Autowired
//	static WebDriver webDriver;


//	@Autowired
//	static WebDriverConfig webDriverConfig;
//
//	@Autowired
//	static HomePage homePage;

	public static void main(String[] args) {
		SpringApplication.run(WebApplication.class, args);

		WebDriverConfig webDriverConfig = new WebDriverConfig();
		WebDriver webDriver = webDriverConfig.getChromeDriver();

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
