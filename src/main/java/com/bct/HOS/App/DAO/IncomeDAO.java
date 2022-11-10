package com.bct.HOS.App.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.bct.HOS.App.BO.IncomeBO;
import com.bct.HOS.App.BO.IncomeHeaderBO;
import com.bct.HOS.App.utils.DBConnector;
import com.bct.HOS.App.utils.NumberingSeriesUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class IncomeDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;

	public IncomeDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
	}
	
	/*CREATE TABLE IF NOT EXISTS "ALMAHA".IncomeHeader(
			transaction_no SERIAL PRIMARY KEY,
			ro_id VARCHAR (50) NOT NULL,
			status VARCHAR (50) NOT NULL,
			transaction_date date NOT NULL default current_date,
			shift_id VARCHAR (50) NOT NULL,
			totalexpense VARCHAR(50) NOT NULL,
			currency VARCHAR(10) NOT NULL,
			created_by VARCHAR (50) NOT NULL,
			created_date date,
			modified_by VARCHAR (50) NOT NULL,
			modified_date date
			);

			CREATE TABLE "ALMAHA".IncomeDetails (
			ro_id VARCHAR (50) NOT NULL,
			transaction_no VARCHAR (50) NOT NULL,
			income_category VARCHAR (50) NOT NULL,
			account_no VARCHAR (50) NOT NULL,
			amount VARCHAR(50) NOT NULL,
			PRIMARY KEY (ro_id, transaction_no)
			);*/
	
	public JsonObject storeIncomeDetails(String site_id, IncomeHeaderBO income) {

		String uniText = site_id+"/INC";
		String transType = "CUSTINC";
		String unqueId = NumberingSeriesUtil.GenerateNSUniqueId(transType,uniText);
		String SQL = "INSERT INTO " + schema + ".IncomeHeader "
				+ "(transaction_no,\"site_id\",\"status\",\"shift_id\",\"totalincome\",\"currency\",\"created_by\",\"modified_by\",\"transaction_date\") "
				+ " VALUES (?,?,?,?,?,?,?,?,?) returning transaction_no";
		
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date parsed = format.parse(income.getTransaction_date()!= null ? income.getTransaction_date() : String.valueOf(java.time.LocalDate.now()));
			java.sql.Date transactionDate = new java.sql.Date(parsed.getTime());
			
			PreparedStatement statement = conn.prepareStatement(SQL);
		
			statement.setString(1, unqueId);
			statement.setString(2, income.getRo_id());
			statement.setString(3, income.getStatus());
			statement.setString(4, income.getShift_id());
			statement.setDouble(5, income.getTotalIncome());
			statement.setString(6, income.getCurrency());
			statement.setString(7, income.getCreated_by());
			statement.setString(8, income.getModified_by());
			statement.setDate(9, transactionDate);
			
			ResultSet rs = statement.executeQuery();
			if(rs.next())
				income.setTransaction_no(rs.getString(1));
			income.setStatus(income.getStatus()); 
			if(income.getIncomes().size() > 0 && income.getTransaction_no() != null) {
			
				 SQL = "INSERT INTO " + schema + ".IncomeDetails "
						+ "(\"transaction_no\",\"site_id\",\"income_category\", " + "\"account_no\",\"amount\") "
						+ " VALUES (?,?,?,?,?)";
				 statement = conn.prepareStatement(SQL);
				 for(IncomeBO inc : income.getIncomes() ) {
					 statement.setString(1, income.getTransaction_no());
					 statement.setString(2, income.getRo_id());
					 statement.setString(3, inc.getIncome_category());
					 statement.setString(4, inc.getAccount_no());
					 statement.setDouble(5, inc.getAmount());
					 statement.addBatch();
				 }
				 statement.executeBatch();
			}
				
			String json = new Gson().toJson(income);
			return new JsonParser().parse(json).getAsJsonObject();
			 
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ IncomeDetails-set ::" + ex.getMessage());
			return null;
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public JsonObject updateIncomeDetails(IncomeHeaderBO income) {
		String SQL = "UPDATE " + schema + ".IncomeHeader SET status = '" + income.getStatus()
		+ "', shift_id ='" + income.getShift_id()
		+ "', totalincome =" + income.getTotalIncome()
		+ ", transaction_date ='" + income.getTransaction_date()
		+ "', modified_date ='" + java.time.LocalDate.now()
		+ "' WHERE transaction_no ='" +  income.getTransaction_no() + "'";
		try {
		
			//System.out.println("SQL" + SQL);
			Statement stmt = conn.createStatement();
			stmt.execute(SQL);
			
			
			SQL = "DELETE FROM " + schema + ".IncomeDetails WHERE transaction_no ='" + income.getTransaction_no() + "';"; 
			//System.out.println("SQL" + SQL);
			stmt = conn.createStatement();
			stmt.execute(SQL);
			
			 SQL = "INSERT INTO " + schema + ".IncomeDetails "
						+ "(\"transaction_no\",\"site_id\",\"income_category\", \"account_no\",\"amount\") "
						+ " VALUES (?,?,?,?,?)";
			   PreparedStatement statement = conn.prepareStatement(SQL);
			   for(IncomeBO inc : income.getIncomes() ) {
					 statement.setString(1, income.getTransaction_no());
					 statement.setString(2, income.getRo_id());
					 statement.setString(3, inc.getIncome_category());
					 statement.setString(4, inc.getAccount_no());
					 statement.setDouble(5, inc.getAmount());
					 statement.addBatch();
				 }
				 statement.executeBatch();
		
				String json = new Gson().toJson(income);
				//System.out.println(json);
				return new JsonParser().parse(json).getAsJsonObject();
			

		} catch (Exception ex) {
			System.out.println("ErrOR @ Income-update ::" +ex.getMessage());
			return null;
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	public boolean deleteIncomeDetailsByRO(String siteID) {
		String SQL = "DELETE FROM " + schema + ".IncomeHeader "
				+ "' WHERE site_id ='" + siteID + "' ;"
				+"DELETE FROM " + schema + ".IncomeDetails "
				+ "' WHERE site_id ='" + siteID + "' ;";
		try {

			Statement stmt = conn.createStatement();
			stmt.execute(SQL);
			return true;

		} catch (Exception ex) {
			System.out.println("ErrOR @ Income-update ::" +ex.getMessage());
			return false;
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	public boolean deleteIncomeDetailsTransactionNo(String transactionNo) {
		String SQL = "DELETE FROM " + schema + ".IncomeHeader "
				+ "' WHERE transaction_no ='" + transactionNo + "' ;" 
				+ "DELETE FROM " + schema + ".IncomeDetails "
				+ "' WHERE transaction_no ='" + transactionNo + "' ;"; 
		try {

			Statement stmt = conn.createStatement();
			stmt.execute(SQL);
			return true;

		} catch (Exception ex) {
			System.out.println("ErrOR @ BankDeposit-update ::" +ex.getMessage());
			return false;
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public boolean deleteIncomeDetailsStatus(String status) {
		String SQL = "DELETE FROM " + schema + ".IncomeDetails "
				+ "' WHERE transaction_no  in ( SELECT  transaction_no FROM " + schema + ".IncomeHeader Where status ='" + status + "' ) ;" 
				+ "DELETE FROM " + schema + ".IncomeHeader "
				+ "' WHERE status ='" + status + "' ;"; 
	
		try {

			Statement stmt = conn.createStatement();
			stmt.execute(SQL);
			return true;

		} catch (Exception ex) {
			System.out.println("ErrOR @ BankDeposit-update ::" +ex.getMessage());
			return false;
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public JSONArray getIncomeDetailsByRO(String siteID) {
		JSONArray json = new JSONArray();

		try {
			String SQL = " SELECT \"site_id\", \"transaction_no\", \"status\", \"shift_id\", \"ms_description\" as shift_description,"
					+"\"totalincome\",\"currency\",\"transaction_date\" "
					+ " FROM " + schema + ".IncomeHeader i"
					+ " join " +schema +".ms_bank_details b "
					+ " on i.shift_id = b.ms_number and b.mastertype ='shift'"
					+ " WHERE site_id = '" + siteID + "' ORDER BY i.id DESC ";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ IncomeHeader ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	public JSONArray getIncomeDetailsByFromdate(String siteID, String fromDate) {
		JSONArray json = new JSONArray();

		try {
			String SQL = " SELECT \"site_id\", \"transaction_no\", \"status\", \"shift_id\", \"ms_description\" as shift_description,"
					+"\"totalincome\",\"currency\",\"transaction_date\" "
					+ " FROM " + schema + ".IncomeHeader i"
					+ " join " +schema +".ms_bank_details b "
					+ " on i.shift_id = b.ms_number and b.mastertype ='shift'"
					+ " WHERE site_id = '" + siteID + "' AND transaction_date = '"+ fromDate +"' ORDER BY i.id DESC ";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ IncomeHeader ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	public JSONObject getIncomeDetailsByTransactionNo(String siteId, String transactionNo) {
		JSONObject json = new JSONObject();

		try {
			String SQL = " SELECT \"income_category\", \"account_no\",\"amount\" "
					+ " FROM " + schema + ".IncomeDetails "
					+ " WHERE site_id='"+siteId+"' and transaction_no = '" + transactionNo + "'";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			JSONArray incomeDetails = dbc.parseRS(rs);
			
			SQL =" SELECT \"site_id\", \"transaction_no\", \"status\", \"shift_id\", \"ms_description\" as shift_description,"
					+"\"totalincome\",\"currency\",\"transaction_date\" "
					+ " FROM " + schema + ".IncomeHeader i"
					+ " join " + schema +".ms_bank_details b "
					+ " on i.shift_id = b.ms_number and b.mastertype ='shift'"
					+ " WHERE site_id='"+siteId+"' and transaction_no = '" + transactionNo + "'";
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs).getJSONObject(0);
			json.put("incomedetails",incomeDetails);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @IncomeHeader ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	public JSONArray getIncomeDetailsByStatus(String status) {
		JSONArray json = new JSONArray();

		try {
			String SQL = " SELECT \"site_id\", \"transaction_no\", \"status\", \"shift_id\", \"ms_description\" as shift_description,"
					+"\"totalincome\",\"currency\",\"transaction_date\" "
					+ " FROM " + schema + ".IncomeHeader i"
					+ " join " +schema +".ms_bank_details b "
					+ " on i.shift_id = b.ms_number and b.mastertype ='shift'"
					+ " WHERE status = '" + status + "'  ORDER BY i.id DESC ";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ IncomeHeader::" + ex.getMessage());
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
