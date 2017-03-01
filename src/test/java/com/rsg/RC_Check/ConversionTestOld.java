package com.rsg.RC_Check;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ConversionTestOld {
	static WebDriver driver;
	static WebDriverWait wait;
	Actions action;
	JavascriptExecutor jse;
	DBUtility db;
	Conversion conv;
	Logger log;
	Reach r;

	@BeforeClass
	public void setUP() {
		System.setProperty("webdriver.chrome.driver", "D://chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--disable-extensions");
		options.addArguments("--dns-prefetch-disable");
		String log4jConfPath = "./log4j.properties";
		PropertyConfigurator.configure(log4jConfPath);
		log = Logger.getLogger("devpinoyLogger");
		driver = new ChromeDriver(options);
		wait = new WebDriverWait(driver, 60);
		action = new Actions(driver);
		jse = (JavascriptExecutor) driver;
		conv = new Conversion();
		r = new Reach();
		driver.get(conv.baseUrl);
		driver.manage().window().maximize();
		driver.findElement(conv.username).sendKeys("ketansa@rsgsystems.com");
		driver.findElement(conv.password).sendKeys("password");
		driver.findElement(conv.signin).click();
	}

	@Test
	public void checkVisibilityOfRCGraphs() throws ClassNotFoundException, SQLException {
		db = new DBUtility();
		db.DBConnection();
		String[][] result = db.conversionProjects();
		System.out.println("Result " + result.length);
		task: for (int i = 0; i < result.length; i++) {
			driver.get(conv.reportUrl + result[i][1]);
			WebElement projectName;
			if (result[i][0].contains("-")) {
				result[i][0] = result[i][0].split("-")[0];
				driver.findElement(conv.typeSearch).sendKeys(result[i][0]);

				wait.until(ExpectedConditions
						.visibilityOfElementLocated(By.xpath(r.autoSuggestPath1 + result[i][0] + "\")]")));
				projectName = driver.findElement(By.xpath(r.autoSuggestPath1 + result[i][0] + "\")]"));
			} else {
				driver.findElement(conv.typeSearch).sendKeys(result[i][0]);
				wait.until(ExpectedConditions
						.visibilityOfElementLocated(By.xpath(r.autoSuggestPath2 + result[i][0] + "\"]")));
				projectName = driver.findElement(By.xpath(r.autoSuggestPath2 + result[i][0] + "\"]"));
			}
			projectName.click();
			driver.findElement(By.id("btnConversion")).click();
			db.waitForAjax(driver);
			boolean flag1 = driver.findElements(conv.convByChannel).size() > 0;
			boolean flag2 = driver.findElements(conv.convByFrequency).size() > 0;
			boolean flag3 = driver.findElements(conv.convVsQuintile).size() > 0;
			boolean flag4 = driver.findElements(conv.convByQuintile).size() > 0;
			boolean flag5 = driver.findElements(conv.convByDaypart).size() > 0;
			boolean flag6 = driver.findElements(conv.convVsNonConv).size() > 0;
			boolean flag7 = driver.findElements(conv.convByCreative).size() > 0;
			boolean flag8 = driver.findElements(conv.convByRecency).size() > 0;
			boolean flag9 = driver.findElements(conv.TotalNetConv).size() > 0;
			boolean flag10 = driver.findElements(conv.convByPromoSpot).size() > 0;
			boolean flag11 = driver.findElements(conv.convByProgarm).size() > 0;
			String convPerSpot = driver.findElement(By.id("lblConvNum")).getText();
			boolean flag12 = !convPerSpot.contains("null");
			boolean allPresent = flag1 && flag2 && flag3 && flag4 && flag5 && flag6 && flag7 && flag8 && flag9 && flag10
					&& flag11 && flag12;
			int rowCount = 0;
			if (!allPresent) {
				String countOfPromos = "Select count(*) from PR_PROMO Where PROMO_ID in(Select Distinct PROMO_ID from PR_PROMO_AIRING where Project_ID ="
						+ result[i][2] + ");";
				rowCount = Integer.parseInt(db.getCountOfRows(countOfPromos));
				if (rowCount != 0)
					log.debug(
							"Conversion graphs missing for " + result[i][0] + " for channel id " + result[i][1] + "\n");
				else {
					log.debug("R/C graphs missing since no promo mapped to " + result[i][0]);
					break task;
				}
			}
			driver.findElement(By.id("btnReach")).click();
			db.waitForAjax(driver);
			boolean flag13 = driver.findElements(r.reachByNtwrk).size() > 0;
			boolean flag14 = driver.findElements(r.reachByFreq).size() > 0;
			boolean flag15 = driver.findElements(r.actualReachPrcntCnt).size() > 0;
			boolean flag16 = driver.findElements(r.actualReachTotalCnt).size() > 0;
			boolean flag17 = driver.findElements(r.actualImpsCnt).size() > 0;
			boolean flag18 = driver.findElements(r.actualGrpCnt).size() > 0;
			boolean chkAll = flag13 && flag14 && flag15 && flag16 && flag17 && flag18;
			if (!chkAll) {
				if (rowCount != 0)
					log.debug("Reach graphs missing for " + result[i][0] + " for channel id " + result[i][1] + "\n");
			}
		}
		db.DBConnectionClose();
	}

	@AfterClass
	public void tearDown() {
		driver.quit();
	}
}
