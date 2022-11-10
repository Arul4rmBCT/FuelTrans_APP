package com.bct.HOS.App.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.HashMap;

import com.bct.HOS.App.utils.DBConnector;
import com.bct.HOS.LVM.DeviceStatus;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DeviceDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;

	public DeviceDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
	}

	/**
	 * 
	 * @return
	 */
	public JSONArray getNozzleDetail(String siteIDs, String date) {
		JSONArray json = new JSONArray();
		try {
			String SQL = " 	SELECT NZ.\"SITE_ID\",ST.\"SITE_NAME\",NZ.\"NOZZLE_NO\",NZ.\"PRODUCT_NO\", "
					+ " UPPER(PR.\"PRODUCT_NAME\") AS \"PRODUCT_NAME\" " + " FROM " + schema + ".\"MS_NOZZLE_LIST\" NZ "
					+ " INNER JOIN " + schema + ".\"MS_PRODUCTS\" PR ON PR.\"PRODUCT_ID\" = NZ.\"PRODUCT_ID\" "
					+ " INNER JOIN " + schema + ".\"MS_SITE\" ST ON ST.\"SITE_ID\" = NZ.\"SITE_ID\" " + " INNER JOIN "
					+ schema + ".\"MS_PUMP_LIST\" PM ON NZ.\"PUMP_ID\" = PM.\"PUMP_ID\" " + " WHERE 1=1 ";

			SQL += " AND PR.\"ADRM_STATUS\" != 'D' AND NZ.\"ADRM_STATUS\" != 'D' AND PM.\"ADRM_STATUS\" != 'D'";
			//SQL += " AND PR.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM " + schema
			//		+ ".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PR.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PR.\"SITE_ID\" ) ";
			
			//SQL += " AND NZ.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_NOZZLE_LIST\" NL1 WHERE NL1.\"SITE_ID\"=NZ.\"SITE_ID\" AND NL1.\"NOZZLE_NO\"=NZ.\"NOZZLE_NO\" ) ";
			//SQL += " AND PM.\"MODIFIED_TIME\" = (SELECT MAX(\"MODIFIED_TIME\") FROM "+schema+".\"MS_PUMP_LIST\" PM1 WHERE PM1.\"SITE_ID\"=PM.\"SITE_ID\" AND PM1.\"PUMP_NO\"=PM.\"PUMP_NO\" ) ";
			
			if (siteIDs != null) {
				SQL += " AND ST.\"SITE_ID\" IN (" + siteIDs + ")";
			}

			if (date != null) {
				SQL += " AND DATE(PM.\"STATUS_TIME\") = '" + date + "' ";
			}

			SQL += "  GROUP BY NZ.\"NOZZLE_NO\",NZ.\"SITE_ID\",NZ.\"PRODUCT_NO\",PR.\"PRODUCT_NAME\",ST.\"SITE_NAME\" "
					+ " ORDER BY NZ.\"SITE_ID\"";

			//// System.out.println("getNozzleDetail = " + SQL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ DeviceDAO-getNozzleDetail ::" + ex.getMessage());
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
	public JSONArray getPumpAlarm(String siteIDs, String date) {
		JSONArray json = new JSONArray();
		try {
			String SQL = " 	 SELECT ROW_NUMBER () OVER (ORDER BY ALM.\"ALARM_ID\") AS \"SNO\",  MTDV.\"VALUE\" AS \"DEVICE_TYPE\",\"DEVICE_ID\",ALM.\"SITE_ID\",\"ALARM_ID\", "
					+ " \"DATE\" , ALM.\"DESCRIPTION\" , \"ISCLEAR\",\"CLEAR_TIME\", "
					+ " \"ACK_BY\",\"ACK_TIME\",\"IS_ACK\",\"PRIORITY\",\"UID\" " + " FROM " + schema
					+ ".\"METADATA\" MTD " + " JOIN " + schema
					+ ".\"METADATA_VALUE\" MTDV ON MTDV.\"METADATA_ID\" = MTD.\"ID\" " + " JOIN " + schema
					+ ".\"ALARM_LIST\" ALM ON ALM.\"DEVICE_TYPE\"::INTEGER =  MTDV.\"KEY\"::INTEGER "
					+ " WHERE MTD.\"METADATA\" = 'BOS_DEVICETYPE' " + " AND MTDV.\"VALUE\" = 'DU/PUMP' ";

			if (siteIDs != null) {
				SQL += " AND ALM.\"SITE_ID\" IN (" + siteIDs + ")";
			}

			if (date != null) {
				SQL += " AND ALM.\"DATE\"::DATE BETWEEN ('" + date + "') AND ('" + date + "') ";
			}

			SQL += "  ORDER BY \"ALARM_ID\" , \"DATE\" DESC ";

			//// System.out.println("getPumpAlarm = " + SQL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ DeviceDAO-getPumpAlarm ::" + ex.getMessage());
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
	public HashMap getDUSales(String siteID, String fromDate, String toDate, String date) {
		HashMap map = new HashMap();
		try {
			String SQL = "  SELECT DSP.\"DU_NO\",SUM(\"VOLUME\") AS \"SUM\"  " + " FROM " + schema
					+ ".\"TRANSACTIONS\" TRN  " + " INNER JOIN " + schema
					+ ".\"MS_NOZZLE_LIST\" NZL ON NZL.\"NOZZLE_NO\" = TRN.\"NOZZLE_NO\" AND NZL.\"PUMP_NO\" = TRN.\"PUMP_NO\" "
					+ " AND NZL.\"SITE_ID\"=TRN.\"SITE_ID\" "
					+ " INNER JOIN " + schema
					+ ".\"MS_DISPENSER\" DSP ON DSP.\"DU_NO\" = NZL.\"DU_NO\" AND DSP.\"SITE_ID\"=NZL.\"SITE_ID\" "
					+ " WHERE 1 = 1 " + " AND TRN.\"SITE_ID\" = " + siteID;
			SQL += " AND NZL.\"ADRM_STATUS\" != 'D' AND DSP.\"ADRM_STATUS\" != 'D' ";
			//SQL += " AND NZL.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_NOZZLE_LIST\" NL1 WHERE NL1.\"SITE_ID\"=NZL.\"SITE_ID\" AND NL1.\"NOZZLE_NO\"=NZL.\"NOZZLE_NO\" ) ";
			//SQL += " AND DSP.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_DISPENSER\" DSP1 WHERE DSP1.\"SITE_ID\"=DSP.\"SITE_ID\" AND DSP1.\"DU_NO\"=DSP.\"DU_NO\" ) ";
			
			if (date != null) {
				//// System.out.println("DATE."+date+".");
				SQL += " AND TRN.\"TRANSACTION_DATE\"::DATE BETWEEN ('" + date + "') AND ('" + date + "') ";
			} else {
				//// System.out.println("FROM DATE."+fromDate+"."+toDate+".");
				SQL += " AND TRN.\"TRANSACTION_DATE\"::DATE BETWEEN ('" + fromDate + "') AND ('" + toDate + "') ";
			}

			SQL += " GROUP BY DSP.\"DU_NO\" ";

			//// System.out.println("getDUSales = " + SQL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			while (rs.next()) {
				map.put(rs.getString("DU_NO"), rs.getString("SUM"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ DeviceDAO-getTankAlarm ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}

	/**
	 * 
	 * @return
	 */
	public JSONArray getPumpSales(String siteID, String fromDate, String toDate, String date) {
		JSONArray json = new JSONArray();
		try {
			/*
			String SQL = "  SELECT DSP.\"DU_NO\",TRN.\"PUMP_NO\",\"PRODUCT_NAME\",SUM(\"VOLUME\") AS \"SUM\"  "
					+ " FROM " + schema + ".\"TRANSACTIONS\" TRN  " + " INNER JOIN " + schema
					+ ".\"MS_PRODUCTS\" PRD ON PRD.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\" AND PRD.\"SITE_ID\"=TRN.\"SITE_ID\" "
					+ " INNER JOIN " + schema
					+ ".\"MS_NOZZLE_LIST\" NZL ON NZL.\"NOZZLE_NO\" = TRN.\"NOZZLE_NO\" AND NZL.\"PUMP_NO\" = TRN.\"PUMP_NO\" AND NZL.\"SITE_ID\"=TRN.\"SITE_ID\" "
					+ " INNER JOIN " + schema
					+ ".\"MS_DISPENSER\" DSP ON DSP.\"DU_NO\" = NZL.\"DU_NO\" AND DSP.\"SITE_ID\"=NZL.\"SITE_ID\" "
					+ " WHERE 1 = 1 "
					+ " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 "
					+ "							   WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) ";
			if (date != null) {
				//// System.out.println("DATE."+date+".");
				SQL += " AND TRN.\"TRANSACTION_DATE\"::DATE BETWEEN ('" + date + "') AND ('" + date + "') ";
			} else {
				//// System.out.println("FROM DATE."+fromDate+"."+toDate+".");
				SQL += " AND TRN.\"TRANSACTION_DATE\"::DATE BETWEEN ('" + fromDate + "') AND ('" + toDate + "') ";
			}
			SQL += " AND TRN.\"SITE_ID\" = " + siteID + " "
					+ " GROUP BY DSP.\"DU_NO\",TRN.\"PUMP_NO\",\"PRODUCT_NAME\" ";
			
			*/
			
			String SQL = " 	SELECT DSP.\"DU_NO\",NZL.\"PUMP_NO\",NZL.\"NOZZLE_NO\",\"PRODUCT_NAME\", " + 
					"	coalesce(T.\"SUM\",0,T.\"SUM\") AS \"SUM\", " + 
					"	coalesce(T.\"AMOUNT\",0,T.\"AMOUNT\") AS \"AMOUNT\", " + 
					"	CNT.\"CURRENCY_CODE\" " + 
					"	FROM "+schema+".\"MS_NOZZLE_LIST\" NZL  " + 
					"	INNER JOIN "+schema+".\"MS_DISPENSER\" DSP ON DSP.\"DU_NO\" = NZL.\"DU_NO\" AND DSP.\"SITE_ID\"=NZL.\"SITE_ID\"  " + 
					"	INNER JOIN "+schema+".\"MS_SITE\" S ON S.\"SITE_ID\" = NZL.\"SITE_ID\"   " + 
					"	INNER JOIN \"BCT\".\"MS_COUNTRY\" CNT ON CNT.\"COUNTRY\" = S.\"COUNTRY\"   " + 
					"	INNER JOIN "+schema+".\"MS_PRODUCTS\" PRD ON PRD.\"PRODUCT_NO\" = NZL.\"PRODUCT_NO\" AND PRD.\"SITE_ID\"=NZL.\"SITE_ID\"  " + 
					"	LEFT OUTER JOIN ( " + 
					"			SELECT TRN.\"SITE_ID\",TRN.\"PUMP_NO\",TRN.\"NOZZLE_NO\",\"PRODUCT_NO\",SUM(\"VOLUME\") AS \"SUM\", " + 
					"			ROUND(SUM(\"AMOUNT\"),\"DEC_VAL\") AS \"AMOUNT\",CNT.\"CURRENCY_CODE\" " + 
					"			FROM "+schema+".\"TRANSACTIONS\" TRN  " + 
					"			INNER JOIN "+schema+".\"MS_SITE\" S ON S.\"SITE_ID\" = TRN.\"SITE_ID\"   " + 
					"			INNER JOIN \"BCT\".\"MS_COUNTRY\" CNT ON CNT.\"COUNTRY\" = S.\"COUNTRY\" " + 
					"			WHERE TRN.\"SITE_ID\" =   " +siteID;
			
				if (date != null) {
					SQL+="			AND \"TRANSACTION_DATE\"::DATE = '"+date+"' " ;
				}else {
					SQL+="			AND \"TRANSACTION_DATE\"::DATE BETWEEN ('"+fromDate+"') AND ('"+toDate+"') " ;
				}
					
					SQL+="			GROUP BY TRN.\"SITE_ID\",TRN.\"PUMP_NO\",TRN.\"NOZZLE_NO\",TRN.\"PRODUCT_NO\",CNT.\"DEC_VAL\",CNT.\"CURRENCY_CODE\" " + 
					"		) T ON T.\"SITE_ID\" = PRD.\"SITE_ID\" AND T.\"PUMP_NO\" = NZL.\"PUMP_NO\" AND NZL.\"NOZZLE_NO\"=T.\"NOZZLE_NO\" " + 
					"		AND T.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" " + 
					"	WHERE 1 = 1   " + 
					" AND PRD.\"ADRM_STATUS\" != 'D'  AND NZL.\"ADRM_STATUS\" != 'D'  AND DSP.\"ADRM_STATUS\" != 'D' " +
					//"	AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1  " + 
					//"							 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" )   " +
					//"   AND NZL.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_NOZZLE_LIST\" NL1 WHERE NL1.\"SITE_ID\"=NZL.\"SITE_ID\" AND NL1.\"NOZZLE_NO\"=NZL.\"NOZZLE_NO\" ) " +
					//" AND DSP.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_DISPENSER\" DSP1 WHERE DSP1.\"SITE_ID\"=DSP.\"SITE_ID\" AND DSP1.\"DU_NO\"=DSP.\"DU_NO\" ) " + 
					"	AND PRD.\"SITE_ID\" IN ("+siteID+") " + 
					"	GROUP BY DSP.\"DU_NO\",NZL.\"PUMP_NO\",NZL.\"NOZZLE_NO\",\"PRODUCT_NAME\",CNT.\"CURRENCY_CODE\",T.\"SUM\",T.\"AMOUNT\"   " + 
					"	ORDER BY DSP.\"DU_NO\" ";
			
			 //System.out.println("getPumpSales = " + SQL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ DeviceDAO-getPumpSales ::" + ex.getMessage());
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
	public JSONArray getTankAlarm(String siteIDs, String date) {
		JSONArray json = new JSONArray();
		try {
			String SQL = " SELECT ROW_NUMBER () OVER (ORDER BY ALM.\"ALARM_ID\") AS \"SNO\",MTDV.\"VALUE\" AS \"DEVICE_TYPE\",\"DEVICE_ID\",ALM.\"SITE_ID\",\"ALARM_ID\", "
					+ " \"DATE\" , ALM.\"DESCRIPTION\" , \"ISCLEAR\",\"CLEAR_TIME\", "
					+ " \"ACK_BY\",\"ACK_TIME\",\"IS_ACK\",\"PRIORITY\",\"UID\" " + " FROM " + schema
					+ ".\"METADATA\" MTD " + " JOIN " + schema
					+ ".\"METADATA_VALUE\" MTDV ON MTDV.\"METADATA_ID\" = MTD.\"ID\" " + " JOIN " + schema
					+ ".\"ALARM_LIST\" ALM ON ALM.\"DEVICE_TYPE\"::INTEGER =  MTDV.\"KEY\"::INTEGER "
					+ " WHERE MTD.\"METADATA\" = 'BOS_DEVICETYPE' " + " AND MTDV.\"VALUE\" = 'TANK' ";

			if (siteIDs != null) {
				SQL += " AND ALM.\"SITE_ID\" IN (" + siteIDs + ")";
			}

			if (date != null) {
				SQL += " AND ALM.\"DATE\"::DATE BETWEEN ('" + date + "') AND ('" + date + "') ";
			}

			SQL += "  ORDER BY \"ALARM_ID\" , \"DATE\" DESC ";

			//// System.out.println("getTankAlarm = " + SQL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ DeviceDAO-getTankAlarm ::" + ex.getMessage());
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
	public JSONArray getPumpDetail(String siteIDs, String date) {
		JSONArray json = new JSONArray();
		try {
			String SQL = " SELECT NZ.\"SITE_ID\",ST.\"SITE_NAME\",PM.\"PUMP_NO\",PM.\"PUMP_TYPE\",NZ.\"PRODUCT_NO\", "
					+ " UPPER(PR.\"PRODUCT_NAME\") AS \"PRODUCT_NAME\" " + " FROM " + schema
					+ ".\"MS_NOZZLE_LIST\" NZ  " + " INNER JOIN " + schema
					+ ".\"MS_PRODUCTS\" PR ON PR.\"PRODUCT_ID\" = NZ.\"PRODUCT_ID\" " + " INNER JOIN " + schema
					+ ".\"MS_SITE\" ST ON ST.\"SITE_ID\" = NZ.\"SITE_ID\" " + " INNER JOIN " + schema
					+ ".\"MS_PUMP_LIST\" PM ON NZ.\"PUMP_ID\" = PM.\"PUMP_ID\" " + " WHERE 1=1 ";
			SQL += " AND PR.\"ADRM_STATUS\" != 'D' AND NZ.\"ADRM_STATUS\" != 'D' AND PM.\"ADRM_STATUS\" != 'D' ";
			//SQL += " AND PR.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM " + schema
			//		+ ".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PR.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PR.\"SITE_ID\" ) ";
			//SQL += " AND NZ.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_NOZZLE_LIST\" NL1 WHERE NL1.\"SITE_ID\"=NZ.\"SITE_ID\" AND NL1.\"NOZZLE_NO\"=NZ.\"NOZZLE_NO\" ) ";
			//SQL += " AND PM.\"MODIFIED_TIME\" = (SELECT MAX(\"MODIFIED_TIME\") FROM "+schema+".\"MS_PUMP_LIST\" PM1 WHERE PM1.\"SITE_ID\"=PM.\"SITE_ID\" AND PM1.\"PUMP_NO\"=PM.\"PUMP_NO\" ) ";
			if (siteIDs != null) {
				SQL += " AND ST.\"SITE_ID\" IN (" + siteIDs + ")";
			}

			if (date != null) {
				SQL += " AND DATE(PM.\"STATUS_TIME\") = '" + date + "' ";
			}

			SQL += " GROUP BY NZ.\"PUMP_NO\",PM.\"PUMP_NO\",PM.\"PUMP_TYPE\",NZ.\"SITE_ID\",NZ.\"PRODUCT_NO\",PR.\"PRODUCT_NAME\",ST.\"SITE_NAME\" "
					+ " ORDER BY NZ.\"SITE_ID\"";

			//// System.out.println("getPumpDetail = " + SQL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ DeviceDAO-getPumpDetail ::" + ex.getMessage());
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
	 * @param siteIDs
	 * @param date
	 * @param du
	 * @return
	 */
	public JSONArray getDuPumpSales(String siteIDs, String date, String du) {
		JSONArray pumpList = new JSONArray();
		try {
			HashMap pumpStatus = new DeviceStatus().getSiteDUpumpStatus(siteIDs, du);

			/*
			String SQL = "  SELECT DSP.\"DU_NO\",TRN.\"PUMP_NO\",\"PRODUCT_NAME\",SUM(\"VOLUME\") AS \"SUM\" , ROUND(SUM(\"AMOUNT\"),\"DEC_VAL\") AS \"AMOUNT\" , CNT.\"CURRENCY_CODE\"  "
					+ " FROM " + schema + ".\"TRANSACTIONS\" TRN  " + " INNER JOIN " + schema
					+ ".\"MS_PRODUCTS\" PRD ON PRD.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\" AND PRD.\"SITE_ID\"=TRN.\"SITE_ID\" "
					+ " INNER JOIN " + schema
					+ ".\"MS_NOZZLE_LIST\" NZL ON NZL.\"NOZZLE_NO\" = TRN.\"NOZZLE_NO\" AND NZL.\"PUMP_NO\" = TRN.\"PUMP_NO\" AND NZL.\"SITE_ID\"=TRN.\"SITE_ID\" "
					+ " INNER JOIN " + schema
					+ ".\"MS_DISPENSER\" DSP ON DSP.\"DU_NO\" = NZL.\"DU_NO\" AND DSP.\"SITE_ID\"=NZL.\"SITE_ID\" "
					+ " INNER JOIN " + schema + ".\"MS_SITE\" S ON S.\"SITE_ID\" = TRN.\"SITE_ID\" "
					+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" CNT ON CNT.\"COUNTRY\" = S.\"COUNTRY\" " + " WHERE 1 = 1 "
					+ " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM " + schema
					+ ".\"MS_PRODUCTS\" PRD1 "
					+ "							   WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) ";

			if (siteIDs != null) {
				SQL += " AND PRD.\"SITE_ID\" IN (" + siteIDs + ")";
			}

			if (date != null) {
				SQL += " AND TRN.\"TRANSACTION_DATE\"::DATE = '" + date + "' ";
			}

			if (du != null) {
				SQL += " AND DSP.\"DU_NO\" = '" + du + "' ";
			}

			SQL += " GROUP BY DSP.\"DU_NO\",TRN.\"PUMP_NO\",\"PRODUCT_NAME\",CNT.\"DEC_VAL\",CNT.\"CURRENCY_CODE\" "
					+ " ORDER BY DSP.\"DU_NO\"";
			*/
			
			String SQL = " 	SELECT DSP.\"DU_NO\",NZL.\"PUMP_NO\",\"PRODUCT_NAME\", " + 
					"	coalesce(T.\"SUM\",0,T.\"SUM\") AS \"SUM\", " + 
					"	coalesce(T.\"AMOUNT\",0,T.\"AMOUNT\") AS \"AMOUNT\", " + 
					"	CNT.\"CURRENCY_CODE\" " + 
					"	FROM "+schema+".\"MS_NOZZLE_LIST\" NZL  " + 
					"	INNER JOIN "+schema+".\"MS_DISPENSER\" DSP ON DSP.\"DU_NO\" = NZL.\"DU_NO\" AND DSP.\"SITE_ID\"=NZL.\"SITE_ID\"  " + 
					"	INNER JOIN "+schema+".\"MS_SITE\" S ON S.\"SITE_ID\" = NZL.\"SITE_ID\"   " + 
					"	INNER JOIN \"BCT\".\"MS_COUNTRY\" CNT ON CNT.\"COUNTRY\" = S.\"COUNTRY\"   " + 
					"	INNER JOIN "+schema+".\"MS_PRODUCTS\" PRD ON PRD.\"PRODUCT_NO\" = NZL.\"PRODUCT_NO\" AND PRD.\"SITE_ID\"=NZL.\"SITE_ID\"  " + 
					"	LEFT OUTER JOIN ( " + 
					"			SELECT TRN.\"SITE_ID\",TRN.\"PUMP_NO\",TRN.\"NOZZLE_NO\",\"PRODUCT_NO\",SUM(\"VOLUME\") AS \"SUM\", " + 
					"			ROUND(SUM(\"AMOUNT\"),\"DEC_VAL\") AS \"AMOUNT\",CNT.\"CURRENCY_CODE\" " + 
					"			FROM "+schema+".\"TRANSACTIONS\" TRN  " + 
					"			INNER JOIN "+schema+".\"MS_SITE\" S ON S.\"SITE_ID\" = TRN.\"SITE_ID\"   " + 
					"			INNER JOIN \"BCT\".\"MS_COUNTRY\" CNT ON CNT.\"COUNTRY\" = S.\"COUNTRY\" " + 
					"			WHERE TRN.\"SITE_ID\" IN ( " +siteIDs +")";
			
				if (date != null) {
					SQL+="			AND \"TRANSACTION_DATE\"::DATE = '"+date+"' " ;
				}
					
					SQL+="			GROUP BY TRN.\"SITE_ID\",TRN.\"PUMP_NO\",TRN.\"NOZZLE_NO\",TRN.\"PRODUCT_NO\",CNT.\"DEC_VAL\",CNT.\"CURRENCY_CODE\" " + 
					"		) T ON T.\"SITE_ID\" = PRD.\"SITE_ID\" AND T.\"PUMP_NO\" = NZL.\"PUMP_NO\" AND NZL.\"NOZZLE_NO\"=T.\"NOZZLE_NO\" " + 
					"		AND T.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" " + 
					"	WHERE 1 = 1   " +
					" AND PRD.\"ADRM_STATUS\" != 'D' AND NZL.\"ADRM_STATUS\" != 'D' AND DSP.\"ADRM_STATUS\" != 'D' " +
					//"	AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1  " + 
					//"							 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" )   " +
					//" AND NZL.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_NOZZLE_LIST\" NL1 WHERE NL1.\"SITE_ID\"=NZL.\"SITE_ID\" AND NL1.\"NOZZLE_NO\"=NZL.\"NOZZLE_NO\" AND NL1.\"PUMP_NO\"=NZL.\"PUMP_NO\" ) " +
					//" AND DSP.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_DISPENSER\" DSP1 WHERE DSP1.\"SITE_ID\"=DSP.\"SITE_ID\" AND DSP1.\"DU_NO\"=DSP.\"DU_NO\" ) " +
					"	AND PRD.\"SITE_ID\" IN ("+siteIDs+") ";
					
					if (du != null) {
						SQL += " AND DSP.\"DU_NO\" = '" + du + "' ";
					}
					
					SQL+="	GROUP BY DSP.\"DU_NO\",NZL.\"PUMP_NO\",\"PRODUCT_NAME\",CNT.\"CURRENCY_CODE\",T.\"SUM\",T.\"AMOUNT\"   " + 
					"	ORDER BY DSP.\"DU_NO\" ";
			
			System.out.println("\n\n\n\n\n\n");
			System.out.println(SQL);
			System.out.println("\n\n\n\n\n\n");
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			// json = dbc.parseRS(rs);

			String pumpNo = null;
			String prd = null;
			String sale = null;
			String amount = null;
			String curCode = null;
			String tmpPump = "";
			String ps=null;
			String lu = null;
			String[] psa = new String[2];
			JSONArray prdList = null;
			JSONObject prdObj = null;
			JSONObject pmpObj = null;
			int i = 0;
			while (rs.next()) {

				pumpNo = rs.getString("PUMP_NO");
				prd = rs.getString("PRODUCT_NAME");
				sale = rs.getString("SUM");
				amount = rs.getString("AMOUNT");
				curCode = rs.getString("CURRENCY_CODE");
				
				
				if (!tmpPump.equalsIgnoreCase(pumpNo)) {
					if(pumpStatus.get(tmpPump)!=null) {
						if(pumpStatus.get(tmpPump).toString().contains("~")) {
							psa = pumpStatus.get(tmpPump).toString().split("~");
							ps = psa[0];
							lu = psa[1];					
						}else {
							ps  = "";
							lu = "";
						}
					}
					if (prdList != null) {
						pmpObj = new JSONObject();
						pmpObj.put("PumpNo", tmpPump);
						pmpObj.put("PumpStatus", ps);
						pmpObj.put("PumpLastConnected", lu);
						pmpObj.put("Products", prdList);
						pumpList.add(pmpObj);
					}
					prdList = new JSONArray();
					tmpPump = pumpNo;
				}

				prdObj = new JSONObject();
				prdObj.put("ProductName", prd);
				prdObj.put("SalesVolume", sale);

				if (!amount.equalsIgnoreCase("0")) {
					//// System.out.println(val + " --- " + column_name);
					if (isNumeric(amount)) {
						if (amount.contains("."))
							amount = String.format("%,.3f", Double.valueOf(amount));
						else
							amount = String.format("%,d", Integer.valueOf(amount));
					}
				}

				prdObj.put("SalesAmount", amount);
				prdObj.put("Currency", curCode);
				prdList.add(prdObj);

				if (rs.isLast()) {
					pmpObj = new JSONObject();
					pmpObj.put("PumpNo", pumpNo);
					if(pumpStatus.get(pumpNo).toString().contains("~")) {
						psa = pumpStatus.get(pumpNo).toString().split("~");
						ps = psa[0];
						lu = psa[1];				
						pmpObj.put("PumpStatus", ps);
						pmpObj.put("PumpLastConnected", lu);
					}else
						pmpObj.put("PumpStatus", pumpStatus.get(pumpNo));
					
					pmpObj.put("Products", prdList);
					pumpList.add(pmpObj);
				}
				i++;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ DeviceDAO-getDuPumpSales ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return pumpList;
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
	 * 
	 * @return
	 */
	public JSONArray getTankDetail(String siteIDs, String date) {
		JSONArray json = new JSONArray();
		try {
			String SQL = " SELECT NZ.\"SITE_ID\",ST.\"SITE_NAME\",TN.\"TANK_NO\",TN.\"PRODUCT_NO\", "
					+ " UPPER(PR.\"PRODUCT_NAME\") AS \"PRODUCT_NAME\",TN.\"MIN_CAPACITY\",TN.\"MAX_CAPACITY\" "
					+ " FROM " + schema + ".\"MS_NOZZLE_LIST\" NZ " + " INNER JOIN " + schema
					+ ".\"MS_SITE\" ST ON ST.\"SITE_ID\" = NZ.\"SITE_ID\" " + " INNER JOIN " + schema
					+ ".\"MS_PUMP_LIST\" PM ON NZ.\"PUMP_ID\" = PM.\"PUMP_ID\" " + " INNER JOIN " + schema
					+ ".\"MS_TANK\" TN ON TN.\"TANK_ID\" = NZ.\"TANK_ID\" " + " INNER JOIN " + schema
					+ ".\"MS_PRODUCTS\" PR ON PR.\"PRODUCT_NO\" = TN.\"PRODUCT_NO\" " + " WHERE 1=1 ";
			SQL += " AND PR.\"ADRM_STATUS\" != 'D' AND TN.\"ADRM_STATUS\" != 'D' AND NZ.\"ADRM_STATUS\" != 'D' AND PM.\"ADRM_STATUS\" != 'D' ";
			//SQL += " AND PR.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM " + schema
			//		+ ".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PR.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PR.\"SITE_ID\" ) ";
			//SQL += " AND TN.\"MODIFIED_TIME\" = (SELECT MAX(\"MODIFIED_TIME\") FROM "+schema+".\"MS_TANK\" TNK1 WHERE TNK1.\"SITE_ID\" = TN.\"SITE_ID\"  AND TNK1.\"TANK_NO\"=TN.\"TANK_NO\" ) ";
			//SQL += " AND NZ.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_NOZZLE_LIST\" NL1 WHERE NL1.\"SITE_ID\"=NZ.\"SITE_ID\" AND NL1.\"NOZZLE_NO\"=NZ.\"NOZZLE_NO\" ) ";
			//SQL += " AND PM.\"MODIFIED_TIME\" = (SELECT MAX(\"MODIFIED_TIME\") FROM "+schema+".\"MS_PUMP_LIST\" PM1 WHERE PM1.\"SITE_ID\"=PM.\"SITE_ID\" AND PM1.\"PUMP_NO\"=PM.\"PUMP_NO\" ) ";
			if (siteIDs != null) {
				SQL += " AND ST.\"SITE_ID\" IN (" + siteIDs + ")";
			}

			if (date != null) {
				SQL += " AND DATE(PM.\"STATUS_TIME\") = '" + date + "' ";
			}

			SQL += " GROUP BY NZ.\"SITE_ID\",PR.\"PRODUCT_NAME\", "
					+ "TN.\"TANK_NO\",ST.\"SITE_NAME\",TN.\"MIN_CAPACITY\",TN.\"MAX_CAPACITY\",TN.\"PRODUCT_NO\" ";

			//// System.out.println("getTankDetail = " + SQL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ DeviceDAO-getTankDetail ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	public JSONObject getTankCurrentDetail(String siteId, String tankNo) {
		JSONObject jsonObj = null;
		ResultSet rs = null;
		String SQL = null;
		try {
			int capacity = 0;
			float volume = 0;
			float percentage;
			DecimalFormat df = new DecimalFormat("0.00");

			SQL = " SELECT TN.\"TANK_NO\",TN.\"PRODUCT_NO\", UPPER(PR.\"PRODUCT_NAME\") AS \"PRODUCT_NAME\", ROUND(TN.\"MIN_CAPACITY\",0) AS \"MIN_CAPACITY\", ROUND(TN.\"MAX_CAPACITY\") AS \"MAX_CAPACITY\"  "
					+ " , ROUND(INV.\"VOLUME\") AS \"CURRENT_CAPACITY\" ,INV.\"INVENTORY_DATE\" , ROUND(INV.\"ULLAGE\") AS \"ULLAGE\" , ROUND(INV.\"HEIGHT\") AS \"HEIGHT\"  , INV.\"TEMPERATURE\" , "
					+ "  ROUND(INV.\"WATER\") AS \"WATER\" , ROUND(INV.\"WATER_HEIGHT\") AS \"WATER_HEIGHT\"  , INV.\"DENSITY_STATUS\" , ROUND(INV.\"DENSITY_ACTUAL\") AS \"DENSITY_ACTUAL\" , ROUND(INV.\"DENSITY_AT15DEG\") AS \"DENSITY_AT15DEG\" , ROUND(INV.\"TCVOLUME\") AS \"TCVOLUME\"   "
					+ " FROM " + schema + ".\"INVENTORY\" INV  " + " INNER JOIN " + schema
					+ ".\"MS_SITE\" ST ON ST.\"SITE_ID\" = INV.\"SITE_ID\" " + " INNER JOIN " + schema
					+ ".\"MS_TANK\" TN ON TN.\"TANK_NO\" = INV.\"TANK_NO\" AND TN.\"SITE_ID\" = INV.\"SITE_ID\" "
					+ " INNER JOIN " + schema
					+ ".\"MS_PRODUCTS\" PR ON PR.\"PRODUCT_NO\" = TN.\"PRODUCT_NO\"  AND TN.\"SITE_ID\" = PR.\"SITE_ID\" "
					+ " WHERE 1=1 AND PR.\"ADRM_STATUS\" != 'D' " 
					//+ " AND PR.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM " + schema
					//+ ".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PR.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PR.\"SITE_ID\" ) "
					+ " AND INV.\"SITE_ID\" IN (" + siteId + ")   " + " AND TN.\"TANK_NO\" =   " + tankNo
					//+ " AND INV.\"INVENTORY_DATE\"::DATE = CURRENT_DATE "
					+ " AND INV.\"INVENTORY_DATE\" = (SELECT MAX(TMP.\"INVENTORY_DATE\") FROM " + schema
					+ ".\"INVENTORY\" TMP WHERE TMP.\"TANK_NO\" = " + tankNo + " AND TMP.\"SITE_ID\" IN (" + siteId
					+ ") ) "
					+ " AND TN.\"ADRM_STATUS\" != 'D' "
					//+ " AND TN.\"MODIFIED_TIME\" = (SELECT MAX(\"MODIFIED_TIME\") FROM "+schema+".\"MS_TANK\" TNK1 WHERE TNK1.\"SITE_ID\" = TN.\"SITE_ID\"  AND TNK1.\"TANK_NO\"=TN.\"TANK_NO\" ) "
					+ " GROUP BY TN.\"TANK_NO\",TN.\"PRODUCT_NO\", PR.\"PRODUCT_NAME\",TN.\"MIN_CAPACITY\",TN.\"MAX_CAPACITY\"  "
					+ " , INV.\"VOLUME\" ,INV.\"INVENTORY_DATE\" , "
					+ " INV.\"ULLAGE\" , INV.\"HEIGHT\"  , INV.\"TEMPERATURE\" , "
					+ "  INV.\"WATER\" , INV.\"WATER_HEIGHT\"  , INV.\"DENSITY_STATUS\" , INV.\"DENSITY_ACTUAL\" , INV.\"DENSITY_AT15DEG\" , INV.\"TCVOLUME\"  "
					+ " ORDER BY TN.\"TANK_NO\",INV.\"INVENTORY_DATE\" DESC ";

			System.out.println("getTankCurrentDetail = " + SQL);
			jsonObj = new JSONObject();
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(SQL);
			while (rs.next()) {
				jsonObj.put("PRODUCT_NO", rs.getString("PRODUCT_NO"));
				jsonObj.put("PRODUCT_NAME", rs.getString("PRODUCT_NAME"));
				volume = Float.parseFloat(rs.getString("CURRENT_CAPACITY"));
				capacity = Integer.parseInt(rs.getString("MAX_CAPACITY"));
				percentage = ((volume) * 100) / capacity;
				if(percentage>100)
					percentage = 100;
				jsonObj.put("CURRENT_CAPACITY", formatValue(String.valueOf(volume)));
				jsonObj.put("MIN_CAPACITY", formatValue(rs.getString("MIN_CAPACITY")));
				jsonObj.put("MAX_CAPACITY", formatValue(String.valueOf(capacity)));
				jsonObj.put("ULLAGE", formatValue(rs.getString("ULLAGE")));
				jsonObj.put("HEIGHT", rs.getString("HEIGHT"));
				jsonObj.put("TEMPERATURE", rs.getString("TEMPERATURE"));
				jsonObj.put("WATER", rs.getString("WATER"));
				jsonObj.put("WATER_HEIGHT", rs.getString("WATER_HEIGHT"));
				jsonObj.put("DENSITY_STATUS", rs.getString("DENSITY_STATUS"));
				jsonObj.put("DENSITY_ACTUAL", rs.getString("DENSITY_ACTUAL"));
				jsonObj.put("DENSITY_AT15DEG", rs.getString("DENSITY_AT15DEG"));
				jsonObj.put("TCVOLUME", formatValue(rs.getString("TCVOLUME")));

				// jsonObj.put("PERCENTAGE", df.format(percentage));
				jsonObj.put("PERCENTAGE", Math.round(percentage));
				// //System.out.println("******************Device DAO RS");
			}
			//// System.out.println("**************************************************
			//// "+jsonObj.toString());

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ DeviceDAO-getTankDetail ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return jsonObj;

	}

	/**
	 * Format the values
	 * @param val
	 * @return
	 */
	private String formatValue(String val){
		try {
			if(val==null)
				val = "0";
			
			if (!val.equalsIgnoreCase("0")) {
				if (isNumeric(val)) {
					if (val.contains(".")) {
						val = String.format("%,.0f", Double.valueOf(val));
					}else {
						val = String.format("%,d", Integer.valueOf(val));
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return val;
	}

	/**
	 * 
	 * @return
	 */
	public JSONArray getROFCC(String siteIDs, String country, String state, String region, String district,
			String city) {
		JSONArray json = new JSONArray();
		try {

			String SQL = " SELECT \"SITE_ID\",\"SITE_NAME\",\"COUNTRY\",\"STATE\",\"REGION\",\"DISTRICT\",\"REGION\",\"BOS_VERSION\",\"FCC_VERSION\" "
					+ " FROM " + schema + ".\"MS_SITE\" " + " WHERE 1 = 1 ";

			if (siteIDs != null) {
				SQL += " AND \"SITE_ID\" IN (" + siteIDs + ")";
			}

			if (country != null) {
				SQL += " AND \"COUNTRY\" = '" + country + "' ";
			}

			if (state != null) {
				SQL += " AND \"STATE\" = '" + state + "' ";
			}

			if (region != null) {
				SQL += " AND \"REGION\" = '" + region + "' ";
			}

			if (district != null) {
				SQL += " AND \"DISTRICT\" = '" + district + "' ";
			}

			if (city != null) {
				SQL += " AND \"CITY\" = '" + city + "' ";
			}

			//// System.out.println("getROFCC >>> " + SQL);

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ DeviceDAO-getROFCC ::" + ex.getMessage());
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
	public boolean storeFW(String fccVersion, String versionName, String releaseNote, String comment, String country) {
		try {
			String SQL = "  INSERT INTO " + schema
					+ ".\"FIRMWARE_UPDATE\"(\"FCC_VERSION\",\"VERSION_NAME\",\"RELEASE_NOTES\",\"COMMENTS\",\"COUNTRY\") "
					+ " VALUES('" + fccVersion + "','" + versionName + "','" + releaseNote + "','" + comment + "','"
					+ country + "') ";
			Statement stm = conn.createStatement();
			//// System.out.println(SQL);
			stm.execute(SQL);
			//// System.out.println("Stored!..");
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ DeviceDAO-storeFW ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

}
