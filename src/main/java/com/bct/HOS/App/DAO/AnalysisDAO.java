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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bct.HOS.App.utils.DBConnector;

import net.sf.json.JSONArray;

public class AnalysisDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;
	private long unitConversion = 0;
	private DecimalFormat myFormatter = new DecimalFormat("###,###.000");
	
	public AnalysisDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
		unitConversion = dbc.getUnitConversion();
	}

	
	
	/**
	 * 
	 * @return
	 */
	public JSONArray salesVolumeComparision(String siteIDs, String country, int currentYear, int previousYear,
			int currentMonth, int previousMonth, int currentQuarter, int previousQuarter, boolean productFilter,
			boolean regionFilter) {
		JSONArray json = new JSONArray();
		try {
			String SQL = "SELECT "
					// + "ROW_NUMBER () OVER (ORDER BY T1.\"COUNTRY\") AS \"SNO\","
					+ "T1.\"COUNTRY\", ";
			if (productFilter)
				SQL += " T1.\"PRODUCT_NAME\", ";
			if (regionFilter)
				SQL += " T1.\"REGION\", ";
			
			SQL +=  " SUM(T1.\"TOTAL_VOLUME\") AS \"COMPARISION PERIOD 1 SALES(Ltr)\", "
					+ " COALESCE(PP.\"PREVIOUS\",0) AS \"COMPARISION PERIOD 2 - SALES(Ltr)\", "
					+ " CAST( ( COALESCE(SUM(T1.\"TOTAL_VOLUME\"),0)  - COALESCE(PP.\"PREVIOUS\",0) ) AS NUMERIC) AS \"DELTA(Ltr)\", "
					+ " CASE WHEN COALESCE(PP.\"PREVIOUS\",0) > 0 THEN  "
					+ "	ROUND(COALESCE(( COALESCE(SUM(T1.\"TOTAL_VOLUME\"),0) - COALESCE(PP.\"PREVIOUS\",0) ) / COALESCE(PP.\"PREVIOUS\",1),0) * 100,2) "
					+ " ELSE '0' END AS \"PERCENTAGE\" " + " FROM " + schema + ".\"SALES_MONTH_REPORTING\" T1 " 
					+ " LEFT OUTER JOIN ( " + "	SELECT \"COUNTRY\",SUM(\"TOTAL_VOLUME\") AS \"PREVIOUS\" ";
			if (productFilter)
				SQL += " ,\"PRODUCT_NAME\" ";
			if (regionFilter)
				SQL += " ,\"REGION\" ";
			SQL += "	FROM " + schema + ".\"SALES_MONTH_REPORTING\" T2 " 
					+ "	WHERE 1 = 1 ";
			if (previousMonth > 0 && currentMonth > 0)
				SQL += "	AND date_part('MONTH',\"DATE\") =  " + previousMonth;
			if (previousQuarter > 0 && currentQuarter > 0)
				SQL += "	AND EXTRACT(QUARTER FROM \"DATE\") =  " + previousQuarter;
			SQL += "	AND date_part('YEAR',\"DATE\") =  " + previousYear;
			if (country != null)
				SQL += "	AND \"COUNTRY\" = '" + country + "' ";
			if (siteIDs != null)
				SQL += "	AND \"SITE_ID\" IN (" + siteIDs + ") ";
			SQL += "	GROUP BY \"COUNTRY\" ";
			if (productFilter)
				SQL += "	,\"PRODUCT_NAME\" ";
			if (regionFilter)
				SQL += "	,\"REGION\" ";
			SQL += " ) AS PP ON PP.\"COUNTRY\" = T1.\"COUNTRY\"  ";
			if (productFilter)
				SQL += " AND PP.\"PRODUCT_NAME\"= T1.\"PRODUCT_NAME\"  ";
			if (regionFilter)
				SQL += " AND PP.\"REGION\" = T1.\"REGION\" ";
			SQL += " WHERE 1 = 1 ";
			if (previousMonth > 0 && currentMonth > 0)
				SQL += "	AND date_part('MONTH',T1.\"DATE\") =  " + currentMonth;
			if (previousQuarter > 0 && currentQuarter > 0)
				SQL += "	AND EXTRACT(QUARTER FROM T1.\"DATE\") =  " + currentQuarter;
			SQL += " AND date_part('YEAR',T1.\"DATE\") =  " + currentYear+
				   " AND T1.\"DATE\"::DATE < NOW() - INTERVAL '1 DAY' ";
			if (country != null)
				SQL += "	AND T1.\"COUNTRY\" = '" + country + "' ";
			if (siteIDs != null)
				SQL += "	AND T1.\"SITE_ID\" IN (" + siteIDs + ") ";
			SQL += " GROUP BY T1.\"COUNTRY\",PP.\"PREVIOUS\" ";
			if (productFilter)
				SQL += "	,T1.\"PRODUCT_NAME\" ";
			if (regionFilter)
				SQL += "	,T1.\"REGION\" ";
			//System.out.println("SQL Analysis ::: "+SQL);
			if (SQL != null) {
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(SQL);
				json = dbc.parseRS(rs);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ AnalysisDAO-salesVolumeComparision ::" + ex.getMessage());
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
	public JSONArray getSiteLevelSalesComparision(String siteIDs, String country, int currentYear, int previousYear,
			int currentMonth, int previousMonth, int currentQuarter, int previousQuarter, boolean productFilter,
			boolean regionFilter) {
		JSONArray json = new JSONArray();
		try {
			String SQL = "SELECT "
					// + "ROW_NUMBER () OVER (ORDER BY T1.\"COUNTRY\") AS \"SNO\","
					+ "T1.\"SITE_ID\",T1.\"SITE_NAME\",T1.\"COUNTRY\", ";
			if (productFilter)
				SQL += " T1.\"PRODUCT_NAME\", ";
			if (regionFilter)
				SQL += " T1.\"REGION\", ";
			
			SQL +=  " SUM(T1.\"TOTAL_VOLUME\") AS \"COMPARISION PERIOD 1 SALES(Ltr)\", "
					+ " COALESCE(PP.\"PREVIOUS\",0) AS \"COMPARISION PERIOD 2 - SALES(Ltr)\", "
					+ " CAST( COALESCE(SUM(T1.\"TOTAL_VOLUME\"),0) - ( COALESCE(PP.\"PREVIOUS\",0)  ) AS NUMERIC) AS \"DELTA(Ltr)\", "
					+ " CASE WHEN COALESCE(PP.\"PREVIOUS\",0) > 0 THEN  "
					+ "	ROUND(COALESCE(( COALESCE(SUM(T1.\"TOTAL_VOLUME\"),0) - COALESCE(PP.\"PREVIOUS\",0) ) / COALESCE(PP.\"PREVIOUS\",1),0) * 100,2) "
					+ " ELSE '0' END AS \"PERCENTAGE\" " + " FROM " + schema + ".\"SALES_MONTH_REPORTING\" T1 "
					+ " LEFT OUTER JOIN ( " + "	SELECT \"SITE_ID\",\"COUNTRY\",SUM(\"TOTAL_VOLUME\") AS \"PREVIOUS\" ";
			if (productFilter)
				SQL += " ,\"PRODUCT_NAME\" ";
			if (regionFilter)
				SQL += " ,\"REGION\" ";
			SQL += "	FROM " + schema + ".\"SALES_MONTH_REPORTING\" T2 " 
					+ "	WHERE 1 = 1 ";
			if (previousMonth > 0 && currentMonth > 0)
				SQL += "	AND date_part('MONTH',\"DATE\") =  " + previousMonth;
			if (previousQuarter > 0 && currentQuarter > 0)
				SQL += "	AND EXTRACT(QUARTER FROM \"DATE\") =  " + previousQuarter;
			SQL += "	AND date_part('YEAR',\"DATE\") =  " + previousYear;
			if (country != null)
				SQL += "	AND \"COUNTRY\" = '" + country + "' ";
			if (siteIDs != null)
				SQL += "	AND \"SITE_ID\" IN (" + siteIDs + ") ";
			SQL += "	GROUP BY \"SITE_ID\",\"COUNTRY\" ";
			if (productFilter)
				SQL += "	,\"PRODUCT_NAME\" ";
			if (regionFilter)
				SQL += "	,\"REGION\" ";
			SQL += " ) AS PP ON PP.\"COUNTRY\" = T1.\"COUNTRY\"  AND PP.\"SITE_ID\" = T1.\"SITE_ID\" ";
			if (productFilter)
				SQL += " AND PP.\"PRODUCT_NAME\"= T1.\"PRODUCT_NAME\"  ";
			if (regionFilter)
				SQL += " AND PP.\"REGION\" = T1.\"REGION\" ";
			SQL += " WHERE 1 = 1 ";
			if (previousMonth > 0 && currentMonth > 0)
				SQL += "	AND date_part('MONTH',T1.\"DATE\") =  " + currentMonth;
			if (previousQuarter > 0 && currentQuarter > 0)
				SQL += "	AND EXTRACT(QUARTER FROM T1.\"DATE\") =  " + currentQuarter;
			SQL += " AND date_part('YEAR',T1.\"DATE\") =  " + currentYear+
				   " AND T1.\"DATE\"::DATE < NOW() - INTERVAL '1 DAY' ";
			if (country != null)
				SQL += "	AND T1.\"COUNTRY\" = '" + country + "' ";
			if (siteIDs != null)
				SQL += "	AND T1.\"SITE_ID\" IN (" + siteIDs + ") ";
			SQL += " GROUP BY T1.\"SITE_ID\",T1.\"SITE_NAME\",T1.\"COUNTRY\",PP.\"PREVIOUS\" ";
			if (productFilter)
				SQL += "	,T1.\"PRODUCT_NAME\" ";
			if (regionFilter)
				SQL += "	,T1.\"REGION\" ";
			//System.out.println("SQL Analysis ::: "+SQL);
			if (SQL != null) {
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(SQL);
				json = dbc.parseRS(rs);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ AnalysisDAO-getSiteLevelSalesComparision ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return json;
	}
	
	
	public HashMap salesReport1(String startDate,String endDate,String stationName, String type) {
		HashMap mainMap = new HashMap();
		try {
			Statement st = null;
			ResultSet rs = null;
			List<String> productList = new ArrayList<>();
			Map<String,Record> bodyRecordMap = new HashMap<>();
			String SQL = null;
			String stationCriteria = "ALL".equalsIgnoreCase(stationName) ? "" : " AND \"SITE_ID\"='"+stationName+"'";
			//SITE_ID
			if("cash".equalsIgnoreCase(type)) {
				SQL = "SELECT \"PRODUCT_NAME\" AS PRODUCT_NAME, DATE_TRUNC('day', \"TRANSACTION_DATE\")::DATE AS TRANSACTION_DATE, sum(\"VOLUME\") AS LITRES, sum(\"AMOUNT\") AS AMOUNT "+
					"FROM (SELECT * FROM "+ schema + ".\"VW_TRX_SALES\" WHERE DATE(\"TRANSACTION_DATE\") BETWEEN to_date('"+startDate+"','DD,MM,YYY') " +
					"AND  to_date('" + endDate + "','DD,MM,YYY') " + stationCriteria + " AND \"TRANSACTION_TYPE\" = '1' ORDER BY \"TRANSACTION_DATE\" DESC) AS expr_qry GROUP BY \"PRODUCT_NAME\", DATE_TRUNC('day', \"TRANSACTION_DATE\") ORDER BY LITRES DESC";
			} else if("bankCard".equalsIgnoreCase(type)) {
				SQL = "SELECT \"PRODUCT_NAME\" AS PRODUCT_NAME, DATE_TRUNC('day', \"TRANSACTION_DATE\")::DATE AS TRANSACTION_DATE, sum(\"VOLUME\") AS LITRES, sum(\"AMOUNT\") AS AMOUNT "+
						"FROM (SELECT * FROM "+ schema + ".\"VW_TRX_SALES\" WHERE DATE(\"TRANSACTION_DATE\") BETWEEN  to_date('"+startDate+"','DD,MM,YYY') " +
						"AND  to_date('" + endDate + "','DD,MM,YYY') " + stationCriteria + " AND \"TRANSACTION_TYPE\" = '2' ORDER BY \"TRANSACTION_DATE\" DESC) AS expr_qry GROUP BY \"PRODUCT_NAME\", DATE_TRUNC('day', \"TRANSACTION_DATE\") ORDER BY LITRES DESC";
			} else if("fuelCard".equalsIgnoreCase(type)) {
				SQL = "SELECT \"PRODUCT_NAME\" AS PRODUCT_NAME, DATE_TRUNC('day', \"TRANSACTION_DATE\")::DATE AS TRANSACTION_DATE, sum(\"VOLUME\") AS LITRES, sum(\"AMOUNT\") AS AMOUNT "+
						"FROM (SELECT * FROM "+ schema + ".\"VW_TRX_SALES\" WHERE DATE(\"TRANSACTION_DATE\") BETWEEN  to_date('"+startDate+"','DD,MM,YYY') " +
						"AND to_date('" + endDate + "','DD,MM,YYY') " + stationCriteria + " AND \"TRANSACTION_TYPE\" = '6' ORDER BY \"TRANSACTION_DATE\" DESC) AS expr_qry GROUP BY \"PRODUCT_NAME\", DATE_TRUNC('day', \"TRANSACTION_DATE\") ORDER BY LITRES DESC";
			} else if("RFID".equalsIgnoreCase(type)) {
				SQL = "SELECT \"PRODUCT_NAME\" AS PRODUCT_NAME, DATE_TRUNC('day', \"TRANSACTION_DATE\")::DATE AS TRANSACTION_DATE, sum(\"VOLUME\") AS LITRES, sum(\"AMOUNT\") AS AMOUNT "+
						"FROM (SELECT * FROM "+ schema + ".\"VW_TRX_SALES\" WHERE DATE(\"TRANSACTION_DATE\") BETWEEN  to_date('"+startDate+"','DD,MM,YYY') " +
						"AND to_date('" + endDate + "','DD,MM,YYY') " + stationCriteria + " AND \"TRANSACTION_TYPE\" = '17' ORDER BY \"TRANSACTION_DATE\" DESC) AS expr_qry GROUP BY \"PRODUCT_NAME\", DATE_TRUNC('day', \"TRANSACTION_DATE\") ORDER BY LITRES DESC";
			} else {
				SQL = "SELECT \"PRODUCT_NAME\" AS PRODUCT_NAME, DATE_TRUNC('day', \"TRANSACTION_DATE\")::DATE AS TRANSACTION_DATE, sum(\"VOLUME\") AS LITRES, sum(\"AMOUNT\") AS AMOUNT "+
						"FROM (SELECT * FROM "+ schema + ".\"VW_TRX_SALES\" WHERE DATE(\"TRANSACTION_DATE\") BETWEEN to_date('"+startDate+"','DD,MM,YYY') " +
						"AND to_date('"+ endDate + "','DD,MM,YYY') " + stationCriteria +" ORDER BY \"TRANSACTION_DATE\" DESC) AS expr_qry GROUP BY \"PRODUCT_NAME\", DATE_TRUNC('day', \"TRANSACTION_DATE\") ORDER BY LITRES DESC";
			}
			System.out.println("SQL == "+SQL);
			st = conn.createStatement();
			rs = st.executeQuery(SQL);
			while(rs.next()) {
				String productName = rs.getString("PRODUCT_NAME");
				String tranDate = rs.getString("TRANSACTION_DATE");
				float litres = rs.getFloat("LITRES");
				float amount = rs.getFloat("AMOUNT");
				if(!productList.contains(productName)) {
					productList.add(productName);
				}
				addRecord(bodyRecordMap,tranDate,new Product(productName,litres,amount));
			}
			
			
			Map headerMap1 = getHeaderMap(productList, type);
			mainMap.put("header", headerMap1);
			Map jsonMap = getBodyList(bodyRecordMap,type);
			Map footerMap = getFooterMap((List) jsonMap.get("bodylist"));
			Collections.sort((List)jsonMap.get("bodylist"), new SortByDate());
			mainMap.put("bodylist",formatNumberBody((List)jsonMap.get("bodylist")));
			mainMap.put("footer", formatNumberFooter(footerMap));
			processRevalue(startDate,endDate,stationName,(List)jsonMap.get("bodylist"),footerMap); 
			System.out.println("mainMap >>> "+mainMap);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ AnalysisDAO-SaesReprt ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return mainMap;
	}
	
	
	private void processRevalue(String stDate, String endDate, String stCode, List bodyList, Map footerMap) {
		try {
			Statement st = null;
			ResultSet rs = null;
			int totTrans = 0;
			float totalAmt = 0f;
			String SQL = "select inhd.transaction_date AS  \"TRANSACTION_DATE\", count(*) AS \"COUNT\", sum(amount) as \"AMOUNT\" from "+schema+".IncomeHeader inhd "+
					"join "+schema+".IncomeDetails indt on inhd.site_id = indt.site_id and inhd.transaction_no  = indt.transaction_no "+
					"and indt.income_category ='Recharge' and inhd.status ='Submitted' AND DATE_TRUNC('day',inhd.TRANSACTION_DATE)  "+ 
					"BETWEEN to_date('"+stDate+"','dd-mm-yyyy') AND to_date('"+endDate+"','dd-mm-yyyy') and inhd.site_id='"+stCode+"' "+
					"group by inhd.site_id,inhd.transaction_date";
			st = conn.createStatement();
			rs = st.executeQuery(SQL);
			while(rs.next()) {
				String transDate = rs.getString("TRANSACTION_DATE");
				int transCount = rs.getInt("COUNT");
				float amount = rs.getFloat("AMOUNT");
				totTrans = totTrans + transCount;
				totalAmt = totalAmt + amount;
				addRevalueDetails(transDate,transCount,amount,bodyList);
			}
			footerMap.put("revalue_trans", totTrans);
			footerMap.put("revalue_amount", totalAmt);
			
		} catch(Exception ex) {
			System.out.println("ErrOR @ AnalysisDAO-SalesReprt ::" + ex.getMessage());
		}
	}
	
	
	private void addRecord(Map<String,Record> bodyRecordMap, String date, Product product) {
		if(bodyRecordMap.containsKey(date)) {
			Record rec = bodyRecordMap.get(date);
			rec.addProduct(product);
		} else {
			Record rec = new Record(date);
			rec.addProduct(product);
			bodyRecordMap.put(date, rec);
		}
	}
	
	private Map getBodyList(Map<String,Record> bodyRecordMap, String type) {
		
		StringBuffer stbuf = new StringBuffer();
		List mainList = new ArrayList();
		Map mainMap = new HashMap();
		boolean revalFlag = "fuelCard".equalsIgnoreCase(type) ? true : false;
		for (Map.Entry<String, Record> entry : bodyRecordMap.entrySet()) {
			String entryKey = entry.getKey();
			Record entryRec = entry.getValue();
			Map subMap = entryRec.getRecordRowMap();
			if(revalFlag) {
				subMap.put("revalue_trans", 0);
				subMap.put("revalue_amount", 0);
			}
			mainList.add(subMap);
	    }
		mainMap.put("bodylist", mainList);
		return mainMap;
	}
	
	private Map getHeaderMap(List<String> productList, String type) {
		Map mainMap = new HashMap();
		List mainList = new ArrayList();
		Map prodMap = new HashMap();
		Map innerMap = new HashMap();
		List pList = new ArrayList();
		prodMap.put("field", "product");
		prodMap.put("headerName", "PRODUCT NAME");
		innerMap.put("field", "tranx_date");
		innerMap.put("headerName", "TRANSACTION DATE");
		pList.add(innerMap);
		prodMap.put("children", pList);
		mainList.add(prodMap);
		for(String prodItem : productList) {
			prodMap = new HashMap();
			prodMap.put("field", prodItem);
			prodMap.put("headerName", prodItem);
			pList = new ArrayList();
			innerMap = new HashMap();
			innerMap.put("field", prodItem+"_litre");
			innerMap.put("headerName", "LITRE");
			pList.add(innerMap);
			innerMap = new HashMap();
			innerMap.put("field", prodItem+"_amount");
			innerMap.put("headerName", "AMOUNT");
			pList.add(innerMap);
			prodMap.put("children", pList);
			mainList.add(prodMap);
		}
		prodMap = new HashMap();
		prodMap.put("field","total");
		prodMap.put("headerName","TOTAL");
		pList = new ArrayList();
		innerMap = new HashMap();
		innerMap.put("field","total_litre");
		innerMap.put("headerName", "LITRE");
		pList.add(innerMap);
		innerMap = new HashMap();
		innerMap.put("field","total_amount");
		innerMap.put("headerName", "AMOUNT");
		pList.add(innerMap);
		prodMap.put("children", pList);
		mainList.add(prodMap);
		if("fuelCard".equalsIgnoreCase(type)) {
			prodMap = new HashMap();
			prodMap.put("field","revalue");
			prodMap.put("headerName","REVALUE TRANSACTIONS");
			pList = new ArrayList();
			innerMap = new HashMap();
			innerMap.put("field","revalue_trans");
			innerMap.put("headerName", "NO OF TRAN");
			pList.add(innerMap);
			innerMap = new HashMap();
			innerMap.put("field","revalue_amount");
			innerMap.put("headerName", "AMOUNT");
			pList.add(innerMap);
			prodMap.put("children", pList);
			mainList.add(prodMap);
		}
		
		mainMap.put("header",mainList);
		return mainMap;
	}
	
	private Map getFooterMap(List bodyMap) {
		Map resultMap = new HashMap();
		Iterator<Map> iterator = bodyMap.iterator();
		while(iterator.hasNext()) {
			Map record = iterator.next();
			Set<Entry<String, String>> entrySet = record.entrySet();
            for(Entry entry : entrySet) {
                String key = (String) entry.getKey();
                if(!"tranx_date".equals(key)) {
                	float value = (float) entry.getValue();
	                if(resultMap.containsKey(key)) {
	                	float innerValue = (float) resultMap.get(key);
	                	innerValue = innerValue + value;
	                	resultMap.put(key, innerValue);
	                } else {
	                	resultMap.put(key, value);
	                }
                }
            }
            resultMap.put("tranx_date","TOTAL");
		}
		return resultMap;
	}
	
	private List formatNumberBody(List bodyMap) {
		Iterator<Map> iterator = bodyMap.iterator();
		while(iterator.hasNext()) {
			Map record = iterator.next();
			Set<Entry<String, String>> entrySet = record.entrySet();
            for(Entry entry : entrySet) {
                String key = (String) entry.getKey();
                if(key.endsWith("_amount") || key.endsWith("_litre")) {
                	float value = (float) entry.getValue();
                	double dbval = Double.valueOf(value);
                	String output = myFormatter.format(dbval);
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
            if(key.endsWith("_amount") || key.endsWith("_litre")) {
            	float value = (float) entry.getValue();
            	double dbval = Double.valueOf(value);
            	String output = myFormatter.format(dbval);
            	entry.setValue(output);
            }
		}
		return footerMap;
	}
	
	private void addRevalueDetails(String transDate, int transCount, float amount, List<Map> bodyMap) {
		
		for(Map subMap : bodyMap) {
			String dateStr = (String)subMap.get("tranx_date");
			if(dateStr.equalsIgnoreCase(transDate)) {
				subMap.put("revalue_trans", transCount);
				subMap.put("revalue_amount", amount);
				break;
			}
		}
	}
	
	 class Record {
			
			private String date=null;
			private List<Product> productList= new ArrayList<>();
			private float literTotal = 0;
			private float amtTotal = 0;
			
			Record(String date) {
				this.date = date;
			}
			
			public void addProduct(Product prod) {
				if(productList.isEmpty()) {
					productList.add(prod);
				} else {
					boolean found = false;
					for(Product product : productList) {
						if(prod.getName().equals(product.getName())) {
							product.addAmount(prod.getAmount());
							product.addVolume(prod.getVolume());
							found = true;
						}
					}
					if(!found) {
						productList.add(prod);
					}
				}
			}
			public Map getRecordRowMap() {
				Map innerMap = new HashMap();
				float litTotal = 0;
				float amtTotal = 0;
				innerMap.put("tranx_date", date);
				for(Product prod : productList) {
					litTotal = litTotal + prod.getVolume();
					amtTotal = amtTotal + prod.getAmount();
					innerMap.put(prod.getName()+"_amount", prod.getAmount());
					innerMap.put(prod.getName()+"_litre", prod.getVolume());
				}
				innerMap.put("total_litre", litTotal);
				innerMap.put("total_amount", amtTotal);
				return innerMap;
			}
			
			public Map getRecordColumnMap() {
				Map mainMap = new HashMap();
				Map innerMap = new HashMap();
				float litTotal = 0;
				float amtTotal = 0;
				for(Product prod : productList) {
					Map subMap = prod.getprodDetails();
					litTotal = litTotal + prod.getVolume();
					amtTotal = amtTotal + prod.getAmount();
					innerMap.put(prod.getName(), subMap);
				}
				Map totMap = new HashMap();
				totMap.put("amount", amtTotal);
				totMap.put("litre", litTotal);
				innerMap.put("TOTAL", totMap);
				mainMap.put(date, innerMap);
				return mainMap;
			}
		}
		 
		 class Product {
			 float volume = 0;
			 float amount=0;
			  String name=null;
			  
			  Product(String name, float volume, float amount) {
				  this.volume = volume;
				  this.amount = amount;
				  this.name = name;
			  }
			  
			  public Map getprodDetails() {
				  Map innerMap = new HashMap();
				  innerMap.put("amount", amount);
				  innerMap.put("litre", volume);
				  return innerMap;
			  }
			  public String getName() {
				  return name;
			  }
			  public void addVolume(float volume) {
				  this.volume = this.volume + volume;
			  }
			  
			  public void addAmount(float amount) {
				  this.amount = this.amount + amount;
			  }
			  
			  public float getAmount() {
				  return amount;
			  }
			  
			  public float getVolume() {
				  return volume;
			  }
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
