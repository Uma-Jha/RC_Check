package com.rsg.RC_Check;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class HomePageAndReachAnalysisTest {
	static WebDriver driver;
	static WebDriverWait wait;
	Actions action;
	JavascriptExecutor jse;
	Conversion conv;
	Logger log;
	DBUtility db;

	@BeforeClass
	public void setUP() {
		System.setProperty("webdriver.chrome.driver", "D://chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--disable-extensions");
		String log4jConfPath = "./log4j.properties";
		PropertyConfigurator.configure(log4jConfPath);
		log = Logger.getLogger("devpinoyLogger");
		driver = new ChromeDriver(options);
		wait = new WebDriverWait(driver, 60);
		action = new Actions(driver);
		jse = (JavascriptExecutor) driver;
		conv = new Conversion();
		driver.get(conv.baseUrl);
		driver.manage().window().maximize();
		driver.findElement(conv.username).sendKeys("ketansa@rsgsystems.com");
		driver.findElement(conv.password).sendKeys("password");
		driver.findElement(conv.signin).click();
	}

	@Test
	public void checkSpotOnHomePage() throws ParseException, ClassNotFoundException, SQLException {
		int[] channels = { 1, 2, 3, 11, 12, 13, 15, 20, 21, 37, 41, 46, 53, 78, 96, 166 };
		for (int k = 0; k < channels.length; k++) {
			driver.get("http://promo.rsgmedia.com/dashboard?quarter=1Q17&fiscalqtr=2Q17&checked=checked&channelID="
					+ channels[k]);
			waitForAjax(driver);
			List<WebElement> projects = driver.findElements(By.cssSelector("[id^='containerDash-']"));
			int numOfProjects = projects.size();
			Map<String, Integer> prjctReachCntList = new HashMap<String, Integer>();
			db = new DBUtility();
			db.DBConnection();
			for (int i = 0; i < projects.size(); i++) {
				jse.executeScript("arguments[0].scrollIntoView(true);", projects.get(i));
				String projectId = projects.get(i).getAttribute("data-projectid");
				String projectName = projects.get(i).getAttribute("data-projectname");
				String countOfPromos = "select count(*) from PR_PROMO_AIRING where PROJECT_ID=" + projectId + ";";
				int rowCount = Integer.parseInt(db.getCountOfRows(countOfPromos));
				if (rowCount != 0) {
					String query = "Select min(PlayDtTime),max(PlayDtTime) from PR_PROMO_AIRING where project_id="
							+ projectId;
					ResultSet rs = db.ReturnResultSet(query);
					rs.next();
					String promoMappingStartDate = rs.getString(1).split(" ")[0];
					String promoMappingEndDate = rs.getString(2).split(" ")[0];
					query = "select count(*) from REF_CAL_PERIOD where FISCAL_QTR='2Q17' and PERIOD_TYPE_CD='W' and START_DATE='"
							+ promoMappingStartDate + "';";
					int flag = Integer.parseInt(db.getCountOfRows(query));
					query = "select count(*) from REF_CAL_PERIOD where FISCAL_QTR='2Q17' and PERIOD_TYPE_CD='W' and (START_DATE BETWEEN '"
							+ promoMappingStartDate + "' AND '" + promoMappingEndDate + "');";
					int weekCount = Integer.parseInt(db.getCountOfRows(query));
					int totalWeeks = flag > 0 ? weekCount : (weekCount + 1);
					int actualReachCount = projects.get(i)
							.findElements(By.cssSelector(
									".highcharts-container g.highcharts-markers.highcharts-tracker>path[fill='#515151']"))
							.size();
					prjctReachCntList.put(projectName, actualReachCount);
					int actualBarCount = projects.get(i)
							.findElements(
									By.cssSelector(".highcharts-container .highcharts-series>rect[fill='#515151']"))
							.size();

					if (totalWeeks != actualBarCount) {
						log.debug("Spot count doesn't match for project " + projectName + " for channel id "
								+ channels[k]);
					}
				}
			}
			if (numOfProjects != 0)
				driver.get("http://promo.rsgmedia.com/projectGraph?channelID=" + channels[k]);
			db.waitForAjax(driver);
			for (Map.Entry<String, Integer> e : prjctReachCntList.entrySet()) {
				driver.findElement(conv.typeSearch).clear();
				String name = e.getKey();
				if (name.contains("-")) {
					String fname = name.split("-")[0];
					String lname = name.split("-")[1];
					driver.findElement(conv.typeSearch).sendKeys(fname);
					String autoSuggestPath = "//*[starts-with(@class,'tt-suggestion')]/p[contains(text(),\"";
					wait.until(ExpectedConditions
							.visibilityOfAllElementsLocatedBy(By.xpath(autoSuggestPath + fname + "\")]")));
					List<WebElement> projectNames = driver.findElements(By.xpath(autoSuggestPath + fname + "\")]"));
					for (int a = 0; a < projectNames.size(); a++) {
						if (projectNames.get(a).getText().contains(lname)) {
							projectNames.get(a).click();
							break;
						}
					}
				} else {
					driver.findElement(conv.typeSearch).sendKeys(name);
					String autoSuggestPath = "//*[starts-with(@class,'tt-suggestion')]/p[text()=\"";
					wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(autoSuggestPath + name + "\"]")));
					driver.findElement(By.xpath(autoSuggestPath + name + "\"]")).click();
				}

				driver.findElement(By.id("btnReach")).click();
				waitForAjax(driver);
				boolean reachByChannel = driver
						.findElements(By.cssSelector(
								".reach-block #reach-buildup g.highcharts-series-group rect[fill='#7cb5ec']"))
						.size() > 0;
				boolean reachByFreq = driver
						.findElements(By.cssSelector(
								".reach-block #reach-quintiles g.highcharts-series-group rect[fill='#7cb5ec']"))
						.size() > 0;
				int actualReachPrcntCount = driver
						.findElements(By.cssSelector(
								"#reachrow .row #container g.highcharts-markers.highcharts-tracker>path[fill='#434348']"))
						.size();
				boolean actualReachPrcnt = actualReachPrcntCount > 0;
				/*
				 * boolean plannedReachPrcnt = driver
				 * .findElements(By.cssSelector(
				 * "#reachrow .row #container g.highcharts-markers.highcharts-tracker>path[fill='#7cb5ec']"
				 * )) .size() > 0;
				 */
				int actualReachTotalCount = driver
						.findElements(By.cssSelector(
								"#reachrow .row #container1 g.highcharts-markers.highcharts-tracker>path[fill='#434348']"))
						.size();
				boolean actualReachTotal = actualReachTotalCount > 0;
				/*
				 * boolean plannedReachTotal = driver
				 * .findElements(By.cssSelector(
				 * "#reachrow .row #container1 g.highcharts-markers.highcharts-tracker>path[fill='#7cb5ec']"
				 * )) .size() > 0;
				 */
				int actualImpsCount = driver
						.findElements(By.cssSelector(
								"#reachrow #impSummDiv g.highcharts-markers.highcharts-tracker>path[fill='#434348']"))
						.size();
				boolean actualImps = actualImpsCount > 0;
				/*
				 * boolean plannedImps = driver .findElements(By.cssSelector(
				 * "#reachrow #impSummDiv g.highcharts-markers.highcharts-tracker>path[fill='#7cb5ec']"
				 * )) .size() > 0;
				 */
				int actualGrpCount = driver
						.findElements(By.cssSelector(
								"#reachrow #grpSummDiv g.highcharts-markers.highcharts-tracker>path[fill='#434348']"))
						.size();
				boolean actualGrp = actualGrpCount > 0;
				/*
				 * boolean plannedGrp = driver .findElements(By.cssSelector(
				 * "#reachrow #grpSummDiv g.highcharts-markers.highcharts-tracker>path[fill='#7cb5ec']"
				 * )) .size() > 0;
				 */
				boolean flag = reachByChannel && reachByFreq && actualReachPrcnt && actualReachTotal && actualImps
						&& actualGrp;
				if (!flag)
					log.debug("All graphs of Reach not present on UI for project " + e.getKey() + " for network id "
							+ channels[k]);
				if (e.getValue() != actualReachPrcntCount || e.getValue() != actualReachTotalCount
						|| e.getValue() != actualImpsCount || e.getValue() != actualGrpCount) {
					log.debug(
							"Count of weeks for which Actual Reach %, Reach Total, Imps and GRP data is shown on UI is not same for project "
									+ e.getKey());
				}

			}
		}
		db.DBConnectionClose();
		driver.quit();
	}

	public void waitForAjax(WebDriver driver) {
		new WebDriverWait(driver, 30).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				JavascriptExecutor js = (JavascriptExecutor) driver;
				return (Boolean) js.executeScript("return window.jQuery != undefined && jQuery.active === 0");
			}
		});
	}
}
