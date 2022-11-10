package com.bct.HOS.App.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimerTask;

import com.bct.HOS.App.DAO.BankDepositSummaryDAO;
import com.bct.HOS.App.DAO.ExpensesSummaryDAO;
import com.bct.HOS.App.DAO.InventorySummaryDAO;
import com.bct.HOS.App.DAO.PdnNoteSummaryDAO;
import com.bct.HOS.App.DAO.SalesSummaryDAO;

public class HosToNavisionPushTimer extends TimerTask {
	
	
	private String transType1 = null;
		
	 public HosToNavisionPushTimer(String transType) {
		this.transType1 = transType;
	}
	 
	@SuppressWarnings("resource")
	@Override
	public void run() {
		
		System.out.println("HosToNavisionPushTimer task started at:"+new Date());
		
		generateSalesReport("FUEL_SLAES");
		generateExpensesReport("EXPENSES");
		generateFuelInventoryReport("FUEL_INVENTORY");
		generatePDNNoteReport("PDN_NOTE");
		System.out.println("Inventory Report Timer task Ended at:"+new Date());
	 }
	
	
	
	 private void generateSalesReport(String tranType) {
		 System.out.println("HosToNavisionPushTimer Sales generation started.........");
	        Date dateObj = null;
	        String xmlStr = null;
	        String todayDt = null;
	        Connection conn = null;
	        Statement st = null;
	        DBConnector dbc = null;
	        ResultSet rs = null;
			try {
				dateObj = getPreviousDt();
				todayDt = getTodayDt();
				dbc = new DBConnector();
	        	conn = dbc.getConnection();
	    		String schema = dbc.getSchema();
	    		
	    		String sql = "insert into "+schema+".navision_job_details (job_type,data_date,generate_status,send_status) values (?,?,?,?) returning job_id";
	    		//String sql = "insert into "+schema+".navision_job_details (job_type,data_date,generate_status,send_status,local_path,remote_path,file_name,status_text) values (?,?,?,?,?,?,?,?) returning job_id";
				PreparedStatement statement = conn.prepareStatement(sql);
				statement.setString(1,tranType);
				java.sql.Date sqlPackageDate
	            = new java.sql.Date(dateObj.getTime());
				statement.setDate(2,sqlPackageDate);
				statement.setString(3,"F");
				statement.setString(4,"F");
				rs = statement.executeQuery();
				String jobId = null;
				if(rs.next()) {
					jobId = rs.getString(1);
				}
				statement.close();
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				String dateStr = dateFormat.format(dateObj);
				String fileName = todayDt+"_"+ jobId +"_"+ tranType +".xml";
				xmlStr = new SalesSummaryDAO().salesSummaryNavAPI(dateStr);
				HOSConfig conf = new HOSConfig();
				String salesPath = conf.getValue("NAVISION_SALES_PATH");
				String filePathName = salesPath + "/" + fileName;
				Map<String, String> result = XMLUtil.storeToFile(xmlStr, filePathName);
				if(result != null) {
					String status = result.get("flag"); 
					int ct = 0;
					if("true".equalsIgnoreCase(status)) {
						sql = "update "+schema+".navision_job_details set generate_status='T',send_status='T',local_path=?,remote_path=?,file_name=?,status_text=? where job_id=?";
						statement = conn.prepareStatement(sql);
						statement.setString(1,filePathName);
						statement.setString(2,filePathName);
						statement.setString(3,fileName);
						statement.setString(4,"File Sent to Remote Server Successfully");
						statement.setString(5,jobId);
						ct = statement.executeUpdate();
						statement.close();
					} else {
						sql = "update "+schema+".navision_job_details set local_path=?,remote_path=?,file_name=?,status_text=? where job_id=?";
						statement = conn.prepareStatement(sql);
						statement.setString(1,filePathName);
						statement.setString(2,filePathName);
						statement.setString(3,fileName);
						statement.setString(4,"File Not Sent: "+result.get("message"));
						statement.setString(5,jobId);
						ct = statement.executeUpdate();
						statement.close();
					}
				} 
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					dbc.closeConnection(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println("HosToNavisionPushTimer Sales generation ended.........");
	    }
	 
	 
	 private void generateExpensesReport(String tranType) {
	        System.out.println("HosToNavisionPushTimer Expenses generation started.........");
	        Date dateObj = null;
	        String finalXmlStr = new String("<Expenses xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
	        String todayDt = null;
	        Connection conn = null;
	        Statement st = null;
	        DBConnector dbc = null;
	        ResultSet rs = null;
			try {
				dateObj = getPreviousDt();
				todayDt = getTodayDt();
				dbc = new DBConnector();
	        	conn = dbc.getConnection();
	    		String schema = dbc.getSchema();
	    		
	    		String sql = "insert into "+schema+".navision_job_details (job_type,data_date,generate_status,send_status) values (?,?,?,?) returning job_id";
	    		//String sql = "insert into "+schema+".navision_job_details (job_type,data_date,generate_status,send_status,local_path,remote_path,file_name,status_text) values (?,?,?,?,?,?,?,?) returning job_id";
				PreparedStatement statement = conn.prepareStatement(sql);
				statement.setString(1,tranType);
				java.sql.Date sqlPackageDate = new java.sql.Date(dateObj.getTime());
				statement.setDate(2,sqlPackageDate);
				statement.setString(3,"F");
				statement.setString(4,"F");
				rs = statement.executeQuery();
				String jobId = null;
				if(rs.next()) {
					jobId = rs.getString(1);
				}
				statement.close();
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				String dateStr = dateFormat.format(dateObj);
				String fileName = todayDt+"_"+ jobId +"_"+ tranType +".xml";
				String bdXmlStr = new BankDepositSummaryDAO().depositSummaryNavAPI(dateStr,dateStr);
				finalXmlStr = finalXmlStr + bdXmlStr;
				String expXmlStr = new ExpensesSummaryDAO().expensesSummaryNavAPI(dateStr, dateStr);
				finalXmlStr = finalXmlStr + expXmlStr;
				finalXmlStr = finalXmlStr + "</Expenses>";
				HOSConfig conf = new HOSConfig();
				String expensesPath = conf.getValue("NAVISION_EXPENSES_PATH");
				String filePathName = expensesPath + "/" + fileName;
				Map<String, String> result = XMLUtil.storeToFile(finalXmlStr, filePathName);
				System.out.println("TEST LOG | Generated Expenses XML result is.......... "+result);
				if(result != null) {
					String status = result.get("flag");
					int ct = 0;
					if("true".equalsIgnoreCase(status)) {
						sql = "update "+schema+".navision_job_details set generate_status='T',send_status='T',local_path=?,remote_path=?,file_name=?,status_text=? where job_id=?";
						statement = conn.prepareStatement(sql);
						statement.setString(1,filePathName);
						statement.setString(2,filePathName);
						statement.setString(3,fileName);
						statement.setString(4,"File Sent to Remote Server Successfully");
						statement.setString(5,jobId);
						ct = statement.executeUpdate();
						statement.close();
					} else {
						sql = "update "+schema+".navision_job_details set local_path=?,remote_path=?,file_name=?,status_text=? where job_id=?";
						statement = conn.prepareStatement(sql);
						statement.setString(1,filePathName);
						statement.setString(2,filePathName);
						statement.setString(3,fileName);
						statement.setString(4,"File Not Sent: "+result.get("message"));
						statement.setString(5,jobId);
						ct = statement.executeUpdate();
						statement.close();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					dbc.closeConnection(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	        System.out.println("HosToNavisionPushTimer Expenses generation ended.........");
	    }
	 
	 private void generateFuelInventoryReport(String tranType) {
	        System.out.println("HosToNavisionPushTimer Fuel Inventory generation started.........");
	        Date dateObj = null;
	        String finalXmlStr = new String("<InventoryFuel xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
	        String todayDt = null;
	        Connection conn = null;
	        Statement st = null;
	        DBConnector dbc = null;
	        ResultSet rs = null;
			try {
				dateObj = getPreviousDt();
				todayDt = getTodayDt();
				dbc = new DBConnector();
	        	conn = dbc.getConnection();
	    		String schema = dbc.getSchema();
	    		
	    		String sql = "insert into "+schema+".navision_job_details (job_type,data_date,generate_status,send_status) values (?,?,?,?) returning job_id";
	    		//String sql = "insert into "+schema+".navision_job_details (job_type,data_date,generate_status,send_status,local_path,remote_path,file_name,status_text) values (?,?,?,?,?,?,?,?) returning job_id";
				PreparedStatement statement = conn.prepareStatement(sql);
				statement.setString(1,tranType);
				java.sql.Date sqlPackageDate = new java.sql.Date(dateObj.getTime());
				statement.setDate(2,sqlPackageDate);
				statement.setString(3,"F");
				statement.setString(4,"F");
				rs = statement.executeQuery();
				String jobId = null;
				if(rs.next()) {
					jobId = rs.getString(1);
				}
				statement.close();
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				String dateStr = dateFormat.format(dateObj);
				String fileName = todayDt+"_"+ jobId +"_"+ tranType +".xml";
				String expXmlStr = new InventorySummaryDAO().fuelInventorySummaryNavAPI(dateStr, dateStr);
				finalXmlStr = finalXmlStr + expXmlStr;
				finalXmlStr = finalXmlStr + "</InventoryFuel>";
				HOSConfig conf = new HOSConfig();
				String expensesPath = conf.getValue("NAVISION_INVENTORY_PATH");
				String filePathName = expensesPath + "/" + fileName;
				Map<String, String> result = XMLUtil.storeToFile(finalXmlStr, filePathName);
				System.out.println("TEST LOG | Generated Fuel Inventory XML result is.......... "+result);
				if(result != null) {
					String status = result.get("flag");
					int ct = 0;
					if("true".equalsIgnoreCase(status)) {
						sql = "update "+schema+".navision_job_details set generate_status='T',send_status='T',local_path=?,remote_path=?,file_name=?,status_text=? where job_id=?";
						statement = conn.prepareStatement(sql);
						statement.setString(1,filePathName);
						statement.setString(2,filePathName);
						statement.setString(3,fileName);
						statement.setString(4,"File Sent to Remote Server Successfully");
						statement.setString(5,jobId);
						ct = statement.executeUpdate();
						statement.close();
					} else {
						sql = "update "+schema+".navision_job_details set local_path=?,remote_path=?,file_name=?,status_text=? where job_id=?";
						statement = conn.prepareStatement(sql);
						statement.setString(1,filePathName);
						statement.setString(2,filePathName);
						statement.setString(3,fileName);
						statement.setString(4,"File Not Sent: "+result.get("message"));
						statement.setString(5,jobId);
						ct = statement.executeUpdate();
						statement.close();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					dbc.closeConnection(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	        System.out.println("HosToNavisionPushTimer Fuel Inventory generation ended.........");
	    }
	 
	 private void generatePDNNoteReport(String tranType) {
	        System.out.println("HosToNavisionPushTimer PDN Notes generation started.........");
	        Date dateObj = null;
	        String finalXmlStr = new String("<PdnNotesFuel xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
	        String todayDt = null;
	        Connection conn = null;
	        Statement st = null;
	        DBConnector dbc = null;
	        ResultSet rs = null;
			try {
				dateObj = getPreviousDt();
				todayDt = getTodayDt();
				dbc = new DBConnector();
	        	conn = dbc.getConnection();
	    		String schema = dbc.getSchema();
	    		
	    		String sql = "insert into "+schema+".navision_job_details (job_type,data_date,generate_status,send_status) values (?,?,?,?) returning job_id";
				PreparedStatement statement = conn.prepareStatement(sql);
				statement.setString(1,tranType);
				java.sql.Date sqlPackageDate = new java.sql.Date(dateObj.getTime());
				statement.setDate(2,sqlPackageDate);
				statement.setString(3,"F");
				statement.setString(4,"F");
				rs = statement.executeQuery();
				String jobId = null;
				if(rs.next()) {
					jobId = rs.getString(1);
				}
				statement.close();
				DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
				String dateStr = dateFormat.format(dateObj);
				String fileName = todayDt+"_"+ jobId +"_"+ tranType +".xml";
				String expXmlStr = new PdnNoteSummaryDAO().pdnNoteSummaryNavAPI(dateStr, dateStr);
				finalXmlStr = finalXmlStr + expXmlStr;
				finalXmlStr = finalXmlStr + "</PdnNotesFuel>";
				HOSConfig conf = new HOSConfig();
				String expensesPath = conf.getValue("NAVISION_PDNNOTE_PATH");
				String filePathName = expensesPath + "/" + fileName;
				Map<String, String> result = XMLUtil.storeToFile(finalXmlStr, filePathName);
				System.out.println("TEST LOG | Generated Fuel PDN Notes XML result is.......... "+result);
				if(result != null) {
					String status = result.get("flag");
					int ct = 0;
					if("true".equalsIgnoreCase(status)) {
						sql = "update "+schema+".navision_job_details set generate_status='T',send_status='T',local_path=?,remote_path=?,file_name=?,status_text=? where job_id=?";
						statement = conn.prepareStatement(sql);
						statement.setString(1,filePathName);
						statement.setString(2,filePathName);
						statement.setString(3,fileName);
						statement.setString(4,"File Sent to Remote Server Successfully");
						statement.setString(5,jobId);
						ct = statement.executeUpdate();
						statement.close();
					} else {
						sql = "update "+schema+".navision_job_details set local_path=?,remote_path=?,file_name=?,status_text=? where job_id=?";
						statement = conn.prepareStatement(sql);
						statement.setString(1,filePathName);
						statement.setString(2,filePathName);
						statement.setString(3,fileName);
						statement.setString(4,"File Not Sent: "+result.get("message"));
						statement.setString(5,jobId);
						ct = statement.executeUpdate();
						statement.close();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					dbc.closeConnection(conn);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	        System.out.println("HosToNavisionPushTimer Fuel PDN notes generation ended.........");
	    }
	 
	 private Date getPreviousDt() {
			
		   //DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		   Date today = new Date();
	       Calendar calendar = Calendar.getInstance();
	       calendar.setTime(today);
	       calendar.add(Calendar.DAY_OF_YEAR, -1);
	       Date previousDate = calendar.getTime();
	      // String result = dateFormat.format(previousDate);
	       return previousDate;
		}
	 
	 private String getTodayDt() {
			
	   DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	   Date today = new Date();
       String result = dateFormat.format(today);
       return result;
	}
	 
	 /*
	  CREATE TABLE "ALMAHA".navision_job_details
(
    seq_id SERIAL,
    job_id character varying(10) NOT NULL DEFAULT LPAD(nextval('"ALMAHA".Navision_Job_id_seq'::regclass)::text, 5, '0'),
    job_type character varying(20) NOT NULL,
    data_date date,
    job_date timestamp DEFAULT CURRENT_TIMESTAMP,
	remote_ref character varying(20),
    generate_status character(1),
    send_status character(1),
    local_path character varying(100),
    remote_path character varying(100),
    file_name character varying(50),
    ack_receive_status character(1),
    ack_send_status character(1),
    status_text character varying(200),
	remote_error text,
    CONSTRAINT navision_job_pkey PRIMARY KEY (job_id)
)
	  */
}
