package com.bct.HOS.App.DAO;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;

import com.bct.HOS.App.BO.Attachments;
import com.bct.HOS.App.BO.ExpenseBO;
import com.bct.HOS.App.BO.ExpenseHeaderBO;
import com.bct.HOS.App.utils.DBConnector;
import com.bct.HOS.App.utils.NumberingSeriesUtil;
import com.bct.HOS.App.utils.OpenCsvWriterUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ExpenseDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;

	public ExpenseDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
	}
	
	/*CREATE TABLE IF NOT EXISTS "ALMAHA".ExpenseHeader(
			transaction_no SERIAL PRIMARY KEY,
			ro_id VARCHAR (50) NOT NULL,
			status VARCHAR (50) NOT NULL,
			transaction_date  date NOT NULL default current_date,
			shift_id VARCHAR (50) NOT NULL,
			totalexpense VARCHAR(50) NOT NULL,
			currency VARCHAR(10) NOT NULL,
			created_by VARCHAR (50) NOT NULL,
			created_date date,
			modified_by VARCHAR (50) NOT NULL,
			modified_date date
			);
			
	CREATE TABLE "ALMAHA".ExpenseDetails (
			ro_id VARCHAR (50) NOT NULL,
			transaction_no VARCHAR (50) NOT NULL,
			expense_category VARCHAR (50) NOT NULL,
			account_no VARCHAR (50) NOT NULL,
			amount VARCHAR(50) NOT NULL,
			PRIMARY KEY (ro_id, transaction_no)
			);
			*/
	
	public JsonObject storeExpenseDetails(String site_id, ExpenseHeaderBO expense) {
		
		String uniText = site_id+"/EXP";
		String transType = "CUSTEXP";
		String unqueId = NumberingSeriesUtil.GenerateNSUniqueId(transType,uniText);
		String SQL = "INSERT INTO " + schema + ".ExpenseHeader "
				+ "(transaction_no,\"site_id\",\"status\",\"shift_id\",\"totalexpense\",\"currency\",\"created_by\",\"modified_by\",\"transaction_date\",hasattachment) "
				+ " VALUES (?,?,?,?,?,?,?,?,?,?) returning transaction_no";
		
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date parsed = format.parse(expense.getTransaction_date());
			java.sql.Date transactionDate = new java.sql.Date(parsed.getTime());
			PreparedStatement statement = conn.prepareStatement(SQL);
			statement.setString(1, unqueId);
		   	statement.setString(2, expense.getRo_id());
			statement.setString(3, expense.getStatus());
			statement.setString(4, expense.getShift_id());
			statement.setDouble(5, expense.getTotalexpense());
			statement.setString(6, expense.getCurrency());
			statement.setString(7, expense.getCreated_by());
			statement.setString(8, expense.getModified_by());
			statement.setDate(9, transactionDate);
			statement.setBoolean(10, !expense.getAttachments().isEmpty());
			ResultSet rs = statement.executeQuery();
			if(rs.next()) {
				expense.setTransaction_no(rs.getString(1));
			}
			expense.setStatus(expense.getStatus());  
			if(expense.getExpenses().size() > 0 &&  expense.getTransaction_no() != null) {
				 SQL = "INSERT INTO " + schema + ".ExpenseDetails "
						+ "(\"transaction_no\",\"site_id\",\"expense_category\", \"account_no\",\"amount\") "
						+ " VALUES (?,?,?,?,?)";
				 statement = conn.prepareStatement(SQL);
				 for(ExpenseBO exp : expense.getExpenses() ) {
					 statement.setString(1, expense.getTransaction_no());
					 statement.setString(2, expense.getRo_id());
					 statement.setString(3, exp.getExpense_category());
					 statement.setString(4, exp.getAccount_no());
					 statement.setDouble(5, exp.getAmount());
					 statement.addBatch();
				 }
				 statement.executeBatch();
			}
			if(expense.getAttachments().size() > 0 &&  expense.getTransaction_no() != null) {
				 SQL = "INSERT INTO " + schema + ".expense_attach_details "
						 + "(trans_no,site_id,file_path) "
						+ " VALUES (?,?,?)";
				 statement = conn.prepareStatement(SQL);
				 for(Attachments attach : expense.getAttachments() ) {
					 statement.setString(1, expense.getTransaction_no());
					 statement.setString(2, expense.getRo_id());
					 statement.setString(3, attach.getImageURL());
					 statement.addBatch();
				 }
				 statement.executeBatch();
			}
			
			String json = new Gson().toJson(expense);
			return new JsonParser().parse(json).getAsJsonObject();
			 
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ ExpenseDetails-storeExpenseDetails ::" + ex.getMessage());
			return null;
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public JsonObject updateExpenseDetails(ExpenseHeaderBO expense) {
		String SQL = "UPDATE " + schema + ".ExpenseHeader SET status = '" + expense.getStatus()
				+ "', shift_id ='" + expense.getShift_id()
				+ "', totalexpense =" + expense.getTotalexpense()
				+ ", transaction_date ='" + expense.getTransaction_date()
				+ "', modified_date ='" + java.time.LocalDate.now()
				+ "', hasattachment ="+!expense.getAttachments().isEmpty()
				+ " WHERE transaction_no ='" +  expense.getTransaction_no() + "'";
		
		
		try {

			Statement stmt = conn.createStatement();
			stmt.execute(SQL);
			
			SQL = "DELETE FROM " + schema + ".ExpenseDetails WHERE transaction_no ='" + expense.getTransaction_no() + "';"; 
			stmt = conn.createStatement();
			stmt.execute(SQL);
			
			 SQL = "INSERT INTO " + schema + ".ExpenseDetails "
						+ "(\"transaction_no\",\"site_id\",\"expense_category\", \"account_no\",\"amount\") "
						+ " VALUES (?,?,?,?,?)";
			   PreparedStatement statement = conn.prepareStatement(SQL);
				 for(ExpenseBO exp : expense.getExpenses() ) {
					 statement.setString(1, expense.getTransaction_no());
					 statement.setString(2, expense.getRo_id());
					 statement.setString(3, exp.getExpense_category());
					 statement.setString(4, exp.getAccount_no());
					 statement.setDouble(5, exp.getAmount());
					 statement.addBatch();
				 }
				 statement.executeBatch();
				 SQL = "DELETE FROM " + schema + ".expense_attach_details WHERE trans_no ='" + expense.getTransaction_no() + "';"; 
				stmt = conn.createStatement();
				stmt.execute(SQL);
				
				 SQL = "INSERT INTO " + schema + ".expense_attach_details "
							+ "(trans_no,site_id,file_path) "
							+ " VALUES (?,?,?)";
				   statement = conn.prepareStatement(SQL);
				   for(Attachments attach : expense.getAttachments() ) {
						 statement.setString(1, expense.getTransaction_no());
						 statement.setString(2, expense.getRo_id());
						 statement.setString(3, attach.getImageURL());
						 statement.addBatch();
					 }
					statement.executeBatch();
			String json = new Gson().toJson(expense);
			return new JsonParser().parse(json).getAsJsonObject();
		
		} catch (Exception ex) {
			System.out.println("ErrOR @ Expense-update ::" +ex.getMessage());
			return null;
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	public boolean deleteExpenseDetailsByRO(String siteID) {
		String SQL = "DELETE FROM " + schema + ".ExpenseHeader "
				+ "' WHERE site_id ='" + siteID + "' ;"
				+"DELETE FROM " + schema + ".ExpenseDetails "
				+ "' WHERE site_id ='" + siteID + "' ;";
		try {

			Statement stmt = conn.createStatement();
			stmt.execute(SQL);
			return true;

		} catch (Exception ex) {
			System.out.println("ErrOR @ Expense - delete ByRO::" +ex.getMessage());
			return false;
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	public boolean deleteExpenseDetailsTransactionNo(String transactionNo) {
		String SQL = "DELETE FROM " + schema + ".ExpenseHeader "
				+ "' WHERE transaction_no ='" + transactionNo + "' ;" 
				+ "DELETE FROM " + schema + ".ExpenseDetails "
				+ "' WHERE transaction_no ='" + transactionNo + "' ;"; 
		try {

			Statement stmt = conn.createStatement();
			stmt.execute(SQL);
			return true;

		} catch (Exception ex) {
			System.out.println("ErrOR @ ExpenseDAO - delete By TransactionNo::" +ex.getMessage());
			return false;
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public boolean deleteExpenseDetailsStatus(String status) {
		String SQL = "DELETE FROM " + schema + ".ExpenseDetails "
				+ "' WHERE transaction_no  in ( SELECT  transaction_no FROM " + schema + ".ExpenseHeader Where status ='" + status + "' ) ;" 
				+ "DELETE FROM " + schema + ".ExpenseHeader "
				+ "' WHERE status ='" + status + "' ;"; 
	
		try {

			Statement stmt = conn.createStatement();
			stmt.execute(SQL);
			return true;

		} catch (Exception ex) {
			System.out.println("ErrOR @ ExpenseDAO - delete By Status ::" +ex.getMessage());
			return false;
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public JSONArray getExpenseDetailsByRO(String siteID) {
		JSONArray json = new JSONArray();

		try {
			String SQL = " SELECT \"site_id\", \"transaction_no\", \"status\", \"shift_id\", \"ms_description\" as shift_description,"
					+"\"totalexpense\",\"currency\",\"transaction_date\",hasattachment "
					+ " FROM " + schema + ".ExpenseHeader e"
					+ " join " +schema +".ms_bank_details b "
					+ " on e.shift_id = b.ms_number and b.mastertype ='shift'"
					+ " WHERE site_id = '" + siteID + "' ORDER BY e.id DESC ";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ get Expense Details By RO ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	public JSONArray getExpenseDetailsByFromdate(String siteID, String fromDate) {
		JSONArray json = new JSONArray();

		try {
			String SQL = " SELECT \"site_id\", \"transaction_no\", \"status\", \"shift_id\", \"ms_description\" as shift_description,"
					+"\"totalexpense\",\"currency\",\"transaction_date\",hasattachment "
					+ " FROM " + schema + ".ExpenseHeader e"
					+ " join " +schema +".ms_bank_details b "
					+ " on e.shift_id = b.ms_number and b.mastertype ='shift'"
					+ " WHERE site_id = '" + siteID + "' AND transaction_date= '"+ fromDate +"' ORDER BY e.id DESC ";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ get Expense Details By Fromdate ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	
	public JSONObject getExpenseDetailsByTransactionNo(String siteId, String transactionNo) {
		JSONObject json = new JSONObject();

		try {
			String SQL = " SELECT file_path FROM " + schema + ".expense_attach_details "
					+ " WHERE site_id='"+siteId+"' and trans_no = '" + transactionNo + "'";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			JSONArray attachments = dbc.parseRS(rs);
			
			SQL = " SELECT \"expense_category\", \"account_no\",\"amount\" "
					+ " FROM " + schema + ".ExpenseDetails "
					+ " WHERE site_id='"+siteId+"' and transaction_no = '" + transactionNo + "'";
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery(SQL);
			JSONArray expenseDetails = dbc.parseRS(rs);
			
			SQL =  " SELECT \"site_id\", \"transaction_no\", \"status\", \"shift_id\", \"ms_description\" as shift_description,"
					+"\"totalexpense\",\"currency\",\"transaction_date\",hasattachment "
					+ " FROM " + schema + ".ExpenseHeader e"
					+ " join " +schema +".ms_bank_details b "
					+ " on e.shift_id = b.ms_number and b.mastertype ='shift'"
					+ " WHERE site_id='"+siteId+"' and transaction_no = '" + transactionNo + "'";
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs).getJSONObject(0);
			json.put("expensedetails",expenseDetails);
			json.put("attachments",attachments);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ get Expense Details By TransactionNo ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	public boolean getExpenseDetailCSVByRO(String siteID,String stDt,
			String edDate,String imgDumpPath, String tempExtractPath) {
		
		boolean res = false;
		ResultSet rs = null;
		try {
			String sqlQuery = "SELECT DATE(\"DATE\")::TEXT AS \"DATE\",\"BILL NO\",\"ACCOUNT NO\",\"PARTICULARS\",\"AMOUNT\"::TEXT "
				+ "FROM (SELECT * FROM "+schema+".\"VW_EXPENSE_DTL\" where DATE(\"DATE\") BETWEEN coalesce('"+stDt+"',current_date)"
				+ "AND coalesce('"+edDate+"',current_date) AND \"SITE ID\"='"+siteID+"' ORDER BY DATE(\"DATE\")) AS EXPR_Q "     
				+ " UNION ALL " 
				+ "SELECT 'TOTAL'::TEXT,' ',' ',' ',TO_CHAR(SUM(\"AMOUNT\"::NUMERIC),'9,999,999.999') FROM "+schema+".\"VW_EXPENSE_DTL\" where DATE(\"DATE\") BETWEEN coalesce('"+stDt+"',current_date)"
				+ "AND coalesce('"+edDate+"',current_date) AND \"SITE ID\"='"+siteID+"'";
			
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(sqlQuery);
			System.out.println("Expense DAO | CSV query executed successfully");
 			res = OpenCsvWriterUtil.convertRSToCSV(rs, tempExtractPath);
 			rs.close();
 			sqlQuery = "SELECT hdr.transaction_no as TransNo, file.file_path as imagePath FROM "+schema+".expenseheader hdr " +
 						"join "+schema+".expense_attach_details file on hdr.transaction_no=file.trans_no "+
 						"where hdr.transaction_date between '"+stDt+"' and '"+edDate+"' and hdr.site_id='"+siteID+"' and hdr.status='Submitted'";
 			stmt = conn.createStatement();
 			rs = stmt.executeQuery(sqlQuery);
 			System.out.println("Expense DAO | attachment query executed successfully");
 			String imgFolder = tempExtractPath + "/attachments";
 			while(rs.next()) {
 				String transNo = rs.getString("TransNo");
 				String imgPath = rs.getString("imagePath");
 				File parentfolder = new File(imgFolder + "/" + transNo);
 				if(!parentfolder.exists()) {
 					parentfolder.mkdir();
 				}
 				File imageFile = new File(imgDumpPath + "/" + imgPath);
 				String tempFileName = imageFile.getName();
 				String orgFileName = tempFileName.substring(0, tempFileName.lastIndexOf("_"));
 				File destFile = new File(parentfolder + "/" + orgFileName);
 				FileUtils.copyFile(imageFile, destFile);
 			}
 			System.out.println("Expense DAO | Expense slip export process executed successfully");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ expenseDAO Slip download ::" + ex.getMessage());
		} finally {
			try {
				if(rs != null) {
					rs.close();
				}
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return res;
	}
	
	public JSONArray getExpenseDetailsByStatus(String status) {
		JSONArray json = new JSONArray();

		try {
			String SQL =  " SELECT \"site_id\", \"transaction_no\", \"status\", \"shift_id\", \"ms_description\" as shift_description,"
					+"\"totalexpense\",\"currency\",\"transaction_date\",hasattachment "
					+ " FROM " + schema + ".ExpenseHeader e"
					+ " join " +schema +".ms_bank_details b "
					+ " on e.shift_id = b.ms_number and b.mastertype ='shift'"
					+ " WHERE status = '" + status + "'  ORDER BY e.id DESC ";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ get Expense Details By Status::" + ex.getMessage());
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
