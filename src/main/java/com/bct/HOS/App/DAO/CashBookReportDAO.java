package com.bct.HOS.App.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bct.HOS.App.DAO.AnalysisDAO.Product;
import com.bct.HOS.App.DAO.AnalysisDAO.Record;
import com.bct.HOS.App.DAO.AnalysisDAO.SortByDate;
import com.bct.HOS.App.utils.DBConnector;

import net.sf.json.JSONArray;

public class CashBookReportDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;
	private long unitConversion = 0;
	private DecimalFormat myFormatter = new DecimalFormat("###,###.000");
	
	public CashBookReportDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
		unitConversion = dbc.getUnitConversion();
	}

	/**
	 * 
	 * @return
	 */
	public JSONArray cashBookReport(String st_date,String end_date,String st_name) {
		JSONArray json = new JSONArray();
		try {
			
			StringBuilder createSQL = new StringBuilder();
			
			createSQL.append(" CREATE TEMPORARY TABLE \"TEMP_TRANSACTIONS\"" + 
					" ( " + 
					" \"SITE_ID\" VARCHAR(100),  			" + 
					" \"UNIT_PRICE\" VARCHAR (100),			" + 
					" \"VOLUME\" NUMERIC(10,2) , 		 	" +
					" \"TRANSACTION_TYPE\" VARCHAR (100),	" +
					" \"AMOUNT\" NUMERIC(10,3),  			" +
					" \"TRANSACTION_DATE\"	DATE			" +
					" ) ");
			
			
			System.out.println("@getCashBookReport Create Temp trans table->"+ createSQL.toString() );
			Statement stmt = conn.createStatement();
			System.out.println("@getCashBookReport Temp trans table Created ?->"+stmt.executeUpdate(createSQL.toString()));
			
			
			createSQL = new StringBuilder();
			
			createSQL.append(" INSERT INTO  \"TEMP_TRANSACTIONS\"   				" + 
					"(\"SITE_ID\",								 					" + 
					"\"UNIT_PRICE\", \"AMOUNT\",									" + 
					"\"TRANSACTION_DATE\" , \"VOLUME\" , \"TRANSACTION_TYPE\"  ) 	" + 
					" SELECT \"SITE_ID\", 				 							" + 
					" \"UNIT_PRICE\" , \"AMOUNT\" , 								" + 
					" \"TRANSACTION_DATE\", \"VOLUME\", \"TRANSACTION_TYPE\"        " + 
					" FROM \"ALMAHA\".\"TRANSACTIONS\" AS TRANS   					" + 
					" WHERE TRANS.\"SITE_ID\" = (CASE WHEN '"+st_name+"' = 'ALL' THEN TRANS.\"SITE_ID\" ELSE '"+st_name+"' END) " + 
					" AND DATE_TRUNC('day',\"TRANSACTION_DATE\")   					" + 
					" BETWEEN to_date('"+st_date+"','dd-mm-yyyy') AND to_date('"+end_date+"','dd-mm-yyyy') " + 
					" ") ; 
			
			System.out.println("@getCashBookReport Insert Temp trans table->"+ createSQL.toString() );
			stmt = conn.createStatement();
			System.out.println("@getCashBookReport Temp trans table Inserted ?->"+stmt.executeUpdate(createSQL.toString()));
			
			
			createSQL = new StringBuilder();
			
			createSQL.append( 
					"CREATE TEMPORARY TABLE \"TEMP_CASH_BOOK_REPORT\"" + 
					"(								" + 
					"\"SITE_ID\"    VARCHAR(100) ,	" + 
					"\"SITE_NAME\"  VARCHAR(200) ,	" + 
					"\"DATE\" DATE,					" +
					"\"TOTAL_SALES\" NUMERIC(10,3),	" + 
					"\"BANK_CARD_SALES\" NUMERIC(10,3),	" +
					"\"ALMAHA_CARD_SALES\" NUMERIC(10,3), " +
					"\"CASH_SALES\" NUMERIC(10,3), " +
					"\"OTHERS_SALES\" NUMERIC(10,3),	  " +
					"\"LUBE_AMOUNT\" NUMERIC(10,3),		" + 
					"\"REVALUE_AMOUNT\" NUMERIC(10,3),	" + 
					"\"LOST_DAMAGE\" NUMERIC(10,3) ,	" + 
					"\"CARD_SALES\" NUMERIC(10,3) ,		" + 
					"\"SUBSIDY_DISCOUNT\" NUMERIC(10,3)," + 
					"\"EXPENSES\" NUMERIC(10,3) ,  		" + 
					"\"SLIP_NO\" VARCHAR (100) , 		" + 
					"\"BANKED\"  NUMERIC(10,3) ,		" +
					" \"DATE_OF_DEPOSIT\" DATE" + 
					") ");
			
			System.out.println("@getCashBookReport Create Temp table Query ->"+ createSQL.toString() );
			stmt = conn.createStatement();
			System.out.println("@getCashBookReport Temp table created ? ->"+stmt.executeUpdate(createSQL.toString()));
			
			createSQL = new StringBuilder();
			
			createSQL.append( "  INSERT INTO \"TEMP_CASH_BOOK_REPORT\" 		" + 
					"			(\"SITE_ID\", \"DATE\")						" + 
					"			SELECT MAX(\"SITE_ID\"), MAX(DATE_TRUNC('day',\"TRANSACTION_DATE\")) " + 
					"			FROM \"TEMP_TRANSACTIONS\"					" + 
					"			GROUP BY \"SITE_ID\" , DATE_TRUNC('day',\"TRANSACTION_DATE\")		 " + 
					"			UNION 										" + 
					"			SELECT SITE_ID , TRANSACTION_DATE			" + 
					"			FROM \"ALMAHA\".EXPENSEHEADER 				" + 
					"			WHERE SITE_ID = ( CASE WHEN '"+st_name+"' = 'ALL' THEN SITE_ID ELSE '"+st_name+"' END) "+ 
					"			AND DATE_TRUNC('day',TRANSACTION_DATE)   							" + 
					"		    BETWEEN to_date('"+st_date+"','dd-mm-yyyy') AND to_date('"+end_date+"','dd-mm-yyyy')" + 
					"			UNION 								" + 
					"			SELECT SITE_ID , TRANSACTION_DATE	" + 
					"			FROM \"ALMAHA\".BANKDEPOSITDETAILS	" + 
					"			WHERE SITE_ID = ( CASE WHEN '"+st_name+"' = 'ALL' THEN SITE_ID ELSE '"+st_name+"' END) "+
					"			AND DATE_TRUNC('day',TRANSACTION_DATE)       	" + 
					"		    BETWEEN to_date('"+st_date+"','dd-mm-yyyy') AND to_date('"+end_date+"','dd-mm-yyyy') " ) ;	
			
			System.out.println("@getCashBookReport Insert Temp table Query ->"+ createSQL.toString() );
			stmt = conn.createStatement();
			System.out.println("@getCashBookReport Temp table Inserted ? ->"+stmt.executeUpdate(createSQL.toString()));
			
			createSQL = new StringBuilder();
			
			createSQL.append( " UPDATE \"TEMP_CASH_BOOK_REPORT\"  AS \"TEMP\"  " + 
					"SET \"EXPENSES\" = REC.\"EXPENSES\"               		   " + 
					"FROM (													   " + 
					"SELECT SITE_ID  AS \"SITE_ID\", TRANSACTION_DATE  AS \"TRANSACTION_DATE\"," + 
					"SUM(TOTALEXPENSE) AS \"EXPENSES\"						   " + 
					"FROM \"ALMAHA\".EXPENSEHEADER 							   " + 
					"WHERE SITE_ID = ( CASE WHEN '"+st_name+"' = 'ALL' THEN SITE_ID ELSE '"+st_name+"' END) "+ 
					"AND DATE_TRUNC('day',TRANSACTION_DATE)   					" + 
					"BETWEEN to_date('"+st_date+"','dd-mm-yyyy') AND to_date('"+end_date+"','dd-mm-yyyy') " + 
					"GROUP BY SITE_ID , TRANSACTION_DATE						" + 
					") REC											     		" + 
					"WHERE \"TEMP\".\"SITE_ID\"  = REC.\"SITE_ID\"		 		" + 
					"AND \"TEMP\".\"DATE\" = REC.\"TRANSACTION_DATE\"	  		 " );
			
			
			System.out.println("@getCashBookReport  Update Query 1->"+ createSQL.toString() );
			stmt = conn.createStatement();
			System.out.println("@getCashBookReport  Update Query 1 Updated ? ->"+stmt.executeUpdate(createSQL.toString()));
			
			
			createSQL = new StringBuilder();
			
			createSQL.append( " UPDATE \"TEMP_CASH_BOOK_REPORT\"  AS \"TEMP\"  " + 
					"SET \"DATE_OF_DEPOSIT\" = REC.\"TRANSACTION_DATE\"     ,  " + 
					"\"SLIP_NO\" = REC.\"SLIP_NUMBER\",						   " + 
					"\"BANKED\"  = REC.\"BANKED\" 							   " + 
					"FROM (													   " + 
					"SELECT SITE_ID  AS \"SITE_ID\", 						   " + 
					"TRANSACTION_DATE  AS \"TRANSACTION_DATE\",				   " + 
					"SUM(DEPOSIT) AS \"BANKED\",								" + 
					"MAX(SLIP_NUMBER) AS \"SLIP_NUMBER\"						" + 
					"FROM \"ALMAHA\".BANKDEPOSITDETAILS 						" + 
					"WHERE SITE_ID = ( CASE WHEN '"+st_name+"' = 'ALL' THEN SITE_ID ELSE '"+st_name+"' END) "+
					"AND STATUS = 'Submitted'									" + 
					"AND DATE_TRUNC('day',TRANSACTION_DATE)   					" + 
					"BETWEEN to_date('"+st_date+"','dd-mm-yyyy') AND to_date('"+end_date+"','dd-mm-yyyy')" + 
					"GROUP BY SITE_ID , TRANSACTION_DATE						" + 
					") REC											     		" + 
					"WHERE \"TEMP\".\"SITE_ID\"  = REC.\"SITE_ID\"		 		" + 
					"AND \"TEMP\".\"DATE\" = REC.\"TRANSACTION_DATE\"	 		" );
			
			
			System.out.println("@getCashBookReport  Update Query 2->"+ createSQL.toString() );
			stmt = conn.createStatement();
			System.out.println("@getCashBookReport  Update Query 2 Updated ? ->"+stmt.executeUpdate(createSQL.toString()));
			
			
           createSQL = new StringBuilder();
			
			createSQL.append( " UPDATE \"TEMP_CASH_BOOK_REPORT\"  AS \"TEMP\"  	" + 
					"SET \"TOTAL_SALES\" = REC.\"TOTAL_SALES\"     ,			" + 
					"\"BANK_CARD_SALES\" = REC.\"BANK_CARD_SALES\",				" + 
					"\"ALMAHA_CARD_SALES\"  = 0,								" + 
					"\"CASH_SALES\"        = REC.\"CASH_SALES\",				" + 
					"\"OTHERS_SALES\"     = REC.\"OTHERS_SALES\"				" + 
					"FROM (														" + 
					"SELECT \"SITE_ID\"  AS \"SITE_ID\", 						" + 
					"\"TRANSACTION_DATE\"  AS \"TRANSACTION_DATE\",				" + 
					"SUM(\"AMOUNT\") AS \"TOTAL_SALES\",						" + 
					"SUM(CASE WHEN ( \"TRANSACTION_TYPE\" = '1') THEN 			" + 
					"\"AMOUNT\" ELSE 0 END)  AS \"CASH_SALES\",					" + 
					"SUM(CASE WHEN ( \"TRANSACTION_TYPE\" = '2') THEN 			" + 
					"\"AMOUNT\" ELSE 0 END)  AS \"BANK_CARD_SALES\",			" + 
					"SUM(CASE WHEN ( \"TRANSACTION_TYPE\" NOT IN ('1', '2')) THEN " + 
					"\"AMOUNT\" ELSE 0 END)  AS \"OTHERS_SALES\" 				" + 
					"FROM \"TEMP_TRANSACTIONS\"									" + 
					"GROUP BY \"SITE_ID\" , \"TRANSACTION_DATE\"				" + 
					") REC											     		" + 
					"WHERE \"TEMP\".\"SITE_ID\"  = REC.\"SITE_ID\"		 		" + 
					"AND \"TEMP\".\"DATE\" = REC.\"TRANSACTION_DATE\" 			" );
			
			
			System.out.println("@getCashBookReport  Update Query 3->"+ createSQL.toString() );
			stmt = conn.createStatement();
			System.out.println("@getCashBookReport  Update Query 3 Updated ? ->"+stmt.executeUpdate(createSQL.toString()));
			
		
			   createSQL = new StringBuilder();
				
				createSQL.append( " UPDATE						" + 
						"  \"TEMP_CASH_BOOK_REPORT\" AS \"TEMP\"" + 
						" SET									" + 
						"  \"REVALUE_AMOUNT\" = REC.\"REVALUE_AMOUNT\"" + 
						" FROM									" + 
						"  (									" +
						"  select inh.site_id AS \"SITE_ID\" ,sum(amount) as \"REVALUE_AMOUNT\"," + 
						" inh.transaction_date AS  \"TRANSACTION_DATE\" " + 
						" from \"ALMAHA\".IncomeHeader inh" + 
						" join \"ALMAHA\".IncomeDetails ind" + 
						" on inh.site_id			= ind.site_id" + 
						" and inh.transaction_no  = ind.transaction_no" + 
						" and ind.income_category ='Recharge'  " + 
						" and inh.status          ='Submitted'" + 
						" AND DATE_TRUNC('day',inh.TRANSACTION_DATE)   					    " + 
						" BETWEEN to_date('"+st_date+"','dd-mm-yyyy') AND to_date('"+end_date+"','dd-mm-yyyy')" + 
						" and inh.site_id= ( CASE WHEN '"+st_name+"' = 'ALL' THEN  inh.site_id  ELSE '"+st_name+"' END) "+
						" group by inh.site_id,inh.transaction_date  " + 
						"  ) REC									" + 
						" WHERE										" + 
						"  \"TEMP\".\"SITE_ID\" = REC.\"SITE_ID\"	" + 
						"  AND \"TEMP\".\"DATE\" = REC.\"TRANSACTION_DATE\"	" + 
						"	" );
				
				
				System.out.println("@getCashBookReport  Update Query 4->"+ createSQL.toString() );
				stmt = conn.createStatement();
				System.out.println("@getCashBookReport  Update Query 4 Updated ? ->"+stmt.executeUpdate(createSQL.toString()));
			
			
			
			String dataSetSQL = " SELECT \"DATE\"  AS \"DATE\" ,	" + 
					" SUM(COALESCE (\"TOTAL_SALES\" , 0.000))   AS \"TOTAL_SALES\",			" + 
					" SUM(COALESCE ( \"ALMAHA_CARD_SALES\", 0.000))  AS \"ALMAHA_CARD_SALES\",	" + 
					" SUM(COALESCE ( \"BANK_CARD_SALES\", 0.000))  AS \"BANK_CARD_SALES\",	" + 
					" SUM(COALESCE ( \"CASH_SALES\" , 0.000))      AS \"CASH_SALES\",		" + 
					" SUM(COALESCE (\"OTHERS_SALES\" , 0.000))   AS \"OTHERS_SALES\"  ,     " +
					" SUM(COALESCE ( \"LUBE_AMOUNT\" , 0.000))     AS \"LUBE_AMOUNT\",		" + 
					" SUM(COALESCE ( \"REVALUE_AMOUNT\", 0.000))   AS \"REVALUE_AMOUNT\",	" + 
					" SUM(COALESCE ( \"LOST_DAMAGE\" , 0.000))     AS \"LOST_DAMAGE\",		" + 
					" SUM(COALESCE ( \"CARD_SALES\" , 0.000))      AS \"CARD_SALES\",		" + 
					" SUM(COALESCE ( \"SUBSIDY_DISCOUNT\", 0.000))  AS \"SUBSIDY_DISCOUNT\",	" + 
					" SUM(COALESCE ( \"EXPENSES\"	, 0.000))		AS \"EXPENSES\" ,		" + 
					" SUM( COALESCE ( COALESCE(\"CASH_SALES\", 0.000)  + COALESCE ( \"REVALUE_AMOUNT\" , 0.000) "+
					" + COALESCE ( \"CARD_SALES\" , 0.000)  -  COALESCE(\"EXPENSES\", 0.000) - COALESCE( \"SUBSIDY_DISCOUNT\" , 0.000) , 0.000))  AS \"TO_BE_BANKED\"," + 
					" MAX(COALESCE ( \"SLIP_NO\" , ' ' ))   AS \"SLIP_NO\",					" + 
					" SUM(COALESCE ( \"BANKED\" , 0.000))    AS \"BANKED\",					" +
					" SUM( COALESCE (  COALESCE(\"BANKED\" , 0.000) - ( COALESCE(\"CASH_SALES\", 0.000)  + COALESCE ( \"REVALUE_AMOUNT\" , 0.000) 	"+
					" + COALESCE ( \"CARD_SALES\" , 0.000)   -  COALESCE(\"EXPENSES\", 0.000) - 				"+
					" COALESCE( \"SUBSIDY_DISCOUNT\" , 0.000) ) , 0.000 )) AS \"DIFFERENCE\"," +
					" MAX(COALESCE ( \"DATE_OF_DEPOSIT\" :: VARCHAR,  '')) AS \"DATE_OF_DEPOSIT\"		" + 
					"FROM \"TEMP_CASH_BOOK_REPORT\"" +
					" GROUP BY \"DATE\"		" +
					" ORDER BY  \"DATE\"  							" ;

			System.out.println("@getCashBookReport dataset Query ->" + dataSetSQL);


			String footerSQL = new String();
			
			footerSQL =  " SELECT 	" + 
					" COALESCE ( SUM(\"TOTAL_SALES\") , 0.000)  AS \"TOTAL_SALES\",	" + 
					" COALESCE ( SUM(\"ALMAHA_CARD_SALES\") , 0.000) AS \"ALMAHA_CARD_SALES\" ,	" + 
					" COALESCE ( SUM(\"BANK_CARD_SALES\") , 0.000) AS \"BANK_CARD_SALES\",		" + 
					" COALESCE ( SUM(\"CASH_SALES\") , 0.000)      AS \"CASH_SALES\",			" + 
					" COALESCE ( SUM( \"OTHERS_SALES\") , 0.000)   AS \"OTHERS_SALES\",        " +
					" COALESCE ( SUM(\"LUBE_AMOUNT\") , 0.000)     AS \"LUBE_AMOUNT\",			" + 
					" COALESCE ( SUM(\"REVALUE_AMOUNT\") , 0.000)  AS \"REVALUE_AMOUNT\",		" + 
					" COALESCE ( SUM(\"LOST_DAMAGE\" ), 0.000)     AS \"LOST_DAMAGE\",			" + 
					" COALESCE ( SUM(\"CARD_SALES\" ) , 0.000)      AS \"CARD_SALES\",			" + 
					" COALESCE ( SUM(\"SUBSIDY_DISCOUNT\") , 0.000) AS \"SUBSIDY_DISCOUNT\",	" + 
					" COALESCE ( SUM(\"EXPENSES\"), 0.000)			AS \"EXPENSES\" ,			" + 
					" COALESCE (  SUM(COALESCE(\"CASH_SALES\", 0.000))  +  SUM(COALESCE ( \"REVALUE_AMOUNT\" , 0.000)) "+
					" + SUM (COALESCE ( \"CARD_SALES\" , 0.000) )  -  SUM( COALESCE(\"EXPENSES\", 0.000) ) - SUM( COALESCE( \"SUBSIDY_DISCOUNT\" , 0.000)) , 0.000) AS \"TO_BE_BANKED\"," +
					" COALESCE ( SUM(\"BANKED\") , 0.000)   AS \"BANKED\",						" + 
					" COALESCE ( SUM(COALESCE (\"BANKED\" , 0.000) ) - ( SUM(COALESCE(\"CASH_SALES\", 0.000))  + SUM( COALESCE ( \"REVALUE_AMOUNT\" , 0.000)) 	"+
					" + SUM( COALESCE ( \"CARD_SALES\" , 0.000) ) -  SUM( COALESCE(\"EXPENSES\", 0.000) ) - 				"+
					" SUM ( COALESCE( \"SUBSIDY_DISCOUNT\" , 0.000) ) ) , 0.000) AS \"DIFFERENCE\" " + 
					"FROM \"TEMP_CASH_BOOK_REPORT\"    						 " ;
					
			System.out.println("@getCashBookReport Footer Query ->" + footerSQL);

			PreparedStatement dataSetStmt = conn.prepareStatement(dataSetSQL);
			PreparedStatement footerStmt = conn.prepareStatement(footerSQL);
			ResultSet dataSet ;
			ResultSet footerSet ;
			
						
			List<String> productList = new ArrayList<>();
			ResultSet productrs      = null;
			Map<String,Record> bodyRecordMap = new HashMap<>();
			List bodyList            = new ArrayList();
			Map<String,String>subBodyList ;
			HashMap mainMap          = new HashMap();
			bodyList                 = new ArrayList();
			String distinctProduct; 
			String distinctProductCode; 
			
			
			double saleSum = 0.0;
			double totalSum = 0.0;		
			
			dataSet = dataSetStmt.executeQuery();
			footerSet = footerStmt.executeQuery();
			
			int count =1 ;
			
				while (dataSet.next())
				{
					subBodyList = new HashMap<>();	
					subBodyList.put("DATE",    dataSet.getString("DATE"));
					subBodyList.put("TOTAL_SALES",  String.format("%,.3f", Double.valueOf(dataSet.getString("TOTAL_SALES"))));
					subBodyList.put("ALMAHA_CARD_SALES",  String.format("%,.3f", Double.valueOf(dataSet.getString("ALMAHA_CARD_SALES"))));
					subBodyList.put("BANK_CARD_SALES",  String.format("%,.3f", Double.valueOf(dataSet.getString("BANK_CARD_SALES"))));
					subBodyList.put("CASH_SALES",  String.format("%,.3f", Double.valueOf(dataSet.getString("CASH_SALES"))));
					subBodyList.put("OTHERS_SALES",  String.format("%,.3f", Double.valueOf(dataSet.getString("OTHERS_SALES"))));
					subBodyList.put("LUBE_AMOUNT",  String.format("%,.3f", Double.valueOf(dataSet.getString("LUBE_AMOUNT"))));
					subBodyList.put("REVALUE_AMOUNT",   String.format("%,.3f", Double.valueOf(dataSet.getString("REVALUE_AMOUNT") ) ) );
					
					subBodyList.put("LOST_DAMAGE",  String.format("%,.3f", Double.valueOf(dataSet.getString("LOST_DAMAGE") ) ) );
					subBodyList.put("CARD_SALES",  String.format("%,.3f", Double.valueOf(dataSet.getString("CARD_SALES") ) ) );
					subBodyList.put("SUBSIDY_DISCOUNT", String.format("%,.3f", Double.valueOf(dataSet.getString("SUBSIDY_DISCOUNT") ) ) );
					subBodyList.put("EXPENSES", String.format("%,.3f", Double.valueOf(dataSet.getString("EXPENSES") ) ) );
					
					subBodyList.put("TO_BE_BANKED", String.format("%,.3f", Double.valueOf(dataSet.getString("TO_BE_BANKED") ) ) );
					subBodyList.put("BANKED", String.format("%,.3f", Double.valueOf(dataSet.getString("BANKED") ) ) );
					subBodyList.put("DIFFERENCE", String.format("%,.3f", Double.valueOf(dataSet.getString("DIFFERENCE") ) ) );
					subBodyList.put("DATE_OF_DEPOSIT",    dataSet.getString("DATE_OF_DEPOSIT"));
					subBodyList.put("SLIP_NO",    dataSet.getString("SLIP_NO"));
					
					bodyList.add(subBodyList);
				}
			
				Map headerMap1 = getHeaderMap();
				mainMap.put("header",  headerMap1);
				
				mainMap.put("dataSet", bodyList);
				
				bodyList            = new ArrayList();	
				
				while (footerSet.next())
				{
					
					subBodyList = new HashMap<>();
					
					subBodyList.put("DATE",    "TOTAL");
					subBodyList.put("TOTAL_SALES",  String.format("%,.3f", Double.valueOf(footerSet.getString("TOTAL_SALES"))));
					subBodyList.put("ALMAHA_CARD_SALES",  String.format("%,.3f", Double.valueOf(footerSet.getString("ALMAHA_CARD_SALES"))));
					subBodyList.put("BANK_CARD_SALES",  String.format("%,.3f", Double.valueOf(footerSet.getString("BANK_CARD_SALES"))));
					subBodyList.put("CASH_SALES",  String.format("%,.3f", Double.valueOf(footerSet.getString("CASH_SALES"))));
					subBodyList.put("OTHERS_SALES",  String.format("%,.3f", Double.valueOf(footerSet.getString("OTHERS_SALES"))));
					subBodyList.put("LUBE_AMOUNT",  String.format("%,.3f", Double.valueOf(footerSet.getString("LUBE_AMOUNT"))));
					subBodyList.put("REVALUE_AMOUNT",   String.format("%,.3f", Double.valueOf(footerSet.getString("REVALUE_AMOUNT") ) ) );
					
					subBodyList.put("LOST_DAMAGE",  String.format("%,.3f", Double.valueOf(footerSet.getString("LOST_DAMAGE") ) ) );
					subBodyList.put("CARD_SALES",  String.format("%,.3f", Double.valueOf(footerSet.getString("CARD_SALES") ) ) );
					subBodyList.put("SUBSIDY_DISCOUNT", String.format("%,.3f", Double.valueOf(footerSet.getString("SUBSIDY_DISCOUNT") ) ) );
					subBodyList.put("EXPENSES", String.format("%,.3f", Double.valueOf(footerSet.getString("EXPENSES") ) ) );
					
					subBodyList.put("TO_BE_BANKED", String.format("%,.3f", Double.valueOf(footerSet.getString("TO_BE_BANKED") ) ) );
					subBodyList.put("BANKED", String.format("%,.3f", Double.valueOf(footerSet.getString("BANKED") ) ) );
					subBodyList.put("DIFFERENCE", String.format("%,.3f", Double.valueOf(footerSet.getString("DIFFERENCE") ) ) );
					
					bodyList.add(subBodyList);
					
				}
			
			mainMap.put("footerSet", bodyList);	
			
			//Converting Hash map to JSON
			json.add(mainMap);
						
			System.out.println("@getCashBookReport Completed->");
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ getCashBookReport-cashBookReport ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}

		private JSONArray JSONObject(HashMap mainMap) {
		// TODO Auto-generated method stub
		return null;
	}

		private Map getHeaderMap() {
			
			Map mainMap = new HashMap();
			List mainList = new ArrayList();
			Map prodMap = new HashMap();
			Map innerMap = new HashMap();
			List pList = new ArrayList();
			
			prodMap.put("headerName", "DATE");
			prodMap.put("field", "DATE");
			prodMap.put("type", "rightAligned");
			mainList.add(prodMap);
			prodMap = new HashMap();
			
			prodMap.put("headerName", "TOTAL FULE SALES");
			prodMap.put("field", "TOTAL_SALES");
			prodMap.put("type", "rightAligned");
			mainList.add(prodMap);
			prodMap = new HashMap();
			
			prodMap.put("headerName", "AL MAHA CARD");
			prodMap.put("field", "ALMAHA_CARD_SALES");
			prodMap.put("type", "rightAligned");
			mainList.add(prodMap);
			
			prodMap = new HashMap();
			prodMap.put("headerName", "BANK CARD");
			prodMap.put("field", "BANK_CARD_SALES");
			prodMap.put("type", "rightAligned");
			mainList.add(prodMap);
			
			
			prodMap = new HashMap();
			prodMap.put("headerName", "NET CASH SALES");
			prodMap.put("field", "CASH_SALES");
			prodMap.put("type", "rightAligned");
			mainList.add(prodMap);
			
			
			prodMap = new HashMap();
			prodMap.put("headerName", "OTHER SALES");
			prodMap.put("field", "OTHERS_SALES");
			prodMap.put("type", "rightAligned");
			mainList.add(prodMap);
			
			prodMap = new HashMap();
			prodMap.put("headerName", "LUBE AMOUNT");
			prodMap.put("field", "LUBE_AMOUNT");
			prodMap.put("type", "rightAligned");
			mainList.add(prodMap);
			
			prodMap = new HashMap();
			prodMap.put("headerName", "REVALUE AMOUNT");
			prodMap.put("field", "REVALUE_AMOUNT");
			prodMap.put("type", "rightAligned");
			mainList.add(prodMap);
			
			prodMap = new HashMap();
			prodMap.put("headerName", "LOST AND DAMAGE");
			prodMap.put("field", "LOST_DAMAGE");
			prodMap.put("type", "rightAligned");
			mainList.add(prodMap);
			
			prodMap = new HashMap();
			prodMap.put("headerName", "CARD SALES");
			prodMap.put("field", "CARD_SALES");
			prodMap.put("type", "rightAligned");
			mainList.add(prodMap);
			
			prodMap = new HashMap();
			prodMap.put("headerName", "SUBSIDY DISCOUNT");
			prodMap.put("field", "SUBSIDY_DISCOUNT");
			prodMap.put("type", "rightAligned");
			mainList.add(prodMap);
			
			prodMap = new HashMap();
			prodMap.put("headerName", "EXPENSES");
			prodMap.put("field", "EXPENSES");
			prodMap.put("type", "rightAligned");
			mainList.add(prodMap);
			
			prodMap = new HashMap();
			prodMap.put("headerName", "AMOUNT TO BE BANKED");
			prodMap.put("field", "TO_BE_BANKED");
			prodMap.put("type", "rightAligned");
			mainList.add(prodMap);
			
			prodMap = new HashMap();
			prodMap.put("headerName", "BANKED");
			prodMap.put("field", "BANKED");
			prodMap.put("type", "rightAligned");
			mainList.add(prodMap);
			
			prodMap = new HashMap();
			prodMap.put("headerName", "DIFF.");
			prodMap.put("field", "DIFFERENCE");
			prodMap.put("type", "rightAligned");
			mainList.add(prodMap);
			
			prodMap = new HashMap();
			prodMap.put("headerName", "DATE OF DEPOSIT");
			prodMap.put("field", "DATE_OF_DEPOSIT");
			mainList.add(prodMap);
			
			prodMap = new HashMap();
			prodMap.put("headerName", "DEPOSIT IF SLIP2 OR SLIP3 IN CDM");
			prodMap.put("field", "SLIP_NO");
			mainList.add(prodMap);
			
			mainMap.put("header",mainList);
			return mainMap;
		}
		
    class SortByDate implements Comparator<Map> {
    @Override
    public int compare(Map first, Map second) {
    	String firstDate = (String) first.get("tranx_date");
    	String secondDate = (String) second.get("tranx_date");
        return secondDate.compareTo(firstDate);
    }
 }
}
