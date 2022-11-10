package com.bct.HOS.App.DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.bct.HOS.App.utils.DBConnector;
import com.bct.HOS.LVM.DeviceStatus;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class SiteStatusDAO {

	private DBConnector dbc = null;
	private Connection conn = null;
	private String schema = null;
	private long unitConversion = 0; 
	
	
	public SiteStatusDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
		unitConversion = dbc.getUnitConversion();
	}

	/**
	 * 
	 * @return
	 */
	public JSONArray getSiteStatusSummary(String siteIDs, String status) {
		JSONArray json = new JSONArray();
		try {
			String sts = null;
			if(status!=null) {
				if(status.equalsIgnoreCase("ONLINE")) {
					sts="1";
				}else {
					sts="0";
				}
			}

			String SQL = " SELECT COUNT(*) , CASE WHEN \"NewStatus\" = '1' THEN 'Online' ELSE 'Offline' END AS \"SITE_STATUS\" FROM "+schema+".\"SiteStatusChangeHistory\" SS  WHERE 1=1 " +
					" AND TO_CHAR(TO_TIMESTAMP( \"LastUpdated\"), 'DD/MM/YYYY HH24:MI:SS') = ( " + 
					"				SELECT MAX(TO_CHAR(TO_TIMESTAMP( \"LastUpdated\"), 'DD/MM/YYYY HH24:MI:SS')) FROM "+schema+".\"SiteStatusChangeHistory\" TMP " + 
					"				WHERE \"SiteID\" = SS.\"SiteID\" " + 
					"			) ";

			if (siteIDs != null) {
				SQL += " AND \"SiteID\" IN (" + siteIDs + ")";
			}

			if (status != null) {
				SQL += " AND \"NewStatus\"  = '" + sts + "' ";
			}

			SQL += " GROUP BY \"NewStatus\" ORDER BY \"SITE_STATUS\" ";
			
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ SiteStatusDAO-getSiteStatusSummary ::" + ex.getMessage());
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
	public String getSiteDetail(String siteId,String country,String state,String region,String district,String city) {
		String siteName = null;
		try {
			String SQL = " SELECT \"SITE_NAME\" FROM "+schema+".\"MS_SITE\" MST WHERE UPPER(MST.\"SITE_ID\") = "+siteId.toUpperCase();
			
			if(country!=null) {
				SQL += " AND MST.\"COUNTRY\" = '"+country+"' ";
			}
			
			if(state!=null) {
				SQL += " AND MST.\"STATE\" = '"+state+"' ";
			}
			
			if(region!=null) {
				SQL += " AND MST.\"REGION\" = '"+region+"' ";
			}
			
			if(district!=null) {
				SQL += " AND MST.\"DISTRICT\" = '"+district+"' ";
			}
			
			if(city!=null) {
				SQL += " AND MST.\"CITY\" = '"+city+"' ";
			}
						
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			while(rs.next()) {
				siteName = rs.getString("SITE_NAME");
			}
			stmt = null;
			rs = null;
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ SiteStatusDAO-getSiteDetail ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return siteName;
	}
	
	public Map getSiteDetails(String siteId,String country,String state,String region,String district,String city) {
		String siteName = null;
		String isAutomated = null;
		Map<String, String> outPutMap = new HashMap<>();
		try {
			String SQL = " SELECT \"SITE_NAME\",\"IS_AUTOMATED\" FROM "+schema+".\"MS_SITE\" MST WHERE UPPER(MST.\"SITE_ID\") = "+siteId.toUpperCase();
			
			if(country!=null) {
				SQL += " AND MST.\"COUNTRY\" = '"+country+"' ";
			}
			
			if(state!=null) {
				SQL += " AND MST.\"STATE\" = '"+state+"' ";
			}
			
			if(region!=null) {
				SQL += " AND MST.\"REGION\" = '"+region+"' ";
			}
			
			if(district!=null) {
				SQL += " AND MST.\"DISTRICT\" = '"+district+"' ";
			}
			
			if(city!=null) {
				SQL += " AND MST.\"CITY\" = '"+city+"' ";
			}
						
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			if(rs.next()) {
				siteName = rs.getString("SITE_NAME");
				isAutomated = rs.getString("IS_AUTOMATED");
				outPutMap.put("SITE_NAME", siteName);
				outPutMap.put("IS_AUTOMATED", isAutomated);
			}
			stmt = null;
			rs = null;
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ SiteStatusDAO-getSiteDetail ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return outPutMap;
	}
	
	public JSONArray getSiteStatus(String siteIDs, String status,String siteName) {
		JSONArray json = new JSONArray();
		try {

			String SQL = " SELECT \"SITE_ID\" , \"SITE_NAME\", \"CLIENT_NAME\" , \"DEALER_NAME\", \"ADDRESS1\" || ' ' || \"ADDRESS2\" || ' ' || \"ADDRESS3\" AS \"ADDRESS\" ,\"CITY\", " + 
					" \"DISTRICT\",\"STATE\",\"REGION\",MS.\"COUNTRY\",\"PIN_CODE\",\"MOBILE_NO\",\"OTHER_CONTACT_NO\",\"EMAIL\",\"SAP_CODE\",\"BOS_VERSION\",\"FCC_VERSION\" , CNT.\"CURRENCY_CODE\" , CNT.\"DEC_VAL\" " + 
					" FROM  "+schema+".\"MS_SITE\" MS" +
					" INNER JOIN \"BCT\".\"MS_COUNTRY\" CNT ON CNT.\"COUNTRY\" = MS.\"COUNTRY\" " +
					" WHERE 1=1 ";

			if (siteIDs != null) {
				SQL += " AND \"SITE_ID\" IN (" + siteIDs + ")";
			}
			
			if(siteName != null) {
				SQL += " AND \"SITE_NAME\" = '"+siteName+"' ";
			}

			if (status != null) {
				SQL += " AND \"SITE_STATUS\"  = '" + status + "'";
			}
			
			//System.out.println("SQL @ SiteStatusDAO::"+SQL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
			/**
			if(json!=null) {
				HashMap obj = new DeviceStatus().getSiteStatus();
				HashMap innerObj = null; 
				//System.out.println(obj.toString());
				String site = null;
				if(obj!=null) {
					JSONObject innerjobj=null; 
					for(int i=0;i<json.size();i++) {
						innerjobj=json.getJSONObject(i);
						site=innerjobj.getString("SITE_ID");
						innerObj=(HashMap) obj.get(site);
						
						innerjobj.put("SITE_STATUS", innerObj.get("Status"));
						innerjobj.put("LAST_CONNECTION_TIME", innerObj.get("LastUpdated"));
						
						json.add(innerjobj);
					}
				}
			}
			**/
			
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ SiteStatusDAO-getSiteStatus ::" + ex.getMessage());
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
	public JSONArray getSiteStatus(String siteIDs, String status, String siteName,
			String country,String state,String region,String district,
			String city) {
		JSONArray json = new JSONArray();
		try {

			String SQL = " SELECT ROW_NUMBER () OVER (ORDER BY \"SITE_ID\") AS \"SNO\"  , \"SITE_ID\",\"SITE_NAME\",\"SITE_TYPE\",\"DEALER_NAME\", "
					+ " \"ADDRESS1\",\"ADDRESS2\",\"ADDRESS3\",\"CITY\",\"DISTRICT\",\"STATE\",\"REGION\", "
					+ " \"COUNTRY\",\"PIN_CODE\",\"MOBILE_NO\",\"OTHER_CONTACT_NO\", "
					+ " \"EMAIL\",\"SAP_CODE\", "
					+ " coalesce(\"BOS_VERSION\",'NA') AS \"BOS_VERSION\" , "
					+ " coalesce(\"FCC_VERSION\",'NA') AS \"FCC_VERSION\" , "
					+ " coalesce(\"IOC_VERSION\",'NA') AS \"IOC_VERSION\" , "
					+ " coalesce(\"IP_ADDR\",'NA') AS \"BOS_IP\" , "
					+ " coalesce(TO_CHAR(\"LAST_CONNECTION_TIME\",'YYYY-MM-DD HH:mi:SS' ),'0000-00-00 00:00:00') AS \"LAST_CONNECTION_TIME\", "
					+ " UPPER(\"SITE_STATUS\") AS \"SITE_STATUS\" "
					+ " FROM "+schema+".\"MS_SITE\" MST WHERE 1=1 ";
			if (siteIDs != null) {
				SQL += " AND \"SITE_ID\" IN (" + siteIDs + ")";
			}

			if (siteName != null) {
				SQL += " AND \"SITE_NAME\" = '" + siteName + "'";
			}

			if (status != null) {
				SQL += " AND UPPER(\"SITE_STATUS\") = UPPER('" + status + "')";
			}
			
			if(country!=null) {
				SQL += " AND MST.\"COUNTRY\" = '"+country+"' ";
			}
			
			if(state!=null) {
				SQL += " AND MST.\"STATE\" = '"+state+"' ";
			}
			
			if(region!=null) {
				SQL += " AND MST.\"REGION\" = '"+region+"' ";
			}
			
			if(district!=null) {
				SQL += " AND MST.\"DISTRICT\" = '"+district+"' ";
			}

			
			if(city!=null) {
				SQL += " AND MST.\"CITY\" = '"+city+"' ";
			}
			


			//System.out.println("SQL::" + SQL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ SiteStatusDAO-getSiteStatus ::" + ex.getMessage());
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
	public JSONArray getSalesVolume(String siteIDs, String fromDate, String toDate, String productName,boolean grouping,String userId) {
		JSONArray json = new JSONArray();
		try {
			String SQL = null;
			if(grouping) {
				SQL = " SELECT UPPER(\"PRODUCT_GROUP\") AS \"PRODUCT_NAME\","
						+ " ROUND(sum(\"VOLUME\") / 1000,2) AS \"VOLUME\"  ";
			}else {
				SQL = " SELECT UPPER(\"PRODUCT_NAME\") AS \"PRODUCT_NAME\","
						+ " ROUND(sum(\"VOLUME\") / 1000,\"DEC_VAL\") AS \"VOLUME\",ROUND(SUM(\"AMOUNT\"),\"DEC_VAL\") AS \"AMOUNT\" , CUR.\"CURRENCY_CODE\" , CUR.\"DEC_VAL\" ";
			}
			/*
			SQL+= " FROM "+schema+".\"TRANSACTIONS\"  "
					+ "INNER JOIN "+schema+".\"MS_PRODUCTS\" PRD ON PRD.\"PRODUCT_NO\" = \"TRANSACTIONS\".\"PRODUCT_NO\" "
							+ " AND  \"TRANSACTIONS\".\"SITE_ID\" = PRD.\"SITE_ID\" "
							+ " INNER JOIN "+schema+".\"MS_SITE\" MST ON PRD.\"SITE_ID\" = MST.\"SITE_ID\"  "
							+ " INNER JOIN \"BCT\".\"MS_COUNTRY\" AS CUR ON MST.\"COUNTRY\" = CUR.\"COUNTRY\" "
							+ " INNER JOIN \"BCT\".\"COUNTRY_PRODUCT_CONFIG\" CPC ON CPC.\"COUNTRY_ID\" = CUR.\"COUNTRY_ID\" AND CPC.\"PRODUCT\" = PRD.\"PRODUCT_NAME\" "
					+ "WHERE 1=1 AND DATE(\"TRANSACTION_DATE\") BETWEEN '" + fromDate + "' AND '" + toDate + "' ";
			SQL += " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) ";
			*/
			
			SQL += " FROM "+schema+".\"TRANSACTIONS\" TRN " + 
					" INNER JOIN "+schema+".\"MS_SITE\" MS  ON MS.\"SITE_ID\" = TRN.\"SITE_ID\" " + 
					" INNER JOIN \"BCT\".user_sites US ON US.site_id = MS.\"SITE_ID\" " + 
					" INNER JOIN \"BCT\".\"MS_COUNTRY\" AS CUR ON CUR.\"COUNTRY\" = MS.\"COUNTRY\"  " + 
					" INNER JOIN "+schema+".\"MS_PRODUCTS\" PRD ON PRD.\"SITE_ID\" = TRN.\"SITE_ID\" AND PRD.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\" AND PRD.\"ADRM_STATUS\" != 'D' " + 
					" INNER JOIN \"BCT\".\"COUNTRY_PRODUCT_CONFIG\" CPC ON CPC.\"PRODUCT\" = PRD.\"PRODUCT_NAME\" " + 
					" WHERE 1=1  " + 
					" AND US.user_id = '"+userId+"' " + 
					" AND CPC.\"COUNTRY_ID\"=CUR.\"COUNTRY_ID\" " +
					" AND TRN.\"TRANSACTION_DATE\"::timestamp::date BETWEEN ('"+fromDate+"') AND ('"+toDate+"') "; 
					//" AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 " + 
					//"							WHERE PRD1.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = TRN.\"SITE_ID\" ) ";
			
			if (siteIDs != null  && siteIDs.contains("','")) {
				SQL += " AND TRN.\"SITE_ID\" IN (" + siteIDs + ")";
			}

			if (productName != null) {
				SQL += " AND UPPER(\"PRODUCT_NAME\") AS \"PRODUCT_NAME\" ='" + productName + "'";
			}

			//SQL += "GROUP BY \"TRANSACTIONS\".\"SITE_ID\",\"TRANSACTIONS\".\"PRODUCT_NO\",\"PRODUCT_NAME\" , CUR.\"CURRENCY_CODE\",CUR.\"DEC_VAL\" ";
			if(grouping) {
				SQL += "GROUP BY \"PRODUCT_GROUP\" ";
			}else {
				SQL += "GROUP BY \"PRODUCT_NAME\" , CUR.\"CURRENCY_CODE\",CUR.\"DEC_VAL\" ";
			}


			//System.out.println("\n\n\n\n");
			//System.out.println("getSalesVolume>>>>>>>>>>>>>>>>>>"+SQL);
			//System.out.println("\n\n\n\n");
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			
			json = dbc.parseRS(rs);

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ SiteStatusDAO-getSalesVolume ::" + ex.getMessage());
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
	public int getSalesVolume(String siteIDs, String fromDate, String toDate,String userId) {
		int siteReceivedCount = 0;
		try {
			String SQL = null;
		
			SQL = " SELECT TRN.\"SITE_ID\" FROM "+schema+".\"TRANSACTIONS\"  TRN " + 
					" INNER JOIN "+schema+".\"MS_SITE\" MS  ON MS.\"SITE_ID\" = TRN.\"SITE_ID\" " + 
					" INNER JOIN \"BCT\".user_sites US ON US.site_id = MS.\"SITE_ID\" " + 
					" WHERE 1 = 1  " + 
					" AND US.user_id = '"+userId+"' ";
			if (siteIDs != null && siteIDs.contains("','")) {
					SQL += " AND TRN.\"SITE_ID\" IN ("+siteIDs+")  ";
			}
			
			SQL +=" AND \"TRANSACTION_DATE\"::DATE BETWEEN ('"+fromDate+"') AND ('"+toDate+"') " + 
					" GROUP BY TRN.\"SITE_ID\" ";

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			while(rs.next()) {
				siteReceivedCount++;
			}
	
			stmt = null;
			rs = null;
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ SiteStatusDAO-getSalesVolume ::" + ex.getMessage());
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
	public JSONArray getNilSales(String siteIDs, String fromDate, String toDate,
			String productName,String country,String state,String region,String district,
			String city,boolean grouping) {
		JSONArray json = new JSONArray();
		try {
			String SQL = null;
			if (!grouping) {
				SQL = " SELECT ROW_NUMBER () OVER (ORDER BY TX.\"SITE_ID\") AS \"SNO\"  ,  TX.\"SITE_ID\", UPPER(\"PRODUCT_NAME\") AS \"PRODUCT_NAME\", \"TRANSACTION_DATE\" AS \"LAST_TRANSACTION\" ,"
						+ " ROUND( CAST(FLOAT8 (EXTRACT(EPOCH FROM current_timestamp-\"TRANSACTION_DATE\")/3600)AS NUMERIC),2) AS \"LAST_TRANSACTION(HH:MM)\" " + 
						" FROM "+schema+".\"TRANSACTIONS\" TX " + 
						" INNER JOIN "+schema+".\"MS_PRODUCTS\" PD ON PD.\"PRODUCT_NO\" = TX.\"PRODUCT_NO\"  AND PD.\"SITE_ID\" = TX.\"SITE_ID\" " + 
						" INNER JOIN "+schema+".\"MS_SITE\" MST ON MST.\"SITE_ID\" = TX.\"SITE_ID\" " +
						" WHERE ROW(TX.\"SITE_ID\",TX.\"PRODUCT_NO\",\"TRANSACTION_DATE\") IN ( " + 
						" 		SELECT \"SITE_ID\",\"PRODUCT_NO\",MAX(\"TRANSACTION_DATE\") FROM "+schema+".\"TRANSACTIONS\" "
								+ " WHERE 1=1 AND \"SITE_ID\" IN ("+siteIDs+") " + 
						" 		GROUP BY \"SITE_ID\",\"PRODUCT_NO\") "  + 
						" AND (current_timestamp - interval '2 hour') > \"TRANSACTION_DATE\" ";
			}else {
				SQL = " SELECT ROW_NUMBER () OVER (ORDER BY \"PRODUCT_NAME\") AS \"SNO\"  , UPPER(\"PRODUCT_NAME\") AS \"PRODUCT_NAME\" ,COUNT(\"PRODUCT_NAME\") AS \"COUNT\" " + 
						" FROM "+schema+".\"TRANSACTIONS\" TX " + 
						" INNER JOIN "+schema+".\"MS_PRODUCTS\" PD ON PD.\"PRODUCT_NO\" = TX.\"PRODUCT_NO\"  AND PD.\"SITE_ID\" = TX.\"SITE_ID\" AND PD.\"ADRM_STATUS\" != 'D' " + 
						" INNER JOIN "+schema+".\"MS_SITE\" MST ON MST.\"SITE_ID\" = TX.\"SITE_ID\" " +
						" WHERE ROW(TX.\"SITE_ID\",TX.\"PRODUCT_NO\",\"TRANSACTION_DATE\") IN ( " + 
						" 		SELECT \"SITE_ID\",\"PRODUCT_NO\",MAX(\"TRANSACTION_DATE\") FROM "+schema+".\"TRANSACTIONS\" "
								+ " WHERE 1=1 AND \"SITE_ID\" IN ("+siteIDs+") " + 
						" 		GROUP BY \"PRODUCT_NO\" ,\"SITE_ID\"  ) "  + 
						" AND (current_timestamp - interval '2 hour') > \"TRANSACTION_DATE\" ";
			}
			//SQL += " AND PD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PD.\"SITE_ID\" ) ";

			if (siteIDs != null) {
				SQL += " AND TX.\"SITE_ID\" IN (" + siteIDs + ") ";
			}

			if (fromDate != null && toDate != null) {
				SQL += " AND TX.\"TRANSACTION_DATE\"::timestamp::date BETWEEN ('" + fromDate + "') AND ('" + toDate
						+ "')";
			}
			
			if(productName != null) {
				SQL += " AND UPPER(PD.\"PRODUCT_NAME\") = UPPER('"+productName+"') ";
			}
			
			if(country!=null) {
				SQL += " AND MST.\"COUNTRY\" = '"+country+"' ";
			}
			
			if(state!=null) {
				SQL += " AND MST.\"STATE\" = '"+state+"' ";
			}
			
			if(region!=null) {
				SQL += " AND MST.\"REGION\" = '"+region+"' ";
			}
			
			if(district!=null) {
				SQL += " AND MST.\"DISTRICT\" = '"+district+"' ";
			}
			
			if(city!=null) {
				SQL += " AND MST.\"CITY\" = '"+city+"' ";
			}
			
			if(!grouping)
				SQL += " GROUP BY TX.\"SITE_ID\", \"PRODUCT_NAME\",\"TRANSACTION_DATE\"  ";
			else
				SQL += " GROUP BY \"PRODUCT_NAME\" ";
			
			//System.out.println(SQL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
			
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ SiteStatusDAO-getNilSales ::" + ex.getMessage());
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
	public JSONArray getSiteProducts(String siteIDs,String country,String uID) {
		JSONArray json = new JSONArray();
		try {
			String SQL = null;
			JSONObject jObj = new JSONObject();
						
			SQL = " SELECT CN.\"COUNTRY\",UPPER(\"PRODUCT\") AS \"PRODUCT\",\"COLOUR\" FROM "+schema+".\"MS_SITE\" ST " + 
					" INNER JOIN \"BCT\".\"MS_COUNTRY\" CN ON ST.\"COUNTRY\" = CN.\"COUNTRY\" " + 
					" INNER JOIN \"BCT\".\"COUNTRY_PRODUCT_CONFIG\" PD ON CN.\"COUNTRY_ID\" = PD.\"COUNTRY_ID\" " +
					" INNER JOIN \"BCT\".user_sites US ON US.site_id = ST.\"SITE_ID\" " +
					" WHERE 1=1 AND US.user_id = '"+uID+"'";
			//System.out.println("country=="+country);
			
			if (siteIDs != null) {
				SQL += " AND ST.\"SITE_ID\" IN (" + siteIDs + ") ";
			}
			
			if(country != null) {
				SQL += " AND UPPER(CN.\"COUNTRY\") = UPPER('"+country+"')";
			}

			SQL += " GROUP BY \"PRODUCT\",\"COLOUR\", CN.\"COUNTRY\" " + 
					" ORDER BY CN.\"COUNTRY\",\"PRODUCT\" ";
			//System.out.println(SQL);
			
			JSONArray jArr = new JSONArray();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			JSONObject tmpObj = null;
			while(rs.next()) {
				
				tmpObj = new JSONObject();
				tmpObj.put("PRODUCT", rs.getString("PRODUCT"));
				tmpObj.put("COLOUR", rs.getString("COLOUR"));
				
				jArr.add(tmpObj);
			}
			jObj.put(uID, jArr);

			json.add(jObj);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ SiteStatusDAO-getSiteProducts ::" + ex.getMessage());
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
	public HashMap<String,String> getSiteNames(String siteIDs) {
		HashMap<String,String> sites = new HashMap<String,String>();
		try {
			
			String SQL = " SELECT \"SITE_ID\",\"SITE_NAME\" FROM "+schema+".\"MS_SITE\" WHERE \"SITE_ID\" IN ("+siteIDs+") ";
			
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			while(rs.next()) {
				sites.put(rs.getString("SITE_ID"), rs.getString("SITE_NAME"));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ SiteStatusDAO-getSiteNames ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sites;
	}	
	
	public List<String> getSites() {
		List json = new ArrayList();
		try {

			String SQL = " SELECT \"SITE_ID\" FROM " + schema +".\"MS_SITE\" " + 
					"where \"IP_ADDR\" is not null";
			
			//System.out.println("SQL::" + SQL);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			while(rs.next()) {
			json.add(rs.getString(1));	
			}
		}catch(Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ SiteStatusDAO-getSiteStatus ::" + ex.getMessage());
	
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
