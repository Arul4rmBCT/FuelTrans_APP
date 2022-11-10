package com.bct.HOS.App.DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import com.bct.HOS.App.utils.DBConnector;

public class PdnNoteSummaryDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;
	private DecimalFormat myFormatter = new DecimalFormat("###,###.000");
	
	public PdnNoteSummaryDAO() {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
	}

		
	public String pdnNoteSummaryNavAPI(String startDate,String endDate) {
		StringBuffer bodyXMLStr = new StringBuffer();
		Statement st = null;
		ResultSet rs = null;
		Map<String, String> productIdMap = new HashMap<>();
		productIdMap.put("MOGAS 95","PR0001");
		productIdMap.put("MOGAS 91","PR0002");
		productIdMap.put("DIESEL","PR0003");
		productIdMap.put("MOGAS 98","PR0004");
		try {
			String  SQL = "select \"SITE_ID\",\"DELIVERY_ID\",\"TANK_NO\",\"PRODUCT_NO\",\"START_TIME\" as delivery_time,\"INVOICE_NO\",\"INVOICE_DATE\",\"INVOICE_QTY\",\"INVOICE_DENSITY\" from "+ schema +"." +"\"MS_DELIVERY_DATA\" where \"CREATED_DATE\" BETWEEN '"+startDate+"' AND '"+startDate+"' ";
			System.out.println("SQL ==> "+SQL);
			st = conn.createStatement();
			rs = st.executeQuery(SQL);
			String dateTime = startDate.replaceAll("-", "/") + " 23:59";
			while(rs.next()) {
				String siteId = rs.getString("SITE_ID");
				String deliveryId = rs.getString("DELIVERY_ID");
				String tankNo = rs.getString("TANK_NO");
				String prodId = rs.getString("PRODUCT_NO");
				String deliTime = rs.getString("delivery_time");
				String prodName = rs.getString("product_name");
				String invNo = rs.getString("INVOICE_NO");
				String invDate = rs.getString("INVOICE_DATE");
				String invQty = rs.getString("INVOICE_QTY");
				String invDen = rs.getString("INVOICE_DENSITY");
				String recXML = generateXMLRecord(dateTime,deliveryId,siteId,tankNo,deliTime,prodName,invNo,invDate,invQty,invDen,productIdMap);
				bodyXMLStr.append(recXML);
			}
			System.out.println("Delivery Note XML geneated success for Navision");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ Delivery Note Summary for Navision ::" + ex.getMessage());
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
	
	private String generateXMLRecord(String dateTime,String deliveryId,String siteId,String tankNo,String deliTime,String prodName,String invoNo, String invDate,String invQty,String invDen,
			Map<String, String> productIdMap) {
		StringBuffer result = new StringBuffer();
		result.append("<NAV_DeliveryNote>");
		result.append("<delivery_id>"+deliveryId+"</delivery_id>");
		result.append("<localtion_code>"+siteId+"</localtion_code>");
		result.append("<product_code>"+productIdMap.get(prodName)+"</product_code>");
		result.append("<prod_description>"+prodName+"</prod_description>");
		result.append("<tank_no>" + tankNo + "</physical_quantity>");
		result.append("<delivery_time>"+ deliTime +"</delivery_time>");
		result.append("<invoice_no>"+ invoNo +"</invoice_no>");
		result.append("<invoice_date>"+ invDate +"</invoice_date>");
		result.append("<invoice_quantity>"+ invQty +"</invoice_quantity>");
		result.append("<invoice_density>"+ invDen +"</invoice_density>");
		result.append("</NAV_DeliveryNote>");
		return result.toString();
	}
	
}
