package com.rsg.RC_Check;

import org.openqa.selenium.By;

public class Reach {
	String reportUrl = "http://promo.rsgmedia.com/projectGraph?channelID=";
	String hpUrl = "http://promo.rsgmedia.com/dashboard?quarter=1Q17&fiscalqtr=2Q17&checked=checked&channelID=";
	By actualReachCnt = By
			.cssSelector(".highcharts-container g.highcharts-markers.highcharts-tracker>path[fill='#515151']");
	By actualBarCnt = By.cssSelector(".highcharts-container .highcharts-series>rect[fill='#515151']");
	By reachByNtwrk = By.cssSelector(".reach-block #reach-buildup g.highcharts-series-group rect[fill='#7cb5ec']");
	By reachByFreq = By.cssSelector(".reach-block #reach-quintiles g.highcharts-series-group rect[fill='#7cb5ec']");
	By actualReachPrcntCnt = By
			.cssSelector("#reachrow .row #container g.highcharts-markers.highcharts-tracker>path[fill='#434348']");
	By actualReachTotalCnt = By
			.cssSelector("#reachrow .row #container1 g.highcharts-markers.highcharts-tracker>path[fill='#434348']");
	By actualImpsCnt = By
			.cssSelector("#reachrow #impSummDiv g.highcharts-markers.highcharts-tracker>path[fill='#434348']");
	By actualGrpCnt = By
			.cssSelector("#reachrow #grpSummDiv g.highcharts-markers.highcharts-tracker>path[fill='#434348']");
	String autoSuggestPath1 = "//*[starts-with(@class,'tt-suggestion')]/p[contains(text(),\"";
	String autoSuggestPath2 = "//*[starts-with(@class,'tt-suggestion')]/p[text()=\"";

}
