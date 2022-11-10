package com.bct.HOS.App.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import com.bct.HOS.App.BO.PriceBO;
import com.bct.HOS.App.utils.DBConnector;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class PriceDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;

	public PriceDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
	}

	/**
	 * 
	 * @return
	 */
	public JSONArray getPriceDetails(String siteIDs, String productName, String diffDays) {
		JSONArray json = new JSONArray();
		try {
			String SQL = " SELECT COALESCE(PRC.\"PRICE_ID\",0) AS \"PRICE_ID\",P.\"SITE_ID\",S.\"SITE_NAME\",P.\"PRODUCT_NO\",UPPER(P.\"PRODUCT_NAME\") AS \"PRODUCT_NAME\", "
					+ " PRC.\"RECORD_APPLY_TIME\",DATE(PRC.\"RECORD_APPLY_TIME\") AS \"RECORD_APPLY_DATE\", "
					+ " PRC.\"PRICE_TYPE\", " + "  COALESCE(PRC.\"NEW_PRICE\",0) AS \"NEW_PRICE\",  "
					+ " COALESCE((     SELECT \"NEW_PRICE\" FROM " + schema + ".\"MS_PRICE_CHANGE\" P1 "
					+ " WHERE        P1.\"PRODUCT_NO\" = PRC.\"PRODUCT_NO\"      "
					+ " AND P1.\"SITE_ID\"=PRC.\"SITE_ID\" "
					+ " AND  DATE(P1.\"RECORD_APPLY_TIME\") = DATE(PRC.\"RECORD_APPLY_TIME\")  - " + diffDays
					+ "   order by P1.\"PRICE_ID\" DESC     limit 1 ),0) AS \"OLD_PRICE\", "
					+ " COALESCE((     SELECT (P1.\"NEW_PRICE\"-PRC.\"NEW_PRICE\") " + " FROM " + schema
					+ ".\"MS_PRICE_CHANGE\" P1 " + " WHERE   P1.\"PRODUCT_NO\" = PRC.\"PRODUCT_NO\"  "
					+ " AND P1.\"SITE_ID\"=PRC.\"SITE_ID\" "
					+ " AND  DATE(P1.\"RECORD_APPLY_TIME\") = DATE(PRC.\"RECORD_APPLY_TIME\")  - " + diffDays
					+ "  order by P1.\"PRICE_ID\" DESC     limit 1 ),0) AS \"DIFF\", "
					+ " CUR.\"CURRENCY_CODE\",PRC.\"RECORD_CREATION_TIME\",PRC.\"RECORD_FETCH_TIME\" " + " FROM "
					+ schema + ".\"MS_PRODUCTS\" P " + " INNER JOIN " + schema
					+ ".\"MS_SITE\" S ON S.\"SITE_ID\"=P.\"SITE_ID\"  "
					+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" AS CUR ON S.\"COUNTRY\" = CUR.\"COUNTRY\"   "
					+ " LEFT OUTER JOIN " + schema + ".\"MS_PRICE_CHANGE\" PRC  "
					+ " ON PRC.\"SITE_ID\"=P.\"SITE_ID\"  " + " AND P.\"PRODUCT_NO\" = PRC.\"PRODUCT_NO\" "
					+ " WHERE 1=1  ";
			SQL += " AND P.\"ADRM_STATUS\" != 'D' " ; 
			
			//SQL += " AND P.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = P.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = P.\"SITE_ID\" ) ";

			if (siteIDs != null) {
				SQL += " AND P.\"SITE_ID\" IN (" + siteIDs + ")";
			}

			if (productName != null) {
				SQL += " AND UPPER(P.\"PRODUCT_NAME\") = UPPER('" + productName + "')";
			}

			SQL += " AND (PRC.\"RECORD_APPLY_TIME\" is NULL OR  "
					+ "	 PRC.\"RECORD_APPLY_TIME\" = ( SELECT P1.\"RECORD_APPLY_TIME\" FROM " + schema
					+ ".\"MS_PRICE_CHANGE\" P1 "
					+ "							   WHERE P1.\"SITE_ID\" = PRC.\"SITE_ID\" AND P1.\"PRODUCT_NO\" = PRC.\"PRODUCT_NO\" "
					+ "							  ORDER BY P1.\"RECORD_APPLY_TIME\" DESC LIMIT 1) " + "	 ) ";

			SQL += " AND PRC.\"PRICE_ID\" = ( " + "	SELECT max(\"PRICE_ID\") FROM " + schema
					+ ".\"MS_PRICE_CHANGE\" P3 " + "	WHERE P3.\"SITE_ID\" =  PRC.\"SITE_ID\"  "
					+ "	AND P3.\"PRODUCT_NO\" = P.\"PRODUCT_NO\" " + "	GROUP BY P3.\"PRICE_ID\" "
					+ "	ORDER BY P3.\"PRICE_ID\" DESC LIMIT 1 " + " ) ";

			SQL += " ORDER BY P.\"PRODUCT_NO\" , P.\"PRODUCT_NAME\" ";

			//System.out.println("getPriceDetails=" + SQL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ PriceDAO-getPriceDetails ::" + ex.getMessage());
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
	public JSONArray getPriceHistoryOld(String siteIDs, String fromDate, String toDate, String productCode) {
		JSONArray json = new JSONArray();
		JSONObject obj = null;
		try {
			String SQL = null;
			String siteID = null;
			String productName = null;
			int product = 0;
			JSONArray innerjson = null;
			Statement stmt = null;
			ResultSet rs = null;

			SQL = " SELECT PRD.\"PRODUCT_NO\",UPPER(PRD.\"PRODUCT_NAME\") AS \"PRODUCT_NAME\",S.\"SITE_ID\",PRD.\"UNIT\",PRD.\"TYPE\", "
					+ " S.\"SITE_NAME\",S.\"COUNTRY\",CNT.\"CURRENCY_CODE\" " + " FROM " + schema
					+ ".\"MS_PRODUCTS\" PRD " + " INNER JOIN " + schema
					+ ".\"MS_SITE\" S ON S.\"SITE_ID\"=PRD.\"SITE_ID\" "
					+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" CNT ON CNT.\"COUNTRY\" = S.\"COUNTRY\" " + " WHERE 1=1 ";
			SQL += " AND PRD.\"ADRM_STATUS\" != 'D' ";
			//SQL += " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) ";
			
			if (productCode != null) {
				SQL += " AND UPPER(PRD.\"PRODUCT_NAME\") = UPPER('" + productCode + "') ";
			}

			if (siteIDs != null) {
				SQL += " AND S.\"SITE_ID\" IN (" + siteIDs + ")";
			}

			SQL += " GROUP BY PRD.\"PRODUCT_NO\",PRD.\"PRODUCT_NAME\",S.\"SITE_ID\",PRD.\"UNIT\",PRD.\"TYPE\", "
					+ " S.\"SITE_ID\",S.\"SITE_NAME\",S.\"COUNTRY\",CNT.\"CURRENCY_CODE\" "
					+ " ORDER BY PRD.\"PRODUCT_NO\",S.\"SITE_ID\",PRD.\"PRODUCT_NAME\" ";

			//System.out.println(SQL);

			Statement prdstm = conn.createStatement();
			ResultSet prdrs = prdstm.executeQuery(SQL);
			while (prdrs.next()) {
				obj = new JSONObject();
				innerjson = new JSONArray();
				siteID = prdrs.getString("SITE_ID");
				product = prdrs.getInt("PRODUCT_NO");
				productName = prdrs.getString("PRODUCT_NAME");

				obj.put("SITE_ID", siteID);
				obj.put("SITE_NAME", prdrs.getString("SITE_NAME"));
				obj.put("PRODUCT_NO", product);
				obj.put("PRODUCT_NAME", productName);
				obj.put("PRICE_UNIT", prdrs.getString("UNIT"));
				obj.put("PRICE_TYPE", prdrs.getString("TYPE"));
				obj.put("COUNTRY", prdrs.getString("COUNTRY"));
				obj.put("CURRENCY_CODE", prdrs.getString("CURRENCY_CODE"));

				SQL = "	SELECT PRC.\"PRICE_ID\",PRC.\"NEW_PRICE\",PRC.\"PRICE_TYPE\",PRC.\"RECORD_APPLY_TIME\" "
						+ " FROM " + schema + ".\"MS_PRODUCTS\" PRD  " + " INNER JOIN " + schema
						+ ".\"MS_SITE\" S ON S.\"SITE_ID\"=PRD.\"SITE_ID\" " + " INNER JOIN " + schema
						+ ".\"MS_PRICE_CHANGE\" PRC ON S.\"SITE_ID\"=PRC.\"SITE_ID\"  and PRC.\"PRODUCT_NO\"=PRD.\"PRODUCT_NO\" " 
						+ " WHERE S.\"SITE_ID\" = '"
						+ siteID + "' AND PRD.\"PRODUCT_NO\" =  " + product
						+ " AND DATE(PRC.\"RECORD_APPLY_TIME\")::timestamp::date BETWEEN '" + fromDate + "' AND '"
						+ toDate + "' "
						+ " AND PRD.\"ADRM_STATUS\" != 'D' "
						//+ " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) "
						+ " GROUP BY PRC.\"PRICE_ID\",PRC.\"NEW_PRICE\",PRC.\"PRICE_TYPE\",PRC.\"RECORD_APPLY_TIME\" "
						+ " ORDER BY PRC.\"PRICE_ID\",PRC.\"NEW_PRICE\",PRC.\"PRICE_TYPE\",PRC.\"RECORD_APPLY_TIME\" ";

				//System.out.println(SQL);
				stmt = conn.createStatement();
				rs = stmt.executeQuery(SQL);
				innerjson = dbc.parseRS(rs);

				obj.put("PRICE_DATA", innerjson);
				json.add(obj);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ PriceDAO-getPriceHistory ::" + ex.getMessage());
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
	public JSONArray getPriceHistory(String siteIDs, String fromDate, String toDate, String productName, String country,
			String state, String region, String district, String city,String status) {
		JSONArray json = new JSONArray();
		try {
			String SQL = null;

			SQL = " SELECT ROW_NUMBER () OVER (ORDER BY MS.\"SITE_ID\") AS \"SNO\"  , MS.\"SITE_ID\", "
					+ " MS.\"SITE_NAME\", " + " UPPER(PRD.\"PRODUCT_NAME\") AS \"PRODUCT_NAME\", " + " TMP.\"GLOBAL_PRD_NO\", "
					+ " TMP.\"NEW_PRICE\" AS \"Product Unit Price\", " + " TMP.\"PRICE_TYPE\", "
					+ " TMP.\"STATUS\" AS \"Updated Status\", "
					+ " CASE WHEN TMP.\"PRICE_TYPE\" = 'BOS' THEN null ELSE TMP.\"RECORD_CREATION_TIME\" END AS \"HOS Price Sent Date\", "
					+ " TMP.\"RECORD_FETCH_TIME\" AS \"RO Price Received Date\", "
					+ " TMP.\"RECORD_APPLY_TIME\" AS \"RO Price Updated Date\", "
					+ " TMP.\"EFFECTIVE_FROM\" AS \"Effective From Date\" " + " FROM " + schema + ".\"MS_SITE\" MS "
					+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" CNT ON CNT.\"COUNTRY\" = MS.\"COUNTRY\" " + " INNER JOIN "
					+ schema + ".\"MS_PRODUCTS\" PRD ON PRD.\"SITE_ID\" = MS.\"SITE_ID\" " + " LEFT OUTER JOIN ( "
					+ "	SELECT \"SITE_ID\",\"PRODUCT_NO\",\"GLOBAL_PRD_NO\",\"NEW_PRICE\",\"PRICE_TYPE\",\"STATUS\",\"RECORD_CREATION_TIME\", "
					+ "	\"RECORD_FETCH_TIME\",\"RECORD_APPLY_TIME\",\"EFFECTIVE_FROM\",\"CREATED_BY\",\"CREATED_DATE\" "
					+ "	FROM " + schema + ".\"MS_PRICE_CHANGE\" " + "	WHERE \"SITE_ID\" IN (" + siteIDs + ") "
					+ " ) AS TMP " + " ON TMP.\"SITE_ID\" = MS.\"SITE_ID\" AND TMP.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" "
					+ " WHERE MS.\"SITE_ID\" IN (" + siteIDs + ") ";

			if (productName != null)
				SQL += " AND UPPER(PRD.\"PRODUCT_NAME\") = UPPER('" + productName + "')";

			if (country != null) {
				SQL += " AND MS.\"COUNTRY\" = '" + country + "' ";
			}

			if (state != null) {
				SQL += " AND MS.\"STATE\" = '" + state + "' ";
			}

			if (region != null) {
				SQL += " AND MS.\"REGION\" = '" + region + "' ";
			}

			if (district != null) {
				SQL += " AND MS.\"DISTRICT\" = '" + district + "' ";
			}

			if (city != null) {
				SQL += " AND MS.\"CITY\" = '" + city + "' ";
			}

			if (fromDate != null & toDate != null)
				SQL += " AND TMP.\"CREATED_DATE\"::DATE BETWEEN '" + fromDate + "' AND '" + toDate + "' ";

			if(status !=null) {
				if(status .equalsIgnoreCase("Mismatch"))
					SQL += " AND TMP.\"PRICE_TYPE\" = 'BOS' ";
				else if(status .equalsIgnoreCase("Updated"))
					SQL += " AND TMP.\"STATUS\" = 'APPLIED' ";
				else
					SQL += " AND TMP.\"STATUS\" = 'PENDING' ";
			}
			SQL += " AND PRD.\"ADRM_STATUS\" != 'D' ";
			//SQL += " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) ";
			
			SQL += " GROUP BY MS.\"SITE_ID\",MS.\"SITE_NAME\", "
					+ " PRD.\"PRODUCT_NAME\",TMP.\"GLOBAL_PRD_NO\",TMP.\"NEW_PRICE\",TMP.\"PRICE_TYPE\",TMP.\"STATUS\",TMP.\"RECORD_CREATION_TIME\", "
					+ " TMP.\"RECORD_FETCH_TIME\",TMP.\"RECORD_APPLY_TIME\",TMP.\"EFFECTIVE_FROM\",TMP.\"CREATED_BY\",TMP.\"CREATED_DATE\" "
					+ " ORDER BY TMP.\"EFFECTIVE_FROM\",TMP.\"RECORD_APPLY_TIME\",PRD.\"PRODUCT_NAME\" ";

			//System.out.println(SQL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);

			stmt = null;
			rs = null;

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ PriceDAO-getPriceHistory ::" + ex.getMessage());
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
	public JSONArray getRSP(String siteIDs, String state) {
		JSONArray json = new JSONArray();
		JSONObject obj = null;
		try {
			String SQL = null;
			if (state != null) {
				if (state.equalsIgnoreCase("Mismatch")) {
					SQL = " SELECT 'Mismatch' AS \"RSP\",COUNT(DISTINCT \"SITE_ID\") AS \"COUNT\" FROM " + schema
							+ ".\"MS_PRICE_CHANGE\" PRC  " + " WHERE 1 = 1   "
							+ " AND \"RECORD_APPLY_TIME\" =  (SELECT MAX(\"RECORD_APPLY_TIME\") FROM " + schema
							+ ".\"MS_PRICE_CHANGE\" PR   "
							+ "								 WHERE PR.\"SITE_ID\" = PRC.\"SITE_ID\" AND PR.\"PRODUCT_NO\" = PRC.\"PRODUCT_NO\"  "
							+ "								AND PR.\"PRICE_TYPE\" = PRC.\"PRICE_TYPE\"  "
							+ "								AND PR.\"PRICE_TYPE\" = 'BOS' "
							+ "								)  " + " AND \"PRICE_TYPE\" = 'BOS'  "
							+ " AND \"SITE_ID\" IN (" + siteIDs + ") ";
				} else if (state.equalsIgnoreCase("Updated")) {
					SQL = " SELECT 'Updated' AS \"RSP\", COUNT(*) AS \"COUNT\" FROM " + schema
							+ ".\"MS_PRICE_CHANGE\" PRC  " + " WHERE 1 = 1   "
							+ " AND \"RECORD_APPLY_TIME\" =  (SELECT MAX(\"RECORD_APPLY_TIME\") FROM " + schema
							+ ".\"MS_PRICE_CHANGE\" PR   "
							+ "								 WHERE PR.\"SITE_ID\" = PRC.\"SITE_ID\" AND PR.\"PRODUCT_NO\" = PRC.\"PRODUCT_NO\"  "
							+ "								AND PR.\"PRICE_TYPE\" = PRC.\"PRICE_TYPE\"  "
							+ "								)  " + " AND \"PRICE_TYPE\" = 'HOS'  "
							+ " AND \"STATUS\" = 'APPLIED'  " + " AND \"SITE_ID\" IN (" + siteIDs + ") ";
				} else if (state.equalsIgnoreCase("Pending")) {
					SQL = " SELECT 'Pending' AS \"RSP\", COUNT(*) AS \"COUNT\" FROM " + schema
							+ ".\"MS_PRICE_CHANGE\" PRC  " + " WHERE 1 = 1   "
							+ " AND \"RECORD_CREATION_TIME\" =  (SELECT MAX(\"RECORD_CREATION_TIME\") FROM " + schema
							+ ".\"MS_PRICE_CHANGE\" PR   "
							+ "								 WHERE PR.\"SITE_ID\" = PRC.\"SITE_ID\" AND PR.\"PRODUCT_NO\" = PRC.\"PRODUCT_NO\"  "
							+ "								AND PR.\"PRICE_TYPE\" = PRC.\"PRICE_TYPE\"  "
							+ "								)  " + " AND \"PRICE_TYPE\" = 'HOS'  "
							+ " AND \"STATUS\" = 'PENDING' " + " AND \"SITE_ID\" IN (" + siteIDs + ") ";
				} else {
					SQL = " SELECT 'Mismatch' AS \"RSP\",COUNT(DISTINCT \"SITE_ID\") AS \"COUNT\" FROM " + schema
							+ ".\"MS_PRICE_CHANGE\" PRC  " + " WHERE 1 = 1   "
							+ " AND \"RECORD_APPLY_TIME\" =  (SELECT MAX(\"RECORD_APPLY_TIME\") FROM " + schema
							+ ".\"MS_PRICE_CHANGE\" PR   "
							+ "								 WHERE PR.\"SITE_ID\" = PRC.\"SITE_ID\" AND PR.\"PRODUCT_NO\" = PRC.\"PRODUCT_NO\"  "
							+ "								AND PR.\"PRICE_TYPE\" = PRC.\"PRICE_TYPE\"  "
							+ "								AND PR.\"PRICE_TYPE\" = 'BOS' "
							+ "								)  " + " AND \"PRICE_TYPE\" = 'BOS'  "
							+ " AND \"SITE_ID\" IN (" + siteIDs + ") " + " UNION   "
							+ " SELECT 'Updated' AS \"RSP\", COUNT(*) AS \"COUNT\" FROM " + schema
							+ ".\"MS_PRICE_CHANGE\" PRC  " + " WHERE 1 = 1   "
							+ " AND \"RECORD_APPLY_TIME\" =  (SELECT MAX(\"RECORD_APPLY_TIME\") FROM " + schema
							+ ".\"MS_PRICE_CHANGE\" PR   "
							+ "								 WHERE PR.\"SITE_ID\" = PRC.\"SITE_ID\" AND PR.\"PRODUCT_NO\" = PRC.\"PRODUCT_NO\"  "
							+ "								AND PR.\"PRICE_TYPE\" = PRC.\"PRICE_TYPE\"  "
							+ "								)  " + " AND \"PRICE_TYPE\" = 'HOS'  "
							+ " AND \"STATUS\" = 'APPLIED'  " + " AND \"SITE_ID\" IN (" + siteIDs + ") " +
							// " GROUP BY PRC.\"SITE_ID\" " +
							" UNION  " + " SELECT 'Pending' AS \"RSP\", COUNT(*) AS \"COUNT\" FROM " + schema
							+ ".\"MS_PRICE_CHANGE\" PRC  " + " WHERE 1 = 1   "
							+ " AND \"RECORD_CREATION_TIME\" =  (SELECT MAX(\"RECORD_CREATION_TIME\") FROM " + schema
							+ ".\"MS_PRICE_CHANGE\" PR   "
							+ "								 WHERE PR.\"SITE_ID\" = PRC.\"SITE_ID\" AND PR.\"PRODUCT_NO\" = PRC.\"PRODUCT_NO\"  "
							+ "								AND PR.\"PRICE_TYPE\" = PRC.\"PRICE_TYPE\"  "
							+ "								)  " + " AND \"PRICE_TYPE\" = 'HOS'  "
							+ " AND \"STATUS\" = 'PENDING' " + " AND \"SITE_ID\" IN (" + siteIDs + ") " +
							// " GROUP BY PRC.\"SITE_ID\" " ;
							" ";
				}
			} else {
				SQL = " SELECT 'Mismatch' AS \"RSP\",COUNT(DISTINCT \"SITE_ID\") AS \"COUNT\" FROM " + schema
						+ ".\"MS_PRICE_CHANGE\" PRC  " + " WHERE 1 = 1   "
						+ " AND \"RECORD_APPLY_TIME\" =  (SELECT MAX(\"RECORD_APPLY_TIME\") FROM " + schema
						+ ".\"MS_PRICE_CHANGE\" PR   "
						+ "								 WHERE PR.\"SITE_ID\" = PRC.\"SITE_ID\" AND PR.\"PRODUCT_NO\" = PRC.\"PRODUCT_NO\"  "
						+ "								AND PR.\"PRICE_TYPE\" = PRC.\"PRICE_TYPE\"  "
						+ "								AND PR.\"PRICE_TYPE\" = 'BOS' "
						+ "								)  " + " AND \"PRICE_TYPE\" = 'BOS'  " + " AND \"SITE_ID\" IN ("
						+ siteIDs + ") " + " UNION   " + " SELECT 'Updated' AS \"RSP\", COUNT(*) AS \"COUNT\" FROM "
						+ schema + ".\"MS_PRICE_CHANGE\" PRC  " + " WHERE 1 = 1   "
						+ " AND \"RECORD_APPLY_TIME\" =  (SELECT MAX(\"RECORD_APPLY_TIME\") FROM " + schema
						+ ".\"MS_PRICE_CHANGE\" PR   "
						+ "								 WHERE PR.\"SITE_ID\" = PRC.\"SITE_ID\" AND PR.\"PRODUCT_NO\" = PRC.\"PRODUCT_NO\"  "
						+ "								AND PR.\"PRICE_TYPE\" = PRC.\"PRICE_TYPE\"  "
						+ "								)  " + " AND \"PRICE_TYPE\" = 'HOS'  "
						+ " AND \"STATUS\" = 'APPLIED'  " + " AND \"SITE_ID\" IN (" + siteIDs + ") " +
						// " GROUP BY PRC.\"SITE_ID\" " +
						" UNION  " + " SELECT 'Pending' AS \"RSP\", COUNT(*) AS \"COUNT\" FROM " + schema
						+ ".\"MS_PRICE_CHANGE\" PRC  " + " WHERE 1 = 1   "
						+ " AND \"RECORD_CREATION_TIME\" =  (SELECT MAX(\"RECORD_CREATION_TIME\") FROM " + schema
						+ ".\"MS_PRICE_CHANGE\" PR   "
						+ "								 WHERE PR.\"SITE_ID\" = PRC.\"SITE_ID\" AND PR.\"PRODUCT_NO\" = PRC.\"PRODUCT_NO\"  "
						+ "								AND PR.\"PRICE_TYPE\" = PRC.\"PRICE_TYPE\"  "
						+ "								)  " + " AND \"PRICE_TYPE\" = 'HOS'  "
						+ " AND \"STATUS\" = 'PENDING' " + " AND \"SITE_ID\" IN (" + siteIDs + ") " +
						// " GROUP BY PRC.\"SITE_ID\" " ;
						" ";
			}
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			obj = new JSONObject();
			while (rs.next()) {
				obj.put(rs.getString("RSP"), rs.getString("COUNT"));
			}
			json.add(obj);
			stmt = null;
			rs = null;

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ PriceDAO-getRSP ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	public boolean setPriceDetails(String userId, String roleId, List<String> siteIDList, String productName,
			float newPrice, String effectiveFrom,String siteID) {
		// APPLIED
//			String SQL = "insert into " + schema + ".\"MS_PRICE_CHANGE\" " + 
//					"(\"SITE_ID\",\"PRODUCT_NO\",\"GLOBAL_PRD_NO\",\"NEW_PRICE\", " + 
//					" \"EFFECTIVE_FROM\",\"STATUS\",\"PRICE_TYPE\",\"CREATED_BY\") " + 
//					"values(?,?,'1683100',?,?,'APPLIED','HOS','HOS')";
		String SQL = "INSERT INTO " + schema + ".\"MS_PRICE_CHANGE\" "
				+ "(\"SITE_ID\",\"PRODUCT_NO\",\"GLOBAL_PRD_NO\", " + "\"NEW_PRICE\",\"EFFECTIVE_FROM\",\"STATUS\", "
				+ " \"PRICE_TYPE\",\"CREATED_BY\",\"CREATED_DATE\") " + "SELECT \"SITE_ID\",\"PRODUCT_NO\",\"GLOBAL_PRD_CODE\", "
				+ "?,?::TIMESTAMP,'PENDING', " + "'HOS','HOS',now()  " + "FROM " + schema + ".\"MS_PRODUCTS\" PRD "
				+ "WHERE PRD.\"SITE_ID\"=? AND UPPER(PRD.\"PRODUCT_NAME\")= ? " 
				+ " AND PRD.\"ADRM_STATUS\" != 'D' ";
				//+ "AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 " 
				//+ "WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" )";
		//System.out.println("setPriceDetails=" + SQL);
		try {

			//System.out.println(newPrice);
			//System.out.println(effectiveFrom);
			//System.out.println(siteIDList);
			//System.out.println(productName);
			//System.out.println(siteID);
			if(siteIDList!=null) {
				//System.out.println("siteIDList not null" + SQL);
				PreparedStatement statement = conn.prepareStatement(SQL);
				for (String eachSiteID : siteIDList) {
					statement.setFloat(1, newPrice);
					statement.setString(2, effectiveFrom);
					statement.setString(3, eachSiteID);
					statement.setString(4, productName);
					statement.addBatch();
				}
				statement.executeBatch();
			} else if(siteID!=null) {
				SQL = "INSERT INTO " + schema + ".\"MS_PRICE_CHANGE\" "
						+ "(\"SITE_ID\",\"PRODUCT_NO\",\"GLOBAL_PRD_NO\", " + "\"NEW_PRICE\",\"EFFECTIVE_FROM\",\"STATUS\", "
						+ " \"PRICE_TYPE\",\"CREATED_BY\",\"CREATED_DATE\") " + "SELECT \"SITE_ID\",\"PRODUCT_NO\",\"GLOBAL_PRD_CODE\", "
						+ "?,?::TIMESTAMP,'PENDING', " + "'BOS','BOS',now()" + "FROM " + schema + ".\"MS_PRODUCTS\" PRD "
						+ "WHERE PRD.\"SITE_ID\"=? AND UPPER(PRD.\"PRODUCT_NAME\")= ? " 
						+ " AND PRD.\"ADRM_STATUS\" != 'D' ";
						//+ "AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 " 
						//+ "WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" )";
				//System.out.println("SiteID not null" + SQL);
				PreparedStatement statement = conn.prepareStatement(SQL);
				statement.setFloat(1, newPrice);
				statement.setString(2, effectiveFrom);
				statement.setString(3, siteID);
				statement.setString(4, productName);
				statement.execute();
			}else {
				//System.out.println("No Site Details!.....");
			}
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ PriceDAO-getPriceDetails ::" + ex.getMessage());
			return false;
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public boolean setBulkPriceDetails(String userId, String roleId, List<PriceBO> bulkPrice) {

		String SQL = "INSERT INTO " + schema + ".\"MS_PRICE_CHANGE\"  "
				+ "(\"SITE_ID\",\"PRODUCT_NO\",\"GLOBAL_PRD_NO\",  " + "\"NEW_PRICE\",\"EFFECTIVE_FROM\",\"STATUS\",  "
				+ " \"PRICE_TYPE\",\"CREATED_BY\")  " + "SELECT \"SITE_ID\",\"PRODUCT_NO\",\"GLOBAL_PRD_CODE\",  "
				+ "?, (Select ? ::timestamptz at time zone 'UTC'),'PENDING',  " + "'HOS','HOS'   " + "FROM " + schema
				+ ".\"MS_PRODUCTS\"   " + "WHERE \"SITE_ID\"=? AND \"PRODUCT_NAME\"= ?"
				+ " AND PRD.\"ADRM_STATUS\" != 'D' ";
				//+ "AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 " 
				//+ "WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" )";
		// //System.out.println("setPriceDetails="+SQL);
		try {

			PreparedStatement statement = conn.prepareStatement(SQL);
			for (PriceBO price : bulkPrice) {
				statement.setFloat(1, price.getNewPrice());
				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
				statement.setTimestamp(2, Timestamp.valueOf(price.getEffectiveFrom()), cal);

				// statement.setString(2, price.getEffectiveFrom());
				statement.setString(3, price.getSiteID());
				statement.setString(4, price.getProductName());
				statement.addBatch();
			}
			statement.executeBatch();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ PriceDAO-getPriceDetails ::" + ex.getMessage());
			return false;
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public JSONArray getPriceROList(String siteIDs, String country, String state, String city, String division,
			String district, String subDistrict, String region, String productName) {
		JSONArray json = new JSONArray();
		try {
			/*
			 * String SQL = "  SELECT MS.\"SITE_ID\",MS.\"SITE_NAME\",  " +
			 * " MPC.\"NEW_PRICE\",MPC.\"STATUS\",MPC.\"EFFECTIVE_FROM\",  " +
			 * " MPC.\"PRICE_TYPE\", PRD.\"PRODUCT_NAME\"  " + " FROM " + schema +
			 * ".\"MS_SITE\" MS    " + " INNER JOIN " + schema +
			 * ".\"MS_RO_LOCATION\" MRL ON MRL.\"COUNTRY\" = MS.\"COUNTRY\"   " +
			 * " INNER JOIN " + schema +
			 * ".\"MS_PRICE_CHANGE\" MPC ON MS.\"SITE_ID\" = MPC.\"SITE_ID\"  " +
			 * " INNER JOIN " + schema +
			 * ".\"MS_PRODUCTS\" PRD ON MS.\"SITE_ID\" = PRD.\"SITE_ID\"  " + " WHERE 1=1  "
			 * ;
			 */

			String SQL = " SELECT MS.\"SITE_ID\",MS.\"SITE_NAME\",  " + " MPC.\"NEW_PRICE\",  " + " MPC.\"STATUS\",  "
					+ " MPC.\"EFFECTIVE_FROM\",  " + " MPC.\"PRICE_TYPE\", UPPER(PRD.\"PRODUCT_NAME\") AS \"PRODUCT_NAME\",  "
					+ " MPC.\"RECORD_APPLY_TIME\"  " + " FROM " + schema + ".\"MS_SITE\" MS  " + " INNER JOIN " + schema
					+ ".\"MS_RO_LOCATION\" MRL ON MRL.\"COUNTRY\" = MS.\"COUNTRY\"  " + " INNER JOIN " + schema
					+ ".\"MS_PRODUCTS\" PRD ON MS.\"SITE_ID\" = PRD.\"SITE_ID\"  " + " LEFT OUTER JOIN (  "
					+ " SELECT \"SITE_ID\",\"PRODUCT_NO\",\"NEW_PRICE\",\"STATUS\",\"EFFECTIVE_FROM\",\"RECORD_APPLY_TIME\",\"PRICE_TYPE\" FROM  "
					+ schema + ".\"MS_PRICE_CHANGE\"  TEMP "
					+ " WHERE TEMP.\"RECORD_APPLY_TIME\" = (SELECT MAX(\"RECORD_APPLY_TIME\") FROM " + schema
					+ ".\"MS_PRICE_CHANGE\" TMP WHERE   "
					+ "							 TEMP.\"SITE_ID\" = TMP.\"SITE_ID\" AND TEMP.\"PRODUCT_NO\" = TMP.\"PRODUCT_NO\"  "
					+ "							 AND TMP.\"RECORD_APPLY_TIME\" IS NOT NULL)  "
					+ " )AS MPC ON MPC.\"SITE_ID\" = MS.\"SITE_ID\" AND MPC.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\"  "
					+ " WHERE 1=1     ";
			SQL += " AND PRD.\"ADRM_STATUS\" != 'D' ";
			//SQL += " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) ";

			if (productName != null) {
				SQL += " AND UPPER(PRD.\"PRODUCT_NAME\") = UPPER('" + productName + "') ";
			}

			if (siteIDs != null)
				SQL += " AND MS.\"SITE_ID\" IN (" + siteIDs + ") ";

			if (country != null)
				SQL += " AND MRL.\"COUNTRY\" = '" + country + "' ";

			if (state != null)
				SQL += " AND MRL.\"STATE\" = '" + state + "' ";

			if (city != null)
				SQL += " AND MRL.\"CITY\" = '" + city + "' ";

			if (region != null)
				SQL += " AND MRL.\"REGION\" = '" + region + "' ";

			if (division != null)
				SQL += " AND MRL.\"DIVISION\" = '" + division + "' ";

			if (district != null) {
				SQL += " AND MRL.\"DISTRICT\" = '" + district + "' ";

				if (subDistrict != null)
					SQL += " AND MRL.\"SUB_DISTRICT\" = '" + subDistrict + "' ";
			}

			SQL += " GROUP BY MS.\"SITE_ID\",MS.\"SITE_NAME\",MPC.\"NEW_PRICE\",MPC.\"STATUS\",MPC.\"EFFECTIVE_FROM\",MPC.\"PRICE_TYPE\", PRD.\"PRODUCT_NAME\",\"RECORD_APPLY_TIME\"  "
					+ " ORDER BY MS.\"SITE_ID\",PRD.\"PRODUCT_NAME\" ";

			System.out.println("get price list " + SQL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ UtilDAO-getROList ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	
	public JSONArray getROCurrentPriceList(String siteIDs, String country, String productName) {
		JSONArray json = new JSONArray();
		try {
			String SQL = " SELECT MS.\"SITE_ID\",MS.\"SITE_NAME\",  " + " MPC.\"NEW_PRICE\",  " + " MPC.\"STATUS\",  "
					+ " MPC.\"EFFECTIVE_FROM\",  " + " MPC.\"PRICE_TYPE\", UPPER(PRD.\"PRODUCT_NAME\") AS \"PRODUCT_NAME\",  "
					+ " MPC.\"RECORD_APPLY_TIME\"  " + " FROM " + schema + ".\"MS_SITE\" MS  " + " INNER JOIN " + schema
					+ ".\"MS_RO_LOCATION\" MRL ON MRL.\"COUNTRY\" = MS.\"COUNTRY\"  " + " INNER JOIN " + schema
					+ ".\"MS_PRODUCTS\" PRD ON MS.\"SITE_ID\" = PRD.\"SITE_ID\"  " + " LEFT OUTER JOIN (  "
					+ " SELECT \"SITE_ID\",\"PRODUCT_NO\",\"NEW_PRICE\",\"STATUS\",\"EFFECTIVE_FROM\",\"RECORD_APPLY_TIME\",\"PRICE_TYPE\" FROM  "
					+ schema + ".\"MS_PRICE_CHANGE\"  TEMP "
					+ " WHERE TEMP.\"RECORD_APPLY_TIME\" = (SELECT MAX(\"RECORD_APPLY_TIME\") FROM " + schema
					+ ".\"MS_PRICE_CHANGE\" TMP WHERE   "
					+ "							 TEMP.\"SITE_ID\" = TMP.\"SITE_ID\" AND TEMP.\"PRODUCT_NO\" = TMP.\"PRODUCT_NO\"  "
					+ "							 AND TMP.\"RECORD_APPLY_TIME\" IS NOT NULL)  "
					+ " )AS MPC ON MPC.\"SITE_ID\" = MS.\"SITE_ID\" AND MPC.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\"  "
					+ " WHERE 1=1   AND MPC.\"NEW_PRICE\" is not null  ";
			SQL += " AND PRD.\"ADRM_STATUS\" != 'D' ";
			//SQL += " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) ";

			if (productName != null) {
				SQL += " AND UPPER(PRD.\"PRODUCT_NAME\") = UPPER('" + productName + "') ";
			}

			if (siteIDs != null)
				SQL += " AND MS.\"SITE_ID\" IN (" + siteIDs + ") ";

			if (country != null)
				SQL += " AND MRL.\"COUNTRY\" = '" + country + "' ";

			SQL += " GROUP BY MS.\"SITE_ID\",MS.\"SITE_NAME\",MPC.\"NEW_PRICE\",MPC.\"STATUS\",MPC.\"EFFECTIVE_FROM\",MPC.\"PRICE_TYPE\", PRD.\"PRODUCT_NAME\",\"RECORD_APPLY_TIME\"  "
			+ " ORDER BY MS.\"SITE_ID\",PRD.\"PRODUCT_NAME\" ";

			System.out.println("get getROCurrentPriceList =" + SQL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ PriceDAO-getROCurrentPriceList ::" + ex.getMessage());
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
