package com.bct.HOS.App.DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.bct.HOS.App.utils.DBConnector;

public class BankDepositSummaryDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;
	private DecimalFormat myFormatter = new DecimalFormat("###,###.000");
	
	public BankDepositSummaryDAO() {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
	}

		
	public String depositSummaryNavAPI(String startDate, String endDate) {
		StringBuffer bodyXMLStr = new StringBuffer();
		Statement st = null;
		ResultSet rs = null;
		try {
			String  SQL = "SELECT dep.transaction_no,dep.status,dep.transaction_date,dep.slip_number,dep.deposit,dep.site_id,"
				+	"dep.bank_name,dep.account_no,acc.account_code,acc.account_desc,acc.account_type "
				+	"FROM "+schema+".BankDepositDetails dep inner join "+schema+".bankacountnumber acc on acc.account_no=dep.account_no "
				+	"WHERE transaction_date between '"+startDate+"' and '"+ endDate +"' and "
				+	"status='Submitted' ORDER BY site_id";
			System.out.println("SQL ==> "+SQL);
			st = conn.createStatement();
			rs = st.executeQuery(SQL);
			SimpleDateFormat sd2 = new SimpleDateFormat("ddMMyy");
			String interfDateRef = sd2.format(new Date());
			String monEndDt  = startDate.replaceAll("-", "/");
			while(rs.next()) {
				String slpNo = rs.getString("slip_number");
				String amount = rs.getString("deposit");
				String siteId = rs.getString("site_id");
				String acctNo = rs.getString("account_no");
				String acctCode = rs.getString("account_code");
				String acctDesc = rs.getString("account_desc");
				String acctType = rs.getString("account_type");
				String recXML = generateXMLRecord(monEndDt,slpNo,amount,siteId,acctCode,acctDesc,acctType,interfDateRef);
				bodyXMLStr.append(recXML);
			}
			System.out.println("Bank Deposit Summary XML geneated success for Navision");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ Bank Deposit Summary for Navision ::" + ex.getMessage());
		} finally {
			try {
				if(st != null) {
					st.close();
				}
				if(rs != null) {
					rs.close();
				}
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bodyXMLStr.toString();
	}
	
	public String depositSummaryNavAPI(String dateStr) {
		StringBuffer bodyXMLStr = new StringBuffer();
		Statement st = null;
		ResultSet rs = null;
		try {
			String  SQL = "SELECT dep.transaction_no,dep.status,dep.transaction_date,dep.slip_number,dep.deposit,dep.site_id,"
				+	"dep.bank_name,dep.account_no,acc.account_code,acc.account_desc,acc.account_type "
				+	"FROM "+schema+".BankDepositDetails dep inner join "+schema+".bankacountnumber acc on acc.account_no=dep.account_no "
				+	"WHERE transaction_date between '"+dateStr+"' and '"+dateStr+"' and "
				+	"status='Submitted' ORDER BY site_id";
			System.out.println("SQL ==> "+SQL);
			st = conn.createStatement();
			rs = st.executeQuery(SQL);
			String newDate  = dateStr.replaceAll("-", "/");
			SimpleDateFormat sd2 = new SimpleDateFormat("ddMMyy");
			String interfDateRef = sd2.format(new Date());
			while(rs.next()) {
				String slpNo = rs.getString("slip_number");
				String amount = rs.getString("deposit");
				String siteId = rs.getString("site_id");
				String acctNo = rs.getString("account_no");
				String acctCode = rs.getString("account_code");
				String acctDesc = rs.getString("account_desc");
				String acctType = rs.getString("account_type");
				String recXML = generateXMLRecord(newDate,slpNo,amount,siteId,acctCode,acctDesc,acctType,interfDateRef);
				bodyXMLStr.append(recXML);
			}
			System.out.println("Bank Deposit Summary XML geneated success for Navision");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ Bank Deposit Summary for Navision ::" + ex.getMessage());
		} finally {
			try {
				if(st != null) {
					st.close();
				}
				if(rs != null) {
					rs.close();
				}
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bodyXMLStr.toString();
	}
	
	private String generateXMLRecord(String startDate,String slpNo,String amount,String siteId,
			String acctCode, String acctDesc, String acctType,String interfDateRef) {
		StringBuffer result = new StringBuffer();
		result.append("<PRAMS_NAV_ExpenseDepositPaidIn>");
		result.append("<Journal_Template>GENERAL</Journal_Template>");
		result.append("<Journal_Batch>EXPENSES</Journal_Batch>");
		result.append("<Filling_Station>"+ siteId +"</Filling_Station>");
		result.append("<Account_Type>"+acctType+"</Account_Type>");
		result.append("<Account_Code>"+acctCode+"</Account_Code>");
		result.append("<Slip_No>"+ slpNo +"</Slip_No>");
		result.append("<Description>"+acctDesc+"</Description>");
		double dbval = Double.valueOf(amount);
    	String output = myFormatter.format(dbval);
		result.append("<Amount>"+ output +"</Amount>");
		result.append("<Month_End_Date>"+ startDate +"</Month_End_Date>");
		result.append("<Interface_Ref>"+siteId+"D"+interfDateRef+"</Interface_Ref>");
		result.append("</PRAMS_NAV_ExpenseDepositPaidIn>");
		return result.toString();
	}
	
}
