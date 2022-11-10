package com.bct.HOS.App.DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bct.HOS.App.DAO.AnalysisDAO.Record;
import com.bct.HOS.App.utils.DBConnector;

public class PDNReceiptReportDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;
	private long unitConversion = 0;
	private DecimalFormat myFormatter = new DecimalFormat("###,###.000");
	
	public PDNReceiptReportDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
		unitConversion = dbc.getUnitConversion();
	}

	/**
	 * 
	 * @return
	 */
	public HashMap pdnReceiptReport(String startDate,String endDate,String stationId) {
		HashMap mainMap = new HashMap();
		try {
			Statement st = null;
			ResultSet rs = null;
						
			String SQL = " select site_id,\"SITE_NAME\",pdn_no,prod_code,product_name,DATE_TRUNC('day', pdn_loaded_date)::DATE as pdn_loaded_date,loaded_qty,"
				+	"DATE_TRUNC('day', delivery_date)::DATE as delivery_date,delivered_qty,\"TANK_NO\",\"PRODUCT_NO\",\"END_VOLUME\"-\"START_VOLUME\" as \"NET_QTY\",\"INVOICE_NO\","
				+	"DATE_TRUNC('day', \"INVOICE_DATE\")::DATE as \"INVOICE_DATE\",\"INVOICE_QTY\" from \"ALMAHA\".\"VW_PDN_RECEIPT\" where site_id='"+stationId+"' and DATE(pdn_loaded_date) BETWEEN to_date('"+startDate+"','DD,MM,YYY') " +
					"AND  to_date('" + endDate + "','DD,MM,YYY')";
			
			System.out.println("SQL == "+SQL);
			st = conn.createStatement();
			rs = st.executeQuery(SQL);
			String pdnNo = null;
			String prodCode = null;
			String prodName = null;
			String loadedDt = null;
			String loadedQty = null;
			String deliveryDt = null;			
			String deliveryQty = null;
			String tankNo = null;
			String stVol = null;
			String endVol = null;
			String netQty = null;
			String invoNo = null;
			String invoDate = null;
			String invoQty = null;
			List bodyList = new ArrayList();
			Map innerMap = null;

			while(rs.next()) {
				innerMap = new HashMap();
				//siteId = rs.getString("site_id");
				//siteName = rs.getString("SITE_NAME");
				pdnNo = rs.getString("pdn_no");
				prodCode = rs.getString("prod_code");
				prodName = rs.getString("product_name");
				loadedDt = rs.getString("pdn_loaded_date");
				loadedQty = rs.getString("loaded_qty");
				deliveryDt = rs.getString("delivery_date");
				deliveryQty = rs.getString("delivered_qty");
				tankNo = rs.getString("TANK_NO");
				netQty = rs.getString("NET_QTY");
				invoNo = rs.getString("INVOICE_NO");
				invoDate = rs.getString("INVOICE_DATE");
				invoQty = rs.getString("INVOICE_QTY");
				innerMap.put("pdn_no", pdnNo);
				innerMap.put("prod_code", prodCode);
				innerMap.put("prod_name", prodName);
				innerMap.put("loaded_date", loadedDt);
				innerMap.put("loaded_qty", loadedQty);
				innerMap.put("deliver_date", deliveryDt);
				innerMap.put("deliver_qty", deliveryQty);
				innerMap.put("tank_no", tankNo);
				innerMap.put("net_qty", netQty);
				innerMap.put("invo_no", invoNo);
				innerMap.put("invo_date", invoDate);
				innerMap.put("invo_qty", invoQty);
				bodyList.add(innerMap);
			}
			
			
			Map headerMap = getHeaderMap();
			mainMap.put("header", headerMap);
			Collections.sort(bodyList, new SortByDate());
			mainMap.put("bodylist",bodyList);
			 
			System.out.println("mainMap >>> "+mainMap);
							
			
			
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
		return mainMap;
	}
	
	private Map getHeaderMap() {
		Map mainMap = new LinkedHashMap();
		List mainList = new ArrayList();
		Map prodMap = new HashMap();
		prodMap.put("field", "loaded_date");
		prodMap.put("headerName", "LOADED DATE");
		mainList.add(prodMap);
		prodMap = new HashMap();
		prodMap.put("field", "pdn_no");
		prodMap.put("headerName", "PDN NO");
		mainList.add(prodMap);
		prodMap = new HashMap();
		prodMap.put("field", "prod_code");
		prodMap.put("headerName", "PRODUCT CODE");
		mainList.add(prodMap);
		prodMap = new HashMap();
		prodMap.put("field", "prod_name");
		prodMap.put("headerName", "PRODUCT NAME");
		mainList.add(prodMap);
		prodMap = new HashMap();
		prodMap.put("field", "loaded_qty");
		prodMap.put("headerName", "LOADED QTY");
		mainList.add(prodMap);
		prodMap = new HashMap();
		prodMap.put("field", "deliver_date");
		prodMap.put("headerName", "DELIVERED DATE");
		mainList.add(prodMap);
		prodMap = new HashMap();
		prodMap.put("field", "deliver_qty");
		prodMap.put("headerName", "DELIVERED QTY");
		mainList.add(prodMap);
		prodMap = new HashMap();
		prodMap.put("field", "tank_no");
		prodMap.put("headerName", "TANK NO");
		mainList.add(prodMap);
		prodMap = new HashMap();
		prodMap.put("field", "net_qty");
		prodMap.put("headerName", "NET QTY");
		mainList.add(prodMap);
		
		prodMap = new HashMap();
		prodMap.put("field", "invo_no");
		prodMap.put("headerName", "INVOICE NO");
		mainList.add(prodMap);
		prodMap = new HashMap();
		prodMap.put("field", "invo_date");
		prodMap.put("headerName", "INVOICE DATE");
		mainList.add(prodMap);
		prodMap = new HashMap();
		prodMap.put("field", "invo_qty");
		prodMap.put("headerName", "INVOICE QTY");
		
		mainList.add(prodMap);
		mainMap.put("header",mainList);
		return mainMap;
	}
	
	
	private List formatNumberBody(List bodyMap) {
		Iterator<Map> iterator = bodyMap.iterator();
		while(iterator.hasNext()) {
			Map record = iterator.next();
			Set<Entry<String, String>> entrySet = record.entrySet();
            for(Entry entry : entrySet) {
                String key = (String) entry.getKey();
                if(key.endsWith("_litre")) {
                	float value = (float) entry.getValue();
                	double dbval = Double.valueOf(value);
                	String output = dbval==0.0D ? "0.000" : myFormatter.format(dbval);
                	entry.setValue(output);
                }
            }
		}
		return bodyMap;
	}
	
	
	private Map formatNumberFooter(Map footerMap) {
		
		Set<Entry<String, String>> entrySet = footerMap.entrySet();
		for(Entry entry : entrySet) {
            String key = (String) entry.getKey();
            if(key.endsWith("_litre")) {
            	float value = (float) entry.getValue();
            	double dbval = Double.valueOf(value);
            	String output = myFormatter.format(dbval);
            	entry.setValue(output);
            }
		}
		return footerMap;
	}
	
	
		 class SortByDate implements Comparator<Map> {
		        @Override
		        public int compare(Map first, Map second) {
		        	String firstDate = (String) first.get("loaded_date");
		        	String secondDate = (String) second.get("loaded_date");
		            return secondDate.compareTo(firstDate);
		        }
			}
}
