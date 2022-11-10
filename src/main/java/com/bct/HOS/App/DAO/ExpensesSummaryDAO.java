package com.bct.HOS.App.DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;

import com.bct.HOS.App.utils.DBConnector;

public class ExpensesSummaryDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;
	private DecimalFormat myFormatter = new DecimalFormat("###,###.000");
	
	public ExpensesSummaryDAO() {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
	}

		
	public String expensesSummaryNavAPI(String startDate,String endDate) {
		StringBuffer bodyXMLStr = new StringBuffer();
		Statement st = null;
		ResultSet rs = null;
		try {
			String  SQL = "SELECT site_id,transaction_no,status,shift_id,ms_description as shift_description,"
				+	"totalexpense,currency,transaction_date,hasattachment "
				+	"FROM "+ schema +".ExpenseHeader e "
				+	 "join "+ schema +".ms_bank_details b "
				+	"on e.shift_id = b.ms_number and b.mastertype ='shift' "
				+	"WHERE transaction_date between '"+ startDate +"' and '"+ endDate +"' and "
				+	"status='Submitted' ORDER BY site_id;";
			System.out.println("SQL ==> "+SQL);
			st = conn.createStatement();
			rs = st.executeQuery(SQL);
			while(rs.next()) {
				String amount = rs.getString("totalexpense");
				String siteId = rs.getString("site_id");
				String recXML = generateXMLRecord(startDate,amount,siteId);
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
	
	private String generateXMLRecord(String startDate,String amount,String siteId) {
		StringBuffer result = new StringBuffer();
		result.append("<NAV_ExpenseDepositPaidIn>");
		result.append("<Journal_Template>GENERAL</Journal_Template>");
		result.append("<Journal_Batch>EXPENSES</Journal_Batch>");
		result.append("<Filling_Station>"+ siteId +"</Filling_Station>");
		result.append("<Account_Type>G/L Account</Account_Type>");
		result.append("<Account_Code></Account_Code>");
		result.append("<Slip_No></Slip_No>");
		result.append("<Description></Description>");
		double dbval = Double.valueOf(amount);
    	String output = myFormatter.format(dbval);
		result.append("<Amount>"+ output +"</Amount>");
		result.append("<Month_End_Date>"+ startDate +"</Month_End_Date>");
		result.append("<Interface_Ref>MEODAUG000001</Interface_Ref>");
		result.append("</NAV_ExpenseDepositPaidIn>");
		return result.toString();
	}
	
}
