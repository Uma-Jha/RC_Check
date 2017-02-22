package com.rsg.RC_Check;

import org.openqa.selenium.By;

public class Conversion {
String baseUrl ="http://promo.rsgmedia.com/login?internallogin=true";
String reportUrl = "http://promo.rsgmedia.com/projectGraph?channelID=";
By username = By.id("username");
By password = By.id("password");
By signin = By.cssSelector(".btn.btn-primary");
By typeSearch = By.id("typesearch");
By convByChannel = By.cssSelector("#ttChannelConversion+div g.highcharts-series-group rect");
By convByFrequency = By.cssSelector("#ttFrequencyConversion+div g.highcharts-series-group rect");
By convVsQuintile = By.cssSelector("#ttConversionByQuintile+div g.highcharts-series-group rect");
By convByQuintile = By.cssSelector("#ttConversionQuintile+div g.highcharts-series.highcharts-tracker path");
By convByDaypart = By.cssSelector("#ttConversionByDaypart+div g.highcharts-series-group rect");
By convVsNonConv = By.cssSelector("#ttConVsNonConv+div g.highcharts-series-group rect");
By convByCreative = By.cssSelector("#ttConByCreative+div g.highcharts-series-group rect");
By convByRecency = By.cssSelector("#ttConByRecency+div g.highcharts-series-group rect");
By TotalNetConv = By.cssSelector("#ttTotalNetConversion+div g.highcharts-series-group");
By convByPromoSpot = By.cssSelector("#ttconvByPromoSpot+div g.highcharts-series-group rect");
By convByProgarm = By.cssSelector("#convByProgramDiv g.highcharts-series-group rect");
}
