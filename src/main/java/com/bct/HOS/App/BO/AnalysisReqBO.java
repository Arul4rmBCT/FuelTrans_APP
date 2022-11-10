package com.bct.HOS.App.BO;

public class AnalysisReqBO {

	String userId = null;
	String roleId = null;
	String country = null;
	String siteID = null;
	int currentYear = 0;
	int previousYear = 0;
	int currentMonth = 0;
	int previousMonth = 0;
	int currentQuarter = 0;
	int previousQuarter = 0;
	
	String st_date = null;
	String end_date = null;
	String st_name = null;
	String type =null;

	boolean productFilter = false;
	boolean regionFilter = false;

	
	
	public String getSt_date() {
		return st_date;
	}

	public void setSt_date(String st_date) {
		this.st_date = st_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public String getSt_name() {
		return st_name;
	}

	public void setSt_name(String st_name) {
		this.st_name = st_name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getSiteID() {
		return siteID;
	}

	public void setSiteID(String siteID) {
		this.siteID = siteID;
	}

	public int getCurrentYear() {
		return currentYear;
	}

	public void setCurrentYear(int currentYear) {
		this.currentYear = currentYear;
	}

	public int getPreviousYear() {
		return previousYear;
	}

	public void setPreviousYear(int previousYear) {
		this.previousYear = previousYear;
	}

	public int getCurrentMonth() {
		return currentMonth;
	}

	public void setCurrentMonth(int currentMonth) {
		this.currentMonth = currentMonth;
	}

	public int getPreviousMonth() {
		return previousMonth;
	}

	public void setPreviousMonth(int previousMonth) {
		this.previousMonth = previousMonth;
	}

	public int getCurrentQuarter() {
		return currentQuarter;
	}

	public void setCurrentQuarter(int currentQuarter) {
		this.currentQuarter = currentQuarter;
	}

	public int getPreviousQuarter() {
		return previousQuarter;
	}

	public void setPreviousQuarter(int previousQuarter) {
		this.previousQuarter = previousQuarter;
	}

	public boolean isProductFilter() {
		return productFilter;
	}

	public void setProductFilter(boolean productFilter) {
		this.productFilter = productFilter;
	}

	public boolean isRegionFilter() {
		return regionFilter;
	}

	public void setRegionFilter(boolean regionFilter) {
		this.regionFilter = regionFilter;
	}

}
