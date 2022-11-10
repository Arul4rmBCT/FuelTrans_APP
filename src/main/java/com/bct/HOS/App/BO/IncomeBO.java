package com.bct.HOS.App.BO;

public class IncomeBO {

	String transaction_no = null;
	String income_category = null;
	String account_no = null;
	String ro_id = null;
	String amount = null;
	public String getTransaction_no() {
		return transaction_no;
	}
	public void setTransaction_no(String transaction_no) {
		this.transaction_no = transaction_no;
	}
	
	public String getIncome_category() {
		return income_category;
	}
	public void setIncome_category(String income_category) {
		this.income_category = income_category;
	}
	public String getAccount_no() {
		return account_no;
	}
	public void setAccount_no(String account_no) {
		this.account_no = account_no;
	}
	public String getRo_id() {
		return ro_id;
	}
	public void setRo_id(String ro_id) {
		this.ro_id = ro_id;
	}
	public double getAmount() {
		return Double.parseDouble(amount);
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	

	
}
