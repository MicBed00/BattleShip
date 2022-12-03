package com.web.springTest;

import com.web.springTest.pages.AddShipPage;
import com.web.springTest.pages.HomePage;
import com.web.springTest.pages.ShootingPage;
import com.web.springTest.pages.StartGamePage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static com.web.springTest.config.WebDriverSingleton.getDriver;

//import static com.web.springTest.config.WebDriverSingleton.getDriver;
//(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebApplicationTests {


	private static String baseURL = "http://localhost:8080/view/welcomeView";
	private static WebDriver webDriver;
	@BeforeAll
	public static void setUp() {
		WebDriverManager.chromedriver().setup();
//		webDriver = new ChromeDriver();
		webDriver = getDriver();
		webDriver.manage().window().maximize();
		webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		webDriver.get(baseURL);


	}
//	@AfterAll
//	public static void tearDown() {
//		webDriver.quit();
//	}


	@Test
	void scenerioTestGame() {
		HomePage homePage = new HomePage();
		AddShipPage addShipPage = homePage.enterToGame();
		AddShipPage addShipPage2 = addShipPage.addShipBoard1();

		StartGamePage startGamePage = addShipPage2.addShipBoard2();
		ShootingPage shootingPage = startGamePage.startGame();

		shootingPage.shots();
	}



}
