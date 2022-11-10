package com.bct.HOS.App.DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.bct.HOS.App.utils.DBConnector;

import net.sf.json.JSONArray;

public class DeliveryDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;

	public DeliveryDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
	}

	/**
	 * 
	 * @return
	 */
	public JSONArray getDeliveryByFilter(String siteIDs, String fromDate, String toDate,String country,String  state,String  region,String  district,String  city) {
		JSONArray json = new JSONArray();
		try {
			String SQL = " SELECT DD.\"SITE_ID\",MS.\"SITE_NAME\",DD.\"DELIVERY_ID\",DD.\"TANK_NO\", "
					+ " TN.\"MIN_CAPACITY\",TN.\"MAX_CAPACITY\",TN.\"CAPACITY\", "
					+ " DD.\"PRODUCT_NO\",UPPER(PR.\"PRODUCT_NAME\") AS \"PRODUCT_NAME\", " + " DATE(DD.\"START_TIME\") AS \"DELIVERY_DATE\", "
					+ " DD.\"START_TIME\",DD.\"END_TIME\", " + " DD.\"START_VOLUME\",DD.\"START_TC_VOLUME\", "
					+ " DD.\"START_HEIGHT\",DD.\"START_WATER_VOL\",DD.\"START_WATER_HEIGHT\",DD.\"START_TEMP\", "
					+ " DD.\"END_VOLUME\",DD.\"END_TC_VOLUME\",DD.\"END_HEIGHT\",DD.\"END_WATER_VOL\",DD.\"END_WATER_HEIGHT\",DD.\"END_TEMP\", "
					+ " DD.\"DENSITY_VALID\",DD.\"DENSITY_ACTUAL\",DD.\"DENSITY_AT15DEG\", "
					+ " DD.\"NET_VOLUME\",DD.\"STATUS\", "
					+ " DD.\"INVOICE_NO\",DD.\"INVOICE_DATE\",DD.\"INVOICE_QTY\",DD.\"INVOICE_DENSITY\", "
					+ " DD.\"LOCATION_NAME\",DD.\"TRANSPORTER_CODE\",DD.\"TRANSPORTER_NAME\", "
					+ " DD.\"TRUCK_NO\",DD.\"COMPARTMENT_NOS\",DD.\"MANUAL_DENSITY\",DD.\"MANUAL_QTY\", "
					+ " DD.\"CREATED_BY\",DD.\"CREATED_DATE\",DD.\"MODIFIED_BY\",DD.\"MODIFIED_DATE\" "
					+ " FROM "+schema+".\"MS_DELIVERY_DATA\" DD "
					+ " INNER JOIN "+schema+".\"MS_SITE\" MS ON MS.\"SITE_ID\" = DD.\"SITE_ID\" "
					+ " INNER JOIN "+schema+".\"MS_PRODUCTS\" PR ON PR.\"PRODUCT_NO\" = DD.\"PRODUCT_NO\" AND PR.\"ADRM_STATUS\" != 'D' "
					+ " INNER JOIN "+schema+".\"MS_TANK\" TN ON TN.\"TANK_NO\" = DD.\"TANK_NO\" " + " WHERE 1 = 1 ";
			
			//SQL += " AND PR.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PR.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PR.\"SITE_ID\" ) ";

			if (siteIDs != null) {
				SQL += " AND DD.\"SITE_ID\" IN (" + siteIDs + ")";
			}

			if (fromDate != null && toDate != null) {
				SQL += " AND DATE(DD.\"START_TIME\") ::timestamp::date BETWEEN '" + fromDate + "' AND '" + toDate + "' ";
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

			SQL += " GROUP BY DD.\"SITE_ID\",MS.\"SITE_NAME\",DD.\"DELIVERY_ID\",DD.\"TANK_NO\", "
					+ " TN.\"MIN_CAPACITY\",TN.\"MAX_CAPACITY\",TN.\"CAPACITY\", "
					+ " DD.\"PRODUCT_NO\",PR.\"PRODUCT_NAME\", " + " DD.\"START_TIME\",DD.\"END_TIME\", "
					+ " DD.\"START_VOLUME\",DD.\"START_TC_VOLUME\", "
					+ " DD.\"START_HEIGHT\",DD.\"START_WATER_VOL\",DD.\"START_WATER_HEIGHT\",DD.\"START_TEMP\", "
					+ " DD.\"END_VOLUME\",DD.\"END_TC_VOLUME\",DD.\"END_HEIGHT\",DD.\"END_WATER_VOL\",DD.\"END_WATER_HEIGHT\",DD.\"END_TEMP\", "
					+ " DD.\"DENSITY_VALID\",DD.\"DENSITY_ACTUAL\",DD.\"DENSITY_AT15DEG\", "
					+ " DD.\"NET_VOLUME\",DD.\"STATUS\", "
					+ " DD.\"INVOICE_NO\",DD.\"INVOICE_DATE\",DD.\"INVOICE_QTY\",DD.\"INVOICE_DENSITY\", "
					+ " DD.\"LOCATION_NAME\",DD.\"TRANSPORTER_CODE\",DD.\"TRANSPORTER_NAME\", "
					+ " DD.\"TRUCK_NO\",DD.\"COMPARTMENT_NOS\",DD.\"MANUAL_DENSITY\",DD.\"MANUAL_QTY\", "
					+ " DD.\"CREATED_BY\",DD.\"CREATED_DATE\",DD.\"MODIFIED_BY\",DD.\"MODIFIED_DATE\" "
					+ " ORDER BY DD.\"SITE_ID\",DD.\"DELIVERY_ID\",DD.\"TANK_NO\", "
					+ " DD.\"PRODUCT_NO\",PR.\"PRODUCT_NAME\", " + " DD.\"START_TIME\" DESC";

			////System.out.println(SQL);

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ DeliveryDAO-getDeliveryByFilter ::" + ex.getMessage());
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
	public JSONArray getIrrTotalizer(String siteIDs, String fromDate, String toDate,String country,String  state,String  region,String  district,String  city) {
		JSONArray json = new JSONArray();
		try {
			String SQL = "  WITH cte AS (  " + 
					"	SELECT TRN.\"SITE_ID\",\"PUMP_NO\",\"NOZZLE_NO\",\"TRANSACTION_DATE\",\"START_TOTALIZER\",\"END_TOTALIZER\",UPPER(\"PRODUCT_NAME\") AS \"PRODUCT_NAME\"  " + 
					"	FROM "+schema+".\"TRANSACTIONS\" TRN  " + 
					"	INNER JOIN "+schema+".\"MS_PRODUCTS\" PRD ON PRD.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\" AND PRD.\"SITE_ID\" = TRN.\"SITE_ID\"  AND PRD.\"ADRM_STATUS\" != 'D' " + 
					"	WHERE TRN.\"SITE_ID\" IN ("+siteIDs+")  " + 
					"	AND \"TRANSACTION_DATE\" ::DATE BETWEEN '"+fromDate+"' AND '"+toDate+"' "+ 
					//" AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 " + 
					//" WHERE PRD1.\"PRODUCT_NO\" = TRN.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = TRN.\"SITE_ID\" ) " +
					"	group BY TRN.\"SITE_ID\",\"PUMP_NO\",\"NOZZLE_NO\",\"TRANSACTION_DATE\",\"START_TOTALIZER\",\"END_TOTALIZER\",PRD.\"PRODUCT_NAME\"  " + 
					") ,CTE2 AS (  " + 
					"SELECT  " + 
					"	\"SITE_ID\",\"PUMP_NO\",\"NOZZLE_NO\",\"TRANSACTION_DATE\",\"START_TOTALIZER\",\"END_TOTALIZER\", UPPER(\"PRODUCT_NAME\") AS \"PRODUCT_NAME\",  " + 
					"	LAG(\"END_TOTALIZER\",1) OVER (  " + 
					"		ORDER BY \"SITE_ID\",\"PUMP_NO\",\"NOZZLE_NO\",\"TRANSACTION_DATE\" ASC  " + 
					"	) \"PREVIOUS_END_TOTALIZER\",  " + 
					"	LAG(\"PUMP_NO\",1) OVER (  " + 
					"		ORDER BY \"SITE_ID\",\"PUMP_NO\",\"NOZZLE_NO\",\"TRANSACTION_DATE\" ASC  " + 
					"	) \"PREVIOUS_PUMP_NO\",  " + 
					"	LAG(\"NOZZLE_NO\",1) OVER (  " + 
					"		ORDER BY \"SITE_ID\",\"PUMP_NO\",\"NOZZLE_NO\",\"TRANSACTION_DATE\" ASC  " + 
					"	) \"PREVIOUS_NOZZLE_NO\"  " + 
					"FROM  " + 
					"	cte  " + 
					"	)  " + 
					"SELECT   ROW_NUMBER () OVER (ORDER BY \"SITE_ID\") AS \"SNO\" , " + 
					"	\"SITE_ID\",\"PUMP_NO\",\"NOZZLE_NO\",\"TRANSACTION_DATE\",\"START_TOTALIZER\",\"PREVIOUS_END_TOTALIZER\",  " + 
					"	(\"START_TOTALIZER\" - \"PREVIOUS_END_TOTALIZER\") AS \"DIFFERENCE\",UPPER(\"PRODUCT_NAME\") AS \"PRODUCT_NAME\"   " + 
					"	from cte2   " + 
					"	WHERE \"SITE_ID\" IN ("+siteIDs+")  " + 
					"	AND (\"START_TOTALIZER\" - \"PREVIOUS_END_TOTALIZER\") != 0  " + 
					"	AND \"PREVIOUS_PUMP_NO\" = \"PUMP_NO\"  " + 
					"	AND \"PREVIOUS_NOZZLE_NO\" = \"NOZZLE_NO\"  " + 
					"	AND \"TRANSACTION_DATE\" ::DATE BETWEEN '"+fromDate+"' AND '"+toDate+"'" ;  
				
				//SQL += " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) ";
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

			SQL += " ORDER BY \"SITE_ID\",\"PUMP_NO\",\"NOZZLE_NO\",\"TRANSACTION_DATE\" ASC ";

			//System.out.println(SQL);

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			json = dbc.parseRS(rs);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ DeliveryDAO-getIrrTotalizer ::" + ex.getMessage());
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
