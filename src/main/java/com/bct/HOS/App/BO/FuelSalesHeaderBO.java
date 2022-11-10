package com.bct.HOS.App.BO;

import java.util.ArrayList;
import java.util.List;

public class FuelSalesHeaderBO {

	
	String transaction_date = null;
	String site_id = null;
	int product_no ;
	double unit_price;
	String currency = null;
	List<FuelSalesBO> sales = new ArrayList();
	
	public String getTransaction_date() {
		return transaction_date;
	}
	public void setTransaction_date(String transaction_date) {
		this.transaction_date = transaction_date;
	}
	public String getSite_id() {
		return site_id;
	}
	public void setSite_id(String site_id) {
		this.site_id = site_id;
	}
	public int getProduct_no() {
		return product_no;
	}
	public void setProduct_no(int product_no) {
		this.product_no = product_no;
	}
	public double getUnit_price() {
		return unit_price;
	}
	public void setUnit_price(double unit_price) {
		this.unit_price = unit_price;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public List<FuelSalesBO> getSales() {
		return sales;
	}
	public void setSales(List<FuelSalesBO> sales) {
		this.sales = sales;
	}
	
	
	
	
}
