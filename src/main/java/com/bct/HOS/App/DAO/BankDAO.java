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
import com.bct.HOS.App.BO.BankDetailsBO;
import com.bct.HOS.App.utils.DBConnector;
import com.bct.HOS.App.utils.NumberingSeriesUtil;
import com.bct.HOS.App.utils.OpenCsvWriterUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class BankDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;

	public BankDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
	}
	
	public JsonObject storeBankDepositDetails(String site_id, BankDetailsBO bankDetails) {

		String uniText = site_id+"/BDP";
		String transType = "CUSTBPD";
		String unqueId = NumberingSeriesUtil.GenerateNSUniqueId(transType,uniText);
		String SQL = "INSERT INTO "+schema+".bankdepositdetails( " + 
				" transaction_no, site_id, status, transaction_date, account_no, bank_name, branch, deposit, " +
				" slip_number, created_by, modified_by, hasattachment, currency) " + 
				" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) returning transaction_no";
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date parsed = format.parse(bankDetails.getTransaction_date() != null ? bankDetails.getTransaction_date() : String.valueOf(java.time.LocalDate.now()));
			java.sql.Date transactionDate = new java.sql.Date(parsed.getTime());
			
			PreparedStatement statement = conn.prepareStatement(SQL);
			statement.setString(1, unqueId);
			statement.setString(2, bankDetails.getRo_id());
			statement.setString(3, bankDetails.getStatus());
			statement.setDate(4, transactionDate);
			statement.setString(5, bankDetails.getAccount_no());
			statement.setString(6, bankDetails.getBank_name());
			statement.setString(7, bankDetails.getBranch());
			statement.setDouble(8, bankDetails.getDeposit());
			statement.setString(9, bankDetails.getSlip_number());
			statement.setString(10, bankDetails.getRo_id());
			statement.setString(11, bankDetails.getRo_id());
			statement.setBoolean(12, !bankDetails.getAttachments().isEmpty());
			statement.setString(13, bankDetails.getCurrency());
			
			ResultSet rs = statement.executeQuery();
			if(rs.next())
				bankDetails.setTransaction_no(rs.getString(1));
			bankDetails.setStatus(bankDetails.getStatus());
			
			if(bankDetails.getAttachments().size() > 0 &&  bankDetails.getTransaction_no() != null) {
				 SQL = "INSERT INTO " + schema + ".bank_attach_details "
						 + "(\"transaction_no\",\"site_id\",\"file_path\") "
						+ " VALUES (?,?,?)";
				 statement = conn.prepareStatement(SQL);
				 for(Attachments attach : bankDetails.getAttachments() ) {
					 statement.setString(1, bankDetails.getTransaction_no());
					 statement.setString(2, bankDetails.getRo_id());
					 statement.setString(3, attach.getImageURL());
					 statement.addBatch();
				 }
				 statement.executeBatch();
			}
			
			String json = new Gson().toJson(bankDetails);
			System.out.println(json);
			return new JsonParser().parse(json).getAsJsonObject();
				
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ BankDeposit-set ::" + ex.getMessage());
			
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public JsonObject updateBankDepositDetails(BankDetailsBO bankDetails) {
		String SQL = "UPDATE " + schema + ".BankDepositDetails SET status = '" + bankDetails.getStatus() 
		        + "', slip_number = '" + bankDetails.getSlip_number()
		        +"', transaction_date = '" + bankDetails.getTransaction_date()
		        +"', account_no = '" + bankDetails.getAccount_no()
		        +"', bank_name = '" + bankDetails.getBank_name()
		        +"', branch = '" + bankDetails.getBranch()
		        + "', modified_date ='" + java.time.LocalDate.now()
		        + "', currency ='" + bankDetails.getCurrency()
		        +"', deposit = " + bankDetails.getDeposit()
		        + ", hasattachment = " + !bankDetails.getAttachments().isEmpty()
				+ "  WHERE transaction_no ='" +  bankDetails.getTransaction_no() + "'";
		try {

			Statement stmt = conn.createStatement();
			stmt.execute(SQL);
			
			SQL = "DELETE FROM " + schema + ".bank_attach_details WHERE transaction_no ='" + bankDetails.getTransaction_no() + "';"; 
			stmt = conn.createStatement();
			stmt.execute(SQL);
			
			 SQL = "INSERT INTO " + schema + ".bank_attach_details "
						+ "(\"transaction_no\",\"site_id\",\"file_path\") "
						+ " VALUES (?,?,?)";
			   PreparedStatement statement = conn.prepareStatement(SQL);
			   for(Attachments attach : bankDetails.getAttachments() ) {
					 statement.setString(1, bankDetails.getTransaction_no());
					 statement.setString(2, bankDetails.getRo_id());
					 statement.setString(3, attach.getImageURL());
					 statement.addBatch();
				 }
				 statement.executeBatch();
				 
			String json = new Gson().toJson(bankDetails);
			return new JsonParser().parse(json).getAsJsonObject();
			
		} catch (Exception ex) {
			System.out.println("ErrOR @ BankDeposit-update ::" +ex.getMessage());
			return null;
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	public boolean deleteBankDepositDetailsByRO(String siteID) {
		String SQL = "DELETE FROM " + schema + ".\"BankDepositDetails\" "
				+ " WHERE ro_id ='" + siteID + "';"
				+"DELETE FROM " + schema + ".bank_attach_details "
				+ " WHERE site_id ='" + siteID + "' ;";
		try {

			Statement stmt = conn.createStatement();
			stmt.execute(SQL);
			return true;

		} catch (Exception ex) {
			System.out.println("ErrOR @ BankDeposit-Delete By RO ::" +ex.getMessage());
			return false;
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}

	public boolean deleteBankDepositDetailsTransactionNo(String transactionNo) {
		String SQL = "DELETE FROM " + schema + ".\"BankDepositDetails\" "
				+ " WHERE transaction_no ='" + transactionNo + "';"
				+ "DELETE FROM " + schema + ".bank_attach_details "
				+ " WHERE transaction_no ='" + transactionNo + "';";
		try {

			Statement stmt = conn.createStatement();
			stmt.execute(SQL);
			return true;

		} catch (Exception ex) {
			System.out.println("ErrOR @ BankDeposit-Delete By TransactionNo ::" +ex.getMessage());
			return false;
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public boolean deleteBankDepositDetailsStatus(String status) {
		String SQL = "DELETE FROM " + schema + ".\"BankDepositDetails\" "
				+ "' WHERE status ='" + status + "';"
				+ "DELETE FROM " + schema + ".bank_attach_details "
				+ "' WHERE status ='" + status + "';";
		try {

			Statement stmt = conn.createStatement();
			stmt.execute(SQL);
			return true;

		} catch (Exception ex) {
			System.out.println("ErrOR @ BankDeposit-Delete By Status ::" +ex.getMessage());
			return false;
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public JSONArray getBankDepositDetailsByRO(String siteID) {
		JSONArray json = new JSONArray();

		try {
			String SQL = " SELECT \"transaction_no\",\"status\",\"transaction_date\",\"slip_number\",\"deposit\",\"site_id\", "
					+ " \"bank_name\", \"account_no\", \"currency\",\"branch\", \"hasattachment\" "
					+ " FROM " + schema + ".BankDepositDetails "
					+ " WHERE site_id = '" + siteID + "'  ORDER BY id DESC ";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ getBankDepositDetailsByRO ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	
	public boolean getBankDepositCSVByRO(String siteID,String stDt,
			String edDate,String imgDumpPath, String tempExtractPath) {
		
		boolean res = false;
		ResultSet rs = null;
		try {
			String sqlQuery = "SELECT \"SALES DATE\"::TEXT,\"DEPOSIT DATE\"::TEXT,\"BRANCH\",\"TRANSACTION NUMBER\",TO_CHAR(\"AMOUNT\",'9,99,999.999') AS \"AMOUNT\", "+ 
					"\"BANK NAME\" FROM "+schema+".\"VW_BANK_DEPOSIT\" where DATE(\"DEPOSIT DATE\") BETWEEN coalesce('"+stDt+"',current_date) "+
					"AND coalesce('"+edDate+"',current_date) AND \"SITE ID\"='"+siteID+"' " +
					"UNION ALL "+
					"SELECT 'TOTAL','','','',TO_CHAR(SUM(\"AMOUNT\"),'9,99,999.999'),'' FROM "+schema+".\"VW_BANK_DEPOSIT\" where DATE(\"DEPOSIT DATE\") BETWEEN coalesce('"+stDt+"',current_date) "+
					"AND coalesce('"+edDate+"',current_date) AND \"SITE ID\"='"+siteID+"'";
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(sqlQuery);
 			System.out.println("==> Download Bankslip | CSV query executed successfully");
 			res = OpenCsvWriterUtil.convertRSToCSV(rs,tempExtractPath);
 			rs.close();
 			sqlQuery = "SELECT dtl.transaction_no as TransNo, file.file_path as imagePath FROM "+schema+".bankdepositdetails dtl " +
 						"join "+schema+".bank_attach_details file on dtl.transaction_no=file.transaction_no "+
 						"where dtl.transaction_date between '"+stDt+"' and '"+edDate+"' and dtl.site_id='"+siteID+"' and dtl.status='Submitted'";
 			stmt = conn.createStatement();
 			rs = stmt.executeQuery(sqlQuery);
 			System.out.println("==> Download Bankslip | attachment query executed successfully");
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
 			System.out.println("==> Export Bank slip process completed successfully");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR Download Bankslip @ bankDAO ::" + ex.getMessage());
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
	
	public JSONArray getBankDepositDetailsByFromdate(String siteID, String fromDate) {
		JSONArray json = new JSONArray();

		try {
			String SQL = " SELECT \"transaction_no\",\"status\",\"transaction_date\",\"slip_number\",\"deposit\",\"site_id\", "
					+ " \"bank_name\", \"account_no\", \"currency\",\"branch\", \"hasattachment\" "
					+ " FROM " + schema + ".BankDepositDetails "
					+ " WHERE site_id = '" + siteID + "' AND transaction_date='" + fromDate + "' ORDER BY id DESC ";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ bankDAO getBankDepositDetailsByFromdate ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	public JSONObject getBankDepositDetailsByTransactionNo(String siteId,String transactionNo) {
		JSONObject json = new JSONObject();

		try {
			String SQL = " SELECT file_path FROM " + schema + ".bank_attach_details "
					+ " WHERE transaction_no = '" + transactionNo + "'";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			JSONArray attachments = dbc.parseRS(rs);
			
			SQL = " SELECT \"transaction_no\",\"status\",\"transaction_date\",\"slip_number\",\"deposit\",\"site_id\", "
					+ " \"bank_name\", \"account_no\", \"currency\", \"branch\", \"hasattachment\" "
					+ " FROM " + schema + ".BankDepositDetails "
					+ " WHERE site_id='"+siteId+"' and transaction_no = '" + transactionNo + "'";
			System.out.println("The SQL query in Bank is....... "+SQL);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs).getJSONObject(0);
			json.put("attachments",attachments);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ BankDAO getBankDepositDetailsByTransactionNo ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	public JSONArray getBankDepositDetailsByStatus(String status) {
		JSONArray json = new JSONArray();

		try {
			String SQL = " SELECT \"transaction_no\",\"status\",\"transaction_date\",\"slip_number\",\"deposit\",\"site_id\", "
					+ " \"bank_name\", \"account_no\", \"currency\", \"branch\" , hasattachment"
					+ " FROM " + schema + ".BankDepositDetails "
					+ " WHERE status = '" + status + "' ORDER BY id DESC ";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ BANKDAO getBankDepositDetailsByStatus ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	
	public JSONObject getBankDepositDetailsByRole(String siteIDs,String fromDate) {
		JSONArray json = new JSONArray();

		try {
			String SQL = " SELECT \"SITE_NAME\", COALESCE(bank.site_id,COALESCE(expense.site_id,income.site_id)) as site_id, bank.transaction_date, deposit, expense, income "
					+ " FROM (SELECT site_id, transaction_date, sum(deposit) as deposit from "+ schema + ".BankDepositDetails " + 
					" WHERE site_id in (" + siteIDs + ") AND transaction_date='" + fromDate + "' GROUP BY site_id,transaction_date) bank " + 
					" FULL JOIN  (SELECT site_id,transaction_date, sum(totalexpense) as expense from "+ schema + ".expenseheader " + 
					" WHERE site_id in (" + siteIDs + ") AND transaction_date='" + fromDate + "' GROUP BY site_id,transaction_date) expense " + 
					" ON bank.site_id = expense.site_id " + 
					" FULL JOIN (SELECT site_id,transaction_date, sum(totalincome) as income from "+ schema + ".incomeheader " + 
					" WHERE site_id in (" + siteIDs + ") AND transaction_date='" + fromDate + "' GROUP BY site_id,transaction_date) income " + 
					" on COALESCE(bank.site_id,expense.site_id) = income.site_id " +
					" JOIN " + schema +".\"MS_SITE\" s ON s.\"SITE_ID\" =  COALESCE(bank.site_id,COALESCE(expense.site_id,income.site_id))";
					
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("total_sites",siteIDs.split(",").length);
			jsonObj.put("data",json);
			return jsonObj;
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ bankDAO ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public JSONArray getMasterTypeList(String masterType) {
		JSONArray json = new JSONArray();

		try {
			String SQL = " SELECT \"ms_description\", \"ms_number\" FROM " + schema + ".MS_bank_details "
					+ " WHERE mastertype = '" + masterType + "'";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ bankDAO ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	public JSONArray getBankAcountNo(String site_id) {
		JSONArray json = new JSONArray();

		try {
			String SQL = " SELECT \"account_no\", \"bank_name\", bank_name as branch FROM " + schema + ".bank_account_mst "
					+ " WHERE site_id = '" + site_id + "'";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ bankDAO getBankAcountNo ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	public JSONArray getAllBankAcountNo() {
		JSONArray json = new JSONArray();

		try {
			String SQL = " SELECT \"bank_name\", \"branch\" FROM " + schema + ".BankAcountNumber GROUP BY bank_name, branch ";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ bankDAO getAllBankAcountNo ::" + ex.getMessage());
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
