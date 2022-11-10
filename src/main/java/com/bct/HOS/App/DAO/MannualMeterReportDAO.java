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

public class MannualMeterReportDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;
	private long unitConversion = 0;
	private DecimalFormat myFormatter = new DecimalFormat("###,###.000");
	
	public MannualMeterReportDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
		unitConversion = dbc.getUnitConversion();
	}

	/**
	 * 
	 * @return
	 */
	public JSONArray mannualMeterReport(String siteIDs,String userId, String roleId,String country,
			String st_date,String end_date,String st_name) {
		JSONArray json = new JSONArray();
		try {
			
			StringBuilder createSQL = new StringBuilder();
			
			createSQL.append(" CREATE TEMPORARY TABLE \"TEMP_TRANSACTIONS\"" + 
					"( " + 
					"\"SITE_ID\" VARCHAR(100),  	" + 
					"\"PRODUCT_NO\" VARCHAR(100),	" + 
					"\"TANK_NO\" VARCHAR (100),		" + 
					"\"DU_NO\" VARCHAR (100),  		" + 
					"\"NOZZLE_NO\" VARCHAR (100),  	" + 
					"\"PUMP_NO\" VARCHAR (100),  	" + 
					"\"START_TOTALIZER\" VARCHAR (100),     " + 
					"\"END_TOTALIZER\" VARCHAR (100),   	" + 
					"\"UNIT_PRICE\" VARCHAR (100),			" + 
					"\"TRANSACTION_DATE\"	TIMESTAMP	,"+
					" \"VOLUME\" NUMERIC(10,2) , "+
					" \"TRANSACTION_TYPE\" VARCHAR (100) " + 
					") ");
			
			
			System.out.println("@getMannualMeterReport Create Temp trans table->"+ createSQL.toString() );
			Statement stmt = conn.createStatement();
			System.out.println("@getMannualMeterReport Temp trans table Created ?->"+stmt.executeUpdate(createSQL.toString()));
			
			
			
			createSQL = new StringBuilder();
			
			createSQL.append(" INSERT INTO  \"TEMP_TRANSACTIONS\"   			" + 
					"(\"SITE_ID\", \"DU_NO\" , 								 	" + 
					"\"NOZZLE_NO\" , \"START_TOTALIZER\" , \"END_TOTALIZER\",  	" + 
					"\"UNIT_PRICE\", \"PRODUCT_NO\",  \"TANK_NO\" , \"PUMP_NO\" ," + 
					"\"TRANSACTION_DATE\" , \"VOLUME\" , \"TRANSACTION_TYPE\"  ) " + 
					"SELECT \"SITE_ID\",  ' ' AS \"DU_NO\",   					 " + 
					"\"NOZZLE_NO\" , \"START_TOTALIZER\" , \"END_TOTALIZER\",    " + 
					"\"UNIT_PRICE\" , \"PRODUCT_NO\",  \"TANK_NO\"  , \"PUMP_NO\" , " + 
					"\"TRANSACTION_DATE\", \"VOLUME\", \"TRANSACTION_TYPE\"         " + 
					"FROM \"ALMAHA\".\"TRANSACTIONS\" AS TRANS   					" + 
					"WHERE TRANS.\"SITE_ID\" = '"+st_name+"'   						" + 
					"AND DATE_TRUNC('day',\"TRANSACTION_DATE\")   					" + 
					"BETWEEN to_date('"+st_date+"','dd-mm-yyyy') AND to_date('"+end_date+"','dd-mm-yyyy') " + 
					" ") ; 
			
			System.out.println("@getMannualMeterReport Insert Temp trans table->"+ createSQL.toString() );
			stmt = conn.createStatement();
			System.out.println("@getMannualMeterReport Temp trans table Inserted ?->"+stmt.executeUpdate(createSQL.toString()));
			
			
			createSQL = new StringBuilder();
			
			createSQL.append( 
					"CREATE TEMPORARY TABLE \"TEMP_MANNUAL_METER_REPORT\"" + 
					"(								" + 
					"\"SITE_ID\"  VARCHAR(100) ,	" + 
					"\"SITE_NAME\"  VARCHAR(200) ,	" + 
					"\"DU_NO\" VARCHAR (100),		" + 
					"\"PUMP_NO\" VARCHAR (100),		" + 
					"\"NOZZLE_NO\" VARCHAR (100),	" + 
					"\"PRODUCT_NO\" VARCHAR (100) ,	" + 
					"\"PRODUCT_NAME\" VARCHAR (200) ," + 
					"\"TANK_NO\" VARCHAR (100),		" + 
					"\"OPEN\" VARCHAR (100) ,  " + 
					"\"CLOSE\" VARCHAR (100) , " + 
					"\"RO_LTR\" VARCHAR (100) " + 
					") ");
			
			System.out.println("@getMannualMeterReport Create Temp table Query ->"+ createSQL.toString() );
			stmt = conn.createStatement();
			System.out.println("@getMannualMeterReport Temp table created ? ->"+stmt.executeUpdate(createSQL.toString()));
			
			createSQL = new StringBuilder();
			
			createSQL.append( " INSERT INTO \"TEMP_MANNUAL_METER_REPORT\" ( 	" + 
					"\"SITE_ID\",\"SITE_NAME\",									" + 
					"\"PRODUCT_NO\",\"PRODUCT_NAME\" ,							" + 
					"\"DU_NO\", \"PUMP_NO\" , \"NOZZLE_NO\")					" + 
					"SELECT \"TRANS\".\"SITE_ID\", \"SITE\".\"SITE_NAME\",		" + 
					"\"TRANS\".\"PRODUCT_NO\", \"PROD\".\"PRODUCT_NAME\", 		" + 
					"\"NOZZLE\".\"DU_NO\", \"NOZZLE\".\"PUMP_NO\" , 			" + 
					"\"TRANS\".\"NOZZLE_NO\" 									" + 
					"FROM \"TEMP_TRANSACTIONS\" AS \"TRANS\" 					" + 
					"INNER JOIN \"ALMAHA\".\"MS_PRODUCTS\" AS \"PROD\"			" + 
					"ON \"TRANS\".\"PRODUCT_NO\" = TO_CHAR(\"PROD\".\"PRODUCT_NO\", 'FM9999')" + 
					"AND \"TRANS\".\"SITE_ID\"   = \"PROD\".\"SITE_ID\"			" + 
					"INNER JOIN \"ALMAHA\".\"MS_NOZZLE_LIST\" AS \"NOZZLE\"		" + 
					"ON \"TRANS\".\"PUMP_NO\" = TO_CHAR( \"NOZZLE\".\"PUMP_NO\" , 'FM9999')     " + 
					"AND \"TRANS\".\"NOZZLE_NO\" = TO_CHAR(\"NOZZLE\".\"NOZZLE_NO\"  , 'FM9999')" + 
					"AND \"TRANS\".\"SITE_ID\"  = \"NOZZLE\".\"SITE_ID\"		" + 
					"AND \"TRANS\".\"PRODUCT_NO\" = TO_CHAR(\"NOZZLE\".\"PRODUCT_NO\" , 'FM9999') " + 
					"AND \"TRANS\".\"TANK_NO\"     = TO_CHAR(\"NOZZLE\".\"TANK_NO\" , 'FM9999')   " + 
					"INNER JOIN \"ALMAHA\".\"MS_SITE\" AS \"SITE\"				" + 
					"ON \"TRANS\".\"SITE_ID\"  = \"SITE\".\"SITE_ID\"			" + 
					"GROUP BY \"TRANS\".\"SITE_ID\", \"SITE\".\"SITE_NAME\",	" + 
					"\"TRANS\".\"PRODUCT_NO\", \"PROD\".\"PRODUCT_NAME\", 		" + 
					"\"TRANS\".\"NOZZLE_NO\" , \"NOZZLE\".\"PUMP_NO\",			" + 
					"\"NOZZLE\".\"DU_NO\" 										" );
			
			System.out.println("@getMannualMeterReport Insert Temp table Query ->"+ createSQL.toString() );
			stmt = conn.createStatement();
			System.out.println("@getMannualMeterReport Temp table Inserted ? ->"+stmt.executeUpdate(createSQL.toString()));
			
			createSQL = new StringBuilder();
			
			createSQL.append( " UPDATE \"TEMP_MANNUAL_METER_REPORT\"  AS \"TEMP\"" + 
					"SET \"OPEN\" = REC.\"START_TOTALIZER\",			 " + 
					"\"RO_LTR\"  =REC.\"UNIT_PRICE\"					 " + 
					"FROM (		" + 
					"SELECT \"TRANS\".* FROM \"TEMP_TRANSACTIONS\" AS \"TRANS\"	 " + 
					"WHERE  (\"TRANS\".\"SITE_ID\",						" + 
					"\"TRANS\".\"PRODUCT_NO\", \"TRANS\".\"NOZZLE_NO\" ," + 
					"\"TRANS\".\"PUMP_NO\" ,							" + 
					"\"TRANS\".\"TRANSACTION_DATE\" ) IN 				" + 
					"(select \"TRANS\".\"SITE_ID\",						" + 
					"\"TRANS\".\"PRODUCT_NO\", \"TRANS\".\"NOZZLE_NO\" ," + 
					"\"TRANS\".\"PUMP_NO\" ,							" + 
					"MIN(\"TRANS\".\"TRANSACTION_DATE\")				" + 
					"from \"TEMP_TRANSACTIONS\" AS \"TRANS\"			" + 
					"GROUP BY \"TRANS\".\"SITE_ID\",					" + 
					"\"TRANS\".\"PRODUCT_NO\", \"TRANS\".\"NOZZLE_NO\" ," + 
					"\"TRANS\".\"PUMP_NO\" 								" + 
					")) REC												" + 
					"WHERE \"TEMP\".\"SITE_ID\"  = REC.\"SITE_ID\"		" + 
					"AND \"TEMP\".\"PRODUCT_NO\" = REC.\"PRODUCT_NO\"	" + 
					"AND \"TEMP\".\"NOZZLE_NO\"  = REC.\"NOZZLE_NO\"	" + 
					"AND \"TEMP\".\"PUMP_NO\"    = REC.\"PUMP_NO\"		" + 
					"AND \"TEMP\".\"PRODUCT_NO\" = REC.\"PRODUCT_NO\"   " );
			
			
			System.out.println("@getMannualMeterReport  Update Query 1->"+ createSQL.toString() );
			stmt = conn.createStatement();
			System.out.println("@getMannualMeterReport  Update Query 1 Updated ? ->"+stmt.executeUpdate(createSQL.toString()));
			
			
			createSQL = new StringBuilder();
			
			createSQL.append( " UPDATE \"TEMP_MANNUAL_METER_REPORT\"  AS \"TEMP\" " + 
					"SET \"CLOSE\" = REC.\"END_TOTALIZER\"               " + 
					"FROM (														  " + 
					"SELECT \"TRANS\".* FROM \"TEMP_TRANSACTIONS\" AS \"TRANS\"   " + 
					"WHERE  (\"TRANS\".\"SITE_ID\",						" + 
					"\"TRANS\".\"PRODUCT_NO\", \"TRANS\".\"NOZZLE_NO\" ," + 
					"\"TRANS\".\"PUMP_NO\" ,							" + 
					"\"TRANS\".\"TRANSACTION_DATE\" ) IN 				" + 
					"(select \"TRANS\".\"SITE_ID\",						" + 
					"\"TRANS\".\"PRODUCT_NO\", \"TRANS\".\"NOZZLE_NO\" ," + 
					"\"TRANS\".\"PUMP_NO\" ,							" + 
					"MAX(\"TRANS\".\"TRANSACTION_DATE\")				" + 
					"from \"TEMP_TRANSACTIONS\" AS \"TRANS\"			" + 
					"GROUP BY \"TRANS\".\"SITE_ID\",					" + 
					"\"TRANS\".\"PRODUCT_NO\", \"TRANS\".\"NOZZLE_NO\" ," + 
					"\"TRANS\".\"PUMP_NO\" 								" + 
					")) REC											    " + 
					"WHERE \"TEMP\".\"SITE_ID\"  = REC.\"SITE_ID\"		" + 
					"AND \"TEMP\".\"PRODUCT_NO\" = REC.\"PRODUCT_NO\"	" + 
					"AND \"TEMP\".\"NOZZLE_NO\"  = REC.\"NOZZLE_NO\"	" + 
					"AND \"TEMP\".\"PUMP_NO\"    = REC.\"PUMP_NO\"		" + 
					"AND \"TEMP\".\"PRODUCT_NO\" = REC.\"PRODUCT_NO\" 	" );
			
			
			System.out.println("@getMannualMeterReport  Update Query 2->"+ createSQL.toString() );
			stmt = conn.createStatement();
			System.out.println("@getMannualMeterReport  Update Query 2 Updated ? ->"+stmt.executeUpdate(createSQL.toString()));
			
			
			
			String dataSetSQL = " SELECT row_number() OVER () AS \"SL_NO\" ,     " +
					"\"TMP\".\"SITE_ID\" AS \"SITE_ID\" ,	            " + 
					"\"TMP\".\"SITE_NAME\" AS \"SITE_NAME\",			" + 
					"\"TMP\".\"PRODUCT_NO\"  AS \"PRODUCT_NO\",			" + 
					"\"TMP\".\"PRODUCT_NAME\" AS \"PRODUCT_NAME\" ,  	" + 
					"\"DU_NO\" AS \"DU_NO\",							" + 
					"\"TMP\".\"PUMP_NO\" \"PUMP_NO\" , 				 	" + 
					"\"NOZZLE_NO\" AS \"NOZZLE_NO\",					" + 
					"\"OPEN\" :: NUMERIC  AS \"OPEN\" ,		 			" + 
					"\"CLOSE\" :: NUMERIC AS \"CLOSE\" ,		 		" + 
					"( \"TMP\".\"CLOSE\" :: NUMERIC  - 					" + 
					"\"TMP\".\"OPEN\" :: NUMERIC  ) AS \"SALES\" ,		" + 
					"\"RO_LTR\" AS \"RO_LTR\" ,	 						" + 
					"\"TMP\".\"RO_LTR\" :: NUMERIC * 					" + 
					"( \"TMP\".\"CLOSE\" :: NUMERIC - 			       	" + 
					"\"TMP\".\"OPEN\" :: NUMERIC )  AS \"TOTAL\" 		" + 
					"FROM \"TEMP_MANNUAL_METER_REPORT\" AS \"TMP\" 		" +
				    " WHERE \"TMP\".\"PRODUCT_NAME\" = ? " + 
					"ORDER BY " +
					" \"PUMP_NO\" :: NUMERIC  ASC ,  " + 
					" \"NOZZLE_NO\" ASC  " ;

			System.out.println("@getMannualMeterReport dataset Query ->" + dataSetSQL);


			String footerSQL = new String();
			
			footerSQL = " SELECT		" + 
			"\"TMP\".\"PRODUCT_NO\",	" + 
			"\"TMP\".\"SITE_ID\",		" + 
			"COALESCE(\"REC\".\"VALUE\", 'OTHERS') AS \"TRANS_TYPE\",	" + 
			"SUM (\"TMP\".\"VOLUME\" :: NUMERIC) AS \"SALES\",			" + 
			"SUM (\"TMP\".\"VOLUME\" :: NUMERIC) * MAX(\"TMP\".\"UNIT_PRICE\" :: NUMERIC) AS \"TOTAL\"	" + 
			"FROM						" + 
			"\"TEMP_TRANSACTIONS\" AS \"TMP\"	" + 
			"LEFT JOIN ( SELECT \"MV\".\"KEY\" AS \"KEY\", \"MV\".\"VALUE\" AS \"VALUE\" FROM  	" + 
			"			   \"ALMAHA\".\"METADATA\" AS \"MD\" 									" + 
			"			   JOIN \"ALMAHA\".\"METADATA_VALUE\" AS \"MV\" 						" + 
			"			   ON ( \"MD\".\"METADATA\" = 'REPORT_TRANS_TYPE'						" + 
			"					AND \"MD\".\"ID\" = \"MV\".\"METADATA_ID\" ) ) \"REC\"			" + 
			"			   ON \"REC\".\"KEY\" = \"TMP\".\"TRANSACTION_TYPE\"					" + 
			"WHERE									" + 
			"\"TMP\".\"PRODUCT_NO\" = ? 			" + 
			"GROUP BY								" + 
			"\"TMP\".\"PRODUCT_NO\",				" + 
			"\"TMP\".\"SITE_ID\",					" + 
			"\"REC\".\"VALUE\"						" + 
			"ORDER BY								" + 
			"\"REC\".\"VALUE\"						" ;
								
			System.out.println("@getMannualMeterReport Footer Query ->" + footerSQL);

			PreparedStatement dataSetStmt = conn.prepareStatement(dataSetSQL);
			PreparedStatement footerStmt = conn.prepareStatement(footerSQL);
			
			StringBuilder SQL = new StringBuilder();
			
			SQL.append(" SELECT DISTINCT \"TMP\".\"PRODUCT_NAME\" AS \"PRODUCT_NAME\", "
					+ "   \"TMP\".\"PRODUCT_NO\" AS  PRODUCT_NO"+
					  "   FROM \"TEMP_MANNUAL_METER_REPORT\" AS \"TMP\" 		"
					+ "   ORDER BY \"TMP\".\"PRODUCT_NAME\" DESC         " );
			
			System.out.println("@getMannualMeterReport  Product Query ->"+ SQL.toString() );
			
			List<String> productList = new ArrayList<>();
			ResultSet productrs      = null;
			Map<String,Record> bodyRecordMap = new HashMap<>();
			List bodyList            = new ArrayList();
			Map<String,String>subBodyList ;
			HashMap mainMap          = new HashMap();
			bodyList                 = new ArrayList();
			String distinctProduct; 
			String distinctProductCode; 
			
			ResultSet dataSet ;
			ResultSet footerSet ;
			

			stmt = conn.createStatement();
			productrs = stmt.executeQuery(SQL.toString());
			
			double saleSum = 0.0;
			double totalSum = 0.0;
			
			while (productrs.next())
			{
				distinctProduct = productrs.getString("PRODUCT_NAME");
				distinctProductCode =  productrs.getString("PRODUCT_NO");
				
				if(!productList.contains(distinctProduct))
					productList.add(distinctProduct);
				
				dataSetStmt.setString(1,distinctProduct);
				dataSet = dataSetStmt.executeQuery();
				
				int count =1 ;
				while (dataSet.next())
				{
					subBodyList = new HashMap<>();	
					subBodyList.put("SL_NO",     ""+count++);
					subBodyList.put("DU_NO",     dataSet.getString("DU_NO"));
					subBodyList.put("PUMP_NO",   dataSet.getString("PUMP_NO"));
					subBodyList.put("NOZZLE_NO", dataSet.getString("NOZZLE_NO"));
					subBodyList.put("PRODUCT_NAME", dataSet.getString("PRODUCT_NAME"));
					subBodyList.put("OPEN",   String.format("%,.3f", Double.valueOf(dataSet.getString("OPEN") ) ) );
					subBodyList.put("CLOSE",  String.format("%,.3f", Double.valueOf(dataSet.getString("CLOSE") ) ) );
					subBodyList.put("SALES",  String.format("%,.3f", Double.valueOf(dataSet.getString("SALES") ) ) );
					subBodyList.put("RO_LTR", String.format("%,.3f", Double.valueOf(dataSet.getString("RO_LTR") ) ) );
					subBodyList.put("TOTAL", String.format("%,.3f", Double.valueOf(dataSet.getString("TOTAL") ) ) );
					
					bodyList.add(subBodyList);
				}
				
				footerStmt.setString(1,distinctProductCode);
				footerSet = footerStmt.executeQuery();
				
				saleSum = 0.0;
				totalSum = 0.0;
				
				while (footerSet.next())
				{
					subBodyList = new HashMap<>();
					subBodyList.put( "CLOSE",footerSet.getString("TRANS_TYPE") +" SALE" );
					subBodyList.put("SALES", String.format("%,.3f", Double.valueOf(footerSet.getString("SALES"))) );
					subBodyList.put("TOTAL",String.format("%,.3f", Double.valueOf(footerSet.getString("TOTAL")))  );
					bodyList.add(subBodyList);
				
					saleSum += Double.valueOf(footerSet.getString("SALES"));
					totalSum += Double.valueOf(footerSet.getString("TOTAL"));
				}
			
				subBodyList = new HashMap<>();
				
				subBodyList.put( "CLOSE", "AL MAHA CARD SALE");
				subBodyList.put("SALES",String.format("%,.3f", Double.valueOf("0") ) );
				subBodyList.put("TOTAL", String.format("%,.3f", Double.valueOf("0") ));
				
				bodyList.add(subBodyList);
			
				
				subBodyList = new HashMap<>();
				
				subBodyList.put( "CLOSE", "TOTAL SALES");
				subBodyList.put("SALES", String.format("%,.3f", saleSum ) ); 
				subBodyList.put("TOTAL", String.format("%,.3f", totalSum ) );
				
				bodyList.add(subBodyList);
			
			}
			
			Map headerMap1 = getHeaderMap(productList);
			mainMap.put("header",  headerMap1);
			mainMap.put("dataSet", bodyList);
				
			
			//Converting Hash map to JSON
			json.add(mainMap);
						
			System.out.println("@getMannualMeterReport Completed->");
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ getMannualMeterReport-mannualMeterReport ::" + ex.getMessage());
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

		private Map getHeaderMap(List<String> productList) {
			
			Map mainMap = new HashMap();
			List mainList = new ArrayList();
			Map prodMap = new HashMap();
			Map innerMap = new HashMap();
			List pList = new ArrayList();
			
			prodMap.put("headerName", "SL No");
			prodMap.put("field", "SL_NO");
			prodMap.put("type", "rightAligned");
			mainList.add(prodMap);
			prodMap = new HashMap();
			
			prodMap.put("headerName", "DU NO");
			prodMap.put("field", "DU_NO");
			prodMap.put("type", "rightAligned");
			mainList.add(prodMap);
			prodMap = new HashMap();
			
			prodMap.put("headerName", "PUMP NO");
			prodMap.put("field", "PUMP_NO");
			prodMap.put("type", "rightAligned");
			mainList.add(prodMap);
			
			prodMap = new HashMap();
			prodMap.put("headerName", "NOZZLE NO");
			prodMap.put("field", "NOZZLE_NO");
			prodMap.put("type", "rightAligned");
			mainList.add(prodMap);
			
			
			prodMap = new HashMap();
			prodMap.put("headerName", "PRODUCT NAME");
			prodMap.put("field", "PRODUCT_NAME");
			mainList.add(prodMap);
			
			prodMap = new HashMap();
			prodMap.put("headerName", "OPEN");
			prodMap.put("field", "OPEN");
			prodMap.put("type", "rightAligned");
			mainList.add(prodMap);
			
			prodMap = new HashMap();
			prodMap.put("headerName", "CLOSE");
			prodMap.put("field", "CLOSE");
			prodMap.put("type", "rightAligned");
			mainList.add(prodMap);
			
			prodMap = new HashMap();
			prodMap.put("headerName", "SALES");
			prodMap.put("field", "SALES");
			prodMap.put("type", "rightAligned");
			mainList.add(prodMap);
			
			prodMap = new HashMap();
			prodMap.put("headerName", "RO LTR");
			prodMap.put("field", "RO_LTR");
			prodMap.put("type", "rightAligned");
			mainList.add(prodMap);
			
			prodMap = new HashMap();
			prodMap.put("headerName", "TOTAL");
			prodMap.put("field", "TOTAL");
			prodMap.put("type", "rightAligned");
			mainList.add(prodMap);
				
			/*}*/
			
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
