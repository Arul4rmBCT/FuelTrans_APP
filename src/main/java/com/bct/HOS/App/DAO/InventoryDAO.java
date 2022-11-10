package com.bct.HOS.App.DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import com.bct.HOS.App.utils.DBConnector;
import com.bct.HOS.App.utils.LVMConnector;
import com.google.gson.JsonObject;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class InventoryDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;
	private long unitConversion = 0;

	public InventoryDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
		unitConversion = dbc.getUnitConversion();
	}

	/**
	 * 
	 * @return
	 */
	public JSONArray getInventorySummary(String userId,String siteIDs,String fromDate,String toDate,String country,boolean grouping) {
		JSONArray json = new JSONArray();
		try {
			String SQL = null;

			if(fromDate!=null && toDate!=null) {
				if(grouping) {
					SQL = " SELECT UPPER(\"PRODUCT_GROUP\") AS \"PRODUCT_NAME\" "
							+ " ,ROUND(SUM(\"VOLUME\") / 1000 ,2) AS \"VOLUME\"  ";
				}else {
					SQL = " SELECT UPPER(\"PRODUCT_NAME\") AS \"PRODUCT_NAME\" "
							+ " ,ROUND(SUM(\"VOLUME\") / 1000 ,\"DEC_VAL\") AS \"VOLUME\", CUR.\"CURRENCY_CODE\"  ";
				}
				SQL+=   " FROM " + schema + ".\"INVENTORY\"  INV " + " INNER JOIN " + schema
						+ ".\"MS_TANK\" MT ON MT.\"TANK_NO\" =  INV.\"TANK_NO\" AND MT.\"SITE_ID\"=INV.\"SITE_ID\"  "
						+ " INNER JOIN " + schema
						+ ".\"MS_PRODUCTS\" PRD ON PRD.\"PRODUCT_NO\" = MT.\"PRODUCT_NO\"  AND  INV.\"SITE_ID\" = PRD.\"SITE_ID\"  "
						+ " INNER JOIN " + schema + ".\"MS_SITE\" MST ON INV.\"SITE_ID\"=MST.\"SITE_ID\"    "
						+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" AS CUR ON MST.\"COUNTRY\" = CUR.\"COUNTRY\"  "
						+ " INNER JOIN \"BCT\".\"COUNTRY_PRODUCT_CONFIG\" CPC ON CPC.\"PRODUCT\"=PRD.\"PRODUCT_NAME\" "
						+ " INNER JOIN \"BCT\".user_sites US ON US.site_id = INV.\"SITE_ID\" " 
						+ " WHERE 1 = 1    "
						+ " AND CPC.\"COUNTRY_ID\"=CUR.\"COUNTRY_ID\" "
						+ " AND INV.\"INVENTORY_DATE\"::DATE BETWEEN ('"+fromDate+"') AND ('"+toDate+"') " 
						+ " AND INV.\"INVENTORY_DATE\" = ( SELECT MAX(TMP.\"INVENTORY_DATE\") FROM "
						+ schema + ".\"INVENTORY\" TMP WHERE TMP.\"SITE_ID\" = INV.\"SITE_ID\" ) ";
						
				SQL += " AND MT.\"MODIFIED_TIME\" = (SELECT MAX(\"MODIFIED_TIME\") FROM "+schema+".\"MS_TANK\" TNK1 WHERE TNK1.\"SITE_ID\" = INV.\"SITE_ID\"  AND TNK1.\"TANK_NO\"=INV.\"TANK_NO\" ) ";
			}else {
				if(grouping) {
					SQL = " SELECT UPPER(\"PRODUCT_GROUP\") AS \"PRODUCT_NAME\" "
							+ " ,ROUND(SUM(\"VOLUME\") / 1000 ,2) AS \"VOLUME\"  ";
				}else {
					SQL = " SELECT UPPER(\"PRODUCT_NAME\") AS \"PRODUCT_NAME\" "
							+ " ,ROUND(SUM(\"VOLUME\") / 1000 ,\"DEC_VAL\") AS \"VOLUME\", CUR.\"CURRENCY_CODE\"  ";
				}
				SQL+= " FROM " + schema + ".\"INVENTORY\"  INV " + " INNER JOIN " + schema
						+ ".\"MS_TANK\" MT ON MT.\"TANK_NO\" =  INV.\"TANK_NO\" AND MT.\"SITE_ID\"=INV.\"SITE_ID\"  "
						+ " INNER JOIN " + schema
						+ ".\"MS_PRODUCTS\" PRD ON PRD.\"PRODUCT_NO\" = MT.\"PRODUCT_NO\"  AND  INV.\"SITE_ID\" = PRD.\"SITE_ID\"  "
						+ " INNER JOIN " + schema + ".\"MS_SITE\" MST ON INV.\"SITE_ID\"=MST.\"SITE_ID\"    "
						+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" AS CUR ON MST.\"COUNTRY\" = CUR.\"COUNTRY\"  "
						+ " INNER JOIN \"BCT\".\"COUNTRY_PRODUCT_CONFIG\" CPC CPC.\"PRODUCT\"=PRD.\"PRODUCT_NAME\" "
						+ " INNER JOIN \"BCT\".user_sites US ON US.site_id = INV.\"SITE_ID\" " 
						+ " WHERE 1 = 1    "
						+ " AND INV.\"INVENTORY_DATE\"::DATE BETWEEN ('"+fromDate+"') AND ('"+toDate+"') "
						+ " AND INV.\"INVENTORY_DATE\" = ( SELECT MAX(TMP.\"INVENTORY_DATE\") FROM "
						+ schema + ".\"INVENTORY\" TMP WHERE TMP.\"SITE_ID\" = INV.\"SITE_ID\") ";
				SQL += " AND MT.\"MODIFIED_TIME\" = (SELECT MAX(\"MODIFIED_TIME\") FROM "+schema+".\"MS_TANK\" TNK1 WHERE TNK1.\"SITE_ID\" = INV.\"SITE_ID\"  AND TNK1.\"TANK_NO\"=INV.\"TANK_NO\" ) ";
			}
			SQL += " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = MT.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = INV.\"SITE_ID\" ) "
					+ " AND US.user_id = '"+userId+"' ";
			
			if (siteIDs != null) {
				SQL += " AND INV.\"SITE_ID\" IN (" + siteIDs + ")";
			}
			
			if (country != null)
				SQL += " AND US.country = '" + country + "' ";

			if(grouping) {
				SQL += " GROUP BY \"PRODUCT_GROUP\" ";
			}else {
				SQL += " GROUP BY \"PRODUCT_NAME\",\"DEC_VAL\" , CUR.\"CURRENCY_CODE\" ";
			}


//			System.out.println("\n\n\n\n");
//			System.out.println("getInventorySummary>>>>>>>>>>>>>>>>>>"+SQL);
//			System.out.println("\n\n\n\n");

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ InventoryDAO-getInventorySummary ::" + ex.getMessage());
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
	 * @param siteId
	 * @return
	 */
	public int getROStatus(String userId,String siteIDs,String country) {
		int intSites = 0;
		try {
			if(siteIDs==null)
				siteIDs= getUserSites(userId,country);
			
//			String SQL = "SELECT SiteID, IF(Sts = 1) then 'Online' else 'Offline' as Status, "
//					+ " format_time(timestamp(string(int(LastUpdated/1000))), \"yyyy-MM-dd, HH:mm:ss\") as LastUpdated "
//					+ " FROM SiteStatus";
			JsonObject req=new JsonObject();
			String response = new LVMConnector().LVMConnect("LVM_SITE_URL", null,req);
			//System.out.println("\n\n\n");
			//System.out.println("response is :: " + response);
			//System.out.println(">>"+siteIDs);
			//System.out.println("\n\n\n");

			JSONArray outerArray = (JSONArray) JSONSerializer.toJSON(response);
			int loop = 0;
			int count = outerArray.size();
			
			String siteID = null;
			for (Object js : outerArray) {
				//if (loop > 0 && loop < (count - 1)) {

					JSONObject arrJson = (JSONObject) js;
					JSONObject dataJson = (JSONObject) arrJson.get("data");
					siteID = (String) dataJson.get("SiteID");
					if (siteID != null) {
						if(siteIDs.contains(siteID)) {
							intSites++;
						}
					}
				//}
				//loop++;
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
		return intSites;
	}
	
	/**
	 * 
	 * @param userId
	 * @return
	 */
	private String getUserSites(String userId,String country) {
		String siteIds = null;
		try {
			StringBuffer sb = new StringBuffer();
			String SQL = " SELECT site_id as \"SITE_ID\" FROM \"BCT\".user_sites "
					+ " WHERE 1 = 1 "
					+ " AND user_id = '"+userId+"' "
					+ " AND site_id NOT LIKE 'MA%'  ";
			if(country!=null) {
				SQL += " AND country = '" +country+"' ";
			}
			
			System.out.println(SQL);
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			while(rs.next()) {
				sb.append(rs.getString("SITE_ID"));
				sb.append(",");
			}
			siteIds = sb.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return siteIds;
	}
	
	public int getInventoryActive(String userId ,String siteIDs,String fromDate,String toDate,String country) {
		int siteReceivedCount= 0;
		try {
			String SQL = " SELECT INV.\"SITE_ID\" FROM "+schema+".\"INVENTORY\" INV "
					+ " INNER JOIN \"BCT\".user_sites US ON US.site_id = INV.\"SITE_ID\" "
					+ " WHERE 1 = 1 "
					+ " AND US.user_id = '"+userId+"' "
					+ " AND INV.\"SITE_ID\" NOT LIKE 'MA%'";					
			if (siteIDs != null) {
					SQL += " AND INV.\"SITE_ID\" IN ("+siteIDs+")  ";
			}
			
			if(country!=null) {
				SQL += " AND US.country = '" +country+"' ";
			}
			
			SQL +=" AND \"INVENTORY_DATE\"::DATE BETWEEN ('"+fromDate+"') AND ('"+toDate+"') " + 
					" GROUP BY INV.\"SITE_ID\" ";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			while(rs.next()) {
				siteReceivedCount++;
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ InventoryDAO-getInventorySummary ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return siteReceivedCount;
	}
	
		
	
	

	/**
	 * 
	 * @return
	 */
	public JSONArray getInventoryDetails(String siteIDs) {
		JSONArray json = new JSONArray();
		try {
			String SQL = "SELECT \"SITE_ID\",\"INVENTORY_ID\",\"INVENTORY_DATE\",\"TANK_NO\",\"VOLUME\", "
					+ "\"TCVOLUME\",\"ULLAGE\",\"HEIGHT\",\"WATER\",\"WATER_HEIGHT\",\"DENSITY_STATUS\", "
					+ "\"DENSITY_ACTUAL\",\"DENSITY_AT15DEG\",\"CREATED_BY\",\"CREATED_DATE\", "
					+ "\"MODIFIED_BY\",\"MODIFIED_DATE\" " + "FROM " + schema + ".\"INVENTORY\"  "
					+ "WHERE ROW(\"SITE_ID\",\"INVENTORY_DATE\") IN ( "
					+ "	SELECT \"SITE_ID\",MAX(\"INVENTORY_DATE\") FROM " + schema + ".\"INVENTORY\" " + "		WHERE "
					+ "		\"SITE_ID\" IN (" + siteIDs + ") " + "		GROUP BY \"SITE_ID\" " + ") ";

			if (siteIDs != null) {
				SQL += " AND \"SITE_ID\" IN (" + siteIDs + ")";
			}

			SQL += " ORDER BY \"ID\" DESC";

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ InventoryDAO-getInventoryDetails ::" + ex.getMessage());
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
	public JSONArray getInvTransactions(String siteIDs, String fromDate, String toDate, String productName,
			String country, String state, String region, String district, String subDistrict, String city,
			String division, String tank,boolean latestRecord) {
		JSONArray json = new JSONArray();
		try {

			String SQL = "   SELECT ROW_NUMBER () OVER (ORDER BY INV.\"SITE_ID\") AS \"SNO\"  , INV.\"SITE_ID\", MST.\"SITE_NAME\", "
					+
					// + ",MST.\"SITE_TYPE\",MST.\"DEALER_NAME\",MST.\"CLIENT_NAME\", " +
					// "
					// MST.\"REGION\",MST.\"COUNTRY\",MST.\"SITE_STATUS\",MST.\"LAST_CONNECTION_TIME\",
					// " +
					" SUM(\"VOLUME\") AS \"VOLUME\" ,  " +
					// " MTN.\"PRODUCT_NO\""
					" UPPER(PRD.\"PRODUCT_NAME\") AS \"PRODUCT_NAME\","
					// + " INV.\"DENSITY_ACTUAL\", INV.\"DENSITY_AT15DEG\" ,"
					+ " INV.\"INVENTORY_DATE\" , INV.\"TANK_NO\"    " + " FROM " + schema + ".\"INVENTORY\" INV                   "
					+ " INNER JOIN " + schema + ".\"MS_SITE\" MST ON INV.\"SITE_ID\"=MST.\"SITE_ID\"  "
					+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" AS CUR ON MST.\"COUNTRY\" = CUR.\"COUNTRY\"     "
					+ " INNER JOIN " + schema
					+ ".\"MS_TANK\" MTN ON INV.\"TANK_NO\"=MTN.\"TANK_NO\" AND MTN.\"SITE_ID\"=INV.\"SITE_ID\"       "
					+ " INNER JOIN " + schema
					+ ".\"MS_PRODUCTS\" PRD ON MTN.\"PRODUCT_NO\"=PRD.\"PRODUCT_NO\" AND PRD.\"SITE_ID\"=MST.\"SITE_ID\"        "
					+ " WHERE 1=1  ";

			SQL += " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) ";
			SQL += " AND MTN.\"MODIFIED_TIME\" = (SELECT MAX(\"MODIFIED_TIME\") FROM "+schema+".\"MS_TANK\" TNK1 WHERE TNK1.\"SITE_ID\" = MTN.\"SITE_ID\"  AND TNK1.\"TANK_NO\"=MTN.\"TANK_NO\" ) ";
			
			if(latestRecord) {
				SQL += " AND INV.\"INVENTORY_DATE\" IN (SELECT MAX(\"INVENTORY_DATE\") FROM " + schema
						+ ".\"INVENTORY\" WHERE \"SITE_ID\" = INV.\"SITE_ID\"  AND \"INVENTORY_DATE\"::timestamp::date BETWEEN ('"+fromDate+"') AND ('"+toDate+"')   ) ";
			}
			
			if (siteIDs != null) {
				SQL += " AND INV.\"SITE_ID\" IN (" + siteIDs + ")";
			}

			if (fromDate != null && toDate != null) {
				SQL += " AND INV.\"INVENTORY_DATE\"::timestamp::date BETWEEN ('" + fromDate + "') AND ('" + toDate
						+ "')";
			} else {
				SQL += " AND INV.\"INVENTORY_DATE\" IN (SELECT MAX(\"INVENTORY_DATE\") FROM " + schema
						+ ".\"INVENTORY\" WHERE \"SITE_ID\" = INV.\"SITE_ID\" "
						+ "							GROUP BY \"SITE_ID\" ) ";
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

			if (tank != null) {
				SQL += " AND MTN.\"TANK_NO\" = " + tank;
			}

			// GROUP BY
			SQL += "  GROUP BY MTN.\"PRODUCT_NO\",INV.\"SITE_ID\", MST.\"SITE_NAME\",MST.\"SITE_TYPE\",MST.\"DEALER_NAME\",MST.\"CLIENT_NAME\",    "
					+ " MST.\"REGION\",MST.\"COUNTRY\",MST.\"SITE_STATUS\",MST.\"LAST_CONNECTION_TIME\" , \"DEC_VAL\" ,  INV.\"DENSITY_ACTUAL\", INV.\"DENSITY_AT15DEG\" ,  "
					+ " PRD.\"PRODUCT_NAME\",PRD.\"PRODUCT_NO\", INV.\"INVENTORY_DATE\" ,INV.\"TANK_NO\" ";

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

			if (tank != null) {
				SQL += " , MTN.\"TANK_NO\" ";
			}

			SQL += " ORDER BY INV.\"SITE_ID\",PRD.\"PRODUCT_NO\" , INV.\"INVENTORY_DATE\" ";

			//System.out.println("getInvTransactions >>> " + SQL);

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ InventoryDAO-getInvTransactions ::" + ex.getMessage());
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
	public JSONArray getInventoryByFilter(String siteIDs, String timePeriodType, String recordLimit, String fromDate,
			String toDate, String productName) {
		JSONArray json = new JSONArray();
		try {
			String[] siteIdCount = siteIDs.split(",");
			int siteCount = 0;

			String SQL = " SELECT ROW_NUMBER () OVER (ORDER BY INV.\"SITE_ID\") AS \"SNO\"  , INV.\"SITE_ID\", MST.\"SITE_NAME\",MST.\"SITE_TYPE\",MST.\"DEALER_NAME\",MST.\"CLIENT_NAME\", "
					+ " MST.\"REGION\",MST.\"COUNTRY\",MST.\"SITE_STATUS\",to_char(MST.\"LAST_CONNECTION_TIME\", 'YYYY-MM-DD HH:MI:SS') AS \"LAST_CONNECTION_TIME\" ,";

			if (timePeriodType.equalsIgnoreCase("WEEKLY")) {
				SQL += " DATE_TRUNC('WEEK',INV.\"INVENTORY_DATE\"::DATE) ::DATE - 1 AS \"WEEKLY\", "
						+ " (extract('day' from date_trunc('week', \"INVENTORY_DATE\") - date_trunc('week', date_trunc('month', \"INVENTORY_DATE\"))) / 7 + 1)  AS \"WEEK\",";
			} else if (timePeriodType.equalsIgnoreCase("MONTHLY")) {
				SQL += " DATE_TRUNC('MONTH',INV.\"INVENTORY_DATE\"::DATE) AS \"MONTHLY\", "
						+ " TO_CHAR(TO_DATE (date_part('month',\"INVENTORY_DATE\")::text, 'MM'), 'Month') AS \"MONTH\",";
			} else {
				//System.out.println("DATE Filter!..");
			}

			// Check for include PRICE - for single RO
			if (siteIdCount != null) {
				siteCount = siteIdCount.length;
				if (siteCount == 1) {
					SQL += " PRD.\"PRICE\", ROUND( (SUM(\"VOLUME\") * PRD.\"PRICE\" ), \"DEC_VAL\") AS \"TOTAL_VALUE\",CUR.\"CURRENCY_CODE\",";
				}
			}

			SQL += " ROUND(SUM(\"VOLUME\") / " + unitConversion
					+ " ,\"DEC_VAL\") AS \"VOLUME\",MTN.\"PRODUCT_NO\",UPPER(PRD.\"PRODUCT_NAME\") AS \"PRODUCT_NAME\" "
					//+ " , INV.\"DENSITY_ACTUAL\", INV.\"DENSITY_AT15DEG\"	" 
					+ " FROM " + schema + ".\"INVENTORY\" INV "
					+ "			INNER JOIN " + schema + ".\"MS_SITE\" MST ON INV.\"SITE_ID\"=MST.\"SITE_ID\" "
					+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" AS CUR ON MST.\"COUNTRY\" = CUR.\"COUNTRY\" "
					+ "			INNER JOIN " + schema
					+ ".\"MS_TANK\" MTN ON INV.\"TANK_NO\"=MTN.\"TANK_NO\" AND MTN.\"SITE_ID\"=INV.\"SITE_ID\" "
					+ "			INNER JOIN " + schema
					+ ".\"MS_PRODUCTS\" PRD ON MTN.\"PRODUCT_NO\"=PRD.\"PRODUCT_NO\" AND PRD.\"SITE_ID\"=MST.\"SITE_ID\" "
					+ "			WHERE 1=1 ";
			SQL += " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) ";
			SQL += " AND MTN.\"MODIFIED_TIME\" = (SELECT MAX(\"MODIFIED_TIME\") FROM "+schema+".\"MS_TANK\" TNK1 WHERE TNK1.\"SITE_ID\" = MTN.\"SITE_ID\"  AND TNK1.\"TANK_NO\"=MTN.\"TANK_NO\" ) ";
			
			if (siteIDs != null) {
				SQL += " AND INV.\"SITE_ID\" IN (" + siteIDs + ")";
			}

			if (fromDate != null && toDate != null) {
				SQL += "AND INV.\"INVENTORY_DATE\"::timestamp::date BETWEEN ('" + fromDate + "') AND ('" + toDate
						+ "')";
			}

			if (productName != null) {
				SQL += " AND UPPER(PRD.\"PRODUCT_NAME\") = UPPER('" + productName + "') ";
			}

			// GROUP BY
			SQL += " GROUP BY MTN.\"PRODUCT_NO\",INV.\"SITE_ID\", MST.\"SITE_NAME\",MST.\"SITE_TYPE\",MST.\"DEALER_NAME\",MST.\"CLIENT_NAME\", "
					+ " MST.\"REGION\",MST.\"COUNTRY\",MST.\"SITE_STATUS\",MST.\"LAST_CONNECTION_TIME\" , \"DEC_VAL\" ";
					//+ ", INV.\"DENSITY_ACTUAL\", INV.\"DENSITY_AT15DEG\" ";

			if (siteCount == 1)
				SQL += " , PRD.\"PRICE\", CUR.\"CURRENCY_CODE\" ";

			SQL += ", PRD.\"PRODUCT_NAME\",PRD.\"PRODUCT_NO\"";
			if (timePeriodType.equalsIgnoreCase("WEEKLY")) {
				SQL += " ,\"WEEKLY\" ,\"WEEK\" ";
			} else if (timePeriodType.equalsIgnoreCase("MONTHLY")) {
				SQL += " ,\"MONTHLY\" ,\"MONTH\" ";
			} else if (timePeriodType.equalsIgnoreCase("HOURLY")) {
				SQL += " ,\"INVENTORY_DATE\" ";
			} else {
				//System.out.println("DATE Filter!..");
			}

			// ORDER BY
			SQL += "ORDER BY INV.\"SITE_ID\",PRD.\"PRODUCT_NO\"";
			if (timePeriodType.equalsIgnoreCase("WEEKLY")) {
				SQL += " ,\"WEEKLY\" ,\"WEEK\" ";
			} else if (timePeriodType.equalsIgnoreCase("MONTHLY")) {
				SQL += " ,\"MONTHLY\" ,\"MONTH\" ";
			} else if (timePeriodType.equalsIgnoreCase("HOURLY")) {
				SQL += " ,\"INVENTORY_DATE\" ";
			} else {
				//System.out.println("DATE Filter!..");
			}
			//System.out.println(SQL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ InventoryDAO-getInventoryByFilter ::" + ex.getMessage());
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
	public JSONArray getForcastingOld(String siteIDs, String days) {
		JSONArray json = new JSONArray();
		try {

			String SQL = " SELECT TRN.\"SITE_ID\", MST.\"SITE_NAME\",MST.\"SITE_TYPE\",MST.\"DEALER_NAME\",MST.\"CLIENT_NAME\", "
					+ " MST.\"REGION\",MST.\"COUNTRY\",MST.\"SITE_STATUS\", " + " ROUND(SUM(TRN.\"VOLUME\") / "
					+ unitConversion + " ,\"DEC_VAL\") AS \"VOLUME\", " + " ROUND( ( (SUM(TRN.\"VOLUME\") / "
					+ unitConversion + " ) / " + days + ") , \"DEC_VAL\") AS \"AVERAGE\", "
					+ " INV.\"VOLUME\",INV.\"TCVOLUME\", " + " CEIL(ROUND( ( (INV.\"VOLUME\" / " + unitConversion
					+ ") / ( (SUM(TRN.\"VOLUME\") / " + unitConversion + " ) / " + days
					+ ")) ,\"DEC_VAL\"))  AS \"FORCAST\", "
					+ " MTN.\"PRODUCT_NO\",UPPER(PRD.\"PRODUCT_NAME\") AS \"PRODUCT_NAME\"  ,TRN.\"TANK_NO\" " + " FROM " + schema
					+ ".\"TRANSACTIONS\" TRN " + " INNER JOIN " + schema
					+ ".\"MS_SITE\" MST ON TRN.\"SITE_ID\"=MST.\"SITE_ID\" "
					+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" AS CUR ON MST.\"COUNTRY\" = CUR.\"COUNTRY\" " + " INNER JOIN "
					+ schema
					+ ".\"MS_TANK\" MTN ON TRN.\"TANK_NO\"=MTN.\"TANK_NO\" AND MTN.\"SITE_ID\"=TRN.\"SITE_ID\" "
					+ " INNER JOIN " + schema
					+ ".\"MS_PRODUCTS\" PRD ON MTN.\"PRODUCT_NO\"=PRD.\"PRODUCT_NO\" AND PRD.\"SITE_ID\"=MST.\"SITE_ID\" "
					+ " INNER JOIN " + schema
					+ ".\"INVENTORY\" INV ON PRD.\"SITE_ID\"=INV.\"SITE_ID\" AND TRN.\"TANK_NO\" = INV.\"TANK_NO\" "
					+ " AND INV.\"INVENTORY_DATE\" = (SELECT MAX(TMP.\"INVENTORY_DATE\") FROM " + schema
					+ ".\"INVENTORY\" TMP "
					+ " WHERE TMP.\"SITE_ID\"=INV.\"SITE_ID\" AND TMP.\"TANK_NO\" = INV.\"TANK_NO\"  " + " LIMIT 1) "
					+ " WHERE 1=1 " + " AND DATE(\"TRANSACTION_DATE\") >= DATE_TRUNC('DAY', CURRENT_DATE - interval '"
					+ days + "' DAY) ";
					
			SQL += " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) ";
			SQL += " AND MTN.\"MODIFIED_TIME\" = (SELECT MAX(\"MODIFIED_TIME\") FROM "+schema+".\"MS_TANK\" TNK1 WHERE TNK1.\"SITE_ID\" = MTN.\"SITE_ID\"  AND TNK1.\"TANK_NO\"=MTN.\"TANK_NO\" ) ";

			if (siteIDs != null) {
				SQL += " AND TRN.\"SITE_ID\" IN (" + siteIDs + ")";
			}

			SQL += " GROUP BY MTN.\"PRODUCT_NO\",TRN.\"SITE_ID\", MST.\"SITE_NAME\",MST.\"SITE_TYPE\",MST.\"DEALER_NAME\",MST.\"CLIENT_NAME\", "
					+ " MST.\"REGION\",MST.\"COUNTRY\",MST.\"SITE_STATUS\", "
					+ " PRD.\"PRODUCT_NAME\",PRD.\"PRODUCT_NO\", "
					+ " INV.\"VOLUME\",INV.\"TCVOLUME\" ,\"DEC_VAL\" , TRN.\"TANK_NO\" "
					+ " ORDER BY TRN.\"SITE_ID\",PRD.\"PRODUCT_NO\" ";

			//System.out.println(SQL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ InventoryDAO-getForcasting ::" + ex.getMessage());
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
	public JSONArray getForcasting(String siteIDs, String days, boolean grouping,String state) {
		JSONArray json = new JSONArray();
		try {
			String SQL = null;
			//System.out.println("state = "+state);
			//System.out.println("grouping = "+grouping);
			if (!grouping) {
				SQL = " SELECT ROW_NUMBER () OVER (ORDER BY TRN.\"SITE_ID\") AS \"SNO\" , "
						+ " TRN.\"SITE_ID\", MST.\"SITE_NAME\"," +
						// + "MST.\"SITE_TYPE\",MST.\"DEALER_NAME\",MST.\"CLIENT_NAME\",
						// MST.\"REGION\",MST.\"COUNTRY\",MST.\"SITE_STATUS\", " +
						" UPPER(PRD.\"PRODUCT_NAME\") AS \"PRODUCT_NAME\" , " + " ROUND(INV.\"VOLUME\" / 1000 ,2) AS \"LAST_INVENTORY(KL)\",   "
						+ " ROUND( ( (SUM(TRN.\"VOLUME\") / 1000 ) / 5) , 2) AS \"AVERAGE_SALES\",  "
						+ " CEIL(ROUND( ( (INV.\"VOLUME\" / 1000) /  "
						+ "			  CASE (( (SUM(TRN.\"VOLUME\") / 1000 ) / 5)) WHEN 0 THEN 1 ELSE "
						+ "			  ( (SUM(TRN.\"VOLUME\") / 1000 ) / 5)  " + "			  END "
						+ "			 ) ,2))  AS \"REMAINING_STOCK_In_Days\"  " +
						// " INV.\"VOLUME\", " +
						// " TRN.\"PRODUCT_NO\", " +
						" FROM " + schema + ".\"TRANSACTIONS\" TRN  " + " INNER JOIN " + schema
						+ ".\"MS_SITE\" MST ON TRN.\"SITE_ID\"=MST.\"SITE_ID\"  "
						+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" AS CUR ON MST.\"COUNTRY\" = CUR.\"COUNTRY\"  "
						+ " INNER JOIN " + schema
						+ ".\"MS_PRODUCTS\" PRD ON TRN.\"PRODUCT_NO\"=PRD.\"PRODUCT_NO\" AND PRD.\"SITE_ID\"=TRN.\"SITE_ID\"  "
						+ " INNER JOIN ( " + "		SELECT TMP.\"SITE_ID\",TNK.\"PRODUCT_NO\" , SUM(\"VOLUME\") AS \"VOLUME\" FROM "
						+ schema + ".\"INVENTORY\" TMP   " + "		INNER JOIN " + schema
						+ ".\"MS_TANK\" TNK ON TNK.\"TANK_NO\" = TMP.\"TANK_NO\" AND TNK.\"SITE_ID\" = TMP.\"SITE_ID\"  "
						+ "		WHERE TMP.\"SITE_ID\" IN (" + siteIDs + ") "
						+ "		AND TMP.\"INVENTORY_DATE\" = (SELECT MAX(\"INVENTORY_DATE\") FROM " + schema
						+ ".\"INVENTORY\" T1 WHERE \"SITE_ID\" = TMP.\"SITE_ID\" )   "
						+" AND TNK.\"MODIFIED_TIME\" = (SELECT MAX(\"MODIFIED_TIME\") FROM "+schema+".\"MS_TANK\" TNK1 WHERE TNK1.\"SITE_ID\" = TNK.\"SITE_ID\"  AND TNK1.\"TANK_NO\"=TNK.\"TANK_NO\" ) "
						+ "		GROUP BY TMP.\"SITE_ID\",DATE(TMP.\"INVENTORY_DATE\"),TNK.\"PRODUCT_NO\" "
						+ " ) INV ON INV.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\" AND INV.\"SITE_ID\" = TRN.\"SITE_ID\" " + " WHERE 1=1   "
						+" AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) " 
						+ " AND DATE(\"TRANSACTION_DATE\") >= DATE_TRUNC('DAY', CURRENT_DATE - interval '" + days
						+ "' DAY)   " + " AND TRN.\"SITE_ID\" IN (" + siteIDs + ")  " + " GROUP BY "
						+ " TRN.\"SITE_ID\", MST.\"SITE_NAME\"," +
						// +
						// "MST.\"SITE_TYPE\",MST.\"DEALER_NAME\",MST.\"CLIENT_NAME\",MST.\"REGION\",MST.\"COUNTRY\",MST.\"SITE_STATUS\",
						// " +
						" PRD.\"PRODUCT_NAME\",\"DEC_VAL\", " + " TRN.\"PRODUCT_NO\" " + ",INV.\"VOLUME\"  "
						+ " ORDER BY TRN.\"SITE_ID\" ";
			} else {
				SQL = " SELECT ROW_NUMBER () OVER (ORDER BY PRD.\"PRODUCT_NAME\") AS \"SNO\" , "
						+ " UPPER(PRD.\"PRODUCT_NAME\") AS \"PRODUCT_NAME\" , " + " ROUND(SUM(TRN.\"VOLUME\") / 1000 ,2) AS \"LAST_INVENTORY(KL)\",   "
						+ " ROUND( ( (SUM(TRN.\"VOLUME\") / 1000 ) / 5) , 2) AS \"AVERAGE_SALES\",  "
						+ " CEIL(ROUND( ( (INV.\"VOLUME\" / 1000) /  "
						+ "			  CASE (( (SUM(TRN.\"VOLUME\") / 1000 ) / 5)) WHEN 0 THEN 1 ELSE "
						+ "			  ( (SUM(TRN.\"VOLUME\") / 1000 ) / 5)  " + "			  END "
						+ "			 ) ,2))  AS \"REMAINING_STOCK_In_Days\"  " + " FROM " + schema + ".\"TRANSACTIONS\" TRN  "
						+ " INNER JOIN " + schema + ".\"MS_SITE\" MST ON TRN.\"SITE_ID\"=MST.\"SITE_ID\"  "
						+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" AS CUR ON MST.\"COUNTRY\" = CUR.\"COUNTRY\"  "
						+ " INNER JOIN " + schema
						+ ".\"MS_PRODUCTS\" PRD ON TRN.\"PRODUCT_NO\"=PRD.\"PRODUCT_NO\" AND PRD.\"SITE_ID\"=TRN.\"SITE_ID\"  "
						+ " INNER JOIN ( "
						+ "		SELECT TNK.\"PRODUCT_NO\" ,"
						//+ " TMP.\"SITE_ID\" , "
						+ " SUM(\"VOLUME\") AS \"VOLUME\" FROM "
						+ schema + ".\"INVENTORY\" TMP   " + "		INNER JOIN " + schema
						+ ".\"MS_TANK\" TNK ON TNK.\"TANK_NO\" = TMP.\"TANK_NO\" AND TNK.\"SITE_ID\" = TMP.\"SITE_ID\"  "
						+ "		WHERE TMP.\"SITE_ID\" IN (" + siteIDs + ") "
						+ "		AND TMP.\"INVENTORY_DATE\" = (SELECT MAX(\"INVENTORY_DATE\") FROM " + schema
						+ ".\"INVENTORY\" T1 WHERE \"SITE_ID\" IN (" + siteIDs + ")  )  "
						+" AND TNK.\"MODIFIED_TIME\" = (SELECT MAX(\"MODIFIED_TIME\") FROM "+schema+".\"MS_TANK\" TNK1 WHERE TNK1.\"SITE_ID\" = TNK.\"SITE_ID\"  AND TNK1.\"TANK_NO\"=TNK.\"TANK_NO\" ) "
						+ "		GROUP BY DATE(TMP.\"INVENTORY_DATE\"),TNK.\"PRODUCT_NO\" "
						//+ ",TMP.\"SITE_ID\" "
						+ " ) INV ON INV.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\" "
						//+ " AND INV.\"SITE_ID\" = TRN.\"SITE_ID\"  "
						+ " WHERE 1=1   "
						+" AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) " 
						+ " AND DATE(\"TRANSACTION_DATE\") >= DATE_TRUNC('DAY', CURRENT_DATE - interval '" + days
						+ "' DAY) GROUP BY " + " PRD.\"PRODUCT_NAME\",\"DEC_VAL\", " + " TRN.\"PRODUCT_NO\" "
						+ ",INV.\"VOLUME\"  ";
			}
			//System.out.println(SQL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			if(state!=null) {
				int forcastDays = 0;
				int sno = 0;
				JSONObject jsonObj = null;
				
				// D = 0
				// R = 0-3
				// A = 4-8
				// G = 9 >
				if(state.equalsIgnoreCase("D")) {	
					while(rs.next()) {
						forcastDays=rs.getInt("REMAINING_STOCK_In_Days");
						if(forcastDays==0) {
							sno++;
							jsonObj = new JSONObject();
							jsonObj.put("SNO", sno);
							jsonObj.put("SITE_ID", rs.getString("SITE_ID"));
							jsonObj.put("SITE_NAME", rs.getString("SITE_NAME"));
							jsonObj.put("PRODUCT_NAME", rs.getString("PRODUCT_NAME"));
							jsonObj.put("LAST_INVENTORY(KL)", rs.getString("LAST_INVENTORY(KL)"));
							jsonObj.put("AVERAGE_SALES", rs.getString("AVERAGE_SALES"));
							jsonObj.put("REMAINING_STOCK_In_Days",forcastDays );
							json.add(jsonObj);
						}
					}					
				}else if(state.equalsIgnoreCase("R")) {
					while(rs.next()) {
						forcastDays=rs.getInt("REMAINING_STOCK_In_Days");
						if(forcastDays>0 && forcastDays<=3) {
							sno++;
							jsonObj = new JSONObject();
							jsonObj.put("SNO", sno);
							jsonObj.put("SITE_ID", rs.getString("SITE_ID"));
							jsonObj.put("SITE_NAME", rs.getString("SITE_NAME"));
							jsonObj.put("PRODUCT_NAME", rs.getString("PRODUCT_NAME"));
							jsonObj.put("LAST_INVENTORY(KL)", rs.getString("LAST_INVENTORY(KL)"));
							jsonObj.put("AVERAGE_SALES", rs.getString("AVERAGE_SALES"));
							jsonObj.put("REMAINING_STOCK_In_Days",forcastDays );
							json.add(jsonObj);
						}
					}
				}else if(state.equalsIgnoreCase("A")) {
					while(rs.next()) {
						forcastDays=rs.getInt("REMAINING_STOCK_In_Days");
						if(forcastDays>=4 && forcastDays<=8) {
							sno++;
							jsonObj = new JSONObject();
							jsonObj.put("SNO", sno);
							jsonObj.put("SITE_ID", rs.getString("SITE_ID"));
							jsonObj.put("SITE_NAME", rs.getString("SITE_NAME"));
							jsonObj.put("PRODUCT_NAME", rs.getString("PRODUCT_NAME"));
							jsonObj.put("LAST_INVENTORY(KL)", rs.getString("LAST_INVENTORY(KL)"));
							jsonObj.put("AVERAGE_SALES", rs.getString("AVERAGE_SALES"));
							jsonObj.put("REMAINING_STOCK_In_Days",forcastDays );
							json.add(jsonObj);
						}
					}
				}else if(state.equalsIgnoreCase("G")) {
					while(rs.next()) {
						forcastDays=rs.getInt("REMAINING_STOCK_In_Days");
						if(forcastDays>=9) {
							sno++;
							jsonObj = new JSONObject();
							jsonObj.put("SNO", sno);
							jsonObj.put("SITE_ID", rs.getString("SITE_ID"));
							jsonObj.put("SITE_NAME", rs.getString("SITE_NAME"));
							jsonObj.put("PRODUCT_NAME", rs.getString("PRODUCT_NAME"));
							jsonObj.put("LAST_INVENTORY(KL)", rs.getString("LAST_INVENTORY(KL)"));
							jsonObj.put("AVERAGE_SALES", rs.getString("AVERAGE_SALES"));
							jsonObj.put("REMAINING_STOCK_In_Days",forcastDays );
							json.add(jsonObj);
						}
					}
				}else {
					json = dbc.parseRS(rs);
				}
			}else {
				json = dbc.parseRS(rs);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ InventoryDAO-getForcasting ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	public JSONArray getForcasting(String siteIDs, String days) {
		JSONArray json = new JSONArray();
		try {
			String SQL = null;

				SQL = " SELECT ROW_NUMBER () OVER (ORDER BY PRD.\"PRODUCT_NAME\") AS \"SNO\" , "
						+ " ROUND(SUM(TRN.\"VOLUME\") / 1000 ,2) AS \"VOLUME\",   "
						+ " ROUND( ( (SUM(TRN.\"VOLUME\") / 1000 ) / 5) , 2) AS \"AVERAGE\",  "
						+ " CEIL(ROUND( ( (INV.\"VOLUME\" / 1000) /  "
						+ "			  CASE (( (SUM(TRN.\"VOLUME\") / 1000 ) / 5)) WHEN 0 THEN 1 ELSE "
						+ "			  ( (SUM(TRN.\"VOLUME\") / 1000 ) / 5)  " + "			  END "
						+ "			 ) ,2))  AS \"FORCAST\"  " + " FROM " + schema + ".\"TRANSACTIONS\" TRN  "
						+ " INNER JOIN " + schema + ".\"MS_SITE\" MST ON TRN.\"SITE_ID\"=MST.\"SITE_ID\"  "
						+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" AS CUR ON MST.\"COUNTRY\" = CUR.\"COUNTRY\"  "
						+ " INNER JOIN " + schema
						+ ".\"MS_PRODUCTS\" PRD ON TRN.\"PRODUCT_NO\"=PRD.\"PRODUCT_NO\" AND PRD.\"SITE_ID\"=TRN.\"SITE_ID\"  "
						+ " INNER JOIN ( "
						+ "		SELECT TNK.\"PRODUCT_NO\" ,"
						+ " SUM(\"VOLUME\") AS \"VOLUME\" FROM "
						+ schema + ".\"INVENTORY\" TMP   " + "		INNER JOIN " + schema
						+ ".\"MS_TANK\" TNK ON TNK.\"TANK_NO\" = TMP.\"TANK_NO\" AND TNK.\"SITE_ID\" = TMP.\"SITE_ID\"  "
						+ "		WHERE TMP.\"SITE_ID\" IN (" + siteIDs + ") "
						+ "		AND TMP.\"INVENTORY_DATE\" = (SELECT MAX(\"INVENTORY_DATE\") FROM " + schema
						+ ".\"INVENTORY\" T1 WHERE \"SITE_ID\" IN (" + siteIDs + ")  )  "
						+ "		GROUP BY DATE(TMP.\"INVENTORY_DATE\"),TNK.\"PRODUCT_NO\" "
						+ " ) INV ON INV.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\" "
						+ " WHERE 1=1   "
						+ " AND DATE(\"TRANSACTION_DATE\") >= DATE_TRUNC('DAY', CURRENT_DATE - interval '" + days
						+ "' DAY) GROUP BY \"DEC_VAL\" ,INV.\"VOLUME\"  ";

			//System.out.println(SQL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ InventoryDAO-getForcasting ::" + ex.getMessage());
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
	public JSONArray getInvForChart(String siteIDs, String fromDate, String toDate, String productCode,
			String timePeriodType) {
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

			SQL = " SELECT PRD.\"PRODUCT_NO\",UPPER(PRD.\"PRODUCT_NAME\") AS \"PRODUCT_NAME\"  FROM " + schema + ".\"MS_PRODUCTS\" PRD "
					+ " INNER JOIN " + schema + ".\"MS_SITE\" S ON S.\"SITE_ID\"=PRD.\"SITE_ID\" " + " WHERE 1=1 " 
					+" AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) " ;
						
			if (productCode != null) {
				SQL += " AND UPPER(PRD.\"PRODUCT_NAME\") = UPPER('" + productCode + "') ";
			}

			if (siteIDs != null) {
				SQL += " AND S.\"SITE_ID\" IN ( " + siteIDs + ")";
			}

			SQL += " GROUP BY PRD.\"PRODUCT_NO\",PRD.\"PRODUCT_NAME\" "
					+ " ORDER BY PRD.\"PRODUCT_NO\",PRD.\"PRODUCT_NAME\" ";

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

					//System.out.println("timePeriodType=" + timePeriodType);

					if (timePeriodType != null) {
						if (timePeriodType.equalsIgnoreCase("WEEKLY")) {
							SQL = "	SELECT DATE_TRUNC('WEEK',INV.\"INVENTORY_DATE\"::DATE)::DATE - 1 AS \"WEEKLY\",  DATE_TRUNC('WEEK',INV.\"INVENTORY_DATE\"::DATE)::DATE - 1 AS \"DATE\" , ";
						}
						if (timePeriodType.equalsIgnoreCase("MONTHLY")) {
							SQL = " SELECT DATE_TRUNC('MONTH',INV.\"INVENTORY_DATE\"::DATE) AS \"MONTHLY\", DATE_TRUNC('MONTH',INV.\"INVENTORY_DATE\"::DATE) AS \"DATE\" , ";
						}
						if (timePeriodType.equalsIgnoreCase("HOURLY")) {
							SQL = " SELECT  DATE_TRUNC('HOUR', INV.\"INVENTORY_DATE\") AS \"HOURLY\", CAST( DATE_TRUNC('HOUR', INV.\"INVENTORY_DATE\") AS TIME ) AS \"DATE\" ,  ";
						}
					} else {
						SQL = " SELECT  DATE_TRUNC('DAY', INV.\"INVENTORY_DATE\") AS \"DAILY\",  DATE_TRUNC('DAY', INV.\"INVENTORY_DATE\")::DATE AS \"DATE\" ,  ";
					}

					SQL += " ROUND( SUM(\"VOLUME\") / " + unitConversion + " ,\"DEC_VAL\") AS \"VOLUME\"  " + " FROM "
							+ schema + ".\"MS_SITE\" MST "
							+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" AS CUR ON MST.\"COUNTRY\" = CUR.\"COUNTRY\" "
							+ " INNER JOIN " + schema + ".\"MS_PRODUCTS\" PRD ON PRD.\"SITE_ID\"=MST.\"SITE_ID\"  "
							+ " INNER JOIN " + schema
							+ ".\"MS_TANK\" TNK ON TNK.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND MST.\"SITE_ID\"=TNK.\"SITE_ID\" "
							+ " INNER JOIN " + schema
							+ ".\"INVENTORY\" INV ON INV.\"SITE_ID\"= MST.\"SITE_ID\"  AND INV.\"TANK_NO\" = TNK.\"TANK_NO\" "
							+ " WHERE 1=1 " + " AND INV.\"SITE_ID\" IN (" + siteIDs + ") AND PRD.\"PRODUCT_NO\" = "
							+ product + " AND UPPER(PRD.\"PRODUCT_NAME\") = UPPER('"+productName+"') ";
					SQL += " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) ";
					SQL += " AND TNK.\"MODIFIED_TIME\" = (SELECT MAX(\"MODIFIED_TIME\") FROM "+schema+".\"MS_TANK\" TNK1 WHERE TNK1.\"SITE_ID\" = TNK.\"SITE_ID\"  AND TNK1.\"TANK_NO\"=TNK.\"TANK_NO\" ) ";
					if (timePeriodType != null) {
						if (!timePeriodType.equalsIgnoreCase("HOURLY")) {
							SQL += " AND INV.\"INVENTORY_DATE\" IN (   SELECT MAX(TMP.\"INVENTORY_DATE\") FROM "
									+ schema + ".\"INVENTORY\" TMP WHERE TMP.\"SITE_ID\" IN (" + siteIDs + ") "
									+ " AND TMP.\"INVENTORY_DATE\"::timestamp::date BETWEEN ('" + fromDate + "') AND ('"
									+ toDate + "') " + " GROUP BY TMP.\"INVENTORY_DATE\" :: DATE ) ";
						}
					} else {
						SQL += " AND INV.\"INVENTORY_DATE\" IN (   SELECT MAX(TMP.\"INVENTORY_DATE\") FROM " + schema
								+ ".\"INVENTORY\" TMP WHERE TMP.\"SITE_ID\" IN (" + siteIDs + ") "
								+ " AND TMP.\"INVENTORY_DATE\"::timestamp::date BETWEEN ('" + fromDate + "') AND ('"
								+ toDate + "') " + " GROUP BY TMP.\"INVENTORY_DATE\" :: DATE ) ";
					}

					if (fromDate != null && toDate != null) {
						SQL += " AND INV.\"INVENTORY_DATE\"::timestamp::date BETWEEN ('" + fromDate + "') AND ('"
								+ toDate + "')";
					}

					if (timePeriodType != null) {
						if (timePeriodType.equalsIgnoreCase("WEEKLY")) {
							SQL += "	GROUP BY \"WEEKLY\" , \"DATE\" ,\"DEC_VAL\"  ORDER BY \"WEEKLY\" ";
						}
						if (timePeriodType.equalsIgnoreCase("MONTHLY")) {
							SQL += "  GROUP BY  \"MONTHLY\" , \"DATE\" ,\"DEC_VAL\"  ORDER BY \"MONTHLY\" ";
						}
						if (timePeriodType.equalsIgnoreCase("HOURLY")) {
							SQL += " GROUP BY  \"HOURLY\" , \"DATE\" ,\"DEC_VAL\"  ORDER BY \"HOURLY\" ";
						}
					} else {
						SQL += " GROUP BY  \"DAILY\", \"DATE\" ,\"DEC_VAL\"  ORDER BY \"DAILY\" ";
					}

					stmt = conn.createStatement();
					rs = stmt.executeQuery(SQL);
					innerjson = dbc.parseRS(rs);

					obj.put("INV_DATA", innerjson);
					json.add(obj);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ InventoryDAO-getInvForChart ::" + ex.getMessage());
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
	public JSONArray getInvForChartGrouping(String siteIDs,String userId,String country, String fromDate, String toDate, String productCode,
			String timePeriodType) {
		JSONArray json = new JSONArray();
		JSONArray prdjson = new JSONArray();
		JSONObject obj = null;
		try {
			String SQL = null;
			String productName = null;
			//int product = 0;
			JSONArray innerjson = null;
			Statement stmt = null;
			ResultSet rs = null;

			SQL = " SELECT UPPER(\"PRODUCT_GROUP\") AS \"PRODUCT_NAME\"  FROM " + schema + ".\"MS_PRODUCTS\" PRD "
					+ " INNER JOIN " + schema + ".\"MS_SITE\" S ON S.\"SITE_ID\"=PRD.\"SITE_ID\" " + " INNER JOIN \"BCT\".user_sites US ON US.site_id = PRD.\"SITE_ID\" "
					+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" CNT ON CNT.\"COUNTRY\" = S.\"COUNTRY\" "
					+ " INNER JOIN \"BCT\".\"COUNTRY_PRODUCT_CONFIG\" CPC ON CPC.\"COUNTRY_ID\" = CNT.\"COUNTRY_ID\" "
					+ " WHERE 1=1 ";
			SQL += " AND US.user_id = '" + userId + "' ";

			if (country != null)
				SQL += " AND US.country = '" + country + "'";

			SQL += " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM " + schema
					+ ".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) ";

			if (productCode != null) {
				SQL += " AND UPPER(PRD.\"PRODUCT_NAME\") = UPPER('" + productCode + "') ";
			}

			if (siteIDs != null && siteIDs.contains("','")) {
				SQL += " AND US.site_id IN ( " + siteIDs + ")";
			}

			SQL += " GROUP BY UPPER(\"PRODUCT_GROUP\") " + " ORDER BY UPPER(\"PRODUCT_GROUP\") ";

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
					productName = objects.getString("PRODUCT_NAME");
					obj.put("PRODUCT_NAME", productName);

					//System.out.println("timePeriodType=" + timePeriodType);

					if (timePeriodType != null) {
						if (timePeriodType.equalsIgnoreCase("WEEKLY")) {
							SQL = "	SELECT DATE_TRUNC('WEEK',INV.\"INVENTORY_DATE\"::DATE)::DATE - 1 AS \"WEEKLY\",  DATE_TRUNC('WEEK',INV.\"INVENTORY_DATE\"::DATE)::DATE - 1 AS \"DATE\" , ";
						}
						if (timePeriodType.equalsIgnoreCase("MONTHLY")) {
							SQL = " SELECT DATE_TRUNC('MONTH',INV.\"INVENTORY_DATE\"::DATE) AS \"MONTHLY\", DATE_TRUNC('MONTH',INV.\"INVENTORY_DATE\"::DATE) AS \"DATE\" , ";
						}
						if (timePeriodType.equalsIgnoreCase("HOURLY")) {
							SQL = " SELECT  DATE_TRUNC('HOUR', INV.\"INVENTORY_DATE\") AS \"HOURLY\", CAST( DATE_TRUNC('HOUR', INV.\"INVENTORY_DATE\") AS TIME ) AS \"DATE\" ,  ";
						}
					} else {
						SQL = " SELECT  DATE_TRUNC('DAY', INV.\"INVENTORY_DATE\") AS \"DAILY\",  DATE_TRUNC('DAY', INV.\"INVENTORY_DATE\")::DATE AS \"DATE\" ,  ";
					}

					SQL += " ROUND( SUM(\"VOLUME\") / " + unitConversion + " ,\"DEC_VAL\") AS \"VOLUME\"  " + " FROM "
							+ schema + ".\"MS_SITE\" MST "
							+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" AS CUR ON MST.\"COUNTRY\" = CUR.\"COUNTRY\" "
							+ " INNER JOIN " + schema + ".\"MS_PRODUCTS\" PRD ON PRD.\"SITE_ID\"=MST.\"SITE_ID\"  "
							+ " INNER JOIN \"BCT\".\"COUNTRY_PRODUCT_CONFIG\" CPC ON CPC.\"COUNTRY_ID\" = CUR.\"COUNTRY_ID\" AND CPC.\"PRODUCT\"=PRD.\"PRODUCT_NAME\" "
							+ " INNER JOIN " + schema
							+ ".\"MS_TANK\" TNK ON TNK.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND MST.\"SITE_ID\"=TNK.\"SITE_ID\" "
							+ " INNER JOIN " + schema
							+ ".\"INVENTORY\" INV ON INV.\"SITE_ID\"= MST.\"SITE_ID\"  AND INV.\"TANK_NO\" = TNK.\"TANK_NO\" "
							+ " INNER JOIN \"BCT\".user_sites US ON US.site_id = INV.\"SITE_ID\" "
							+ " WHERE 1=1 " + " AND US.user_id = '" + userId + "'"
							+ " AND UPPER(\"PRODUCT_GROUP\") = UPPER('" + productName + "') ";
					SQL += " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) ";
					SQL += " AND TNK.\"MODIFIED_TIME\" = (SELECT MAX(\"MODIFIED_TIME\") FROM "+schema+".\"MS_TANK\" TNK1 WHERE TNK1.\"SITE_ID\" = TNK.\"SITE_ID\"  AND TNK1.\"TANK_NO\"=TNK.\"TANK_NO\" ) ";
					if (timePeriodType != null) {
						if (!timePeriodType.equalsIgnoreCase("HOURLY")) {
							SQL += " AND INV.\"INVENTORY_DATE\" IN (   SELECT MAX(TMP.\"INVENTORY_DATE\") FROM "
									+ schema + ".\"INVENTORY\" TMP WHERE TMP.\"SITE_ID\" IN (" + siteIDs + ") "
									+ " AND TMP.\"INVENTORY_DATE\"::timestamp::date BETWEEN ('" + fromDate + "') AND ('"
									+ toDate + "') " + " GROUP BY TMP.\"INVENTORY_DATE\" :: DATE ) ";
						}
					} else {
						SQL += " AND INV.\"INVENTORY_DATE\" IN (   SELECT MAX(TMP.\"INVENTORY_DATE\") FROM " + schema
								+ ".\"INVENTORY\" TMP WHERE TMP.\"SITE_ID\" IN (" + siteIDs + ") "
								+ " AND TMP.\"INVENTORY_DATE\"::timestamp::date BETWEEN ('" + fromDate + "') AND ('"
								+ toDate + "') " + " GROUP BY TMP.\"INVENTORY_DATE\" :: DATE ) ";
					}

					if (fromDate != null && toDate != null) {
						SQL += " AND INV.\"INVENTORY_DATE\"::timestamp::date BETWEEN ('" + fromDate + "') AND ('"
								+ toDate + "')";
					}

					if (timePeriodType != null) {
						if (timePeriodType.equalsIgnoreCase("WEEKLY")) {
							SQL += "	GROUP BY \"WEEKLY\" , \"DATE\" ,\"DEC_VAL\"  ORDER BY \"WEEKLY\" ";
						}
						if (timePeriodType.equalsIgnoreCase("MONTHLY")) {
							SQL += "  GROUP BY  \"MONTHLY\" , \"DATE\" ,\"DEC_VAL\"  ORDER BY \"MONTHLY\" ";
						}
						if (timePeriodType.equalsIgnoreCase("HOURLY")) {
							SQL += " GROUP BY  \"HOURLY\" , \"DATE\" ,\"DEC_VAL\"  ORDER BY \"HOURLY\" ";
						}
					} else {
						SQL += " GROUP BY  \"DAILY\", \"DATE\" ,\"DEC_VAL\"  ORDER BY \"DAILY\" ";
					}

					stmt = conn.createStatement();
					rs = stmt.executeQuery(SQL);
					innerjson = dbc.parseRS(rs);

					obj.put("INV_DATA", innerjson);
					json.add(obj);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ InventoryDAO-getInvForChartGrouping ::" + ex.getMessage());
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
	public JSONArray getSalesData(String siteIDs, String period, String fromDate, String toDate) {
		JSONArray json = new JSONArray();
		try {

			String SQL = " SELECT DATE(\"TRANSACTION_DATE\"), " + " SUM(\"VOLUME\") AS \"VOLUME\", "
					+ " SUM(\"AMOUNT\") AS \"AMOUNT\" , " + " CUR.\"CURRENCY_CODE\" " + " FROM " + schema
					+ ".\"TRANSACTIONS\"  TRN " + " INNER JOIN " + schema
					+ ".\"MS_PRODUCTS\" PRD ON PRD.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\"  AND  TRN.\"SITE_ID\" = PRD.\"SITE_ID\"   "
					+ " INNER JOIN " + schema + ".\"MS_SITE\" MST ON PRD.\"SITE_ID\" = MST.\"SITE_ID\"    "
					+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" AS CUR ON MST.\"COUNTRY\" = CUR.\"COUNTRY\"  "
					+ " WHERE 1=1 ";
			
			SQL += " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) ";

			if (siteIDs != null) {
				SQL += " AND TRN.\"SITE_ID\" IN (" + siteIDs + ")";
			}

			if (period.equalsIgnoreCase("MONTH")) {
				SQL += " AND DATE(\"TRANSACTION_DATE\") BETWEEN (SELECT (date_trunc('MONTH', CURRENT_TIMESTAMP)::date)) AND (SELECT ( (date_trunc('MONTH', CURRENT_TIMESTAMP)+ '31 days'::interval)::date ) ) ";
			} else if (period.equalsIgnoreCase("WEEK")) {
				SQL += " AND DATE(\"TRANSACTION_DATE\") BETWEEN (SELECT (date_trunc('week', CURRENT_TIMESTAMP)::date)) AND (SELECT ( (date_trunc('week', CURRENT_TIMESTAMP)+ '6 days'::interval)::date ) ) ";
			} else {
				SQL += " AND DATE(\"TRANSACTION_DATE\") BETWEEN '" + fromDate + "' AND '" + toDate + "' ";
			}

			SQL += " GROUP BY CUR.\"CURRENCY_CODE\",DATE(\"TRANSACTION_DATE\") ";

			//System.out.println(SQL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ InventoryDAO-getSalesData ::" + ex.getMessage());
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
	public JSONArray getInventoryData(String siteIDs, String period, String fromDate, String toDate) {
		JSONArray json = new JSONArray();
		try {
			if (conn == null || conn.isClosed()) {
				conn = dbc.getConnection();
			}

			String SQL = " SELECT DATE(\"INVENTORY_DATE\"), " + " SUM(\"VOLUME\") AS \"VOLUME\", "
					+ " CUR.\"CURRENCY_CODE\" " + " FROM " + schema + ".\"INVENTORY\" INV " + " INNER JOIN " + schema
					+ ".\"MS_TANK\" PRD  ON PRD.\"TANK_NO\" = INV.\"TANK_NO\"  AND  INV.\"SITE_ID\" = PRD.\"SITE_ID\"   "
					+ " INNER JOIN " + schema + ".\"MS_SITE\" MST ON PRD.\"SITE_ID\" = MST.\"SITE_ID\"    "
					+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" AS CUR ON MST.\"COUNTRY\" = CUR.\"COUNTRY\"  "
					+ " WHERE 1=1 ";
			//SQL += " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) ";
			SQL += " AND PRD.\"MODIFIED_TIME\" = (SELECT MAX(\"MODIFIED_TIME\") FROM "+schema+".\"MS_TANK\" TNK1 WHERE TNK1.\"SITE_ID\" = PRD.\"SITE_ID\"  AND TNK1.\"TANK_NO\"=PRD.\"TANK_NO\" ) ";
			
			if (siteIDs != null) {
				SQL += " AND PRD.\"SITE_ID\" IN (" + siteIDs + ")";
			}

			if (period.equalsIgnoreCase("MONTH")) {
				SQL += " AND DATE(INV.\"INVENTORY_DATE\") BETWEEN (SELECT (date_trunc('MONTH', CURRENT_TIMESTAMP)::date)) AND (SELECT ( (date_trunc('MONTH', CURRENT_TIMESTAMP)+ '31 days'::interval)::date ) ) ";
			} else if (period.equalsIgnoreCase("WEEK")) {
				SQL += " AND DATE(INV.\"INVENTORY_DATE\") BETWEEN (SELECT (date_trunc('week', CURRENT_TIMESTAMP)::date)) AND (SELECT ( (date_trunc('week', CURRENT_TIMESTAMP)+ '6 days'::interval)::date ) ) ";
			} else {
				SQL += " AND DATE(INV.\"INVENTORY_DATE\") BETWEEN '" + fromDate + "' AND '" + toDate + "' ";
			}

			SQL += " GROUP BY CUR.\"CURRENCY_CODE\",DATE(\"INVENTORY_DATE\") ";

			//System.out.println(SQL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ InventoryDAO-getInventoryData ::" + ex.getMessage());
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
