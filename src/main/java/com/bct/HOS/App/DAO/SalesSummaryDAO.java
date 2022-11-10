package com.bct.HOS.App.DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.bct.HOS.App.utils.DBConnector;

import net.sf.json.JSONArray;

public class SalesSummaryDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;
	private long unitConversion = 0;
	private DecimalFormat myFormatter = new DecimalFormat("###,###.000");
	//private DecimalFormat upFormatter = new DecimalFormat("###,###.000000");
	
	public SalesSummaryDAO() {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
		unitConversion = dbc.getUnitConversion();
	}

	
	public HashMap salesSummaryReport(String startDate,String endDate) {
		HashMap mainMap = new HashMap();
		try {
			Statement st = null;
			ResultSet rs = null;
			List<String> productList = new ArrayList<>();
			Map<String,Record> bodyRecordMap = new HashMap<>();
			String SQL = null;
			//SITE_ID
			
				SQL = "SELECT \"PRODUCT_NAME\" AS \"PRODUCT_NAME\", \"SITE_ID\", sum(\"VOLUME\") AS LITRES, sum(\"AMOUNT\") AS AMOUNT "+
						"FROM (SELECT * FROM "+ schema + ".\"VW_TRX_SALES\" WHERE DATE(\"TRANSACTION_DATE\") BETWEEN to_date('"+startDate+"','DD,MM,YYY') " +
						"AND to_date('"+ endDate + "','DD,MM,YYY')" + " ORDER BY \"TRANSACTION_DATE\" DESC) AS expr_qry GROUP BY \"PRODUCT_NAME\", \"SITE_ID\" ORDER BY LITRES DESC";
			
			System.out.println("SQL == "+SQL);
			st = conn.createStatement();
			rs = st.executeQuery(SQL);
			while(rs.next()) {
				String productName = rs.getString("PRODUCT_NAME");
				//String tranDate = rs.getString("TRANSACTION_DATE");
				String siteId = rs.getString("SITE_ID");
				//String siteName = rs.getString("SITE_NAME");
				float litres = rs.getFloat("LITRES");
				float amount = rs.getFloat("AMOUNT");
				if(!productList.contains(productName)) {
					productList.add(productName);
				}
				addRecord(bodyRecordMap,siteId,siteId,new Product(productName,litres,amount));
			}
						
			Map headerMap1 = getHeaderMap(productList);
			mainMap.put("header", headerMap1);
			Map jsonMap = getBodyList(bodyRecordMap);
			Map footerMap = getFooterMap((List) jsonMap.get("bodylist"));
			Collections.sort((List)jsonMap.get("bodylist"), new SortByDate());
			mainMap.put("bodylist",formatNumberBody((List)jsonMap.get("bodylist")));
			mainMap.put("footer", formatNumberFooter(footerMap));
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
	
	public String salesSummaryNavAPI(String startDate) {
		String mainMap = null;
		try {
			Statement st = null;
			ResultSet rs = null;
			List<String> productList = new ArrayList<>();
			Map<String,Record> bodyRecordMap = new HashMap<>();
			String SQL = null;
			
			    SQL = "select DISTINCT(\"GLOBAL_PRD_CODE\"),\"PRODUCT_NAME\" from "+ schema +".\"MS_PRODUCTS\" ";
			st = conn.createStatement();
			rs = st.executeQuery(SQL);
			Map<String, String> productMap = new HashMap<>();
			while(rs.next()) {
				String prdName = rs.getString("PRODUCT_NAME");
				String prdCode = rs.getString("GLOBAL_PRD_CODE");
				productMap.put(prdName, prdCode);
			}
			
				SQL = "SELECT \"PRODUCT_TYPE\",\"CUST_ID\" FROM "+ schema + ".\"MS_CUST_PRODUCT\"";
				st = conn.createStatement();
				rs = st.executeQuery(SQL);	
				Map<String, String> customerMap = new HashMap<>();
				while(rs.next()) {
					String prdType = rs.getString("PRODUCT_TYPE");
					String custId = rs.getString("CUST_ID");
					customerMap.put(prdType, custId);
				}
				
				SQL = "SELECT \"PRODUCT_NAME\" AS \"PRODUCT_NAME\", \"SITE_ID\", sum(\"VOLUME\") AS LITRES, sum(\"AMOUNT\") AS AMOUNT, \"UNIT_PRICE\" "+
						"FROM (SELECT * FROM "+ schema + ".\"VW_TRX_SALES\" WHERE DATE(\"TRANSACTION_DATE\") BETWEEN to_date('"+startDate+"','DD,MM,YYY') " +
						"AND to_date('"+ startDate + "','DD,MM,YYY')" + " ORDER BY \"TRANSACTION_DATE\" DESC) AS expr_qry GROUP BY \"PRODUCT_NAME\", \"SITE_ID\",\"UNIT_PRICE\" ORDER BY LITRES DESC";
			
			System.out.println("SQL == "+SQL);
			st = conn.createStatement();
			rs = st.executeQuery(SQL);
			while(rs.next()) {
				String productName = rs.getString("PRODUCT_NAME");
				String siteId = rs.getString("SITE_ID");
				float litres = rs.getFloat("LITRES");
				float amount = rs.getFloat("AMOUNT");
				String unitPrice = rs.getString("UNIT_PRICE");
				if(!productList.contains(productName)) {
					productList.add(productName);
				}
				String prdCode = productMap.get(productName)==null ?  productName : productMap.get(productName);
				addRecord(bodyRecordMap,siteId,siteId,new Product(productName,unitPrice,prdCode,litres,amount,false,"LTR"));
			}
			
			rs.close();
			st.close();
			SQL = "select site_id,product_id,(select \"PRODUCT_NAME\" from "+ schema + ".\"MS_NF_PRODUCTS\" where \"PRODUCT_CODE\"=product_id) as prodName,(select \"SALES_UNIT\" from "+ schema + ".\"MS_NF_PRODUCTS\" where \"PRODUCT_CODE\"=product_id) as prodUnit,sum(product_amt) as totAmount,sum(quantity) as totQuantity,price from (select hd.transaction_no,hd.site_id,product_id,quantity,price,product_amt from "+ schema + ".nonfuel_sale_hdr hd join "+ schema + ".nonfuel_sale_dtl dt " 
					+ "on hd.transaction_no=dt.transaction_no and hd.site_id=dt.site_id "
					+ "WHERE DATE(hd.transaction_date) BETWEEN to_date('"+ startDate + "','DD,MM,YYY') "
					+ "AND to_date('"+ startDate + "','DD,MM,YYY') order by hd.transaction_date desc) as expr_qry "
					+  "group by site_id,product_id,price";
			System.out.println("SQL == "+SQL);
			st = conn.createStatement();
			rs = st.executeQuery(SQL);
			while(rs.next()) {
				String siteId = rs.getString("site_id");
				String prodId = rs.getString("product_id");
				String prodName = rs.getString("prodName");
				String price = rs.getString("price");
				String unit = rs.getString("prodUnit");
				float qty = rs.getFloat("totQuantity");
				float amount = rs.getFloat("totAmount");
				
				addRecord(bodyRecordMap,siteId,siteId,new Product(prodName,price,prodId,qty,amount,true,unit));
			}
			
			String dtInvc = startDate.replaceAll("-", "/");
			String custId = customerMap.get("FUEL");
			Map<String, String> productIdMap = new HashMap<>();
			productIdMap.put("MOGAS 95","PR0001");
			productIdMap.put("MOGAS 91","PR0002");
			productIdMap.put("DIESEL","PR0003");
			productIdMap.put("MOGAS 98","PR0004");
			String bodyStr = getBodyXmlList(bodyRecordMap,dtInvc,custId,productIdMap);
			String headrStr = "<OrderDetails xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
			//Map footerMap = getFooterMap((List) bvodyMap.get("bodylist"));
			//Collections.sort((List)bvodyMap.get("bodylist"), new SortByDate());
			//mainMap.put("bodylist",formatNumberBody((List)bvodyMap.get("bodylist")));
			//mainMap.put("footer", formatNumberFooter(footerMap));
			mainMap = headrStr + bodyStr + "</OrderDetails>";
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
	
	private void addRecord(Map<String,Record> bodyRecordMap, String site, String siteName, Product product) {
		if(bodyRecordMap.containsKey(site)) {
			Record rec = bodyRecordMap.get(site);
			rec.addProduct(product);
		} else {
			Record rec = new Record(site,siteName);
			rec.addProduct(product);
			bodyRecordMap.put(site, rec);
		}
	}
	
	private Map getBodyList(Map<String,Record> bodyRecordMap) {
		
		List mainList = new ArrayList();
		Map mainMap = new HashMap();
		for (Map.Entry<String, Record> entry : bodyRecordMap.entrySet()) {
			String entryKey = entry.getKey();
			Record entryRec = entry.getValue();
			Map subMap = entryRec.getRecordRowMap();
			mainList.add(subMap);
	    }
		mainMap.put("bodylist", mainList);
		return mainMap;
	}
	
	private String getBodyXmlList(Map<String,Record> bodyRecordMap, String dateStr,
			String custId, Map<String, String> productIdMap) {
		
		StringBuffer mainString = new StringBuffer();
		int lineNo = 10000;
		for (Map.Entry<String, Record> entry : bodyRecordMap.entrySet()) {
			String entryKey = entry.getKey();
			Record entryRec = entry.getValue();
			String innerCont = entryRec.getRecordXMLMap(entryKey,dateStr,lineNo,custId,productIdMap);
			int totPrd = entryRec.getTotProduct();
			lineNo = lineNo + totPrd;
			mainString.append(innerCont);
	    }
		return mainString.toString();
	}
	
	private Map getHeaderMap(List<String> productList) {
		Map mainMap = new HashMap();
		List mainList = new ArrayList();
		Map prodMap = new HashMap();
		Map innerMap = new HashMap();
		List pList = new ArrayList();
		prodMap.put("field", "product");
		prodMap.put("headerName", "PRODUCT NAME");
		innerMap.put("field", "site_name");
		innerMap.put("headerName", "FUEL STATIONS");
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
                if(!"site_name".equals(key)) {
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
            resultMap.put("site_name","TOTAL");
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
			String dateStr = (String)subMap.get("site_name");
			if(dateStr.equalsIgnoreCase(transDate)) {
				subMap.put("revalue_trans", transCount);
				subMap.put("revalue_amount", amount);
				break;
			}
		}
	}
	
	 class Record {
			
			private String site=null;
			private String siteName=null;
			private List<Product> productList= new ArrayList<>();
			private float literTotal = 0;
			private float amtTotal = 0;
			
			Record(String site, String siteName) {
				this.site = site;
				this.siteName = siteName;
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
				innerMap.put("site_name", site);
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
			public int getTotProduct() {
				return productList.size();
			}
			public String getRecordXMLMap(String siteName, String dateStr,int lineNo,String custId,Map<String, String> productIdMap) {
				Map mainMap = new HashMap();
				StringBuffer innerStr = new StringBuffer();
				SimpleDateFormat sd1 = new SimpleDateFormat("MMM");
				SimpleDateFormat sd2 = new SimpleDateFormat("ddMMyy");
				String dateRef = sd2.format(new Date());
				for(Product prod : productList) {
					String subStr = prod.getprodXMLDetails();
					innerStr.append("<Order>");
					innerStr.append("<FromSystem>"+ "PRAMS" +"</FromSystem>");
					innerStr.append("<Transaction_Type>"+ "CASH" +"</Transaction_Type>");
					innerStr.append("<Sub_Trans_Type>"+ "" +"</Sub_Trans_Type>");
					innerStr.append("<LineNo>" + (++lineNo) + "</LineNo>");
					innerStr.append("<Station_Dealer_ID>" + siteName + "</Station_Dealer_ID>");
					innerStr.append("<Customer_ID>" + custId + "</Customer_ID>"); //CUST02150
					innerStr.append("<Customer_Name>"+ "" +"</Customer_Name>");
					innerStr.append("<CStore>"+ "No" +"</CStore>");
					innerStr.append("<GLAccount>"+ "" +"</GLAccount>");
					innerStr.append("<GL_Code>"+ "" +"</GL_Code>");
					if(prod.getNfFlag()) {
						innerStr.append("<Product_ID>" + prod.getId() + "</Product_ID>");
					} else {
						innerStr.append("<Product_ID>" + productIdMap.get(prod.getName()) + "</Product_ID>");
					}
					innerStr.append("<Product_Name>" + prod.getName() + "</Product_Name>");
					innerStr.append("<UOM>" + prod.getUnit() + "</UOM>");
					innerStr.append(subStr);
					innerStr.append("<Summary_Reference>" + siteName + "D" + dateRef + "</Summary_Reference>");
					innerStr.append("<InvoiceDescription>" + "CASHSALES" + "</InvoiceDescription>");
					innerStr.append("<Invoice_Month>" + dateStr + "</Invoice_Month>");
					innerStr.append("</Order>");
				}
				return innerStr.toString();
			}
		}
		 
		 class Product {
			 float volume = 0;
			 float amount=0;
			 String name=null;
			 String id = null;
			 String uPrice = null;
			 private DecimalFormat myFormatter = new DecimalFormat("###,###.000");
			 private DecimalFormat upFormat = new DecimalFormat("###,##0.000000"); 
			 boolean nfFlag = false;
			 String unit = null;
			 
			 Product(String name, float volume, float amount) {
				  this.volume = volume;
				  this.amount = amount;
				  this.name = name;
			  }
			  
			  Product(String name, String uPrice,String id,float volume, float amount, boolean nfFlag,String unit) {
				  this.volume = volume;
				  this.amount = amount;
				  this.name = name;
				  this.uPrice = uPrice;
				  this.id = id;
				  this.nfFlag = nfFlag;
				  this.unit = unit;
			  }
			  
			  public Map getprodDetails() {
				  Map innerMap = new HashMap();
				  innerMap.put("amount", amount);
				  innerMap.put("litre", volume);
				  return innerMap;
			  }
			  public String getprodXMLDetails() {
				  StringBuffer result = new StringBuffer();
				  double dbvalQty = Double.valueOf(volume);
				  String output = myFormatter.format(dbvalQty);
				  result.append("<Quantity>"+output+"</Quantity>");
				  double prodOrgPrice = getOrgPrice();
				  result.append("<Unit_Rate>" + upFormat.format(prodOrgPrice) + "</Unit_Rate>");
				  double dbvalAmt = prodOrgPrice * dbvalQty;
              	  output = myFormatter.format(dbvalAmt);
				  result.append("<Amount>"+output+"</Amount>");
				  return result.toString();
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

			public String getuPrice() {
				return uPrice;
			}
			
			public String getUnit() {
				return unit;
			}
			
			public boolean getNfFlag() {
				return nfFlag;
			}
			
			public double getOrgPrice() {
				double upDouble = Double.valueOf(uPrice);
				double dp = upDouble-(upDouble-(upDouble*100/(100+5)));
				System.out.println("TEST LOG | Uni Price after VAT: "+upDouble +"--"+dp);
				return dp;
			}
			
			public String getId() {
				return id;
			}
		 }
		 
		 class SortByDate implements Comparator<Map> {
	        @Override
	        public int compare(Map first, Map second) {
	        	String firstDate = (String) first.get("site_name");
	        	String secondDate = (String) second.get("site_name");
	            return secondDate.compareTo(firstDate);
	        }
		 }
}
