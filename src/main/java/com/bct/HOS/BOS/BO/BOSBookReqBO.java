package com.bct.HOS.BOS.BO;

public class BOSBookReqBO {

	String siteId = null;
	String date = null;
	String currencyCode = null;
	double deposit = 0;
	double expenses = 0;

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public double getDeposit() {
		return deposit;
	}

	public void setDeposit(double deposit) {
		this.deposit = deposit;
	}

	public double getExpenses() {
		return expenses;
	}

	public void setExpenses(double expenses) {
		this.expenses = expenses;
	}

}
