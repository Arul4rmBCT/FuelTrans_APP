package com.bct.HOS.App.BO;

public class NFSaleDetailBO {

	String transaction_no = null;
	String site_id = null;
	String product_id = null;
	String prod_name = null;
	String quantity = null;
	String price = null;
	String amount = null;
	
	
	public String getTransaction_no() {
		return transaction_no;
	}
	public void setTransaction_no(String transaction_no) {
		this.transaction_no = transaction_no;
	}
	
	public String getRo_id() {
		return site_id;
	}
	public void setRo_id(String ro_id) {
		this.site_id = ro_id;
	}
	public double getAmount() {
		return Double.parseDouble(amount);
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getProduct_id() {
		return product_id;
	}
	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}
	public double getQuantity() {
		return Double.parseDouble(quantity);
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	public double getPrice() {
		return Double.parseDouble(price);
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getProd_name() {
		return prod_name;
	}
	public void setProd_name(String prod_name) {
		this.prod_name = prod_name;
	}
}
