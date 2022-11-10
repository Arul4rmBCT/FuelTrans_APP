package com.bct.HOS.App.BO;

import java.util.ArrayList;
import java.util.List;

public class BankDetailsBO {

	String transaction_no = null;
	String status = null;
	String slip_number = null;
	String site_id = null;
	String deposit = null;
	String transaction_date = null;
	String account_no = null;
	String bank_name = null;
	String branch = null;
	String currency = null;
	String fromDate = null;
	String mode = null;
	List<Attachments> attachments = new ArrayList();
	
	
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getCurrency() {
		return (currency == null || currency.isEmpty()) ? "OMR" : currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getAccount_no() {
		return account_no;
	}
	public void setAccount_no(String account_no) {
		this.account_no = account_no;
	}
	public String getBank_name() {
		return bank_name;
	}
	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}
	public String getTransaction_no() {
		return transaction_no;
	}
	public void setTransaction_no(String transaction_no) {
		this.transaction_no = transaction_no;
	}
	public String getStatus() {
		return (status == null || status.isEmpty()) ? "New" : status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSlip_number() {
		return slip_number;
	}
	public void setSlip_number(String slip_number) {
		this.slip_number = slip_number;
	}
	public String getRo_id() {
		return site_id;
	}
	public void setRo_id(String site_id) {
		this.site_id = site_id;
	}
	
	public double getDeposit() {
		return Double.parseDouble(deposit);
	}
	
	public void setDeposit(String deposit) {
		this.deposit = deposit;
	}
	public String getTransaction_date() {
		return transaction_date;
	}
	public void setTransaction_date(String transaction_date) {
		this.transaction_date = transaction_date;
	}
		
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public List<Attachments> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<Attachments> attachments) {
		this.attachments = attachments;
	}



	
	
}
