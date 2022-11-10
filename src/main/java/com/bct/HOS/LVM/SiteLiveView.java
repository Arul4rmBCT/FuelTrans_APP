package com.bct.HOS.LVM;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang.time.DateUtils;

import com.bct.HOS.App.DAO.AlarmsDAO;
import com.bct.HOS.App.DAO.NotificationDAO;
import com.bct.HOS.App.DAO.SiteStatusDAO;
import com.bct.HOS.App.utils.LVMConnector;
import com.google.gson.JsonObject;

import net.sf.json.JSONArray;
import net.sf.json.JSONNull;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class SiteLiveView {

	/**
	 * All Site Status
	 * 
	 * @param siteNames
	 * @return
	 */
	public HashMap<String, Object> getSiteStatus(HashMap<String, Integer> roSummary,
			HashMap<String, Integer> tankSummary, HashMap<String, Integer> duSummary, String siteIDs) {
		HashMap<String, Object> siteDetails = null;
		HashMap<String, String> siteNames = null;
		HashMap<String, String> siteAlarmCount = null;
		HashMap<String, String> siteNotificationCount = null;
		try {

			// Get SiteNames
			siteNames = new SiteStatusDAO().getSiteNames(siteIDs);
			siteAlarmCount = new AlarmsDAO().getDayAlarmCount(siteIDs);
			siteNotificationCount = new NotificationDAO().getDayNotificationCount(siteIDs);
			JsonObject req = new JsonObject();
			String response = new LVMConnector().LVMConnect("LVM_SITE_URL", null, req);
			//System.out.println("(getSiteStatus) response is :: " + response);

			JSONArray outerArray = (JSONArray) JSONSerializer.toJSON(response);
			int loop = 0;
			int count = outerArray.size();

			int roActiveCount = 0;
			int roInActiveCount = 0;
			boolean data = false;
			siteDetails = new HashMap<String, Object>();
			//System.out.println("SiltLiveView >count>> " + count);
			if (count > 0) {
				siteDetails = new HashMap<String, Object>();
				String siteID = null;
				String status = null;
				HashMap<String, Object> siteHash = null;
				//System.out.println("SiltLiveView outerArray >>>>>>>>>>>>>>>> " + outerArray.size() + " >>>>>>>>>>>>>>>>"
					//	+ outerArray.toString());
				for (Object js : outerArray) {
					//System.out.println("SiltLiveView outerArray>>>>>>>>>" + loop + ". count >>>>" + (count - 1));
					// if (loop > 0 && loop < (count - 1)) {
					JSONObject arrJson = (JSONObject) js;
					JSONObject dataJson = (JSONObject) arrJson.get("data");
					siteID = (String) dataJson.get("SiteID");
					//System.out.println(
						//	"SiltLiveView siteID>>>>>>>>>>>>>>>>>>>" + siteID + " >>>>>>>>>>>>>>>>>>> " + siteIDs);
					if (siteID != null && siteIDs.contains(siteID)) {
						siteHash = new HashMap<String, Object>();
						siteHash.put("SiteID", siteID);
						siteHash.put("SiteName", siteNames.get(siteID));
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
						
						if (status.equalsIgnoreCase("Online") || status.equalsIgnoreCase("1")) {
							status = "Online";
							roActiveCount++;
						}else if(status.equalsIgnoreCase("Offline") || status.equalsIgnoreCase("0")) {
							status = "Offline";
							roInActiveCount++;
						}else {
							
						}
						siteHash.put("Status", status);
						if(dataJson.get("LastUpdated") instanceof JSONNull) {
							siteHash.put("LastUpdated", "0000-00-00 00:00:00");
						}else {
							siteHash.put("LastUpdated", convertTime(dataJson.get("LastUpdated").toString()));
						}
						
						siteHash.put("Tanks", new HashMap<String, Object>());
						siteHash.put("Pumps", new HashMap<String, Object>());
						siteHash.put("DUs", new HashMap<String, Object>());
						siteHash.put("Devices", new HashMap<String, Object>());
						siteHash.put("LastTransaction", "");
						siteHash.put("LastInventory", "");
						siteHash.put("ToDayAlarms", siteAlarmCount.get(siteID));
						siteHash.put("ToDayNotifications", siteNotificationCount.get(siteID));
						siteHash.put("Interlocks", new HashMap<String, Object>());
						siteDetails.put(siteID, siteHash);
						data = true;
					}

					// }
					// loop++;
				}

				/*
				 * --Total Tank (active/inactive) --Total DU (active/inactive)
				 */
				if (data) {
					//System.out.println("SiltLiveView>>");
					roSummary.put("COUNT", (roActiveCount + roInActiveCount));
					roSummary.put("ACTIVE_COUNT", roActiveCount);
					roSummary.put("INACTIVE_COUNT", roInActiveCount);

					// siteDetails.put("RO_SUMMARY", roSummary);
					//System.out.println("\n\n\n");
					//System.out.println("siteDetails>>>>>>>>>>>>>" + siteDetails);
					//System.out.println("tankSummary>>>>>>>>>>>>>" + tankSummary);
					//System.out.println("siteIDs>>>>>>>>>>>>>>>>>" + siteIDs);

					//System.out.println("\n\n\n");
					getTankStatus(siteDetails, tankSummary, siteIDs);
					// siteDetails.put("TANK_SUMMARY", tankSummary);

					getDUStatus(siteDetails, duSummary, siteIDs);
					// siteDetails.put("DU_SUMMARY", duSummary);

					getLastTransaction(siteDetails, siteIDs);
					getLastInventory(siteDetails, siteIDs);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return siteDetails;
	}

	/**
	 * To get Tank status count
	 * 
	 * @param siteId
	 * @return
	 */
	public void getTankStatus(HashMap<String, Object> siteDetails, HashMap<String, Integer> tankSummary,
			String siteIDs) {
		try {
			HashMap<String, Object> siteHash = null;
			HashMap<String, Object> tankHash = null;
			// String SQL = "SELECT SiteID, if (Status=1) then 'Online' else 'Offline' as
			// Stat, count(Status) as Count FROM TankStatus group by SiteID ,Status ";
			JsonObject req = new JsonObject();
			String response = new LVMConnector().LVMConnect("LVM_TANK_URL", null, req);
			// System.out.println("response is :: " + response);

			JSONArray outerArray = (JSONArray) JSONSerializer.toJSON(response);
			int loop = 0;
			int tnkActiveCount = 0;
			int tnkInActiveCount = 0;
			int count = outerArray.size();

			String status = null;
			String siteID = null;
			int intSts = 0;
			for (Object js : outerArray) {
				//if (loop > 0 && loop < (count - 1)) {
					JSONObject arrJson = (JSONObject) js;
					JSONObject dataJson = (JSONObject) arrJson.get("data");

					siteID = (String) dataJson.getString("SiteID");
					
					if (siteIDs.contains(siteID)) {
						status = (String) dataJson.getString("Status");
						
						if (status.equalsIgnoreCase("Online") || status.equalsIgnoreCase("1")) {
							intSts = 1;
							status = "Online";
							tnkActiveCount++;
						} else {
							intSts = 0;
							status = "Offline";
							tnkInActiveCount++;
						}

						siteHash = (HashMap<String, Object>) siteDetails.get(siteID);
						if(siteHash!=null) {
							tankHash = (HashMap<String, Object>) siteHash.get("Tanks");
							if(tankHash.containsKey(status)) {
								int stsCount = Integer.parseInt(tankHash.get(status).toString());
								stsCount = stsCount+1;
								tankHash.put(status, stsCount);
							}else {
								tankHash.put(status, 1);
							}
							siteHash.put("Tanks", tankHash);
						}
					}else {
						System.out.println("ELSE!..........");
					}
					
//				}
//				loop++;
			}

			tankSummary.put("COUNT", (tnkActiveCount + tnkInActiveCount));
			tankSummary.put("ACTIVE_COUNT", tnkActiveCount);
			tankSummary.put("INACTIVE_COUNT", tnkInActiveCount);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * To get DU status
	 * 
	 * @param siteId
	 * @return
	 */
	public void getDUStatus(HashMap<String, Object> siteDetails, HashMap<String, Integer> duSummary, String siteIDs) {
		try {
			HashMap<String, Object> siteHash = null;
			HashMap<String, Object> duHash = null;
			// String SQL = "SELECT SiteID,DispenserNo,max(LastUpdated) as
			// LastUpdated,max(DispenserStatus) as DispenserStatus FROM DuStatus GROUP BY
			// SiteID,DispenserNo";
			JsonObject req = new JsonObject();
			String response = new LVMConnector().LVMConnect("LVM_DU_PUMP_URL", null, req);

			// System.out.println("response is :: " + response);

			JSONArray outerArray = (JSONArray) JSONSerializer.toJSON(response);
			int loop = 0;
			int count = outerArray.size();
			int duActiveCount = 0;
			int duInActiveCount = 0;

			String status = null;
			String siteID = null;
			int statusCount = 0;
			for (Object js : outerArray) {
				// if (loop > 0 && loop < (count - 1)) {
				JSONObject arrJson = (JSONObject) js;
				JSONObject dataJson = (JSONObject) arrJson.get("data");

				siteID = (String) dataJson.getString("SiteID");
				if (siteIDs.contains(siteID)) {
					statusCount = dataJson.getInt("DispenserNo");
					status = (String) dataJson.getString("DispenserStatus");

					siteHash = (HashMap<String, Object>) siteDetails.get(siteID);
					if(siteHash!=null) {
						duHash = (HashMap<String, Object>) siteHash.get("DUs");
						if (status.equals("0") || status.equals("Offline")) {
							if (duHash.containsKey("Offline")) {
								int val = Integer.parseInt(duHash.get("Offline").toString());
								val++;
								duHash.put("Offline", val);
								duInActiveCount++;
							} else {
								duHash.put("Offline", 1);
								duInActiveCount++;
							}
						} else {
							if (duHash.containsKey("Online")) {
								duActiveCount++;
								int val = Integer.parseInt(duHash.get("Online").toString());
								val++;
								duHash.put("Online", val);
							} else {
								duActiveCount++;
								duHash.put("Online", 1);
							}
						}
					}
				}
				// }
				// loop++;
			}

			duSummary.put("COUNT", (duActiveCount + duInActiveCount));
			duSummary.put("ACTIVE_COUNT", duActiveCount);
			duSummary.put("INACTIVE_COUNT", duInActiveCount);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return
	 */
	public void getLastTransaction(HashMap<String, Object> siteDetails, String siteIDs) {
		try {
			HashMap<String, Object> siteHash = null;
			HashMap<String, Object> tankHash = null;
			// String SQL = " SELECT SITE_ID as SITEID,
			// format_time(timestamp(string(int(max(LastUpdated)/1000))), \"yyyy-MM-dd,
			// HH:mm:ss\") as TRANSACTION_DATE FROM Transactions group by SITE_ID ";
			JsonObject req = new JsonObject();
			String response = new LVMConnector().LVMConnect("LVM_TRANS_URL", null, req);

			JSONArray outerArray = (JSONArray) JSONSerializer.toJSON(response);
			int loop = 0;
			int count = outerArray.size();

			String siteID = null;
			String lastDate = null;
			for (Object js : outerArray) {
				//if (loop > 0 && loop < (count - 1)) {
					JSONObject arrJson = (JSONObject) js;
					JSONObject dataJson = (JSONObject) arrJson.get("data");

					siteID = (String) dataJson.getString("SITEID");
					if (siteIDs.contains(siteID)) {
						lastDate = (String) dataJson.getString("TRANSACTION_DATE");

						siteHash = (HashMap<String, Object>) siteDetails.get(siteID);
						if (siteHash != null) {
							siteHash.put("LastTransaction", convertTimeFormat(lastDate));
						}
					}
//				}
//				loop++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return
	 */
	public void getLastInventory(HashMap<String, Object> siteDetails, String siteIDs) {
		try {
			HashMap<String, Object> siteHash = null;
			// String SQL = " select SITE_ID,
			// format_time(timestamp(string(int(max(LastUpdated)/1000))), \"yyyy-MM-dd,
			// HH:mm:ss\") as INVENTORY_DATE from Inventory group by SITE_ID";
			JsonObject req = new JsonObject();
			String response = new LVMConnector().LVMConnect("LVM_INV_URL", null, req);

			JSONArray outerArray = (JSONArray) JSONSerializer.toJSON(response);
			int loop = 0;
			int count = outerArray.size();

			String siteID = null;
			String lastDate = null;
			for (Object js : outerArray) {
				//if (loop > 0 && loop < (count - 1)) {
					JSONObject arrJson = (JSONObject) js;
					JSONObject dataJson = (JSONObject) arrJson.get("data");

					siteID = (String) dataJson.getString("SITE_ID");
					if (siteIDs.contains(siteID)) {
						lastDate = (String) dataJson.getString("INVENTORY_DATE");

						siteHash = (HashMap<String, Object>) siteDetails.get(siteID);
						if (siteHash != null) {
							siteHash.put("LastInventory", convertTimeFormat(lastDate));
						}
					}
//				}
//				loop++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private String convertTimeFormat(String timeinlong) {
		if (timeinlong != null) {
			if (timeinlong != "") {
				if (timeinlong != "null") {
					try {
						if(timeinlong.contains("T")) {
							SimpleDateFormat inputParser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
							Date date_out = inputParser.parse(timeinlong);
							SimpleDateFormat formatterout = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							return formatterout.format(date_out);
						}else {
							SimpleDateFormat inputParser = new SimpleDateFormat("yyyyMMddHHmmss");
							Date date_out = inputParser.parse(timeinlong);
							SimpleDateFormat formatterout = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							return formatterout.format(date_out);
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						return timeinlong;
					}
				} else {
					return timeinlong;
				}
			} else {
				return timeinlong;
			}
		} else {
			return timeinlong;
		}
	}
	
	private String convertTime(String timeinlong) {
		if(timeinlong != null ) {
			if(timeinlong != "") {
				if(timeinlong != "null") {
					Date expiry = new Date(Long.parseLong(timeinlong));
					SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					return formatter.format(expiry);
				}else {
					return timeinlong;
				}
			}else {
				return timeinlong;
			}
		}else {
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
	
	public static void main(String args[]) {
		System.out.println(new SiteLiveView().convertTimeFormat("2022-10-13T07:14:46.000Z"));
	}
	
}
