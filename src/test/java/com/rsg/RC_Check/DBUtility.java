package com.rsg.RC_Check;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class DBUtility {

	public Connection con = null;
	public String url = "jdbc:mysql://mediamantra-promo.cn3dq5atl3li.us-east-1.rds.amazonaws.com:3306/advantdb";
	public String username = "nielsenuser";
	public String pwd = "nielsenpass";

	public void DBConnection() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		con = DriverManager.getConnection(url, username, pwd);
	}

	public ResultSet ReturnResultSet(String Query) throws SQLException {
		Statement stms = con.createStatement();
		ResultSet rst = stms.executeQuery(Query);
		return rst;
	}

	public void DBConnectionClose() throws SQLException {
		con.close();
	}

	public String getCountOfRows(String query) throws SQLException {
		Statement stms = con.createStatement();
		ResultSet rst = stms.executeQuery(query);
		rst.next();
		return rst.getString(1);
	}

	public String[][] conversionProjects() throws ClassNotFoundException, SQLException {
		String noOfRowsQuery = "select count(*) from PR_PROJECT where DATEDIFF(CURDATE()-INTERVAL 4 DAY,DATE(greatest(PREMIERE_DATE,END_DATE)))<=14 and DATEDIFF(DATE(greatest(PREMIERE_DATE,END_DATE)),CURDATE()-INTERVAL 4 DAY)<=0 and MEDIA_OUTLET_ID in (1, 2, 3, 11, 12, 13, 15, 20, 21, 37, 41, 46, 53, 78, 96, 166) order by MEDIA_OUTLET_ID;";
		ResultSet rs = ReturnResultSet(noOfRowsQuery);
		rs.next();
		int rows = rs.getInt(1);
		System.out.println("Total projects : " + rows);
		String[][] result = new String[rows][3];
		int i = 0;
		String prjctChannnelIdQuery = "select NAME,MEDIA_OUTLET_ID,PROJECT_ID from PR_PROJECT where DATEDIFF(CURDATE()-INTERVAL 4 DAY,DATE(greatest(PREMIERE_DATE,END_DATE)))<=14 and DATEDIFF(DATE(greatest(PREMIERE_DATE,END_DATE)),CURDATE()-INTERVAL 4 DAY)<=0 and MEDIA_OUTLET_ID in (12) order by MEDIA_OUTLET_ID;";
		rs = ReturnResultSet(prjctChannnelIdQuery);
		while (rs.next()) {
			result[i][0] = rs.getString("NAME");
			result[i][1] = rs.getString("MEDIA_OUTLET_ID");
			result[i][2] = rs.getString("PROJECT_ID");
			i++;
		}
		return result;
	}

	public void waitForAjax(WebDriver driver) {
		new WebDriverWait(driver, 30).until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				JavascriptExecutor js = (JavascriptExecutor) driver;
				return (Boolean) js.executeScript("return jQuery.active == 0");
			}
		});
	}
}
