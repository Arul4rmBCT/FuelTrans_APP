package com.bct.HOS.App.BO;

import java.util.*;

public class OfflineROInventoryBO {

	String transaction_no = null;
	String transaction_date = null;
	String transaction_time = null;
	String site_id = null;
	List<TankProductInventoryBO> inventorydetails = new ArrayList();
	
	public String getSite_id() {
		return site_id;
	}
	public void setSite_id(String site_id) {
		this.site_id = site_id;
	}
	public String getTransaction_no() {
		return transaction_no;
	}
	public void setTransaction_no(String transaction_no) {
		this.transaction_no = transaction_no;
	}
	public String getTransaction_date() {
		return transaction_date;
	}
	public void setTransaction_date(String transaction_date) {
		this.transaction_date = transaction_date;
	}
	public String getTransaction_time() {
		return transaction_time;
	}
	public void setTransaction_time(String transaction_time) {
		this.transaction_time = transaction_time;
	}
	public List<TankProductInventoryBO> getInventorydetails() {
		return inventorydetails;
	}
	public void setInventorydetails(List<TankProductInventoryBO> inventorydetails) {
		this.inventorydetails = inventorydetails;
	}
	
	
	
}
