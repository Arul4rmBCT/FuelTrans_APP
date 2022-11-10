package com.bct.HOS.App.DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.bct.HOS.App.utils.DBConnector;
import com.bct.HOS.App.utils.HOSConfig;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SalesDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;
	private long unitConversion = 0;

	public SalesDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
		unitConversion = dbc.getUnitConversion();
	}

	static int percent(int a, int b) {
		float result = 0;
		result = ((b - a) * 100) / a;
		return (int) result;
	}

	/**
	 * 
	 * @return
	 */
	public JSONArray getTOPSalesSites(String userId, String siteIDs, String country, String fromDate, String toDate,
			String limitCount) {
		JSONArray json = new JSONArray();
		try {
			String SQL = " 		SELECT TX.\"SITE_ID\",\"SITE_NAME\",S.\"COUNTRY\","
					+ " to_char(ROUND(SUM(\"VOLUME\") / " + unitConversion
					+ ",\"DEC_VAL\"), '99G99G999D999') AS \"VOLUME\" " + "		FROM " + schema
					+ ".\"TRANSACTIONS\" TX " + "		INNER JOIN " + schema
					+ ".\"MS_PRODUCTS\" PD ON PD.\"PRODUCT_NO\" = TX.\"PRODUCT_NO\" AND TX.\"SITE_ID\"= PD.\"SITE_ID\" "
					+ "		INNER JOIN " + schema + ".\"MS_SITE\" S ON S.\"SITE_ID\" = TX.\"SITE_ID\" "
					+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" CNT ON CNT.\"COUNTRY\" = S.\"COUNTRY\" " + " INNER JOIN \"BCT\".user_sites US ON US.site_id = TX.\"SITE_ID\" "
					+ "		WHERE DATE(\"TRANSACTION_DATE\")::timestamp::date BETWEEN '" + fromDate + "' AND '" + toDate
					+ "'  AND US.user_id = '" + userId + "' ";
			SQL += " AND PD.\"ADRM_STATUS\" != 'D' ";
			//SQL += " AND PD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM " + schema
			//		+ ".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = TX.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = TX.\"SITE_ID\" ) ";

			if (siteIDs != null) {
				SQL += " AND TX.\"SITE_ID\" IN (" + siteIDs + ")";
			}

			if (country != null) {
				SQL += " AND US.country = '" + country + "'";
			}

			SQL += " GROUP BY TX.\"SITE_ID\",\"SITE_NAME\",S.\"COUNTRY\", \"DEC_VAL\" "
					+ "	ORDER BY SUM(\"VOLUME\") DESC";

			SQL += " LIMIT " + limitCount;

			System.out.println("\n\n\n\n");
			System.out.println("getTOPSalesSites>>>>>>>>>>>>>>>>>>"+SQL);
			System.out.println("\n\n\n\n");

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ SalesDAO-getTOPSalesSites ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	/**
	 * 
	 * @return
	 */
	public JSONArray getSalesPercentage(String userId, String siteIDs, String fromDate, String toDate, String country,
			boolean grouping) {
		JSONArray json = new JSONArray();
		try {
			String SQL = null;

			if(grouping) {
				SQL = " SELECT UPPER(\"PRODUCT_GROUP\") AS \"PRODUCT_NAME\", " ;
			}else {
				SQL = " SELECT UPPER(PRD.\"PRODUCT_NAME\") AS \"PRODUCT_NAME\", " ;
			}
			
			SQL +=	" SUM(\"VOLUME\") AS \"VOLUME\", " + 
					" ROUND((SUM(\"VOLUME\") *100) / (SELECT SUM(\"VOLUME\") FROM "+schema+".\"TRANSACTIONS\" T  " + 
					"			INNER JOIN \"BCT\".user_sites US1 ON US1.site_id = T.\"SITE_ID\" " + 
					"			WHERE 1 = 1  " + 
					"			AND US1.user_id = '" +userId +"'" ;
			if(country!=null)		
				SQL += " AND US1.country = '"+country+"' " ;
			
			SQL +=  " AND \"TRANSACTION_DATE\" :: DATE BETWEEN '"+fromDate+"' AND '"+toDate+"' ) ,2)  AS \"PERCENTAGE\" " + 
					" FROM "+schema+".\"TRANSACTIONS\" TRN  " + 
					" INNER JOIN "+schema+".\"MS_SITE\" MS ON MS.\"SITE_ID\" = TRN.\"SITE_ID\"  " + 
					" INNER JOIN "+schema+".\"MS_PRODUCTS\" PRD ON PRD.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\" AND TRN.\"SITE_ID\"=PRD.\"SITE_ID\"  " + 
					" INNER JOIN \"BCT\".user_sites US ON US.site_id = TRN.\"SITE_ID\"  " + 
					//" INNER JOIN \"BCT\".\"MS_COUNTRY\" CNT ON CNT.\"COUNTRY\" = US.country   " + 
					" INNER JOIN \"BCT\".\"COUNTRY_PRODUCT_CONFIG\" CPC ON CPC.\"PRODUCT\" = PRD.\"PRODUCT_NAME\" " + 
					" WHERE 1 = 1  AND PRD.\"ADRM_STATUS\" != 'D' " + 
					//" AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1  " + 
					//"							WHERE PRD1.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\"  " + 
					//"						   AND PRD1.\"SITE_ID\" = TRN.\"SITE_ID\" )  " + 
					" AND US.user_id = '" +userId + "'"+ 
					" AND \"TRANSACTION_DATE\" :: DATE BETWEEN '"+fromDate+"' AND '"+toDate+"'  ";
			
			if(country!=null)		
				SQL +=  " AND US.country = '"+country+"' ";

			if (grouping) {
				SQL += " GROUP BY \"PRODUCT_GROUP\" ";
			} else {
				SQL += " GROUP BY PRD.\"PRODUCT_NAME\" ";
			}

			System.out.println("\n\n\n\n");
			System.out.println("getSalesPercentage >>>> " + SQL);
			System.out.println("\n\n\n\n");

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ SalesDAO-getSalesPercentage ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	/**
	 * 
	 * @return
	 */
	public JSONArray getCountrySummary(String siteIDs, String fromDate, String toDate) {
		JSONArray json = new JSONArray();
		try {
			String SQL = " 	SELECT ROW_NUMBER () OVER (ORDER BY MSC.\"COUNTRY\") AS \"SNO\", MSC.\"COUNTRY\",  "
					+ " CNT.\"LATITUDE\" , CNT.\"LONGITUDE\", "
					+ " coalesce(TMP2.\"COUNT\",0,TMP2.\"COUNT\") AS \"COUNT\", "
					+ " to_char(coalesce(TMP.\"SALES\",0,TMP.\"SALES\"), '99G99G999D999')  AS \"SALES\",  "
					+ " coalesce(TMP1.\"INVENTORY\",0,TMP1.\"INVENTORY\") AS \"INVENTORY\"  " + " FROM " + schema
					+ ".\"MS_SITE\" MSC  "
					+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" CNT ON CNT.\"COUNTRY\" = MSC.\"COUNTRY\"  "
					+ " LEFT OUTER JOIN (   " + " SELECT MS.\"COUNTRY\",   " + " COUNT(*) AS \"COUNT\"  " + " FROM "
					+ schema + ".\"MS_SITE\" MS  " + " WHERE 1 = 1    " +
					// " AND TRN.\"SITE_ID\"IN () " +
					// " AND UPPER(\"SITE_STATUS\")='ONLINE' " +
					" GROUP BY MS.\"COUNTRY\"  " + " ) AS TMP2 ON TMP2.\"COUNTRY\"= MSC.\"COUNTRY\" "
					+ " LEFT OUTER JOIN (  " + "	SELECT MS.\"COUNTRY\",  " + "	SUM(\"VOLUME\") AS \"SALES\"  "
					+ "	FROM " + schema + ".\"TRANSACTIONS\" TRN  " + "	INNER JOIN " + schema
					+ ".\"MS_PRODUCTS\" PRD ON PRD.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\" AND TRN.\"SITE_ID\"=PRD.\"SITE_ID\"   "
					+ "	INNER JOIN " + schema + ".\"MS_SITE\" MS ON MS.\"SITE_ID\" = TRN.\"SITE_ID\"  "
					+ "	WHERE 1 = 1   AND PRD.\"ADRM_STATUS\" != 'D' "
					//+ "   AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM " + schema
					//+ ".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = TRN.\"SITE_ID\" ) "
					+
					// " AND TRN.\"SITE_ID\" IN () " +
					"	AND \"TRANSACTION_DATE\" :: DATE BETWEEN '" + fromDate + "' AND '" + toDate + "'  "
					+ "	GROUP BY MS.\"COUNTRY\"  " + " ) AS TMP ON TMP.\"COUNTRY\" = MSC.\"COUNTRY\"  "
					+ " LEFT OUTER JOIN (  " + "	SELECT MS.\"COUNTRY\",  " + "	SUM(\"VOLUME\") AS \"INVENTORY\"  "
					+ "	FROM " + schema + ".\"INVENTORY\" INV  " + "	INNER JOIN " + schema
					+ ".\"MS_SITE\" MS ON MS.\"SITE_ID\" = INV.\"SITE_ID\"  " + "	WHERE 1 = 1   " +
					// " AND INV.\"SITE_ID\" IN () " +
					"	AND INV.\"INVENTORY_DATE\" = (  " + "		SELECT MAX(INV1.\"INVENTORY_DATE\")   "
					+ "		FROM " + schema + ".\"INVENTORY\" INV1  " + "		INNER JOIN " + schema
					+ ".\"MS_SITE\"  MS1 ON MS1.\"SITE_ID\" = INV1.\"SITE_ID\"  " + "		WHERE 1 = 1   "
					+ "		AND MS1.\"SITE_ID\" = MS.\"SITE_ID\"  " + "		AND MS1.\"COUNTRY\" = MS.\"COUNTRY\"  "
					+ "		AND INV1.\"INVENTORY_DATE\" :: DATE BETWEEN '" + fromDate + "' AND '" + toDate + "'  "
					+ "	)  " + "	GROUP BY MS.\"COUNTRY\"  " + " ) AS TMP1 ON TMP1.\"COUNTRY\" = MSC.\"COUNTRY\" "
					+ " GROUP BY MSC.\"COUNTRY\",CNT.\"LATITUDE\" , CNT.\"LONGITUDE\",TMP2.\"COUNT\",TMP.\"SALES\",TMP1.\"INVENTORY\" ";

			// System.out.println("\n\n\n\n");
			//System.out.println("getCountrySummary>>>>>>>>>>>>>>>>>>"+SQL);
			// System.out.println("\n\n\n\n");

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ SalesDAO-getCountrySummary ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	/**
	 * 
	 * @return
	 */
	public JSONArray getSalesByFilter(String siteIDs, String timePeriodType, String recordLimit, String fromDate,
			String toDate, boolean paymentMode, String productName) {
		JSONArray json = new JSONArray();
		try {

			String[] siteIdCount = siteIDs.split(",");
			int siteCount = 0;

			String SQL = " SELECT ROW_NUMBER () OVER (ORDER BY TRN.\"SITE_ID\") AS \"SNO\"  , TRN.\"SITE_ID\", MST.\"SITE_NAME\",MST.\"SITE_TYPE\",MST.\"DEALER_NAME\",MST.\"CLIENT_NAME\", "
					+ " MST.\"REGION\",MST.\"COUNTRY\",\"DEC_VAL\" ,MST.\"SITE_STATUS\",TO_CHAR(MST.\"LAST_CONNECTION_TIME\" ,'YYYY-MM-DD HH:MI:SS') AS \"LAST_CONNECTION_TIME\"  ,";
			if (paymentMode) {
				SQL += " TRN.\"MOPD1\",TRN.\"MOPD2\",TRN.\"MOPD3\",TRN.\"MOPD4\",TRN.\"MOPD5\",TRN.\"MOPD6\", ";
			}
			if (timePeriodType.equalsIgnoreCase("WEEKLY")) {
				SQL += " DATE_TRUNC('WEEK',TRN.\"TRANSACTION_DATE\"::DATE)::DATE -1 AS \"WEEKLY\", "
						+ " (extract('day' from date_trunc('week', \"TRANSACTION_DATE\") - date_trunc('week', date_trunc('month', \"TRANSACTION_DATE\"))) / 7 + 1)  AS \"WEEK\",";
			} else if (timePeriodType.equalsIgnoreCase("MONTHLY")) {
				SQL += " DATE_TRUNC('MONTH',TRN.\"TRANSACTION_DATE\"::DATE) AS \"MONTHLY\", "
						+ " TO_CHAR(TO_DATE (date_part('month',\"TRANSACTION_DATE\")::text, 'MM'), 'Month') AS \"MONTH\",";
			} else {
				// System.out.println("DATE Filter!..");
			}

			// Check for include PRICE - for single RO
			if (siteIdCount != null) {
				siteCount = siteIdCount.length;
				if (siteCount == 1) {
					SQL += " PRD.\"PRICE\",ROUND( (SUM(\"AMOUNT\") ) ,\"DEC_VAL\"  ) AS \"TOTAL_VALUE\",CUR.\"CURRENCY_CODE\",";
				}
			}

			SQL += " to_char(ROUND( SUM(\"VOLUME\") / " + unitConversion
					+ "  ,\"DEC_VAL\"), '99G99G999D999') AS \"VOLUME\", "
					+ " MTN.\"PRODUCT_NO\",UPPER(PRD.\"PRODUCT_NAME\") AS \"PRODUCT_NAME\"  " + " FROM " + schema
					+ ".\"TRANSACTIONS\" TRN " + " INNER JOIN " + schema
					+ ".\"MS_SITE\" MST ON TRN.\"SITE_ID\"=MST.\"SITE_ID\" "
					+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" AS CUR ON MST.\"COUNTRY\" = CUR.\"COUNTRY\" " + " INNER JOIN "
					+ schema
					+ ".\"MS_TANK\" MTN ON TRN.\"TANK_NO\"=MTN.\"TANK_NO\"  AND MTN.\"PRODUCT_NO\"=TRN.\"PRODUCT_NO\" AND MTN.\"SITE_ID\"=TRN.\"SITE_ID\" "
					+ " INNER JOIN " + schema
					+ ".\"MS_PRODUCTS\" PRD ON MTN.\"PRODUCT_NO\"=PRD.\"PRODUCT_NO\" AND PRD.\"SITE_ID\"=MST.\"SITE_ID\" "
					+ " WHERE 1=1 AND PRD.\"ADRM_STATUS\" != 'D' AND MTN.\"ADRM_STATUS\" != 'D' ";
			//SQL += " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM " + schema
				//	+ ".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = TRN.\"SITE_ID\" ) ";
			//SQL += " AND MTN.\"MODIFIED_TIME\" = (SELECT MAX(\"MODIFIED_TIME\") FROM "+schema+".\"MS_TANK\" TNK1 WHERE TNK1.\"SITE_ID\" = MTN.\"SITE_ID\"  AND TNK1.\"TANK_NO\"=MTN.\"TANK_NO\" ) ";
			
			if (siteIDs != null) {
				SQL += " AND TRN.\"SITE_ID\" IN (" + siteIDs + ")";
			}

			if (fromDate != null && toDate != null) {
				SQL += " AND TRN.\"TRANSACTION_DATE\"::timestamp::date BETWEEN ('" + fromDate + "') AND ('" + toDate
						+ "')";
			}

			if (productName != null) {
				SQL += " AND UPPER(PRD.\"PRODUCT_NAME\") = UPPER('" + productName + "') ";
			}

			// GROUP BY
			SQL += " GROUP BY MTN.\"PRODUCT_NO\",TRN.\"SITE_ID\", MST.\"SITE_NAME\",MST.\"SITE_TYPE\",MST.\"DEALER_NAME\",MST.\"CLIENT_NAME\", "
					+ " MST.\"REGION\",MST.\"COUNTRY\",MST.\"SITE_STATUS\",MST.\"LAST_CONNECTION_TIME\", \"DEC_VAL\" , ";

			if (siteCount == 1)
				SQL += " PRD.\"PRICE\",CUR.\"CURRENCY_CODE\", ";

			if (paymentMode)
				SQL += " TRN.\"MOPD1\",TRN.\"MOPD2\",TRN.\"MOPD3\",TRN.\"MOPD4\",TRN.\"MOPD5\",TRN.\"MOPD6\", ";

			SQL += " PRD.\"PRODUCT_NAME\",PRD.\"PRODUCT_NO\"";
			if (timePeriodType.equalsIgnoreCase("WEEKLY")) {
				SQL += " ,\"WEEKLY\" ,\"WEEK\" ";
			} else if (timePeriodType.equalsIgnoreCase("MONTHLY")) {
				SQL += " ,\"MONTHLY\" ,\"MONTH\" ";
			} else if (timePeriodType.equalsIgnoreCase("HOURLY")) {
				SQL += " ,\"TRANSACTION_DATE\" ";
			} else {
				// System.out.println("DATE Filter!..");

			}

			// ORDER BY
			SQL += "ORDER BY TRN.\"SITE_ID\",PRD.\"PRODUCT_NO\"";
			if (timePeriodType.equalsIgnoreCase("WEEKLY")) {
				SQL += " ,\"WEEKLY\" ,\"WEEK\" ";
			} else if (timePeriodType.equalsIgnoreCase("MONTHLY")) {
				SQL += " ,\"MONTHLY\" ,\"MONTH\" ";
			} else if (timePeriodType.equalsIgnoreCase("HOURLY")) {
				SQL += " ,\"TRANSACTION_DATE\" ";
			} else {
				// System.out.println("DATE Filter!..");

			}

			if (paymentMode)
				SQL += ", TRN.\"MOPD1\",TRN.\"MOPD2\",TRN.\"MOPD3\",TRN.\"MOPD4\",TRN.\"MOPD5\",TRN.\"MOPD6\" ,CUR.\"CURRENCY_CODE\" ";

			// System.out.println(SQL);

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ SalesDAO-getSalesByFilter ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	/**
	 * 
	 * @return
	 */
	public JSONArray getSalesTransactions(String siteIDs, String fromDate, String toDate, String fromTime, 
			String toTime, String productName,
			String country, String state, String region, String district, String subDistrict, String city,
			String division, String pump, String tank, String nozzle, String du, String mode) {
		JSONArray json = new JSONArray();
		try {
			// MST.\"SITE_TYPE\",MST.\"DEALER_NAME\",MST.\"CLIENT_NAME\",
			// MST.\"REGION\",MST.\"COUNTRY\",\"DEC_VAL\"
			// ,MST.\"SITE_STATUS\",MST.\"LAST_CONNECTION_TIME\",
			// MTN.\"PRODUCT_NO\",
			String SQL = "  SELECT ROW_NUMBER () OVER (ORDER BY TRN.\"SITE_ID\") AS \"SNO\"  , TRN.\"SITE_ID\", MST.\"SITE_NAME\","
					+ " TRN.\"TRANSACTION_DATE\",  " + " \"VOLUME\",\"UNIT_PRICE\",\"AMOUNT\",CUR.\"CURRENCY_CODE\",  "
					+ " CASE WHEN \"MOPD6\" = '0' THEN 'CASH' ELSE 'DIGITAL' END AS \"PAYMENT_MODE\" , "
					+ " UPPER(PRD.\"PRODUCT_NAME\") AS \"PRODUCT_NAME\" , "
					+ " TRN.\"PUMP_NO\" , TRN.\"NOZZLE_NO\" , \"START_TOTALIZER\" , \"END_TOTALIZER\" " + " FROM "
					+ schema + ".\"TRANSACTIONS\" TRN  INNER JOIN " + schema
					+ ".\"MS_SITE\" MST ON TRN.\"SITE_ID\"=MST.\"SITE_ID\"  "
					+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" AS CUR ON MST.\"COUNTRY\" = CUR.\"COUNTRY\"   "
					+ " INNER JOIN " + schema
					+ ".\"MS_TANK\" MTN ON TRN.\"TANK_NO\"=MTN.\"TANK_NO\"  AND MTN.\"PRODUCT_NO\"=TRN.\"PRODUCT_NO\" AND MTN.\"SITE_ID\"=TRN.\"SITE_ID\"    "
					+ " INNER JOIN " + schema
					+ ".\"MS_PRODUCTS\" PRD ON MTN.\"PRODUCT_NO\"=PRD.\"PRODUCT_NO\" AND PRD.\"SITE_ID\"=MST.\"SITE_ID\"   "
					+ " WHERE 1=1 AND PRD.\"ADRM_STATUS\" != 'D' AND MTN.\"ADRM_STATUS\" != 'D' ";
			//SQL += " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM " + schema
			//		+ ".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = TRN.\"SITE_ID\" ) ";
			//SQL += " AND MTN.\"MODIFIED_TIME\" = (SELECT MAX(\"MODIFIED_TIME\") FROM "+schema+".\"MS_TANK\" TNK1 WHERE TNK1.\"SITE_ID\" = TRN.\"SITE_ID\"  AND TNK1.\"TANK_NO\"=TRN.\"TANK_NO\" ) ";
			
			if (siteIDs != null) {
				SQL += " AND TRN.\"SITE_ID\" IN (" + siteIDs + ")";
			}

			if (fromTime != null && toTime != null) {
				SQL += " AND TRN.\"TRANSACTION_DATE\" BETWEEN ('" + fromDate +" "+ fromTime + "') AND ('" + toDate
						+" "+ toTime + "')";
			}else if (fromDate != null && toDate != null) {
				SQL += " AND TRN.\"TRANSACTION_DATE\"::timestamp::date BETWEEN ('" + fromDate + "') AND ('" + toDate
						+ "')";
			}

			if (productName != null) {
				SQL += " AND UPPER(PRD.\"PRODUCT_NAME\") = UPPER('" + productName + "') ";
			}

			if (country != null) {
				SQL += " AND MST.\"COUNTRY\" = '" + country + "' ";
			}

			if (state != null) {
				SQL += " AND MST.\"STATE\" = '" + state + "' ";
			}

			if (region != null) {
				SQL += " AND MST.\"REGION\" = '" + region + "' ";
			}

			if (district != null) {
				SQL += " AND MST.\"DISTRICT\" = '" + district + "' ";
			}

			if (subDistrict != null) {
				SQL += " AND MST.\"SUB_DISTRICT\" = '" + subDistrict + "' ";
			}

			if (city != null) {
				SQL += " AND MST.\"CITY\" = '" + city + "' ";
			}

			if (division != null) {
				SQL += " AND MST.\"DIVISION\" = '" + division + "' ";
			}

			if (pump != null) {
				SQL += " AND TRN.\"PUMP_NO\" = " + pump;
			}

			if (tank != null) {
				SQL += " AND TRN.\"TANK_NO\" = " + tank;
			}

			if (nozzle != null) {
				SQL += " AND TRN.\"NOZZLE_NO\" = " + nozzle;
			}

			if (du != null) {
				SQL += " AND DR.\"DU_NO\" = " + du;
			}

			if (mode != null) {
				SQL += " AND TRN.\"MOPD6\" = '" + mode + "'";
			}

			// GROUP BY
			SQL += "  GROUP BY MTN.\"PRODUCT_NO\",TRN.\"SITE_ID\", MST.\"SITE_NAME\",MST.\"SITE_TYPE\",MST.\"DEALER_NAME\",  "
					+ " MST.\"CLIENT_NAME\",  MST.\"REGION\",MST.\"COUNTRY\",MST.\"SITE_STATUS\",MST.\"LAST_CONNECTION_TIME\", \"DEC_VAL\" ,   "
					+ " PRD.\"PRODUCT_NAME\",PRD.\"PRODUCT_NO\" ,\"VOLUME\" ,TRN.\"TRANSACTION_DATE\",\"UNIT_PRICE\",  "
					+ " \"AMOUNT\",CUR.\"CURRENCY_CODE\" ,\"MOPD6\" , "
					+ " TRN.\"PUMP_NO\" , TRN.\"NOZZLE_NO\" , \"START_TOTALIZER\" , \"END_TOTALIZER\" ";

			if (country != null) {
				SQL += " ,MST.\"COUNTRY\" ";
			}

			if (state != null) {
				SQL += " , MST.\"STATE\" ";
			}

			if (region != null) {
				SQL += " , MST.\"REGION\" ";
			}

			if (district != null) {
				SQL += " , MST.\"DISTRICT\" ";
			}

			if (subDistrict != null) {
				SQL += " , MST.\"SUB_DISTRICT\" ";
			}

			if (city != null) {
				SQL += " , MST.\"CITY\" ";
			}

			if (division != null) {
				SQL += " , MST.\"DIVISION\" ";
			}

			if (pump != null) {
				SQL += " , TRN.\"PUMP_NO\" ";
			}

			if (tank != null) {
				SQL += " , TRN.\"TANK_NO\" ";
			}

			if (nozzle != null) {
				SQL += " , TRN.\"NOZZLE_NO\" ";
			}

			if (du != null) {
				SQL += " , DR.\"DU_NO\" ";
			}

			if (mode != null) {
				SQL += " , TRN.\"MOPD6\"  ";
			}

			SQL += " ORDER BY TRN.\"TRANSACTION_DATE\" DESC , TRN.\"SITE_ID\",PRD.\"PRODUCT_NO\" ";

			// System.out.println("getSalesTransactions >>> " + SQL);

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ SalesDAO-getSalesTransactions ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	/**
	 * 
	 * @return
	 */
	public JSONArray getToDaySales(String siteIDs, String fromDate, String toDate) {
		JSONArray json = new JSONArray();
		try {

			String SQL = "  SELECT MST.\"SITE_ID\", MST.\"SITE_NAME\",MST.\"SITE_TYPE\",MST.\"DEALER_NAME\",MST.\"CLIENT_NAME\",  MST.\"REGION\",MST.\"COUNTRY\",\"DEC_VAL\" ,  "
					+ " MST.\"SITE_STATUS\",MST.\"LAST_CONNECTION_TIME\", PRD.\"PRICE\",\"CURRENCY_CODE\",  "
					+ " PRD.\"PRODUCT_NO\",UPPER(PRD.\"PRODUCT_NAME\") AS \"PRODUCT_NAME\",  "
					+ " CASE WHEN TMP.\"VOLUME\" > 0  THEN to_char(ROUND(TMP.\"VOLUME\",\"DEC_VAL\"), '99G99G999D999') ELSE '0' END AS \"VOLUME\",    "
					+ " CASE WHEN TMP.\"VALUE\" > 0 THEN to_char(ROUND((TMP.\"VALUE\"),\"DEC_VAL\"), '99G99G999D999') ELSE '0' END AS \"TOTAL_VALUE\"   "
					+ " FROM  " + schema + ".\"MS_SITE\" MST  " + " INNER JOIN " + schema
					+ ".\"MS_PRODUCTS\" PRD ON PRD.\"SITE_ID\"=MST.\"SITE_ID\"  "
					+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" AS CUR ON MST.\"COUNTRY\" = CUR.\"COUNTRY\"   "
					+ " LEFT OUTER  JOIN   " + " (  " + "	SELECT TRN.\"SITE_ID\",  "
					+ "	(SUM(\"AMOUNT\") )  AS \"VALUE\",  " + "	SUM(\"VOLUME\") / 1000   AS \"VOLUME\",   "
					+ "	TRN.\"PRODUCT_NO\"  " + "	FROM " + schema + ".\"TRANSACTIONS\" TRN  " + "	WHERE 1=1   "
					+ "	AND TRN.\"SITE_ID\" IN (" + siteIDs + ")   "
					+ "	AND TRN.\"TRANSACTION_DATE\"::timestamp::date BETWEEN ('" + fromDate + "') AND ('" + toDate
					+ "')  " + "	GROUP BY TRN.\"SITE_ID\",\"PRODUCT_NO\"  " + " ) TMP  "
					+ " ON TMP.\"SITE_ID\" = MST.\"SITE_ID\"  " + " AND PRD.\"PRODUCT_NO\"=tmp.\"PRODUCT_NO\"  "
					+ " WHERE 1=1  " + " AND MST.\"SITE_ID\" IN (" + siteIDs + ") AND PRD.\"ADRM_STATUS\" != 'D' ";
			//SQL += " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM " + schema
			//		+ ".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) ";

			// System.out.println("getToDaySales===="+SQL);

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ SalesDAO-getToDaySales ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	/**
	 * 
	 * @return
	 */
	public String getToDaySalesTotal(String siteIDs, String fromDate, String toDate) {
		String json = null;
		try {

			String SQL = "  SELECT "
					+ " CASE WHEN TMP.\"VALUE\" > 0 THEN to_char(ROUND((TMP.\"VALUE\"),\"DEC_VAL\"), '99G99G999D999') ELSE '0' END AS \"TOTAL_VALUE\"   "
					+ " FROM  " + schema + ".\"MS_SITE\" MST  "
					+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" AS CUR ON MST.\"COUNTRY\" = CUR.\"COUNTRY\"   "
					+ " LEFT OUTER  JOIN   " + " (  " + "	SELECT TRN.\"SITE_ID\",  "
					+ "	(SUM(\"AMOUNT\") )  AS \"VALUE\",  " + "	SUM(\"VOLUME\") / 1000   AS \"VOLUME\"   "
					+ "	FROM " + schema + ".\"TRANSACTIONS\" TRN  " + "	WHERE 1=1   " + "	AND TRN.\"SITE_ID\" IN ("
					+ siteIDs + ")   " + "	AND TRN.\"TRANSACTION_DATE\"::timestamp::date BETWEEN ('" + fromDate
					+ "') AND ('" + toDate + "')  " + "	GROUP BY TRN.\"SITE_ID\" " + " ) TMP  "
					+ " ON TMP.\"SITE_ID\" = MST.\"SITE_ID\"  " + " WHERE 1=1  " + " AND MST.\"SITE_ID\" IN (" + siteIDs
					+ ") ";
			// System.out.println("getToDaySalesTotal===="+SQL);

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			while (rs.next()) {
				json = rs.getString("TOTAL_VALUE");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ SalesDAO-getToDaySalesTotal ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	/**
	 * 
	 * @return
	 */
	public JSONArray getPaymentMode(String siteIDs, String fromDate, String toDate, boolean grouping,String userId) {
		JSONArray json = new JSONArray();
		try {

			String SQL = null;
			if (!grouping) {
				SQL = "  SELECT TRN.\"SITE_ID\", MST.\"SITE_NAME\",MST.\"SITE_TYPE\",MST.\"DEALER_NAME\",MST.\"CLIENT_NAME\",  "
						+ " MST.\"REGION\",MST.\"COUNTRY\",\"DEC_VAL\" ,MST.\"SITE_STATUS\",MST.\"LAST_CONNECTION_TIME\", "
						+ " ROUND( (SUM(\"AMOUNT\") ) ,\"DEC_VAL\"  ) AS \"TOTAL_VALUE\",CUR.\"CURRENCY_CODE\", "
						+ " TRN.\"MOPD1\",TRN.\"MOPD2\",TRN.\"MOPD3\",TRN.\"MOPD4\",TRN.\"MOPD5\",TRN.\"MOPD6\" "
						+ " FROM " + schema + ".\"TRANSACTIONS\" TRN  " + " INNER JOIN " + schema
						+ ".\"MS_SITE\" MST ON TRN.\"SITE_ID\"=MST.\"SITE_ID\"  "
						+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" AS CUR ON MST.\"COUNTRY\" = CUR.\"COUNTRY\" "
						+ " INNER JOIN " + schema
						+ ".\"MS_TANK\" MTN ON TRN.\"TANK_NO\"=MTN.\"TANK_NO\"  AND MTN.\"PRODUCT_NO\"=TRN.\"PRODUCT_NO\"  AND MTN.\"SITE_ID\"=TRN.\"SITE_ID\" "
						+ " INNER JOIN " + schema
						+ ".\"MS_PRODUCTS\" PRD ON MTN.\"PRODUCT_NO\"=PRD.\"PRODUCT_NO\" AND PRD.\"SITE_ID\"=MST.\"SITE_ID\"   "
						+ " WHERE 1=1 AND TRN.\"SITE_ID\" IN (" + siteIDs + ")  AND PRD.\"ADRM_STATUS\" != 'D' "
						//+ " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM " + schema
						//+ ".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = TRN.\"SITE_ID\" ) "
						+ " AND TRN.\"TRANSACTION_DATE\"::timestamp::date BETWEEN ('" + fromDate + "') AND ('" + toDate
						+ "') "
						+ " AND MTN.\"ADRM_STATUS\" != 'D' "
						//+" AND MTN.\"MODIFIED_TIME\" = (SELECT MAX(\"MODIFIED_TIME\") FROM "+schema+".\"MS_TANK\" TNK1 WHERE TNK1.\"SITE_ID\" = MTN.\"SITE_ID\"  AND TNK1.\"TANK_NO\"=MTN.\"TANK_NO\" ) "
						+ " GROUP BY TRN.\"SITE_ID\", MST.\"SITE_NAME\",MST.\"SITE_TYPE\",MST.\"DEALER_NAME\",MST.\"CLIENT_NAME\",  "
						+ " MST.\"REGION\",MST.\"COUNTRY\",MST.\"SITE_STATUS\",MST.\"LAST_CONNECTION_TIME\", \"DEC_VAL\" ,  CUR.\"CURRENCY_CODE\",  "
						+ " TRN.\"MOPD1\",TRN.\"MOPD2\",TRN.\"MOPD3\",TRN.\"MOPD4\",TRN.\"MOPD5\",TRN.\"MOPD6\" "
						+ " ORDER BY TRN.\"SITE_ID\" ";
			} else {
				SQL = "  SELECT ROUND( (SUM(\"AMOUNT\") ) ,\"DEC_VAL\"  ) AS \"TOTAL_VALUE\",CUR.\"CURRENCY_CODE\", "
						+ " TRN.\"MOPD6\" " + " FROM " + schema + ".\"TRANSACTIONS\" TRN  " + " INNER JOIN " + schema
						+ ".\"MS_SITE\" MST ON TRN.\"SITE_ID\"=MST.\"SITE_ID\"  "
						+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" AS CUR ON MST.\"COUNTRY\" = CUR.\"COUNTRY\" "
						+ " INNER JOIN \"BCT\".user_sites US ON US.site_id = MST.\"SITE_ID\"   "
						+ " WHERE 1=1 AND US.user_id = '" + userId + "'  "
						+ " AND TRN.\"TRANSACTION_DATE\"::timestamp::date BETWEEN ('" + fromDate + "') AND ('" + toDate +"') "
						+ " GROUP BY  \"DEC_VAL\" ,  CUR.\"CURRENCY_CODE\",  " + " TRN.\"MOPD6\" ";
			}

			// System.out.println(SQL);

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ SalesDAO-getPaymentMode ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	/**
	 * 
	 * @return
	 */
	public JSONArray getSalesForChart(String userId, String siteIDs, String country, String fromDate, String toDate,
			String productCode, String timePeriodType) {
		JSONArray json = new JSONArray();
		JSONArray prdjson = new JSONArray();
		JSONObject obj = null;
		try {
			String SQL = null;
			String productName = null;
			int product = 0;
			JSONArray innerjson = null;
			Statement stmt = null;
			ResultSet rs = null;

			SQL = " SELECT " + "PRD.\"PRODUCT_NO\"," + "UPPER(TRIM(PRD.\"PRODUCT_NAME\")) AS \"PRODUCT_NAME\"  FROM " + schema
					+ ".\"MS_PRODUCTS\" PRD " + " INNER JOIN " + schema
					+ ".\"MS_SITE\" S ON S.\"SITE_ID\"=PRD.\"SITE_ID\" " + " INNER JOIN \"BCT\".user_sites US ON US.site_id = PRD.\"SITE_ID\" " + " WHERE 1=1 ";
			SQL += " AND US.user_id = '" + userId + "' ";

			if (country != null)
				SQL += " AND US.country = '" + country + "'";

			SQL += " AND PRD.\"ADRM_STATUS\" != 'D' ";
			//SQL += " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM " + schema
			//		+ ".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) ";

			if (productCode != null) {
				SQL += " AND UPPER(TRIM(PRD.\"PRODUCT_NAME\")) = UPPER('" + productCode + "') ";
			}

			if (siteIDs != null) {
				SQL += " AND US.site_id IN ( " + siteIDs + ")";
			}

			SQL += " GROUP BY " + "PRD.\"PRODUCT_NO\"," + "UPPER(TRIM(PRD.\"PRODUCT_NAME\")) " + " ORDER BY "
					+ "PRD.\"PRODUCT_NO\"," + "UPPER(TRIM(PRD.\"PRODUCT_NAME\")) ";

			Statement prdstm = conn.createStatement();
			ResultSet prdrs = prdstm.executeQuery(SQL);
			prdjson = dbc.parseRS(prdrs);
			json.add(prdjson);

			if (prdjson.size() > 0) {
				for (int i = 0; i < prdjson.size(); i++) {
					SQL = "";
					JSONObject objects = prdjson.getJSONObject(i);

					obj = new JSONObject();
					innerjson = new JSONArray();
					product = objects.getInt("PRODUCT_NO");
					productName = objects.getString("PRODUCT_NAME");

					obj.put("PRODUCT_NO", product);
					obj.put("PRODUCT_NAME", productName);

					// System.out.println("timePeriodType="+timePeriodType);

					if (timePeriodType != null) {
						if (timePeriodType.equalsIgnoreCase("WEEKLY")) {
							SQL = " SELECT x::DATE,COALESCE(TMP.\"WEEKLY\",x) AS \"WEEKLY\" ,COALESCE(TMP.\"DATE\",x::DATE) AS \"DATE\" ,"
									+ "COALESCE(TMP.\"VOLUME\",0) AS \"VOLUME\",COALESCE(TMP.\"VALUE\",0) AS \"VALUE\", "
									// + "TMP.\"CURRENCY_CODE\","
									+ " TMP.\"PRODUCT_NO\" " + " FROM   generate_series(timestamp '" + fromDate + "' "
									+ "                     , timestamp '" + toDate + "' "
									+ "                     , interval  '1 WEEK') t(x) " + "LEFT OUTER  JOIN    ("
									+ "	SELECT DATE_TRUNC('WEEK',TRN.\"TRANSACTION_DATE\"::DATE)::DATE - 1 AS \"WEEKLY\",  DATE_TRUNC('WEEK',TRN.\"TRANSACTION_DATE\"::DATE)::DATE - 1 AS \"DATE\" , ";
						}
						if (timePeriodType.equalsIgnoreCase("MONTHLY")) {
							SQL = " SELECT x::DATE,COALESCE(TMP.\"MONTHLY\",x) AS \"MONTHLY\" ,COALESCE(TMP.\"DATE\",x::DATE) AS \"DATE\" ,COALESCE(TMP.\"VOLUME\",0) AS \"VOLUME\",COALESCE(TMP.\"VALUE\",0) AS \"VALUE\", "
									// + "TMP.\"CURRENCY_CODE\", "
									+ " TMP.\"PRODUCT_NO\" " + " FROM   generate_series(timestamp '" + fromDate + "' "
									+ "                     , timestamp '" + toDate + "' "
									+ "                     , interval  '1 MONTH') t(x) " + "LEFT OUTER  JOIN    ("
									+ " SELECT DATE_TRUNC('MONTH',TRN.\"TRANSACTION_DATE\"::DATE) AS \"MONTHLY\", DATE_TRUNC('MONTH',TRN.\"TRANSACTION_DATE\"::DATE) AS \"DATE\" , ";
						}
						if (timePeriodType.equalsIgnoreCase("HOURLY")) {
							SQL = " SELECT x::time,COALESCE(TMP.\"HOURLY\",x) AS \"HOURLY\" ,COALESCE(TMP.\"DATE\",x::time) AS \"DATE\" ,COALESCE(TMP.\"VOLUME\",0) AS \"VOLUME\",COALESCE(TMP.\"VALUE\",0) AS \"VALUE\", "
									// + "TMP.\"CURRENCY_CODE\", "
									+ " TMP.\"PRODUCT_NO\" " + " FROM   generate_series(timestamp '" + fromDate
									+ " 00:00' " + "                     , timestamp '" + toDate + " 23:00' "
									+ "                     , interval  '1 hour') t(x) " + "LEFT OUTER  JOIN    ("
									+ " SELECT  DATE_TRUNC('HOUR', TRN.\"TRANSACTION_DATE\") AS \"HOURLY\",  CAST( DATE_TRUNC('HOUR', TRN.\"TRANSACTION_DATE\") AS TIME ) AS \"DATE\" , ";
						}
					} else {
						SQL = " SELECT x::DATE,COALESCE(TMP.\"DAILY\",x) AS \"DAILY\" ,COALESCE(TMP.\"DATE\",x::DATE) AS \"DATE\" ,COALESCE(TMP.\"VOLUME\",0) AS \"VOLUME\",COALESCE(TMP.\"VALUE\",0) AS \"VALUE\", "
								// + "TMP.\"CURRENCY_CODE\","
								+ " TMP.\"PRODUCT_NO\" " + " FROM   generate_series(timestamp '" + fromDate + "' "
								+ "                     , timestamp '" + toDate + "' "
								+ "                     , interval  '1 DAY') t(x) " + "LEFT OUTER  JOIN    ("
								+ " SELECT  DATE_TRUNC('DAY', TRN.\"TRANSACTION_DATE\") AS \"DAILY\", DATE_TRUNC('DAY', TRN.\"TRANSACTION_DATE\")::DATE AS \"DATE\" , ";
					}

					SQL += " ROUND(SUM(\"VOLUME\") / " + unitConversion + ",\"DEC_VAL\") AS \"VOLUME\" ,"
							+ " ROUND(SUM(\"AMOUNT\") ,\"DEC_VAL\")  AS \"VALUE\" ,"
							// + " CUR.\"CURRENCY_CODE\" AS \"CURRENCY_CODE\" , "
							+ " MTN.\"PRODUCT_NO\"  " + "  FROM " + schema + ".\"MS_SITE\" MST  "
							+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" AS CUR ON MST.\"COUNTRY\" = CUR.\"COUNTRY\"  "
							+ " INNER JOIN " + schema + ".\"MS_PRODUCTS\" PRD ON PRD.\"SITE_ID\" = MST.\"SITE_ID\" "
							+ " INNER JOIN " + schema
							+ ".\"MS_TANK\" MTN ON MTN.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND MTN.\"SITE_ID\"=MST.\"SITE_ID\"  "
							+ " INNER JOIN " + schema
							+ ".\"TRANSACTIONS\" TRN ON TRN.\"SITE_ID\" = MST.\"SITE_ID\" AND MTN.\"TANK_NO\" = TRN.\"TANK_NO\"  "
							+ " INNER JOIN \"BCT\".user_sites US ON US.site_id = TRN.\"SITE_ID\" "
							+ " WHERE 1=1 "
							// + " AND TRN.\"SITE_ID\" IN (" + siteIDs + ") "
							+ " AND US.user_id = '" + userId + "'" + "AND PRD.\"PRODUCT_NO\"=" + product
							+ " AND UPPER(PRD.\"PRODUCT_NAME\") = UPPER('" + productName + "') ";
					SQL += " AND PRD.\"ADRM_STATUS\" != 'D' AND MTN.\"ADRM_STATUS\" != 'D' ";
					//SQL += " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM " + schema
					//		+ ".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = TRN.\"SITE_ID\" ) ";
					//SQL += " AND MTN.\"MODIFIED_TIME\" = (SELECT MAX(\"MODIFIED_TIME\") FROM "+schema+".\"MS_TANK\" TNK1 WHERE TNK1.\"SITE_ID\" = MTN.\"SITE_ID\"  AND TNK1.\"TANK_NO\"=MTN.\"TANK_NO\" ) ";

					if (siteIDs != null) {
						SQL += " AND US.site_id IN ( " + siteIDs + ")";
					}

					if (country != null) {
						SQL += " AND US.country = '" + country + "'";
					}

					if (fromDate != null && toDate != null) {
						SQL += " AND TRN.\"TRANSACTION_DATE\"::timestamp::date BETWEEN ('" + fromDate + "') AND ('"
								+ toDate + "')";
					}

					if (timePeriodType != null) {
						if (timePeriodType.equalsIgnoreCase("WEEKLY")) {
							SQL += "	GROUP BY \"WEEKLY\" , \"DATE\" , \"DEC_VAL\" , MTN.\"PRODUCT_NO\" "
									// + ", CUR.\"CURRENCY_CODE\" "
									+ " ORDER BY \"WEEKLY\"   " + " ) TMP " + " ON TMP.\"WEEKLY\" = t.x";
						}
						if (timePeriodType.equalsIgnoreCase("MONTHLY")) {
							SQL += "  GROUP BY  \"MONTHLY\" , \"DATE\", \"DEC_VAL\" , MTN.\"PRODUCT_NO\" "
									// + ", CUR.\"CURRENCY_CODE\" "
									+ " ORDER BY \"MONTHLY\" " + " ) TMP " + " ON TMP.\"MONTHLY\" = t.x";
						}
						if (timePeriodType.equalsIgnoreCase("HOURLY")) {
							SQL += " GROUP BY  \"HOURLY\" , \"DATE\" , \"DEC_VAL\" , MTN.\"PRODUCT_NO\" "
									// + " , CUR.\"CURRENCY_CODE\" "
									+ " ORDER BY \"HOURLY\" " + " ) TMP " + " ON TMP.\"HOURLY\" = t.x";
						}
					} else {
						SQL += " GROUP BY  \"DAILY\" , \"DATE\" , \"DEC_VAL\" , MTN.\"PRODUCT_NO\" "
								// + ", CUR.\"CURRENCY_CODE\" "
								+ "  ORDER BY \"DAILY\" " + " ) TMP " + " ON TMP.\"DAILY\" = t.x";
					}
					System.out.println("\n\n\n\n");
					System.out.println("getSalesForChart>>>>>>>>>>>>>>>>>>"+SQL);
					System.out.println("\n\n\n\n");
					stmt = conn.createStatement();
					rs = stmt.executeQuery(SQL);
					
					innerjson = dbc.parseRS(rs);

					obj.put("SALES_DATA", innerjson);
					json.add(obj);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ SalesDAO-getSalesForChart ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	/**
	 * 
	 * @return
	 */
	public JSONArray getSalesForChartGrouping(String userId, String siteIDs, String country, String fromDate,
			String toDate, String productCode, String timePeriodType,String roleId) {
		JSONArray json = new JSONArray();
		JSONArray prdjson = new JSONArray();
		JSONObject obj = null;
		try {
			String SQL = null;
			String productName = null;
			JSONArray innerjson = null;
			Statement stmt = null;
			ResultSet rs = null;

			SQL = " SELECT UPPER(\"PRODUCT_GROUP\") AS \"PRODUCT_NAME\"  FROM " + schema + ".\"MS_PRODUCTS\" PRD "
					+ " INNER JOIN " + schema + ".\"MS_SITE\" S ON S.\"SITE_ID\"=PRD.\"SITE_ID\" " 
					+ " INNER JOIN \"BCT\".user_sites US ON US.site_id = PRD.\"SITE_ID\" "
					+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" CNT ON CNT.\"COUNTRY\" = S.\"COUNTRY\" "
					+ " INNER JOIN \"BCT\".\"COUNTRY_PRODUCT_CONFIG\" CPC ON CPC.\"COUNTRY_ID\" = CNT.\"COUNTRY_ID\" "
					+ " WHERE 1=1 ";
			SQL += " AND US.user_id = '" + userId + "' ";

			if (country != null)
				SQL += " AND US.country = '" + country + "'";

			SQL += " AND PRD.\"ADRM_STATUS\" != 'D' ";
			//SQL += " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM " + schema
			//		+ ".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) ";

			if (productCode != null) {
				SQL += " AND UPPER(PRD.\"PRODUCT_NAME\") = UPPER('" + productCode + "') ";
			}

			if (siteIDs != null) {
				SQL += " AND US.site_id IN ( " + siteIDs + ")";
			}

			SQL += " GROUP BY UPPER(\"PRODUCT_GROUP\") " + " ORDER BY UPPER(\"PRODUCT_GROUP\") ";

			Statement prdstm = conn.createStatement();
			ResultSet prdrs = prdstm.executeQuery(SQL);
			//System.out.println("getSalesForChartGrouping(PRD)="+SQL);
			
			prdjson = dbc.parseRS(prdrs);
			json.add(prdjson);

			if (prdjson.size() > 0) {
				for (int i = 0; i < prdjson.size(); i++) {
					SQL = "";
					JSONObject objects = prdjson.getJSONObject(i);

					obj = new JSONObject();
					innerjson = new JSONArray();
					productName = objects.getString("PRODUCT_NAME");

					obj.put("PRODUCT_NAME", productName);

					// System.out.println("timePeriodType="+timePeriodType);
					
					if(roleId.equalsIgnoreCase("CXO") || roleId.equalsIgnoreCase("HO") && timePeriodType == null) {
						SQL = " SELECT x::DATE,COALESCE(TMP.\"DAILY\",x) AS \"DAILY\" ,COALESCE(TMP.\"DATE\",x::DATE) AS \"DATE\" " + 
								" ,COALESCE(TMP.\"VOLUME\",0) AS \"VOLUME\",COALESCE(TMP.\"VALUE\",0) AS \"VALUE\"  " + 
								" FROM   generate_series(timestamp '"+fromDate+"'  " + 
								"				, timestamp '"+toDate+"'   " + 
								"				, interval  '1 DAY') t(x)    " + 
								" LEFT OUTER  JOIN    (  " + 
								" SELECT  DATE_TRUNC('DAY', TRN.\"DATE\") AS \"DAILY\", DATE_TRUNC('DAY', TRN.\"DATE\")::DATE AS \"DATE\" ,  " + 
								" ROUND(SUM(\"TOTAL_VOLUME\")/1000,\"DEC_VAL\") AS \"VOLUME\" ,  " + 
								" ROUND(SUM(\"TOTAL_AMOUNT\"),\"DEC_VAL\")  AS \"VALUE\"   " + 
								" FROM "+schema+".\"SALES_MONTH_REPORTING\" TRN    " + 
								" INNER JOIN "+schema+".\"MS_SITE\" MS  ON MS.\"SITE_ID\" = TRN.\"SITE_ID\"    " + 
								" INNER JOIN \"BCT\".user_sites US ON US.site_id = MS.\"SITE_ID\"    " + 
								" INNER JOIN \"BCT\".\"MS_COUNTRY\" AS CUR ON CUR.\"COUNTRY\" = MS.\"COUNTRY\"    " + 
								" INNER JOIN "+schema+".\"MS_PRODUCTS\" PRD ON PRD.\"SITE_ID\" = TRN.\"SITE_ID\" AND PRD.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\"    " + 
								" INNER JOIN \"BCT\".\"COUNTRY_PRODUCT_CONFIG\" CPC ON CPC.\"PRODUCT\" = PRD.\"PRODUCT_NAME\"   " + 
								" WHERE 1=1 AND US.user_id = '"+userId+"'  " + 
								" AND UPPER(CPC.\"PRODUCT_GROUP\") = UPPER ('"+productName+"')  " + 
								" AND PRD.\"ADRM_STATUS\" != 'D' " +
								//" AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1    " + 
								//"						WHERE PRD1.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = TRN.\"SITE_ID\" )   " + 
								" AND TRN.\"DATE\"::timestamp::date BETWEEN ('"+fromDate+"') AND ('"+toDate+"')  " + 
								" GROUP BY  \"DAILY\" , \"DATE\" , \"DEC_VAL\"  " + 
								" ORDER BY \"DAILY\"  ) TMP ON TMP.\"DAILY\" = t.x";
					}else {
						if (timePeriodType != null) {
							if (timePeriodType.equalsIgnoreCase("WEEKLY")) {
								SQL = " SELECT x::DATE,COALESCE(TMP.\"WEEKLY\",x) AS \"WEEKLY\" ,COALESCE(TMP.\"DATE\",x::DATE) AS \"DATE\" ,"
										+ "COALESCE(TMP.\"VOLUME\",0) AS \"VOLUME\",COALESCE(TMP.\"VALUE\",0) AS \"VALUE\" "
										// + "TMP.\"CURRENCY_CODE\","
										//+ " TMP.\"PRODUCT_NO\" " 
										+ " FROM   generate_series(timestamp '" + fromDate + "' "
										+ "                     , timestamp '" + toDate + "' "
										+ "                     , interval  '1 WEEK') t(x) " + "LEFT OUTER  JOIN    ("
										+ "	SELECT DATE_TRUNC('WEEK',TRN.\"TRANSACTION_DATE\"::DATE)::DATE - 1 AS \"WEEKLY\",  DATE_TRUNC('WEEK',TRN.\"TRANSACTION_DATE\"::DATE)::DATE - 1 AS \"DATE\" , ";
							}
							if (timePeriodType.equalsIgnoreCase("MONTHLY")) {
								SQL = " SELECT x::DATE,COALESCE(TMP.\"MONTHLY\",x) AS \"MONTHLY\" ,COALESCE(TMP.\"DATE\",x::DATE) AS \"DATE\" ,COALESCE(TMP.\"VOLUME\",0) AS \"VOLUME\",COALESCE(TMP.\"VALUE\",0) AS \"VALUE\" "
										// + "TMP.\"CURRENCY_CODE\", "
										//+ " TMP.\"PRODUCT_NO\" " 
										+ " FROM   generate_series(timestamp '" + fromDate + "' "
										+ "                     , timestamp '" + toDate + "' "
										+ "                     , interval  '1 MONTH') t(x) " + "LEFT OUTER  JOIN    ("
										+ " SELECT DATE_TRUNC('MONTH',TRN.\"TRANSACTION_DATE\"::DATE) AS \"MONTHLY\", DATE_TRUNC('MONTH',TRN.\"TRANSACTION_DATE\"::DATE) AS \"DATE\" , ";
							}
							if (timePeriodType.equalsIgnoreCase("HOURLY")) {
								SQL = " SELECT x::time,COALESCE(TMP.\"HOURLY\",x) AS \"HOURLY\" ,COALESCE(TMP.\"DATE\",x::time) AS \"DATE\" ,COALESCE(TMP.\"VOLUME\",0) AS \"VOLUME\",COALESCE(TMP.\"VALUE\",0) AS \"VALUE\"  "
										// + "TMP.\"CURRENCY_CODE\", "
										//+ " TMP.\"PRODUCT_NO\" " 
										+ " FROM   generate_series(timestamp '" + fromDate
										+ " 00:00' " + "                     , timestamp '" + toDate + " 23:00' "
										+ "                     , interval  '1 hour') t(x) " + "LEFT OUTER  JOIN    ("
										+ " SELECT  DATE_TRUNC('HOUR', TRN.\"TRANSACTION_DATE\") AS \"HOURLY\",  CAST( DATE_TRUNC('HOUR', TRN.\"TRANSACTION_DATE\") AS TIME ) AS \"DATE\" , ";
							}
						} else {
							SQL = " SELECT x::DATE,COALESCE(TMP.\"DAILY\",x) AS \"DAILY\" ,COALESCE(TMP.\"DATE\",x::DATE) AS \"DATE\" ,COALESCE(TMP.\"VOLUME\",0) AS \"VOLUME\",COALESCE(TMP.\"VALUE\",0) AS \"VALUE\"  "
									// + "TMP.\"CURRENCY_CODE\","
									//+ " TMP.\"PRODUCT_NO\" " 
									+ " FROM   generate_series(timestamp '" + fromDate + "' "
									+ "                     , timestamp '" + toDate + "' "
									+ "                     , interval  '1 DAY') t(x) " + "LEFT OUTER  JOIN    ("
									+ " SELECT  DATE_TRUNC('DAY', TRN.\"TRANSACTION_DATE\") AS \"DAILY\", DATE_TRUNC('DAY', TRN.\"TRANSACTION_DATE\")::DATE AS \"DATE\" , ";
						}
	
						SQL += " ROUND(SUM(\"VOLUME\") / " + unitConversion + ",\"DEC_VAL\") AS \"VOLUME\" ,"
								+ " ROUND(SUM(\"AMOUNT\") ,\"DEC_VAL\")  AS \"VALUE\" "
								+ "  FROM "+schema+".\"TRANSACTIONS\" TRN " + 
								"	INNER JOIN "+schema+".\"MS_SITE\" MS  ON MS.\"SITE_ID\" = TRN.\"SITE_ID\" " + 
								"	INNER JOIN \"BCT\".user_sites US ON US.site_id = MS.\"SITE_ID\" " + 
								"	INNER JOIN \"BCT\".\"MS_COUNTRY\" AS CUR ON CUR.\"COUNTRY\" = MS.\"COUNTRY\" " + 
								"	INNER JOIN "+schema+".\"MS_PRODUCTS\" PRD ON PRD.\"SITE_ID\" = TRN.\"SITE_ID\" AND PRD.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\" " + 
								"	INNER JOIN \"BCT\".\"COUNTRY_PRODUCT_CONFIG\" CPC ON CPC.\"PRODUCT\" = PRD.\"PRODUCT_NAME\" "
								+ " WHERE 1=1 " + " AND US.user_id = '" + userId + "'"
								+ " AND UPPER(CPC.\"PRODUCT_GROUP\") = UPPER ('"+productName+"')"
								+ " AND PRD.\"ADRM_STATUS\" != 'D' ";
								//+ " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 " + 
								//"								WHERE PRD1.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = TRN.\"SITE_ID\" ) ";
	
						if (siteIDs != null) {
							SQL += " AND US.site_id IN ( " + siteIDs + ")";
						}
	
						if (country != null) {
							SQL += " AND US.country = '" + country + "'";
						}
	
						if (fromDate != null && toDate != null) {
							SQL += " AND TRN.\"TRANSACTION_DATE\"::timestamp::date BETWEEN ('" + fromDate + "') AND ('"
									+ toDate + "')";
						}
	
						if (timePeriodType != null) {
							if (timePeriodType.equalsIgnoreCase("WEEKLY")) {
								SQL += "	GROUP BY \"WEEKLY\" , \"DATE\" , \"DEC_VAL\" "
										// + ", CUR.\"CURRENCY_CODE\" "
										+ " ORDER BY \"WEEKLY\"   " + " ) TMP " + " ON TMP.\"WEEKLY\" = t.x";
							}
							if (timePeriodType.equalsIgnoreCase("MONTHLY")) {
								SQL += "  GROUP BY  \"MONTHLY\" , \"DATE\", \"DEC_VAL\" "
										// + ", CUR.\"CURRENCY_CODE\" "
										+ " ORDER BY \"MONTHLY\" " + " ) TMP " + " ON TMP.\"MONTHLY\" = t.x";
							}
							if (timePeriodType.equalsIgnoreCase("HOURLY")) {
								SQL += " GROUP BY  \"HOURLY\" , \"DATE\" , \"DEC_VAL\" "
										// + " , CUR.\"CURRENCY_CODE\" "
										+ " ORDER BY \"HOURLY\" " + " ) TMP " + " ON TMP.\"HOURLY\" = t.x";
							}
						} else {
							SQL += " GROUP BY  \"DAILY\" , \"DATE\" , \"DEC_VAL\" "
									// + ", CUR.\"CURRENCY_CODE\" "
									+ "  ORDER BY \"DAILY\" " + " ) TMP " + " ON TMP.\"DAILY\" = t.x";
						}
					}
					// System.out.println("\n\n\n\n");
					//System.out.println("getSalesForChartGrouping>>>>>>>>>>>>>>>>>>"+SQL);
					// System.out.println("\n\n\n\n");
					stmt = conn.createStatement();

					// Instant start = Instant.now();
					rs = stmt.executeQuery(SQL);
					innerjson = dbc.parseRS(rs);

					obj.put("SALES_DATA", innerjson);
					json.add(obj);
				}
				
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ SalesDAO-getSalesForChartGrouping ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	/**
	 * 
	 * @return
	 */
	public JSONArray getLastFewSalesChart(String siteIDs, String dayDiff, String productName) {
		JSONArray json = new JSONArray();
		JSONArray prdjson = new JSONArray();
		JSONArray prcjson = new JSONArray();
		JSONObject obj = new JSONObject();
		try {
			String SQL = null;
			SQL = " SELECT UPPER(PRD.\"PRODUCT_NAME\") AS \"PRODUCT_NAME\", DATE_TRUNC('DAY',TRN.\"TRANSACTION_DATE\")::date AS \"SALES_DATE\",  "
					+ " ROUND(SUM(\"VOLUME\") / " + unitConversion + ",\"DEC_VAL\") AS \"VOLUME\" " + " FROM " + schema
					+ ".\"TRANSACTIONS\" TRN " + " INNER JOIN " + schema
					+ ".\"MS_SITE\" MST ON TRN.\"SITE_ID\"=MST.\"SITE_ID\" "
					+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" AS CUR ON MST.\"COUNTRY\" = CUR.\"COUNTRY\" " + " INNER JOIN "
					+ schema
					+ ".\"MS_TANK\" MTN ON TRN.\"TANK_NO\"=MTN.\"TANK_NO\" AND MTN.\"PRODUCT_NO\"=TRN.\"PRODUCT_NO\" AND MTN.\"SITE_ID\"=TRN.\"SITE_ID\" "
					+ " INNER JOIN " + schema + ".\"MS_PRODUCTS\" PRD ON MTN.\"PRODUCT_NO\"=PRD.\"PRODUCT_NO\" "
					+ " AND PRD.\"SITE_ID\"=MST.\"SITE_ID\" " + " WHERE 1=1 ";
			SQL += " AND PRD.\"ADRM_STATUS\" != 'D' AND MTN.\"ADRM_STATUS\" != 'D' ";
			//SQL += " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM " + schema
			//		+ ".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) ";
			//SQL += " AND MTN.\"MODIFIED_TIME\" = (SELECT MAX(\"MODIFIED_TIME\") FROM "+schema+".\"MS_TANK\" TNK1 WHERE TNK1.\"SITE_ID\" = MTN.\"SITE_ID\"  AND TNK1.\"TANK_NO\"=MTN.\"TANK_NO\" ) ";

			if (siteIDs != null) {
				SQL += " AND TRN.\"SITE_ID\" IN ( " + siteIDs + ")";
			}

			if (productName != null) {
				SQL += " AND UPPER(PRD.\"PRODUCT_NAME\") = UPPER('" + productName + "')";
			}

			SQL += " AND TRN.\"TRANSACTION_DATE\"::timestamp::date BETWEEN (CURRENT_DATE-" + dayDiff
					+ ") AND (CURRENT_DATE) ";
			SQL += " GROUP BY \"SALES_DATE\" ,\"DEC_VAL\" , PRD.\"PRODUCT_NAME\" " + " ORDER BY \"SALES_DATE\"";

			//System.out.println(SQL);

			Statement prdstm = conn.createStatement();
			ResultSet prdrs = prdstm.executeQuery(SQL);
			prdjson = dbc.parseRS(prdrs);
			obj.put("SALES_RECORD", prdjson);

			prdstm = null;
			prdrs = null;
			SQL = " SELECT UPPER(PRD.\"PRODUCT_NAME\") AS \"PRODUCT_NAME\",PRC.\"PRICE_ID\",PRC.\"NEW_PRICE\",PRC.\"PRICE_TYPE\",PRC.\"RECORD_APPLY_TIME\"  "
					+ " FROM " + schema + " .\"MS_PRODUCTS\" PRD " + " INNER JOIN " + schema
					+ ".\"MS_SITE\" S ON S.\"SITE_ID\"=PRD.\"SITE_ID\" " + " INNER JOIN " + schema
					+ ".\"MS_PRICE_CHANGE\" PRC ON S.\"SITE_ID\"=PRC.\"SITE_ID\" AND PRC.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" "
					+ " WHERE 1=1 ";
			SQL += " AND PRD.\"ADRM_STATUS\" != 'D' ";
			//SQL += " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM " + schema
			//		+ ".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) ";

			if (productName != null) {
				SQL += " AND UPPER(PRD.\"PRODUCT_NAME\") = UPPER('" + productName + "') ";
			}

			SQL += " AND DATE(PRC.\"RECORD_APPLY_TIME\")::timestamp::date BETWEEN (CURRENT_DATE-" + dayDiff
					+ ") AND (CURRENT_DATE)  "
					+ " GROUP BY PRD.\"PRODUCT_NAME\",PRC.\"PRICE_ID\",PRC.\"NEW_PRICE\",PRC.\"PRICE_TYPE\",PRC.\"RECORD_APPLY_TIME\",PRC.\"PRODUCT_NO\"  "
					+ " ORDER BY PRD.\"PRODUCT_NAME\",PRC.\"PRICE_ID\",PRC.\"NEW_PRICE\",PRC.\"PRICE_TYPE\",PRC.\"RECORD_APPLY_TIME\"";

			//System.out.println(SQL);
			prdstm = conn.createStatement();
			prdrs = prdstm.executeQuery(SQL);
			prcjson = dbc.parseRS(prdrs);
			obj.put("PRICE_RECORD", prcjson);

			json.add(obj);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ SalesDAO-getLastFewSalesChart ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	/**
	 * 
	 * @return
	 */
	public JSONArray getSiteStockDetails(String siteID) {
		JSONArray json = new JSONArray();
		try {
			String SQL = null;
			String productNo = null;
			String productName = null;
			String productUnit = null;
			String productType = null;
			JSONObject jObj = null;
			HOSConfig conf = new HOSConfig();

			SQL = "  SELECT \"PRODUCT_NO\",UPPER(\"PRODUCT_NAME\") AS \"PRODUCT_NAME\",\"UNIT\",\"TYPE\" FROM " + schema
					+ ".\"MS_PRODUCTS\" PRD " + " WHERE PRD.\"SITE_ID\" = " + siteID + " ";
			SQL = " AND PRD.\"ADRM_STATUS\" != 'D' ";
			//SQL += " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM " + schema
			//		+ ".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) ";

			// System.out.println(SQL);

			Statement prdstm = conn.createStatement();
			ResultSet prdrs = prdstm.executeQuery(SQL);

			SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date cdate = new Date();
			String now = formatter1.format(cdate);

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date();
			String dt = formatter.format(date);
			String ydt = formatter.format(yesterday());

			String odt = ydt + " " + conf.getValue("DAILY_STOCK_PERIOD_START_STIME");
			String odtEnd = dt + " " + conf.getValue("DAILY_STOCK_PERIOD_START_ETIME");

			String cdt = ydt + " " + conf.getValue("DAILY_STOCK_PERIOD_END_STIME");
			String cdtEnd = dt + " " + conf.getValue("DAILY_STOCK_PERIOD_END_ETIME");

			while (prdrs.next()) {
				jObj = new JSONObject();

				productNo = prdrs.getString("PRODUCT_NO");
				productName = prdrs.getString("PRODUCT_NAME");
				productUnit = prdrs.getString("UNIT");
				productType = prdrs.getString("TYPE");

				jObj.put("PRODUCT_NO", productNo);
				jObj.put("PRODUCT_NAME", productName);
				jObj.put("UNIT", productUnit);
				jObj.put("TYPE", productType);

				// OPENING STOCK
				SQL = " SELECT \"VOLUME\",\"DENSITY_AT15DEG\" " + " FROM " + schema + ".\"INVENTORY\" INV "
						+ " WHERE INV.\"INVENTORY_DATE\" = (SELECT TMP.\"INVENTORY_DATE\" FROM " + schema
						+ ".\"INVENTORY\" TMP  "
						+ "							 	WHERE TMP.\"INVENTORY_DATE\" BETWEEN ('" + odt + "')  "
						+ " AND ('" + odtEnd + "') "
						+ "							    ORDER BY TMP.\"INVENTORY_DATE\" "
						+ "							  	LIMIT 1 " + "							  ) "
						+ " AND INV.\"SITE_ID\" = " + siteID + " "
						+ " AND INV.\"TANK_NO\" = (SELECT \"T1\".\"TANK_NO\" FROM " + schema
						+ ".\"MS_TANK\" \"T1\" WHERE \"T1\".\"TANK_NO\" = INV.\"TANK_NO\" "
						+ "					AND \"T1\".\"SITE_ID\" = INV.\"SITE_ID\" AND \"T1\".\"PRODUCT_NO\"= "
						+ productNo + " ) " + " ORDER BY INV.\"INVENTORY_DATE\" ";
				jObj.put("OPENING_STOCK", getData(SQL, conn));

				// CLOSING STOCK
				SQL = " SELECT \"VOLUME\",\"DENSITY_AT15DEG\" " + " FROM " + schema + ".\"INVENTORY\" INV  "
						+ " WHERE INV.\"INVENTORY_DATE\" = (SELECT TMP.\"INVENTORY_DATE\" FROM " + schema
						+ ".\"INVENTORY\" TMP " + "							 	WHERE TMP.\"INVENTORY_DATE\" BETWEEN ('"
						+ cdt + "')  AND ('" + cdtEnd + "') "
						+ "							    ORDER BY TMP.\"INVENTORY_DATE\" DESC "
						+ "							  	LIMIT 1 " + "							  ) "
						+ " AND INV.\"SITE_ID\" IN (" + siteID + ") "
						+ " AND INV.\"TANK_NO\" = (SELECT \"T1\".\"TANK_NO\" FROM " + schema
						+ ".\"MS_TANK\" \"T1\" WHERE \"T1\".\"TANK_NO\" = INV.\"TANK_NO\" "
						+ "					AND \"T1\".\"SITE_ID\" = INV.\"SITE_ID\" AND \"T1\".\"PRODUCT_NO\"= "
						+ productNo + " ) " + " ORDER BY INV.\"INVENTORY_DATE\" DESC ";
				jObj.put("CLOSING_STOCK", getData(SQL, conn));

				// PRIMARY RECEIPT
				SQL = " SELECT SUM(\"NET_VOLUME\") AS \"VOLUME\" " + " FROM " + schema + ".\"MS_DELIVERY_DATA\" DLV "
						+ " WHERE DLV.\"END_TIME\" BETWEEN ('" + odt + "')  AND ('" + now + "') "
						+ " AND \"SITE_ID\" = " + siteID + " " + " AND \"PRODUCT_NO\" =  " + productNo
						+ " GROUP BY \"PRODUCT_NO\" ";
				jObj.put("PRIMARY_RECEIPT", getData(SQL, conn));

				// SECONDARY SALES
				SQL = " SELECT SUM(\"VOLUME\") AS \"VOLUME\"  " + " FROM " + schema + ".\"TRANSACTIONS\" "
						+ " WHERE 1=1  " + " AND \"SITE_ID\" = " + siteID + " " + " AND \"PRODUCT_NO\" = '" + productNo
						+ "' " + " AND \"TRANSACTION_DATE\" BETWEEN ('" + odt + "') AND ('" + now + "') "
						+ " GROUP BY \"SITE_ID\" , \"PRODUCT_NO\" ";
				jObj.put("SECONDARY_SALES", getData(SQL, conn));

				json.add(jObj);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ SalesDAO-getSiteStockDetails ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	/**
	 * 
	 * @return
	 */
	public JSONArray getSitesStockDetails(String siteIDs, String filterDate, String fromDate, String toDate) {
		JSONArray json = new JSONArray();
		try {
			String SQL = null;
			String productNo = null;
			String productName = null;
			String productUnit = null;
			String productType = null;
			String siteName = null;
			JSONObject jObj = null;
			HOSConfig conf = new HOSConfig();

			if (fromDate == null && toDate == null) {
				fromDate = filterDate;
				toDate = filterDate;
			}

			String stockStartST = fromDate + " " + conf.getValue("DAILY_STOCK_PERIOD_START_STIME");
			String stockStartET = fromDate + " " + conf.getValue("DAILY_STOCK_PERIOD_START_ETIME");

			String stockCloseST = toDate + " " + conf.getValue("DAILY_STOCK_PERIOD_END_STIME");
			String stockCloseET = toDate + " " + conf.getValue("DAILY_STOCK_PERIOD_END_ETIME");

			// stockStartST=convertDateFormat(stockStartST);
			// stockStartET=convertDateFormat(stockStartET);
			// stockCloseST=convertDateFormat(stockCloseST);
			// stockCloseET=convertDateFormat(stockCloseET);

			SQL = "  SELECT PRD.\"SITE_ID\", \"SITE_NAME\", \"PRODUCT_NO\",UPPER(\"PRODUCT_NAME\") AS \"PRODUCT_NAME\",\"UNIT\",\"TYPE\" FROM "
					+ schema + ".\"MS_PRODUCTS\" PRD " + " INNER JOIN " + schema
					+ ".\"MS_SITE\" MS ON MS.\"SITE_ID\" = PRD.\"SITE_ID\" " + " WHERE PRD.\"SITE_ID\" IN( " + siteIDs
					+ " ) ";
			//SQL += " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM " + schema
			//		+ ".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) "
			SQL += " AND PRD.\"ADRM_STATUS\" != 'D' "
					+ " GROUP BY PRD.\"SITE_ID\", \"SITE_NAME\", \"PRODUCT_NO\",\"PRODUCT_NAME\",\"UNIT\",\"TYPE\" ";

			// System.out.println(SQL);

			Statement prdstm = conn.createStatement();
			ResultSet prdrs = prdstm.executeQuery(SQL);
			String site = null;
			while (prdrs.next()) {
				jObj = new JSONObject();

				productNo = prdrs.getString("PRODUCT_NO");
				productName = prdrs.getString("PRODUCT_NAME");
				productUnit = prdrs.getString("UNIT");
				productType = prdrs.getString("TYPE");
				site = prdrs.getString("SITE_ID");
				siteName = prdrs.getString("SITE_NAME");

				jObj.put("SITE_ID", site);
				jObj.put("SITE_NAME", siteName);
				jObj.put("PRODUCT_NO", productNo);
				jObj.put("PRODUCT_NAME", productName);
				jObj.put("UNIT", productUnit);
				jObj.put("TYPE", productType);

				// OPENING STOCK
				SQL = " SELECT SUM(\"VOLUME\") AS \"VOLUME\" " + "FROM " + schema + ".\"INVENTORY\" INV "
						+ "INNER JOIN " + schema
						+ ".\"MS_TANK\" TNK ON TNK.\"TANK_NO\" = INV.\"TANK_NO\" AND TNK.\"SITE_ID\" = INV.\"SITE_ID\" "
						+ "INNER JOIN " + schema
						+ ".\"MS_PRODUCTS\" PRD ON PRD.\"PRODUCT_NO\" = TNK.\"PRODUCT_NO\" AND TNK.\"SITE_ID\" = PRD.\"SITE_ID\" "
						+ "WHERE 1 = 1 " + "AND INV.\"SITE_ID\" IN('" + site + "')   "
						+ "AND INV.\"INVENTORY_DATE\" = (SELECT MIN(TMP.\"INVENTORY_DATE\") AS \"INVENTORY_DATE\"  "
						+ "							  FROM " + schema + ".\"INVENTORY\" TMP "
						+ "							  WHERE TMP.\"INVENTORY_DATE\"::DATE BETWEEN ('" + fromDate
						+ "')   AND ('" + fromDate + "') " + "AND TMP.\"SITE_ID\" IN('" + site + "') "
						+ " 						  GROUP BY TMP.\"INVENTORY_DATE\" "
						+ "							  ORDER BY TMP.\"INVENTORY_DATE\" ASC "
						+ "							  LIMIT 1 " + "							 )   "
						+ " AND PRD.\"ADRM_STATUS\" != 'D' "
						//+ " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM " + schema
						//+ ".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\"  ) "
						+ "AND UPPER(PRD.\"PRODUCT_NAME\") = UPPER('" + productName + "') "
						+ "GROUP BY PRD.\"PRODUCT_NAME\" " + "ORDER BY PRD.\"PRODUCT_NAME\" ";
				jObj.put("OPENING_STOCK", getDataAsString(SQL, conn));

				// CLOSING STOCK
				SQL = " SELECT SUM(\"VOLUME\") AS \"VOLUME\" " + "FROM " + schema + ".\"INVENTORY\" INV "
						+ "INNER JOIN " + schema
						+ ".\"MS_TANK\" TNK ON TNK.\"TANK_NO\" = INV.\"TANK_NO\" AND TNK.\"SITE_ID\" = INV.\"SITE_ID\" "
						+ "INNER JOIN " + schema
						+ ".\"MS_PRODUCTS\" PRD ON PRD.\"PRODUCT_NO\" = TNK.\"PRODUCT_NO\" AND TNK.\"SITE_ID\" = PRD.\"SITE_ID\" "
						+ "WHERE 1 = 1 " + "AND INV.\"SITE_ID\" IN('" + site + "')   "
						+ "AND INV.\"INVENTORY_DATE\" = (SELECT MAX(TMP.\"INVENTORY_DATE\") AS \"INVENTORY_DATE\"  "
						+ "							  FROM " + schema + ".\"INVENTORY\" TMP "
						+ "							  WHERE TMP.\"INVENTORY_DATE\"::DATE BETWEEN ('" + toDate
						+ "')   AND ('" + toDate + "') " + "AND TMP.\"SITE_ID\" IN('" + site + "') "
						+ " 						  GROUP BY TMP.\"INVENTORY_DATE\" "
						+ "							  ORDER BY TMP.\"INVENTORY_DATE\" DESC "
						+ "							  LIMIT 1 " + "							 )   "
						+ " AND PRD.\"ADRM_STATUS\" != 'D' "
						//+ " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM " + schema
						//+ ".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\"  ) "
						+ "AND UPPER(PRD.\"PRODUCT_NAME\") = UPPER('" + productName + "') "
						+ "GROUP BY PRD.\"PRODUCT_NAME\" " + "ORDER BY PRD.\"PRODUCT_NAME\" ";
				jObj.put("CLOSING_STOCK", getDataAsString(SQL, conn));

				// PRIMARY RECEIPT
				SQL = " SELECT SUM(\"NET_VOLUME\") AS \"VOLUME\" " + " FROM " + schema + ".\"MS_DELIVERY_DATA\" DLV "
						+ " WHERE DLV.\"END_TIME\"::DATE BETWEEN ('" + fromDate + "')  AND ('" + toDate + "') "
						+ " AND \"SITE_ID\" IN( '" + site + "') " + " AND \"PRODUCT_NO\" =  " + productNo
						+ " GROUP BY \"PRODUCT_NO\" ";
				jObj.put("PRIMARY_RECEIPT", getDataAsString(SQL, conn));

				// SECONDARY SALES
				SQL = " SELECT SUM(\"VOLUME\") AS \"VOLUME\"  " + " FROM " + schema + ".\"TRANSACTIONS\" "
						+ " WHERE 1=1  " + " AND \"SITE_ID\" IN( '" + site + "') " + " AND \"PRODUCT_NO\" = '"
						+ productNo + "' " + " AND \"TRANSACTION_DATE\"::DATE BETWEEN ('" + fromDate + "') AND ('"
						+ toDate + "') " + " GROUP BY \"SITE_ID\" , \"PRODUCT_NO\" ";
				jObj.put("SECONDARY_SALES", getDataAsString(SQL, conn));

				json.add(jObj);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ SalesDAO-getSitesStockDetails ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	/**
	 * 
	 * @return
	 */
	public JSONArray getDeviceLevelSalesDetails(String siteIDs, String fromDate, String toDate, String productName) {
		JSONArray json = new JSONArray();
		try {
			String SQL = null;
			JSONArray tankSales = new JSONArray();
			JSONArray pumpSales = new JSONArray();
			JSONArray nozzleSales = new JSONArray();
			JSONArray duSales = new JSONArray();
			JSONArray pnSales = new JSONArray();
			JSONObject jObj = new JSONObject();
			Statement prdstm = null;
			ResultSet prdrs = null;

			// Tank Sales
			SQL = "  SELECT \"TRANSACTION_DATE\"::DATE,TRN.\"TANK_NO\",TRN.\"PRODUCT_NO\",UPPER(PRD.\"PRODUCT_NAME\") AS \"PRODUCT_NAME\", "
					+ " SUM(\"VOLUME\") AS \"VOLUME\",SUM(\"AMOUNT\") AS \"VALUE\",\"CURRENCY_CODE\",\"DEC_VAL\"  "
					+ " FROM " + schema + ".\"TRANSACTIONS\" TRN " + " INNER JOIN " + schema
					+ ".\"MS_PRODUCTS\" PRD ON PRD.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\" AND PRD.\"SITE_ID\" = TRN.\"SITE_ID\" "
					+ " INNER JOIN " + schema + ".\"MS_SITE\" ST ON ST.\"SITE_ID\" = TRN.\"SITE_ID\" "
					+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" CNT ON CNT.\"COUNTRY\" = ST.\"COUNTRY\" " + " WHERE 1=1 "
					+ " AND PRD.\"ADRM_STATUS\" != 'D' "
					//+ " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM " + schema
					//+ ".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) "
					+ " AND TRN.\"SITE_ID\" IN (" + siteIDs + ")  " + " AND \"VOLUME\" != 0 "
					+ " AND \"TRANSACTION_DATE\"::DATE BETWEEN ( '" + fromDate + "' ) AND ('" + toDate + "') " +
					// " AND PRD.\"PRODUCT_NAME\" = '"+productName+"' " +
					" GROUP BY TRN.\"TANK_NO\",TRN.\"PRODUCT_NO\",PRD.\"PRODUCT_NAME\",\"TRANSACTION_DATE\"::DATE,\"CURRENCY_CODE\",\"DEC_VAL\"  "
					+ " ORDER BY \"TRANSACTION_DATE\" DESC,\"TANK_NO\" ";
			prdstm = conn.createStatement();
			prdrs = prdstm.executeQuery(SQL);
			tankSales = dbc.parseRS(prdrs);
			prdstm = null;
			prdrs = null;

			// Pump Sales
			SQL = "  SELECT \"TRANSACTION_DATE\"::DATE,TRN.\"PUMP_NO\",TRN.\"PRODUCT_NO\",UPPER(PRD.\"PRODUCT_NAME\") AS \"PRODUCT_NAME\", "
					+ " SUM(\"VOLUME\") AS \"VOLUME\",SUM(\"AMOUNT\") AS \"VALUE\",\"CURRENCY_CODE\",\"DEC_VAL\"  "
					+ " FROM " + schema + ".\"TRANSACTIONS\" TRN " + " INNER JOIN " + schema
					+ ".\"MS_PRODUCTS\" PRD ON PRD.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\" AND PRD.\"SITE_ID\" = TRN.\"SITE_ID\" "
					+ " INNER JOIN " + schema + ".\"MS_SITE\" ST ON ST.\"SITE_ID\" = TRN.\"SITE_ID\" "
					+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" CNT ON CNT.\"COUNTRY\" = ST.\"COUNTRY\" " + " WHERE 1=1 "
					+ " AND PRD.\"ADRM_STATUS\" != 'D' "
					//+ " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM " + schema
					//+ ".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) "
					+ " AND TRN.\"SITE_ID\" IN (" + siteIDs + ")   " + " AND \"VOLUME\" != 0 "
					+ " AND \"TRANSACTION_DATE\"::DATE BETWEEN ( '" + fromDate + "' ) AND ('" + toDate + "') "
					+ " GROUP BY TRN.\"PUMP_NO\",TRN.\"PRODUCT_NO\",PRD.\"PRODUCT_NAME\",\"TRANSACTION_DATE\"::DATE,\"CURRENCY_CODE\",\"DEC_VAL\"  "
					+ " ORDER BY \"TRANSACTION_DATE\" DESC,\"PUMP_NO\" ";
			prdstm = conn.createStatement();
			prdrs = prdstm.executeQuery(SQL);
			pumpSales = dbc.parseRS(prdrs);
			prdstm = null;
			prdrs = null;

			// Nozzle Sales
			SQL = "  SELECT \"TRANSACTION_DATE\"::DATE,TRN.\"PRODUCT_NO\",UPPER(PRD.\"PRODUCT_NAME\") AS \"PRODUCT_NAME\",TRN.\"NOZZLE_NO\", "
					+ " SUM(\"VOLUME\") AS \"VOLUME\",SUM(\"AMOUNT\") AS \"VALUE\",\"CURRENCY_CODE\",\"DEC_VAL\"  "
					+ " FROM " + schema + ".\"TRANSACTIONS\" TRN " + " INNER JOIN " + schema
					+ ".\"MS_PRODUCTS\" PRD ON PRD.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\" AND PRD.\"SITE_ID\" = TRN.\"SITE_ID\" "
					+ " INNER JOIN " + schema + ".\"MS_SITE\" ST ON ST.\"SITE_ID\" = TRN.\"SITE_ID\" "
					+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" CNT ON CNT.\"COUNTRY\" = ST.\"COUNTRY\" " + " WHERE 1=1 "
					+ " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM " + schema
					+ ".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) "
					+ " AND TRN.\"SITE_ID\" IN (" + siteIDs + ")   " + " AND \"VOLUME\" != 0 "
					+ " AND \"TRANSACTION_DATE\"::DATE BETWEEN ( '" + fromDate + "' ) AND ('" + toDate + "') "
					+ " GROUP BY TRN.\"PRODUCT_NO\",PRD.\"PRODUCT_NAME\",TRN.\"NOZZLE_NO\",\"TRANSACTION_DATE\"::DATE,\"CURRENCY_CODE\",\"DEC_VAL\"  "
					+ " ORDER BY \"TRANSACTION_DATE\" DESC,\"NOZZLE_NO\" ";
			prdstm = conn.createStatement();
			prdrs = prdstm.executeQuery(SQL);
			nozzleSales = dbc.parseRS(prdrs);
			prdstm = null;
			prdrs = null;

			// DU Sales
			SQL = "  SELECT \"TRANSACTION_DATE\"::DATE,TRN.\"TANK_NO\",TRN.\"PUMP_NO\",TRN.\"PRODUCT_NO\",UPPER(PRD.\"PRODUCT_NAME\") AS \"PRODUCT_NAME\",TRN.\"NOZZLE_NO\", "
					+ " DS.\"DU_NO\", "
					+ " SUM(\"VOLUME\") AS \"VOLUME\",SUM(\"AMOUNT\") AS \"VALUE\",\"CURRENCY_CODE\",\"DEC_VAL\"  "
					+ " FROM " + schema + ".\"TRANSACTIONS\" TRN " + " INNER JOIN " + schema
					+ ".\"MS_PRODUCTS\" PRD ON PRD.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\" AND PRD.\"SITE_ID\" = TRN.\"SITE_ID\" "
					+ " INNER JOIN " + schema
					+ ".\"MS_NOZZLE_LIST\" NL ON NL.\"NOZZLE_NO\" = TRN.\"NOZZLE_NO\" AND NL.\"SITE_ID\" = TRN.\"SITE_ID\" AND NL.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\" AND NL.\"TANK_NO\" = TRN.\"TANK_NO\" "
					+ " INNER JOIN " + schema
					+ ".\"MS_DISPENSER\" DS ON DS.\"DU_NO\" = NL.\"DU_NO\" AND DS.\"SITE_ID\" = NL.\"SITE_ID\" "
					+ " INNER JOIN " + schema + ".\"MS_SITE\" ST ON ST.\"SITE_ID\" = TRN.\"SITE_ID\" "
					+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" CNT ON CNT.\"COUNTRY\" = ST.\"COUNTRY\" " + " WHERE 1=1 "
					+ " AND PRD.\"ADRM_STATUS\" != 'D' "
					//+ " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM " + schema
					//+ ".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) "
					+ " AND TRN.\"SITE_ID\" IN (" + siteIDs + ")   " + " AND \"VOLUME\" != 0 "
					+ " AND \"TRANSACTION_DATE\"::DATE BETWEEN ( '" + fromDate + "' ) AND ('" + toDate + "') "
					//+ " AND NL.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_NOZZLE_LIST\" NL1 WHERE NL1.\"SITE_ID\"=NL.\"SITE_ID\" AND NL1.\"NOZZLE_NO\"=NL.\"NOZZLE_NO\" ) "
					//+ " AND DS.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_DISPENSER\" DSP1 WHERE DSP1.\"SITE_ID\"=DS.\"SITE_ID\" AND DSP1.\"DU_NO\"=DS.\"DU_NO\" ) " 
					+ " AND NL.\"ADRM_STATUS\" != 'D' AND DS.\"ADRM_STATUS\" != 'D' "
					+ " GROUP BY TRN.\"TANK_NO\",TRN.\"PUMP_NO\",TRN.\"PRODUCT_NO\",PRD.\"PRODUCT_NAME\",TRN.\"NOZZLE_NO\",DS.\"DU_NO\",\"TRANSACTION_DATE\"::DATE,\"CURRENCY_CODE\",\"DEC_VAL\"  "
					+ " ORDER BY \"TRANSACTION_DATE\" DESC,\"PUMP_NO\" ";
			prdstm = conn.createStatement();
			prdrs = prdstm.executeQuery(SQL);
			duSales = dbc.parseRS(prdrs);
			prdstm = null;
			prdrs = null;

			// Pump/Nozzle Sales
			SQL = "  SELECT \"TRANSACTION_DATE\"::DATE,TRN.\"TANK_NO\",TRN.\"PUMP_NO\",TRN.\"PRODUCT_NO\",UPPER(PRD.\"PRODUCT_NAME\") AS \"PRODUCT_NAME\",TRN.\"NOZZLE_NO\", "
					+ " SUM(\"VOLUME\") AS \"VOLUME\",SUM(\"AMOUNT\") AS \"VALUE\",\"CURRENCY_CODE\",\"DEC_VAL\"  "
					+ " FROM " + schema + ".\"TRANSACTIONS\" TRN " + " INNER JOIN " + schema
					+ ".\"MS_PRODUCTS\" PRD ON PRD.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\" AND PRD.\"SITE_ID\" = TRN.\"SITE_ID\" "
					+ " INNER JOIN " + schema + ".\"MS_SITE\" ST ON ST.\"SITE_ID\" = TRN.\"SITE_ID\" "
					+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" CNT ON CNT.\"COUNTRY\" = ST.\"COUNTRY\" " + " WHERE 1=1 "
					+ " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM " + schema
					+ ".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) "
					+ " AND TRN.\"SITE_ID\" IN (" + siteIDs + ")   " + " AND \"VOLUME\" != 0 "
					+ " AND \"TRANSACTION_DATE\"::DATE BETWEEN ( '" + fromDate + "' ) AND ('" + toDate + "') "
					+ " GROUP BY TRN.\"TANK_NO\",TRN.\"PUMP_NO\",TRN.\"PRODUCT_NO\",PRD.\"PRODUCT_NAME\",TRN.\"NOZZLE_NO\",\"TRANSACTION_DATE\"::DATE,\"CURRENCY_CODE\",\"DEC_VAL\"  "
					+ " ORDER BY \"TRANSACTION_DATE\" DESC,\"PUMP_NO\" ";
			prdstm = conn.createStatement();
			prdrs = prdstm.executeQuery(SQL);
			pnSales = dbc.parseRS(prdrs);
			prdstm = null;
			prdrs = null;

			jObj.put("TANK_SALES", tankSales);
			jObj.put("PUMP_SALES", pumpSales);
			jObj.put("NOZZLE_SALES", nozzleSales);
			jObj.put("DU_SALES", duSales);
			jObj.put("PUMP_NOZZLE_SALES", pnSales);
			json.add(jObj);

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ SalesDAO-getDeviceLevelSalesDetails ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	private String convertDateFormat(String date) {
		LocalDateTime datetime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		return datetime.toString();
	}

	private JSONArray getData(String SQL, Connection con) {
		JSONArray arr = new JSONArray();

		try {
			// System.out.println("getData = "+SQL);
			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(SQL);
			arr = dbc.parseRS(rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arr;
	}

	private String getDataAsString(String SQL, Connection con) {
		String str = new String();

		try {
			// System.out.println("getDataAsString = " + SQL);
			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery(SQL);
			while (rs.next()) {
				str = formatValue(rs.getString("VOLUME"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 
	 * @param strNum
	 * @return
	 */
	public static boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		try {
			double d = Double.parseDouble(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	/**
	 * Format the values
	 * 
	 * @param val
	 * @return
	 */
	private String formatValue(String val) {
		try {
			if (!val.equalsIgnoreCase("0")) {
				if (isNumeric(val)) {
					if (val.contains("."))
						val = String.format("%,.3f", Double.valueOf(val));
					else
						val = String.format("%,d", Integer.valueOf(val));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return val;
	}

	private Date yesterday() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -2);
		return cal.getTime();
	}

	private Date nextday() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1);
		return cal.getTime();
	}

	private Date addDays(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}

	private Date day() {
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}

	/**
	 * 
	 * @return
	 */
	public JSONArray getAverageFill(String userId, String siteIDs, String fromDate, String toDate, String country,
			boolean grouping) {
		JSONArray json = new JSONArray();
		try {

			String SQL = "  SELECT COUNT(*)  AS \"TOTAL\", "
					+ " ROUND( (SUM(\"VOLUME\")) ,0) AS \"VOLUME\", "
					+ " ROUND( (SUM(\"VOLUME\") / COUNT(*) ) ,0) AS \"AVERAGE\", ";

			if (grouping) {
				SQL += " UPPER(\"PRODUCT_GROUP\") AS \"PRODUCT_NAME\"   ";
			} else {
				SQL += " UPPER(\"PRODUCT_NAME\") AS \"PRODUCT_NAME\"   ";
			}

			SQL += " FROM " + schema + ".\"TRANSACTIONS\" TRN   " + " INNER JOIN " + schema
					+ ".\"MS_PRODUCTS\" PRD ON PRD.\"PRODUCT_NO\"=TRN.\"PRODUCT_NO\" AND PRD.\"SITE_ID\"=TRN.\"SITE_ID\"  "
					+ " INNER JOIN \"BCT\".user_sites US ON US.site_id = TRN.\"SITE_ID\" "
					// + " INNER JOIN " + schema + ".\"MS_SITE\" MS ON MS.\"SITE_ID\" =
					// TRN.\"SITE_ID\" "
					+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" CNT ON CNT.\"COUNTRY\" = US.country "
					+ " INNER JOIN \"BCT\".\"COUNTRY_PRODUCT_CONFIG\" CPC ON CPC.\"PRODUCT\"=PRD.\"PRODUCT_NAME\" "
					+ " WHERE 1 = 1   " 
					+ " AND PRD.\"ADRM_STATUS\" != 'D' "
					//+ " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM " + schema+ ".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = TRN.\"SITE_ID\" ) "
					// + " AND TRN.\"SITE_ID\" IN (" + siteIDs + ") "
					+ " AND US.user_id = '" + userId + "' "  
					+ " AND CPC.\"COUNTRY_ID\"=CNT.\"COUNTRY_ID\" " 
					+ " AND \"TRANSACTION_DATE\"::DATE BETWEEN ('"
					+ fromDate + "') AND ('" + toDate + "')  ";

			if (country != null)
				SQL += " AND US.country = '" + country + "' ";

			if (siteIDs != null)
				if (!siteIDs.contains(","))
					SQL += " AND TRN.\"SITE_ID\" = " + siteIDs;

			if (grouping) {
				SQL += " GROUP BY \"PRODUCT_GROUP\" ";
			} else {
				SQL += " GROUP BY \"PRODUCT_NAME\" ";
			}

			System.out.println("\n\n\n\n");
			System.out.println("getAverageFill>>>>>>>>>>>>>>>>>>" + SQL);
			System.out.println("\n\n\n\n");

			Statement stmt = conn.createStatement();
			long startTimeData = System.nanoTime();
			ResultSet rs = stmt.executeQuery(SQL);
			long stopTimeData = System.nanoTime();
			//System.out.println("getAverageFill (DATA)="+TimeUnit.SECONDS.convert((stopTimeData - startTimeData), TimeUnit.NANOSECONDS));
			
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ SalesDAO-getAverageFill ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	/**
	 * 
	 * @return
	 */
	public JSONArray getSalesByTagID(String userId, String siteID, String fromDate, String toDate, String country,String tagId,String attendeeName,boolean grouping,String productName) {
		JSONArray json = new JSONArray();
		try {

			String SQL = " SELECT TRN.\"SITE_ID\",MS.\"SITE_NAME\",\"TRANSACTION_DATE\"::DATE,\"DSM_NAME\",TRN.\"TAG_ID\",COUNT(*) AS \"TOTAL TRANSACTIONS\",\"PRODUCT_NAME\",SUM(\"VOLUME\") AS \"VOLUME\",SUM(\"AMOUNT\") AS \"AMOUNT\",\"CURRENCY_CODE\" ";
			
			if(tagId != null)
				SQL+= ", \"PUMP_NO\",\"NOZZLE_NO\" ";
					
			SQL+= " FROM " + schema + ".\"TRANSACTIONS\" TRN " + " INNER JOIN " + schema
					+ ".\"MS_SITE\" MS ON MS.\"SITE_ID\" = TRN.\"SITE_ID\" "
					+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" CNT ON CNT.\"COUNTRY\" = MS.\"COUNTRY\"  " 
					+ " INNER JOIN \"BCT\".user_sites USS ON USS.site_id = TRN.\"SITE_ID\" " 
					+ " INNER JOIN " + schema + ".\"MS_PRODUCTS\" PRD ON PRD.\"SITE_ID\" = TRN.\"SITE_ID\" AND PRD.\"PRODUCT_NO\"=TRN.\"PRODUCT_NO\" "
					+ " INNER JOIN " + schema
					+ ".\"MS_DSM\" DSM ON DSM.\"SITE_ID\" = TRN.\"SITE_ID\" AND TRN.\"TAG_ID\" = DSM.\"RFID_TAG\" "
					+ " WHERE 1 = 1 " + " AND USS.user_id = '" + userId + "' "
					+ " AND TRN.\"TRANSACTION_DATE\"::DATE BETWEEN '" + fromDate + "' AND '" + toDate + "' "
					+ " AND PRD.\"ADRM_STATUS\" != 'D' ";
					//+ " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM " + schema+ ".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = TRN.\"SITE_ID\" ) ";

			if (siteID != null)
				SQL += " AND TRN.\"SITE_ID\" = " + siteID + " ";

			if (country != null)
				SQL += " AND CNT.\"COUNTRY\" = '" + country + "' ";
			
			if(tagId!=null)
				SQL +=" AND TRN.\"TAG_ID\" = '"+tagId+"' ";
			
			if(attendeeName!=null)
				SQL +=" AND \"DSM_NAME\" = '"+attendeeName+"' ";
			
			if(productName!=null) {
				SQL +=" AND \"PRODUCT_NAME\" = '"+productName+"' ";
			}

			if(grouping)
				SQL += " GROUP BY TRN.\"SITE_ID\",MS.\"SITE_NAME\",\"TRANSACTION_DATE\",\"DSM_NAME\",TRN.\"TAG_ID\",\"PRODUCT_NAME\",\"CURRENCY_CODE\" ";
			else
				SQL += " GROUP BY TRN.\"SITE_ID\",MS.\"SITE_NAME\",\"TRANSACTION_DATE\"::DATE,\"DSM_NAME\",TRN.\"TAG_ID\",\"PRODUCT_NAME\",\"CURRENCY_CODE\" ";
			if (tagId!=null)
				SQL+= ", \"PUMP_NO\", \"NOZZLE_NO\" ";
			
			SQL += " ORDER BY \"TRANSACTION_DATE\"::DATE,\"DSM_NAME\",\"PRODUCT_NAME\" ";
			if (tagId!=null)
				SQL+= ", \"PUMP_NO\", \"NOZZLE_NO\" ";

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ SalesDAO-getSalesByTagID ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	/**
	 * 
	 * @return
	 */
	public JSONArray getTotalFills(String userId, String siteIDs, String fromDate, String toDate, String country) {
		JSONArray json = new JSONArray();
		try {

			String SQL = "  SELECT \"TRANSACTION_DATE\"::DATE AS \"DATE\",COUNT(*) AS \"COUNT\", ROUND(SUM(\"VOLUME\"),2) AS \"VOLUME\", "
					+ " ROUND(SUM(\"VOLUME\")/COUNT(*),2) \"AVERAGE\" FROM " + schema + ".\"TRANSACTIONS\" T "
					+ " INNER JOIN \"BCT\".user_sites US ON US.site_id = T.\"SITE_ID\" " + " INNER JOIN "
					+ schema + ".\"MS_SITE\" MS ON MS.\"SITE_ID\" = T.\"SITE_ID\" " + " WHERE 1 = 1  "
					// + " AND MS.\"SITE_ID\" IN (" + siteIDs + ") "
					+ " AND US.user_id = '" + userId + "' " + " AND \"TRANSACTION_DATE\"::DATE BETWEEN ('"
					+ fromDate + "') AND ('" + toDate + "')  ";

			if (country != null)
				SQL += " AND US.country = '" + country + "' ";

			if (siteIDs != null)
				if (!siteIDs.contains(","))
					SQL += " AND MS.\"SITE_ID\" = " + siteIDs;

			SQL += " GROUP BY \"TRANSACTION_DATE\"::DATE  ";
			//System.out.println("getTotalFills====" + SQL);

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ SalesDAO-getTotalFills ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	
	/**
	 * 
	 * @return
	 */
	public JSONArray getTotalFillsProducts(String userId, String siteIDs, String fromDate, String toDate, String country,String productName,boolean grouping) {
		JSONArray json = new JSONArray();
		try {

			String SQL = "  SELECT \"TRANSACTION_DATE\"::DATE AS \"DATE\",COUNT(*) AS \"COUNT\", "
					+ " ROUND(SUM(\"VOLUME\"),2) AS \"VOLUME\", ";
			
					if (grouping) {
						SQL += " UPPER(\"PRODUCT_GROUP\") AS \"PRODUCT_NAME\"   ";
					} else {
						SQL += " UPPER(\"PRODUCT_NAME\") AS \"PRODUCT_NAME\"   ";
					}
					
					SQL+= " , ROUND(SUM(\"VOLUME\")/COUNT(*),2) \"AVERAGE\" "
					+ " FROM "+schema+".\"TRANSACTIONS\" TRN " + 
					" INNER JOIN "+schema+".\"MS_SITE\" MS  ON MS.\"SITE_ID\" = TRN.\"SITE_ID\" " + 
					" INNER JOIN \"BCT\".user_sites US ON US.site_id = MS.\"SITE_ID\" " + 
					" INNER JOIN \"BCT\".\"MS_COUNTRY\" AS CUR ON CUR.\"COUNTRY\" = MS.\"COUNTRY\" " + 
					" INNER JOIN "+schema+".\"MS_PRODUCTS\" PRD ON PRD.\"SITE_ID\" = TRN.\"SITE_ID\" AND PRD.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\" " + 
					" INNER JOIN \"BCT\".\"COUNTRY_PRODUCT_CONFIG\" CPC ON CPC.\"PRODUCT\" = PRD.\"PRODUCT_NAME\" " + 
					" WHERE 1=1  " + 
					" AND US.user_id = '"+userId+"' " + 
					" AND TRN.\"TRANSACTION_DATE\"::timestamp::date BETWEEN ('"+fromDate+"') AND ('"+toDate+"') " + 
					" AND CPC.\"COUNTRY_ID\"=CUR.\"COUNTRY_ID\" "+
					" AND PRD.\"ADRM_STATUS\" != 'D' ";
					//" AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 " + 
					//"							WHERE PRD1.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = TRN.\"SITE_ID\" )";

			if(productName!=null) {
				SQL +=" AND \"PRODUCT_NAME\" = '"+productName+"' ";
			}					
			if (country != null)
				SQL += " AND US.country = '" + country + "' ";

			if (siteIDs != null)
				if (!siteIDs.contains(","))
					SQL += " AND MS.\"SITE_ID\" = " + siteIDs;

			SQL += " GROUP BY \"TRANSACTION_DATE\"::DATE  ";
			
			if (grouping) {
				SQL += " ,UPPER(\"PRODUCT_GROUP\") ";
			} else {
				SQL += " ,UPPER(\"PRODUCT_NAME\") ";
			}
			
			//System.out.println("getTotalFills====" + SQL);

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);

			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ SalesDAO-getTotalFillsProducts ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	/**
	 * 
	 */
	public void runDailySales() {
		try {
			String SQL = " call " + schema + ".\"DAILY_SALES_PRC\"(1) ";
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(SQL);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ SalesDAO-runDailySales ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
