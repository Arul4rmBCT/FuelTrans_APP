package com.bct.HOS.App.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


public class LVMConnector {

	HOSConfig conf = null;
	static JsonArray resArray = null;
	static String serviceName = null;

	public LVMConnector() {
		conf = new HOSConfig();
	}

	/**
	 * 
	 * @param serviceName
	 * @param params
	 * @return
	 */
	public String LVMConnect(String sName, String siteId, JsonObject requestObj) {
		String res = null;
		String jsonStr = null;
		HttpURLConnection connection = null;
		try {
				this.serviceName = sName;
				Date d1 = new Date();
				URL url = new URL(conf.getValue(sName));
				connection = (HttpURLConnection) url.openConnection();
				connection.setDoOutput(true);
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/json");
				//System.out.println(sName + " >> REQUEST >> " + requestObj.toString());
				OutputStream os = connection.getOutputStream();
				os.write(requestObj.toString().getBytes());
				os.flush();

				BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
				while ((res = br.readLine()) != null) {
					//System.out.println(url + " >> RESPONSE - "
					//+ new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.SSS").format(new Date()) + " >>" + res);
					if (res != null)
						jsonStr = res;
				}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		//System.out.println(serviceName + ">>>>>>>>>>>>> Response Array >>>>>>>>>>>>>>>>" + jsonStr);
		return jsonStr;
	}

	private void iterateMap(Map<String, Object> map, JsonObject dataObj) {
		try {
			Gson gson = new Gson();
			// Object obj = null;
			Map<String, Object> innerMap = null;
			if (map != null) {
				Iterator entries = map.entrySet().iterator();
				JsonObject innerObj = new JsonObject();
				while (entries.hasNext()) {
					Entry thisEntry = (Entry) entries.next();
					Object key = thisEntry.getKey();
					Object value = thisEntry.getValue();

					// System.out.println(key +" = "+ value);
					if (value instanceof ArrayList) {
						ArrayList arr1 = (ArrayList) value;
						for (int i = 0; i < arr1.size(); i++) {
							innerMap = (Map<String, Object>) arr1.get(i);
							iterateMap(innerMap, new JsonObject());
						}
					} else if (value instanceof String) {
						if (key.toString().equalsIgnoreCase("Sts")) {
							innerObj.addProperty("Status", value.toString());
						} else if (key.toString().equalsIgnoreCase("Status")
								&& this.serviceName.equalsIgnoreCase("LVM_DU_PUMP_URL")) {
							innerObj.addProperty("DispenserStatus", value.toString());
						} else if (key.toString().equalsIgnoreCase("Status")
								&& this.serviceName.equalsIgnoreCase("LVM_TANK_URL")) {
							innerObj.addProperty("Stat", value.toString());
						} else {
							innerObj.addProperty(key.toString(), value.toString());
						}
						dataObj.add("data", innerObj);
						resArray.add(dataObj);
					} else {
						Map<String, Object> inMap = (Map<String, Object>) value;
						for (String entry : inMap.keySet()) {
							Object v = inMap.get(entry);
							String vs = null;
							if (v != null) {
								vs = v.toString();
							} else {
								vs = "";
							}
							if (entry.equalsIgnoreCase("Sts")) {
								innerObj.addProperty("Status", vs);
							} else if (entry.equalsIgnoreCase("Status")
									&& this.serviceName.equalsIgnoreCase("LVM_DU_PUMP_URL")) {
								innerObj.addProperty("DispenserStatus", vs);
							} else if (entry.toString().equalsIgnoreCase("Status")
									&& this.serviceName.equalsIgnoreCase("LVM_TANK_URL")) {
								innerObj.addProperty("Stat", vs);
							} else {
								innerObj.addProperty(entry, vs);
							}

						}
						innerObj.addProperty("SiteID", key.toString());
						dataObj.add("data", innerObj);
						resArray.add(dataObj);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		try {
			String req = "{\"STATION7\":[{\"Status\":\"0\",\"TankNo\":\"1\",\"lastUpdateTime\":\"1656170894\"},{\"Status\":\"0\",\"TankNo\":\"2\",\"lastUpdateTime\":\"1656170894\"},{\"Status\":\"0\",\"TankNo\":\"3\",\"lastUpdateTime\":\"1656170894\"}],\"12345\":[{\"Status\":\"0\",\"TankNo\":\"1\",\"lastUpdateTime\":\"1658058085000\"},{\"Status\":\"0\",\"TankNo\":\"2\",\"lastUpdateTime\":\"1658058085000\"}],\"ALMFS010\":[{\"Status\":\"1\",\"TankNo\":\"1\",\"lastUpdateTime\":\"1657109210\"},{\"Status\":\"1\",\"TankNo\":\"2\",\"lastUpdateTime\":\"1657109210\"},{\"Status\":\"1\",\"TankNo\":\"3\",\"lastUpdateTime\":\"1657109210\"}],\"000175\":[{\"Status\":\"1\",\"TankNo\":\"2\",\"lastUpdateTime\":\"1658058085005\"},{\"Status\":\"1\",\"TankNo\":\"3\",\"lastUpdateTime\":\"1658058085005\"},{\"Status\":\"1\",\"TankNo\":\"1\",\"lastUpdateTime\":\"1658058085005\"}]}";
			resArray = new JsonArray();
			serviceName = "LVM_TANK_URL";
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> map = mapper.readValue(req, Map.class);
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + map);
			JsonObject innerObj = new JsonObject();
			new LVMConnector().iterateMap(map, innerObj);
			System.out.println(resArray);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
