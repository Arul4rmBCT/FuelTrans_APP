package com.bct.HOS.App.DAO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.bct.HOS.App.utils.DBConnector;

public class PurchaseDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;
	private DecimalFormat myFormatter = new DecimalFormat("###,###.000");
	
	public PurchaseDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
	}
	
	public HashMap purchaseReport(String startDate,String endDate,String stationId) {
		HashMap mainMap = new HashMap();
		try {
			Statement st = null;
			ResultSet rs = null;
			List<String> productList = new ArrayList<>();
			Map<String,Record> bodyRecordMap = new HashMap<>();
			List<LocalDate> dateList = getDatesBetween(startDate, endDate);
			for(LocalDate locDate : dateList) {
				bodyRecordMap.put(locDate.toString(), null);
			}
			String SQL = "SELECT \"PRODUCT_NO\",(select \"PRODUCT_NAME\" from \"ALMAHA\".\"MS_PRODUCTS\" WHERE \"GLOBAL_PRD_CODE\"=del.\"PRODUCT_NO\" LIMIT 1) AS \"NAME\",\"INVOICE_DATE\" AS \"LOADED DATE\", \"INVOICE_NO\" AS \"PDN NUMBER\"," +
					"\"END_TIME\"::DATE AS \"RECEIVED DATE\",\"END_VOLUME\" AS \"VOLUME\"" +
					"FROM \"ALMAHA\".\"MS_DELIVERY_DATA\" del WHERE DATE(\"END_TIME\") BETWEEN '"+startDate+"'" +
					" AND '"+endDate+"' AND \"SITE_ID\"='"+stationId+"'";
			
			System.out.println("SQL == "+SQL);
			st = conn.createStatement();
			rs = st.executeQuery(SQL);
			String productName = null;
			String loadedDate = null;
			String invoiceNo = null;
			String recdDate = null;
			while(rs.next()) {
				productName = rs.getString("NAME");
				loadedDate = rs.getString("LOADED DATE");
				invoiceNo = rs.getString("PDN NUMBER");
				recdDate = rs.getString("RECEIVED DATE");
				float litres = rs.getFloat("VOLUME");
				if(!productList.contains(productName)) {
					productList.add(productName);
				}
				addRecord(bodyRecordMap,loadedDate,recdDate,new Product(productName,litres,invoiceNo));
			}
			
			
			Map headerMap1 = getHeaderMap(productList);
			mainMap.put("header", headerMap1);
			Map jsonMap = getBodyList(bodyRecordMap, productList);
			Map footerMap = getFooterMap((List) jsonMap.get("bodylist"));
			Collections.sort((List)jsonMap.get("bodylist"), new SortByDate());
			mainMap.put("bodylist",formatNumberBody((List)jsonMap.get("bodylist")));
			mainMap.put("footer", formatNumberFooter(footerMap));
			 
			System.out.println("mainMap >>> "+mainMap);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("ErrOR @ PurchaseDAO-purchaseReport ::" + ex.getMessage());
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return mainMap;
	}
	
	private void addRecord(Map<String,Record> bodyRecordMap, String loadedDate, String recdDate, Product product) {
		if(bodyRecordMap.containsKey(recdDate)) {
			Record rec = bodyRecordMap.get(recdDate);
			if(rec == null) {
				rec = new Record(loadedDate,recdDate);
				rec.addProduct(product);
				bodyRecordMap.put(recdDate, rec);
			} else {
				rec.addProduct(product);
			}
		} else {
			Record rec = new Record(loadedDate,recdDate);
			rec.addProduct(product);
			bodyRecordMap.put(recdDate, rec);
		}
	}
	
	
	private Map getBodyList(Map<String,Record> bodyRecordMap, List<String> productList) {
		
		StringBuffer stbuf = new StringBuffer();
		List mainList = new ArrayList();
		Map mainMap = new HashMap();
		for (Map.Entry<String, Record> entry : bodyRecordMap.entrySet()) {
			String entryKey = entry.getKey();
			Record entryRec = entry.getValue();
			Map subMap = null;
			if(entryRec == null) {
				subMap = getDummyData(productList, entryKey);
			} else {
				subMap = entryRec.getRecordRowMap();
			}
			
			mainList.add(subMap);
	    }
		
		mainMap.put("bodylist", mainList);
		return mainMap;
	}
	
	private Map getDummyData(List<String> productList, String receiveDate) {
		Map innerMap = new HashMap();
		innerMap.put("loaded_date", "");
		innerMap.put("received_date", receiveDate);
		for(String prodName : productList) {
			innerMap.put(prodName+"_litre", 0.0F);
		}
		innerMap.put("pdn_note", "");
		return innerMap;
	}
	
	private Map getHeaderMap(List<String> productList) {
		Map mainMap = new LinkedHashMap();
		List mainList = new ArrayList();
		Map prodMap = new HashMap();
		prodMap.put("field", "loaded_date");
		prodMap.put("headerName", "LOADED DATE");
		mainList.add(prodMap);
		prodMap = new HashMap();
		prodMap.put("field", "pdn_note");
		prodMap.put("headerName", "PDN NUMBER");
		mainList.add(prodMap);
		prodMap = new HashMap();
		prodMap.put("field", "received_date");
		prodMap.put("headerName", "RECEIVED DATE");
		mainList.add(prodMap);
		for(String prodItem : productList) {
			prodMap = new HashMap();
			prodMap.put("field", prodItem+"_litre");
			prodMap.put("headerName", prodItem);
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
                if(!("loaded_date".equals(key) || "pdn_note".equals(key) || "received_date".equals(key))) {
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
            resultMap.put("loaded_date","TOTAL");
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
	
	public static List<LocalDate> getDatesBetween(String startDate, String endDate) { 
		 
		DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		
		LocalDate startLocalDate = LocalDate.parse(startDate, formatter1);
		LocalDate endLocalDate = LocalDate.parse(endDate, formatter1);
			    long numOfDaysBetween = ChronoUnit.DAYS.between(startLocalDate, endLocalDate); 
			    return IntStream.iterate(0, i -> i + 1)
			      .limit(numOfDaysBetween+1)
			      .mapToObj(i -> startLocalDate.plusDays(i))
			      .collect(Collectors.toList()); 
	}
	 class Record {
			
			private String recdDate = "";
			private List<Product> productList= new ArrayList<>();
			private float literTotal = 0;
			private String loadDate = "";
			//private float amtTotal = 0;
			
			Record(String loadDate, String recdDate) {
				this.recdDate = recdDate;
				this.loadDate = loadDate;
			}
			
			public void addProduct(Product prod) {
				if(productList.isEmpty()) {
					productList.add(prod);
				} else {
					boolean found = false;
					for(Product product : productList) {
						if(prod.getName().equals(product.getName())) {
							//product.addAmount(prod.getAmount());
							product.addVolume(prod.getVolume(), prod.getPdnNo());
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
				//float litTotal = 0;
				//float amtTotal = 0;
				innerMap.put("loaded_date", loadDate);
				innerMap.put("received_date", recdDate);
				StringBuffer pdnBuffer = new StringBuffer();
				for(Product prod : productList) {
					innerMap.put(prod.getName()+"_litre", prod.getVolume());
					pdnBuffer.append(prod.getPdnNo()==null?"":prod.getPdnNo());
				}
				innerMap.put("pdn_note", pdnBuffer.toString());
				return innerMap;
			}
		}
		 
		 class Product {
			 float volume = 0;
			  String name=null;
			  String pdnNo = null;
			  
			  Product(String name, float volume, String pdnNo) {
				  this.volume = volume;
				  this.name = name;
				  this.pdnNo = pdnNo;
			  }
			  
			  public Map getprodDetails() {
				  Map innerMap = new HashMap();
				  innerMap.put("litre", volume);
				  return innerMap;
			  }
			  
			  public String getName() {
				  return name;
			  }
			  
			  public void addVolume(float volume, String pdnNo) {
				  this.volume = this.volume + volume;
				  if(pdnNo == null) {
					  this.pdnNo = pdnNo;
				  } else {
					  this.pdnNo = this.pdnNo + pdnNo;
				  }
				  
			  }
			  			  
			  public float getVolume() {
				  return volume;
			  }

			public String getPdnNo() {
				return pdnNo;
			}

			public void setPdnNo(String pdnNo) {
				this.pdnNo = pdnNo;
			}
		 }
		 
		 class SortByDate implements Comparator<Map> {
		        @Override
		        public int compare(Map first, Map second) {
		        	String firstDate = (String) first.get("received_date");
		        	String secondDate = (String) second.get("received_date");
		            return secondDate.compareTo(firstDate);
		        }
			}
}
