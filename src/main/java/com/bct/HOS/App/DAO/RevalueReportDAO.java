package com.bct.HOS.App.DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.bct.HOS.App.utils.DBConnector;

import net.sf.json.JSONArray;

public class RevalueReportDAO {
	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;

	public RevalueReportDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
	}
	
	public JSONArray getRevalueReport(String siteID, String fromDate, String toDate) {
		JSONArray json = new JSONArray();

		try {
			String SQL = "select coalesce(t1.transaction_date,t2.transaction_date) as \"Date\", " + 
					"coalesce(t1.site_id,t2.site_id) as site_id, " + 
					"coalesce(t2.totalincome,0) as revalue_amount, " + 
					"coalesce(sum(deposit),0) as deposit_amount, " + 
					"t1.transaction_date as date_of_deposit, " +
					"count(deposit)  as no_of_transaction  " + 
					"from \"ALMAHA\".BankDepositDetails t1   " + 
					"full outer join (select inh.site_id,sum(amount) as totalincome,inh.transaction_date \r\n" + 
					"from \"ALMAHA\".IncomeHeader inh " + 
					"join \"ALMAHA\".IncomeDetails ind " + 
					"on inh.site_id=ind.site_id " + 
					"and inh.transaction_no=ind.transaction_no " +
					"where ind.income_category='Recharge' and status='Submitted'  " + 
					"and inh.transaction_date between '"+fromDate+"' and '"+toDate+"'  " + 
					"and inh.site_id='"+siteID+"' " + 
					"group by inh.site_id,inh.transaction_date) t2 " + 
					"on t1.transaction_date=t2.transaction_date  " + 
					"Where coalesce(t1.transaction_date,t2.transaction_date) between '"+fromDate+"' and '"+toDate+"' " + 
					"and coalesce(t1.site_id,t2.site_id)='"+siteID+"' " + 
					"group by coalesce(t1.transaction_date,t2.transaction_date),  " + 
					"coalesce(t1.site_id,t2.site_id),t2.totalincome, " + 
					"t1.transaction_date " + 
					"order by coalesce(t1.transaction_date,t2.transaction_date)";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ REPORTDAO :: getrevaluereport" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	

	public JSONArray getTotalRevalueReport(String siteID, String fromDate, String toDate) {
		JSONArray json = new JSONArray();

		try {
			String SQL = "select 'TOTAL' as \"Date\", " + 
					"coalesce(t1.site_id,t2.site_id) as site_id, " + 
					"coalesce(t2.totalincome,0)::numeric(10,3) as revalue_amount, " + 
					"coalesce(sum(deposit),0)::numeric(10,3) as deposit_amount, " + 
					"'' as date_of_deposit, " + 
					"count(deposit)  as no_of_transaction " + 
					"from \"ALMAHA\".BankDepositDetails t1  " + 
					"full outer join (select inh.site_id,sum(amount) as totalincome " + 
					"from \"ALMAHA\".IncomeHeader inh " + 
					"join \"ALMAHA\".IncomeDetails ind " + 
					"on inh.site_id=ind.site_id " + 
					"and inh.transaction_no=ind.transaction_no	" + 
					"where ind.income_category='Recharge' and status='Submitted' " + 
					"and inh.transaction_date between '"+fromDate+"' and '"+toDate+"'  " + 
					"and inh.site_id='"+siteID+"' " + 
					"group by inh.site_id) t2 " + 
					"on t1.site_id=t2.site_id " + 
					"Where t1.transaction_date between '"+fromDate+"' and '"+toDate+"' " + 
					"and coalesce(t1.site_id,t2.site_id)='"+siteID+"' " + 
					"group by coalesce(t1.site_id,t2.site_id),t2.totalincome";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ REPORTDAO :: getTotalrevaluereport" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
}
