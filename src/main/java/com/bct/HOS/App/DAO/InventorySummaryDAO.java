package com.bct.HOS.App.DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import com.bct.HOS.App.utils.DBConnector;

public class InventorySummaryDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;
	private DecimalFormat myFormatter = new DecimalFormat("###,###.000");
	
	public InventorySummaryDAO() {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
	}

		
	public String fuelInventorySummaryNavAPI(String startDate,String endDate) {
		StringBuffer bodyXMLStr = new StringBuffer();
		Statement st = null;
		ResultSet rs = null;
		Map<String, String> productIdMap = new HashMap<>();
		productIdMap.put("MOGAS 95","PR0001");
		productIdMap.put("MOGAS 91","PR0002");
		productIdMap.put("DIESEL","PR0003");
		productIdMap.put("MOGAS 98","PR0004");
		try {
			String  SQL = "SELECT INV.\"SITE_ID\",PRD.\"PRODUCT_NAME\",SUM(\"VOLUME\") AS \"CLOSING_STOCK\" "
				+ "	FROM \"ALMAHA\".\"INVENTORY\" INV "
				+ "	INNER JOIN \"ALMAHA\".\"MS_TANK\" TNK ON TNK.\"TANK_NO\" = INV.\"TANK_NO\" AND INV.\"SITE_ID\"=TNK.\"SITE_ID\" "
				+ "	INNER JOIN \"ALMAHA\".\"MS_PRODUCTS\" PRD ON PRD.\"PRODUCT_NO\" = TNK.\"PRODUCT_NO\" AND PRD.\"SITE_ID\"=TNK.\"SITE_ID\" AND PRD.\"ADRM_STATUS\" != 'D' "
				+ "	WHERE \"INVENTORY_DATE\" = (SELECT MIN(\"INVENTORY_DATE\") "
				+ "	FROM \"ALMAHA\".\"INVENTORY\" INV1 "
				+ "	WHERE \"INVENTORY_DATE\"::DATE = INV.\"INVENTORY_DATE\"::DATE "
				+ "	AND INV1.\"SITE_ID\"=INV.\"SITE_ID\") "
				+ "	AND INV.\"INVENTORY_DATE\"::DATE = '"+startDate+"'::DATE + 1 "
				//+ "	AND PRD.\"MODIFIED_DATE\"=(SELECT MAX(\"MODIFIED_DATE\") FROM \"ALMAHA\".\"MS_PRODUCTS\" PRD1 WHERE "
				//+ "		 PRD1.\"PRODUCT_NO\" = TNK.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = INV.\"SITE_ID\" ) "
				+ "	AND TNK.\"MODIFIED_TIME\" = (SELECT MAX(\"MODIFIED_TIME\") FROM \"ALMAHA\".\"MS_TANK\" TNK1 WHERE "
				+ "		   TNK1.\"SITE_ID\" = INV.\"SITE_ID\"  AND TNK1.\"TANK_NO\"=INV.\"TANK_NO\" ) "
				+ "	GROUP BY INV.\"SITE_ID\",PRD.\"PRODUCT_NAME\"";
			System.out.println("SQL ==> "+SQL);
			st = conn.createStatement();
			rs = st.executeQuery(SQL);
			String dateTime = startDate.replaceAll("-", "/") + " 23:59";
			while(rs.next()) {
				String stockLtr = rs.getString("CLOSING_STOCK");
				String siteId = rs.getString("SITE_ID");
				String prodName = rs.getString("PRODUCT_NAME");
				String recXML = generateXMLRecord(dateTime,prodName,stockLtr,siteId,productIdMap);
				bodyXMLStr.append(recXML);
			}
			System.out.println("Fuel Inventory XML geneated success for Navision");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ Fuel Inventory Summary for Navision ::" + ex.getMessage());
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
	
	private String generateXMLRecord(String startDate,String productName,String closingStock,String siteId,
			Map<String, String> productIdMap) {
		StringBuffer result = new StringBuffer();
		result.append("<NAV_FuelInventory>");
		result.append("<closing_date_time>"+startDate+"</closing_date_time>");
		result.append("<localtion_code>"+siteId+"</localtion_code>");
		result.append("<product_code>"+productIdMap.get(productName)+"</product_code>");
		result.append("<description>"+productName+"</description>");
		result.append("<physical_quantity>"+closingStock+"</physical_quantity>");
		result.append("<unit_of_measure>"+ "LTS" +"</unit_of_measure>");
		result.append("</NAV_FuelInventory>");
		return result.toString();
	}
	
}
