package com.bct.HOS.App.BO;

import java.util.ArrayList;
import java.util.List;

public class NFSalesHeaderBO {


	String transaction_no = null;
	String status = null;
	String shift_id = null;
	String site_id = null;
	String product_amt = null;
	String transaction_date = null;
	String vat_amt = null;
	String total_amt = null;
	String currency = null;
	String created_by = null;
	String created_date = null;
	String modified_by = null;
	String modified_date = null;
	List<NFSaleDetailBO> nfDetails = new ArrayList<NFSaleDetailBO>();
	
	
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
	public String getShift_id() {
		return shift_id;
	}
	public void setShift_id(String shift_id) {
		this.shift_id = shift_id;
	}
	public String getRo_id() {
		return site_id;
	}
	public void setRo_id(String site_id) {
		this.site_id = site_id;
	}
	
	public String getTransaction_date() {
		return transaction_date;
	}
	public void setTransaction_date(String transaction_date) {
		this.transaction_date = transaction_date;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getCreated_by() {
		return created_by;
	}
	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}
	public String getCreated_date() {
		return created_date;
	}
	public void setCreated_date(String created_date) {
		this.created_date = created_date;
	}
	public String getModified_by() {
		return modified_by;
	}
	public void setModified_by(String modified_by) {
		this.modified_by = modified_by;
	}
	public String getModified_date() {
		return modified_date;
	}
	public void setModified_date(String modified_date) {
		this.modified_date = modified_date;
	}
	public List<NFSaleDetailBO> getNFDetails() {
		return nfDetails;
	}
	public void setIncomes(List<NFSaleDetailBO> incomes) {
		this.nfDetails = incomes;
	}
	public double getProduct_amt() {
		return Double.valueOf(product_amt);
	}
	public void setProduct_amt(String product_amt) {
		this.product_amt = product_amt;
	}
	public double getVat_amt() {
		return Double.valueOf(vat_amt);
	}
	public void setVat_amt(String vat_amt) {
		this.vat_amt = vat_amt;
	}
	public double getTotal_amt() {
		return Double.valueOf(total_amt);
	}
	public void setTotal_amt(String total_amt) {
		this.total_amt = total_amt;
	}
	
}
