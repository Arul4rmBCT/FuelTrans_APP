package com.bct.HOS.App.BO;

import net.sf.json.JSONArray;

public class SiteInfoSalesBO {

	JSONArray Transactions = new JSONArray();

	public JSONArray getTransactions() {
		return Transactions;
	}

	public void setDataset(JSONArray Transactions) {
		this.Transactions = Transactions;
	}
}
