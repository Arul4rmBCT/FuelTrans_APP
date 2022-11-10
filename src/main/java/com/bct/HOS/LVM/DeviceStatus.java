package com.bct.HOS.LVM;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.time.DateUtils;

import com.bct.HOS.App.DAO.AlarmsDAO;
import com.bct.HOS.App.DAO.DeviceDAO;
import com.bct.HOS.App.DAO.NotificationDAO;
import com.bct.HOS.App.DAO.SiteInfoDAO;
import com.bct.HOS.App.DAO.SiteStatusDAO;
import com.bct.HOS.App.utils.HOSConfig;
import com.bct.HOS.App.utils.HOSUtil;
import com.bct.HOS.App.utils.LVMConnector;
import com.google.gson.JsonObject;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class DeviceStatus {

	HOSConfig conf = null;

	public DeviceStatus() {
		conf = new HOSConfig();
	}

	public JSONArray getTankStatus(String siteId) {
		JSONArray tankArray = new JSONArray();
		try {
			//String SQL = "SELECT TankNo, Status,format_time(timestamp(string(int(LastUpdated/1000))), \"yyyy-MM-dd, HH:mm:ss\") as LastUpdated FROM TankStatus WHERE SiteID = " + siteId;
			JsonObject req=new JsonObject();
			req.addProperty("siteId", siteId.replaceAll("'", ""));
			String response = new LVMConnector().LVMConnect("LVM_TANK_URL", null,req);
			//System.out.println("DeviceStatus >> getTankStatus >>> response is :: " + response);

			JSONArray outerArray = (JSONArray) JSONSerializer.toJSON(response);
			ArrayList<String> tankNos = new ArrayList<String>();
			int loop = 0;
			int count = outerArray.size();
			
			String tankNo = null;
			JSONObject jsonObj = null;
			JSONObject obj = null;
			for (Object js : outerArray) {
				//if (loop >= 0 && loop < (count - 1)) {
					jsonObj = new JSONObject();
					JSONObject arrJson = (JSONObject) js;
					JSONObject dataJson = (JSONObject) arrJson.get("data");
					tankNo = (String) dataJson.get("TankNo");
					if (!tankNos.contains(tankNo)) {
						jsonObj.put("TankNo", tankNo);
						jsonObj.put("Status", dataJson.get("Status"));
						jsonObj.put("LastUpdated", convertTime(dataJson.get("LastUpdated").toString()));
						obj = new DeviceDAO().getTankCurrentDetail(siteId, tankNo);

						if (obj != null) {
							//System.out.println("obj is > " + obj.toString());
							jsonObj.put("PRODUCT_NO", obj.get("PRODUCT_NO"));
							jsonObj.put("PRODUCT_NAME", obj.get("PRODUCT_NAME"));
							jsonObj.put("CURRENT_CAPACITY", obj.get("CURRENT_CAPACITY"));
							jsonObj.put("MIN_CAPACITY", obj.get("MIN_CAPACITY"));
							jsonObj.put("MAX_CAPACITY", obj.get("MAX_CAPACITY"));
							jsonObj.put("PERCENTAGE", obj.get("PERCENTAGE"));
							jsonObj.put("ULLAGE", obj.get("ULLAGE"));
							jsonObj.put("HEIGHT", obj.get("HEIGHT"));
							jsonObj.put("TEMPERATURE", obj.get("TEMPERATURE"));
							jsonObj.put("WATER", obj.get("WATER"));
							jsonObj.put("WATER_HEIGHT", obj.get("WATER_HEIGHT"));
							jsonObj.put("DENSITY_STATUS", obj.get("DENSITY_STATUS"));
							jsonObj.put("DENSITY_ACTUAL", obj.get("DENSITY_ACTUAL"));
							jsonObj.put("DENSITY_AT15DEG", obj.get("DENSITY_AT15DEG"));
							jsonObj.put("TCVOLUME", obj.get("TCVOLUME"));
						}else {
							jsonObj.put("PRODUCT_NO", "null");
							jsonObj.put("PRODUCT_NAME", "null");
							jsonObj.put("CURRENT_CAPACITY", "0");
							jsonObj.put("MIN_CAPACITY", "0");
							jsonObj.put("MAX_CAPACITY", "0");
							jsonObj.put("PERCENTAGE", "0");
							jsonObj.put("ULLAGE", "0");
							jsonObj.put("HEIGHT", "0");
							jsonObj.put("TEMPERATURE", "0");
							jsonObj.put("WATER", "0");
							jsonObj.put("WATER_HEIGHT", "0");
							jsonObj.put("DENSITY_STATUS", "0");
							jsonObj.put("DENSITY_ACTUAL", "0");
							jsonObj.put("DENSITY_AT15DEG", "0");
							jsonObj.put("TCVOLUME", "0");
						}
						tankNos.add(tankNo);
						tankArray.add(jsonObj);
					}
				//}
				//loop++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return tankArray;
	}

	/**
	 * 
	 * @param siteId
	 * @return
	 */
	public JSONObject getROStatus(String siteId,String siteName,int index) {
		JSONObject jsonObj = null;
		try {
//			String SQL = "SELECT SiteID, IF(Sts = 1 && LastUpdated > to_milliseconds(now()-minutes(15))) then 'Online' else 'Offline' as Status, "
//					+ " format_time(timestamp(string(int(LastUpdated/1000))), \"yyyy-MM-dd, HH:mm:ss\") as LastUpdated "
//					+ " FROM SiteStatus where SiteID = " + siteId;
			JsonObject req=new JsonObject();
			req.addProperty("siteId", siteId.replaceAll("'", ""));
			String response = new LVMConnector().LVMConnect("LVM_SITE_URL", null,req);
			//System.out.println("response is :: " + response); 

			JSONArray outerArray = (JSONArray) JSONSerializer.toJSON(response);
			int loop = 0;
			int count = outerArray.size();
			

			String siteID = null;
			for (Object js : outerArray) {
				//if (loop > 0 && loop < (count - 1)) {
					jsonObj = new JSONObject();
					JSONObject arrJson = (JSONObject) js;
					JSONObject dataJson = (JSONObject) arrJson.get("data");
					siteID = (String) dataJson.get("SiteID");
					if (siteID != null) {
						jsonObj.put("SNO", index);
						jsonObj.put("Site_ID", siteId.replaceAll("'", ""));
						jsonObj.put("Site_Name", siteName);
						jsonObj.put("Status", dataJson.get("Status"));
						getTankStatusCount(siteId,jsonObj);
						getDUStatusCount(siteId,jsonObj);
						jsonObj.put("SiteName", siteName);
						jsonObj.put("LastUpdated", convertTime(dataJson.get("LastUpdated").toString()));
					}
				//}
				//loop++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObj;
	}
	
	/**
	 * To get Tank status count
	 * @param siteId
	 * @return
	 */
	public void getTankStatusCount(String siteId,JSONObject jsonObj) {
		int onlineCount = 0;
		int offlineCount = 0;
		try {
			//String SQL = "SELECT TankNo, if (Status=1) then 'Online' else 'Offline' as Stat FROM TankStatus WHERE SiteID = " + siteId;
			JsonObject req=new JsonObject();
			req.addProperty("siteId", siteId.replaceAll("'", ""));
			String response = new LVMConnector().LVMConnect("LVM_TANK_URL", null,req);
			//System.out.println("response is :: " + response);

			JSONArray outerArray = (JSONArray) JSONSerializer.toJSON(response);
			int loop = 0;
			int count = outerArray.size();

			String status = null;
			for (Object js : outerArray) {
				//if (loop > 0 && loop < (count - 1)) {
					JSONObject arrJson = (JSONObject) js;
					JSONObject dataJson = (JSONObject) arrJson.get("data");

					status = (String) dataJson.getString("Status");
					if(status.equalsIgnoreCase("Online"))
						onlineCount++;
					else
						offlineCount++;
//				}
//				loop++;
			}
			jsonObj.put("Tank_Total", (onlineCount+offlineCount));
			jsonObj.put("Tank_Online", onlineCount);
			jsonObj.put("Tank_Offline", offlineCount);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JSONArray getDUStatus(String siteId) {
		JSONArray tankArray = new JSONArray();
		try {
			// String SQL = "SELECT PumpNo, Status, DispenserNo, DispenserStatus, NozzleNo,
			// LastUpdated FROM DuStatus WHERE SiteID = "+siteId;
			//String SQL = "SELECT SiteID,DispenserNo,max(LastUpdated) as LastUpdated, max(DispenserStatus) as DispenserStatus FROM DuStatus WHERE SiteID = " + siteId+ " GROUP BY SiteID,DispenserNo";
			JsonObject req=new JsonObject();
			req.addProperty("siteId", siteId.replaceAll("'", ""));
			String response = new LVMConnector().LVMConnect("LVM_DU_PUMP_URL", null,req);
			//System.out.println("LVM_DU_PUMP_URL response is :: " + response);

			JSONArray outerArray = (JSONArray) JSONSerializer.toJSON(response);
			int loop = 0;
			int count = outerArray.size();
			String duNo = null;
			String duSts = null;
			ArrayList<String> duNos = new ArrayList<String>();
			JSONObject jsonObj = null;
			for (Object js : outerArray) {
				//if (loop > 0 && loop < (count - 1)) {
					jsonObj = new JSONObject();
					JSONObject arrJson = (JSONObject) js;
					JSONObject dataJson = (JSONObject) arrJson.get("data");
					/*
					 * pumpNo = (String) dataJson.get("PumpNo"); if(!pumpNos.contains(pumpNo)) {
					 * jsonObj.put("PumpNo", pumpNo); jsonObj.put("Status", dataJson.get("Status"));
					 * jsonObj.put("DispenserNo", dataJson.get("DispenserNo"));
					 * jsonObj.put("DispenserStatus", dataJson.get("DispenserStatus"));
					 * jsonObj.put("NozzleNo", dataJson.get("NozzleNo")); jsonObj.put("LastUpdated",
					 * dataJson.get("LastUpdated")); pumpNos.add(pumpNo); tankArray.add(jsonObj); }
					 */

					duNo = dataJson.getString("DispenserNo");
					//if (!duNos.contains(duNo)) {
						jsonObj.put("DispenserNo", dataJson.get("DispenserNo"));
						duSts = dataJson.get("DispenserStatus").toString();
						if(duSts!=null) {
							jsonObj.put("DispenserStatus", Integer.parseInt(duSts));
						}else {
							jsonObj.put("DispenserStatus", 0);
						}
						jsonObj.put("LastUpdated", convertTime(dataJson.getOrDefault("LastUpdated","null").toString()));
						duNos.add(duNo);
						tankArray.add(jsonObj);
					//}
				//}
				//loop++;
			}
			//tankArray = new HOSUtil().sortJSOArray(tankArray, "DispenserNo");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tankArray;
	}
	
	/**
	 * To get the DU status count
	 * @param siteId
	 * @return
	 */
	public void getDUStatusCount(String siteId,JSONObject jsonObj) {
		int onlineCount = 0;
		int offlineCount = 0;
		try {
			//String SQL = "SELECT  SiteID,DispenserNo,max(LastUpdated) as LastUpdated,max(DispenserStatus) as DispenserStatus FROM DuStatus  WHERE SiteID="+siteId+" GROUP BY SiteID,DispenserNo";
			JsonObject req=new JsonObject();
			req.addProperty("siteId", siteId.replaceAll("'", ""));
			String response = new LVMConnector().LVMConnect("LVM_DU_PUMP_URL", null,req);
			//System.out.println("response is :: " + response);

			JSONArray outerArray = (JSONArray) JSONSerializer.toJSON(response);
			int loop = 0;
			int count = outerArray.size();
			String status = null;
			for (Object js : outerArray) {
				//if (loop > 0 && loop < (count - 1)) {
					JSONObject arrJson = (JSONObject) js;
					JSONObject dataJson = (JSONObject) arrJson.get("data");
					
					status = (String) dataJson.getString("DispenserStatus");
					if(status.equals("1"))
						onlineCount++;
					else
						offlineCount++;
				//}
				//loop++;
			}
			jsonObj.put("DU_Total", (onlineCount+offlineCount));
			jsonObj.put("DU_Online", onlineCount);
			jsonObj.put("DU_Offline", offlineCount);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public JSONArray getDUpumpStatus(String siteId) {
		JSONArray tankArray = new JSONArray();
		try {
//			String SQL = "SELECT PumpNo, max(Status) as Status, DispenserNo, max(DispenserStatus) as DispenserStatus,"
//					+ " format_time(timestamp(string(int(max(LastUpdated)/1000))), \"yyyy-MM-dd, HH:mm:ss\") as LastUpdated "
//					+ " FROM DuStatus WHERE SiteID = "+siteId +" GROUP BY PumpNo, DispenserNo ";
			JsonObject req=new JsonObject();
			req.addProperty("siteId", siteId.replaceAll("'", ""));
			String response = new LVMConnector().LVMConnect("LVM_PUMP_URL", null,req);
			//System.out.println("response is :: " + response);

			JSONArray outerArray = (JSONArray) JSONSerializer.toJSON(response);
			int loop = 0;
			int count = outerArray.size();
			String pumpNo = null;
			ArrayList<String> pumpNos = new ArrayList<String>();
			JSONObject jsonObj = null;
			for (Object js : outerArray) {
				//if (loop > 0 && loop < (count - 1)) {
					jsonObj = new JSONObject();
					JSONObject arrJson = (JSONObject) js;
					JSONObject dataJson = (JSONObject) arrJson.get("data");

					JSONArray pumpList = (JSONArray) dataJson.get("pumpList");
					for(Object pjs:pumpList) {
						JSONObject pmpJson = (JSONObject) pjs;
						pumpNo = pmpJson.getString("PumpNo");
						//if (!pumpNos.contains(pumpNo)) {
							jsonObj.put("PumpNo", pumpNo);
							if(pmpJson.get("Status").toString().equals("{}"))
								jsonObj.put("Status", pmpJson.getOrDefault("Status","0"));
							else
								jsonObj.put("Status", pmpJson.get("Status"));
							
							jsonObj.put("DispenserNo", pmpJson.get("DispenserNo"));
							if(pmpJson.get("DispenserStatus").toString().equals("{}"))
								jsonObj.put("DispenserStatus", pmpJson.getOrDefault("DispenserStatus","0"));
							else
								jsonObj.put("DispenserStatus", pmpJson.get("DispenserStatus"));
							
							//jsonObj.put("NozzleNo", dataJson.get("NozzleNo"));
							if(pmpJson.get("LastUpdated").toString().equals("{}"))
								jsonObj.put("LastUpdated", convertTime(pmpJson.getOrDefault("LastUpdated","0000-00-00 00:00:00").toString()));
							else
								jsonObj.put("LastUpdated", convertTime(pmpJson.get("LastUpdated").toString()));
							
							//pumpNos.add(pumpNo);
							tankArray.add(jsonObj);
						//}
					}					

				//}
				//loop++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return tankArray;
	}
	
	
	public HashMap getSiteDUpumpStatus(String siteId,String du) {
		HashMap tankArray = new HashMap();
		try {
			//String SQL = "SELECT PumpNo, Status FROM DuStatus WHERE SiteID = "+siteId + " and DispenserNo = '"+du+"' group by PumpNo,Status";
//			String SQL = "SELECT PumpNo, "
//					+ " format_time(timestamp(string(int(max(LastUpdated)/1000))), \"yyyy-MM-dd, HH:mm:ss\") as LastUpdated, "
//					+ " max(Status) as Status FROM DuStatus WHERE SiteID = "+siteId+" and DispenserNo='"+du+"'  GROUP BY PumpNo";
			JsonObject req=new JsonObject();
			req.addProperty("siteId", siteId.replaceAll("'", ""));
			// TBD
			String response = new LVMConnector().LVMConnect("LVM_PUMP_URL", null,req);

			JSONArray outerArray = (JSONArray) JSONSerializer.toJSON(response);
			int loop = 0;
			int count = outerArray.size();
			String pumpNo = null;
			ArrayList<String> pumpNos = new ArrayList<String>();
			String status = null;
			String lu = null;
			for (Object js : outerArray) {
				//if (loop > 0 && loop < (count - 1)) {
					JSONObject arrJson = (JSONObject) js;
					JSONObject dataJson = (JSONObject) arrJson.get("data");

//					pumpNo = dataJson.getString("PumpNo");
//					if (!pumpNos.contains(pumpNo)) {
//						status= (String) dataJson.get("Status");
//						lu =(String) dataJson.get("LastUpdated");
//						tankArray.put(pumpNo,status+"~"+lu);
//					}
					
					JSONArray pumpList = (JSONArray) dataJson.get("pumpList");
					for(Object pjs:pumpList) {
						JSONObject pmpJson = (JSONObject) pjs;
						pumpNo = pmpJson.getString("PumpNo");
						status= (String) pmpJson.getOrDefault("Status","0");
						lu =convertTime((String) pmpJson.get("LastUpdated"));
						tankArray.put(pumpNo,status+"~"+lu);
					}

				//}
				//loop++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return tankArray;
	}

	public JSONObject getSiteStatus(String siteId) {
		JSONObject jsonObj = null;
		try {
//			String SQL = "SELECT SiteID, IF(Sts = 1 && LastUpdated > to_milliseconds(now()-minutes(120))) then 'Online' else 'Offline' as Status, "
//					+ " format_time(timestamp(string(int(LastUpdated/1000))), \"yyyy-MM-dd, HH:mm:ss\") as LastUpdated "
//					+ " FROM SiteStatus where SiteID = " + siteId;
			JsonObject req=new JsonObject();
			req.addProperty("siteId", siteId.replaceAll("'", ""));
			String response = new LVMConnector().LVMConnect("LVM_SITE_URL", null,req);
			//System.out.println("response is :: " + response);

			JSONArray outerArray = (JSONArray) JSONSerializer.toJSON(response);
			int loop = 0;
			int count = outerArray.size();

			String siteID = null;
			for (Object js : outerArray) {
				//if (loop > 0 && loop < (count - 1)) {
					jsonObj = new JSONObject();
					JSONObject arrJson = (JSONObject) js;
					JSONObject dataJson = (JSONObject) arrJson.get("data");
					siteID = (String) dataJson.get("SiteID");
					if (siteID != null) {
						jsonObj.put("SiteID", siteID);
						jsonObj.put("Status", dataJson.get("Status"));
						jsonObj.put("LastUpdated", convertTime(dataJson.get("LastUpdated").toString()));
					}
//				}
//				loop++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObj;
	}
	
	
	public HashMap getSiteStatus() {
		HashMap mapObj = null;
		HashMap innerMap = null;
		try {

//			String SQL = "SELECT SiteID,\r\n" + 
//					"IF\r\n" + 
//					"((LastUpdated < to_milliseconds(now()-minutes(120)))) then 'Offline'\r\n" + 
//					"ELSE IF ((LastUpdated < to_milliseconds(today()-days(3)))) then 'Disconnected'\r\n" + 
//					"ELSE 'Online' as status,\r\n" + 
//					"format_time(timestamp(string(int(LastUpdated/1000))), \"yyyy-MM-dd, HH:mm:ss\") as LastUpdated\r\n" + 
//					"FROM SiteStatus";
			JsonObject req=new JsonObject();	
			String response = new LVMConnector().LVMConnect("LVM_SITE_URL", null,req);
			//System.out.println("response is :: " + response);

			JSONArray outerArray = (JSONArray) JSONSerializer.toJSON(response);
			int loop = 0;
			int count = outerArray.size();

			String siteID = null;
			mapObj = new HashMap();
			
			for (Object js : outerArray) {
				//if (loop > 0 && loop < (count - 1)) {
					JSONObject arrJson = (JSONObject) js;
					JSONObject dataJson = (JSONObject) arrJson.get("data");
					siteID = (String) dataJson.get("SiteID");
					if (siteID != null) {
						innerMap = new HashMap();
						innerMap.put("Status", dataJson.get("Status"));
						innerMap.put("LastUpdated", convertTime(dataJson.get("LastUpdated").toString()));
						mapObj.put(siteID, innerMap);
					}
				//}
				//loop++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapObj;
	}
	
	
	/**
	 * All Site Status
	 * 
	 * @param siteNames
	 * @return
	 */
	public JSONArray getSiteStatus(String siteCodes,String country,String siteStatus) {
		JSONArray jsonArr = new JSONArray();
		try {
			StringBuffer onlineSites = new StringBuffer();
			StringBuffer offlineSites = new StringBuffer();
			StringBuffer disconnectSites = new StringBuffer();
			String[] siteList = null;
			if(siteCodes!=null) {
				siteCodes = siteCodes.replace("'", "");
				siteList = siteCodes.split(",");
			}
			
			JSONObject jsonObj=new JSONObject();
			JSONObject jsonInObj = new JSONObject();
			JSONObject jsonDisconnectObj = new JSONObject();
			// Get SiteNames
//			String SQL ="SELECT SiteID,\r\n" + 
//					"IF\r\n" + 
//					"((LastUpdated < to_milliseconds(now()-minutes(120)))) then 'Offline'\r\n" + 
//					"ELSE IF ((LastUpdated < to_milliseconds(today()-days(3)))) then 'Disconnected'\r\n" + 
//					"ELSE 'Online' as Status,\r\n" + 
//					"format_time(timestamp(string(int(LastUpdated/1000))), \"yyyy-MM-dd, HH:mm:ss\") as LastUpdated\r\n" + 
//					"FROM SiteStatus";
			JsonObject req=new JsonObject();		
			
			String response = new LVMConnector().LVMConnect("LVM_SITE_URL", null,req);
			//System.out.println("DeviceStatus response is :: " + response);
	
			JSONArray outerArray = (JSONArray) JSONSerializer.toJSON(response);
			int loop = 0;
			int count = outerArray.size();
			
			int roActiveCount = 0;
			int roInActiveCount = 0;
			int roDisconnectCount = 0;
			
			if (count > 0) {
				List<String> sites= new SiteStatusDAO().getSites();
				List<String> resultSites = new ArrayList();
				String siteID = null;
				String status = null;
				for (Object js : outerArray) {
					//if (loop > 0 && loop < (count - 1)) {
						JSONObject arrJson = (JSONObject) js;
						JSONObject dataJson = (JSONObject) arrJson.get("data");
						siteID = (String) dataJson.get("SiteID");
						//System.out.println("DeviceStatus siteID :: "+siteID);
						if (siteID != null) {
							if(siteList!=null) {
								boolean contains = ArrayUtils.contains( siteList, siteID);
								long lsup = 0;
								if(dataJson.get("LastUpdated") instanceof JSONNull) {
									lsup = 0;
								}else {
									lsup =  (Long) dataJson.get("LastUpdated");
								}
								if(lsup > 0) {
									status = getStatusFromLastUpdated(String.valueOf(lsup));
								}else {
									status = "Offline";
								}
								if(contains) {
									//status = dataJson.get("Status").toString();
									if(status.equalsIgnoreCase("Online") || status.equalsIgnoreCase("1")) {
										onlineSites.append("'"+siteID+"'");
										resultSites.add(siteID);
										roActiveCount++;
									} else if(status.equalsIgnoreCase("Offline") || status.equalsIgnoreCase("0")) {
										offlineSites.append("'"+siteID+"'");
										resultSites.add(siteID);
										roInActiveCount++;
									}else {
										disconnectSites.append("'"+siteID+"'");
										roDisconnectCount++;
									}
								}
							}else {
								//status = dataJson.get("Status").toString();
								
								long lsup = 0;
								if(dataJson.get("LastUpdated") instanceof JSONNull) {
									lsup = 0;
								}else {
									lsup =  (Long) dataJson.get("LastUpdated");
								}
								if(lsup > 0) {
									status = getStatusFromLastUpdated(String.valueOf(lsup));
								}else {
									status = "Offline";
								}
								
								if(status.equalsIgnoreCase("Online")) {
									onlineSites.append("'"+siteID+"'");
									resultSites.add(siteID);
									roActiveCount++;
								} else if(status.equalsIgnoreCase("Offline")) {
									offlineSites.append("'"+siteID+"'");
									resultSites.add(siteID);
									roInActiveCount++;
								}else {
									disconnectSites.append("'"+siteID+"'");
									roDisconnectCount++;
								}
							}
						}
					//}
					//loop++;
				}
				
				jsonObj.put("SITES", onlineSites.toString());				
				jsonObj.put("COUNT", roActiveCount);
				jsonObj.put("SITE_STATUS","Online");
				jsonObj.put("COLOR_CODE","#39B249");
				jsonObj.put("STATUS_NAME","Online");
				jsonArr.add(jsonObj);
				
				jsonInObj.put("SITES", offlineSites.toString());
				jsonInObj.put("COUNT", roInActiveCount);
				jsonInObj.put("SITE_STATUS","Offline");
				jsonInObj.put("COLOR_CODE","#f6780d");
				jsonInObj.put("STATUS_NAME","Offline < 3 Days");
				jsonArr.add(jsonInObj);
				
				//System.out.println("DeviceStatus sites" + sites.size());
				//System.out.println("DeviceStatus sitesresult" + resultSites.size());
				sites.removeAll(resultSites);
				sites.stream().forEach(site -> disconnectSites.append("'"+site+"'"));
				
				jsonDisconnectObj.put("SITES", disconnectSites.toString());
				jsonDisconnectObj.put("COUNT", sites.size());
				jsonDisconnectObj.put("SITE_STATUS","Disconnected");
				jsonDisconnectObj.put("COLOR_CODE","#E9242C");
				jsonDisconnectObj.put("STATUS_NAME","Offline > 3 Days");
				jsonArr.add(jsonDisconnectObj);
				
			}
	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonArr;
	}
	
	private String convertTime(String timeinlong) {
		if(timeinlong != null ) {
			if(timeinlong != "") {
				if(timeinlong != "null") {
					Date expiry = new Date(Long.parseLong(timeinlong));
					SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					return formatter.format(expiry);
				}else {
					timeinlong = "0000-00-00 00:00:00";
					return timeinlong;
				}
			}else {
				timeinlong = "0000-00-00 00:00:00";
				return timeinlong;
			}
		}else {
			timeinlong = "0000-00-00 00:00:00";
			return timeinlong;
		}
	}
	
	private String getStatusFromLastUpdated(String lastUpdatedStr) {
		java.util.Date lastUpdated=new java.util.Date(Long.valueOf(lastUpdatedStr));		
		
		Calendar c= Calendar.getInstance();
		c.add(Calendar.DATE, -3);
		Date d=c.getTime();
		
		if (lastUpdated.before(d)) {
			return "Disconnected";
		}else if ( DateUtils.isSameDay(lastUpdated,new Date())){
			return "Online";
		}else {
			return "Offline";
		}
	}
	
		
}
