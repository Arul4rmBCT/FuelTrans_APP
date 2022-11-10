package com.bct.HOS.App.DAO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.collections.map.HashedMap;

import com.bct.HOS.App.utils.DBConnector;
import com.bct.HOS.App.utils.HOSConfig;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SiteInfoDAO {

	private Connection conn = null;
	private DBConnector dbc = null;
	private String schema = null;
	private String uuid = null;
	boolean createSite = false;
	HOSConfig conf = null;
	HashMap globalPRDCode = null;

	ArrayList<String> sitesList = new ArrayList<String>();
	HashMap<String, ArrayList<String>> productsList = new HashMap<String, ArrayList<String>>();

	Logger logger = Logger.getLogger("SiteInfoDAO");
	FileHandler fh;

	public SiteInfoDAO() {
		dbc = new DBConnector();
		conn = dbc.getConnection();
		schema = dbc.getSchema();
		conf = new HOSConfig();
		globalPRDCode = collectGPRDCode();
		uuid = generateType1UUID().toString();

		createSite = Boolean.parseBoolean(conf.getValue("CREATE_SITE"));

		try {
			fh = new FileHandler("SiteInfo.log");
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static long get64LeastSignificantBitsForVersion1() {
		Random random = new Random();
		long random63BitLong = random.nextLong() & 0x3FFFFFFFFFFFFFFFL;
		long variant3BitFlag = 0x8000000000000000L;
		return random63BitLong + variant3BitFlag;
	}

	private static long get64MostSignificantBitsForVersion1() {
		LocalDateTime start = LocalDateTime.of(1582, 10, 15, 0, 0, 0);
		Duration duration = Duration.between(start, LocalDateTime.now());
		long seconds = duration.getSeconds();
		long nanos = duration.getNano();
		long timeForUuidIn100Nanos = seconds * 10000000 + nanos * 100;
		long least12SignificatBitOfTime = (timeForUuidIn100Nanos & 0x000000000000FFFFL) >> 4;
		long version = 1 << 12;
		return (timeForUuidIn100Nanos & 0xFFFFFFFFFFFF0000L) + version + least12SignificatBitOfTime;
	}

	public static UUID generateType1UUID() {

		long most64SigBits = get64MostSignificantBitsForVersion1();
		long least64SigBits = get64LeastSignificantBitsForVersion1();

		return new UUID(most64SigBits, least64SigBits);
	}

	/*
	 * private HashMap collectGPRDCode() { String str =
	 * conf.getValue("GLOBAL_PCODE"); HashMap<String, String> hm = new
	 * HashMap<String, String>(); try { String[] tmp = null; String[] tmpv = null;
	 * tmp = str.split(";"); for (String tmps : tmp) { tmpv = tmps.split("~");
	 * hm.put(tmpv[0], tmpv[1]); } } catch (Exception e) { e.printStackTrace(); }
	 * return hm; }
	 */

	private HashMap collectGPRDCode() {
		HashMap<String, HashMap<String, String>> hm = new HashMap<String, HashMap<String, String>>();
		try {
			String SQL = " SELECT \"GLOBAL_ID\",\"SAP_CODE\",\"ITEM_NAME\" FROM  " + schema + ".\"MS_GLOBAL_PRD\"  ";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			String productName = null;
			String globalId = null;
			String sapCode = null;
			HashMap<String, String> ihm = null;
			while (rs.next()) {
				productName = rs.getString("ITEM_NAME");
				globalId = rs.getString("GLOBAL_ID");
				sapCode = rs.getString("SAP_CODE");

				ihm = new HashMap<String, String>();
				ihm.put("GLOBALID", globalId);
				ihm.put("SAPCODE", sapCode);
				hm.put(productName, ihm);

			}
			rs = null;
			stmt = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hm;
	}

	public void processSites(JSONArray siteDataSet) {
		try {
			String siteId = null;
			String siteName = null;
			String productName = null;
			String SQL = null;
			String sapCode = null;
			String productNo = null;
			int tankNo = 0;
			int count = 0;
			int pid = 0;
			int duid = 0;
			int tankid = 0;
			int pumpid = 0;
			Statement stm = conn.createStatement();
			ResultSet rs = null;
			
			String invId = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());

			String hosSITEINVURL = conf.getValue("STORE_INVENTORY");
			String hosPRDURL = conf.getValue("STORE_PRODUCT");
			String hosTNKURL = conf.getValue("STORE_TANK");
			JSONObject prd = null;
			JSONObject tnk = null;
			JSONObject reqPRDJSONObj = null;
			JSONObject reqTNKJSONObj = null;
			JSONObject reqSSJSONObj = null;
			JSONArray products = new JSONArray();
			JSONArray tanks = new JSONArray();
			JSONArray siteStatus = new JSONArray();
			Date dt = null;
			SimpleDateFormat simpleDF = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			//String df = simpleDateFormat.format(dt);
			String df = null;
			if (siteDataSet != null) {
				logger.info(uuid + " >> SITEINFO DATA SIZE :: " + siteDataSet.size());
				for (int index = 0; index < siteDataSet.size(); index++) {

					JSONObject obj = siteDataSet.getJSONObject(index);
					siteId = obj.getString("LOCATIONID");
					productName = obj.getString("PRODUCTNAME");
					siteName = obj.getString("SITENAME");
					tankNo = obj.getInt("TANKNO");
					HashMap gid = (HashMap) globalPRDCode.get(productName);
					String globalID = (String) gid.get("GLOBALID");
					sapCode = (String) gid.get("SAPCODE");

					dt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS").parse(obj.getString("LASTCOMMUNICATION"));
					df = simpleDF.format(dt);

					sitesList.add(siteId);
					productsList.put(siteId, new ArrayList<String>());

					if (!productAvailable(siteId, productName)) {
						// create product
						reqPRDJSONObj = new JSONObject();
						reqPRDJSONObj.put("SiteId", siteId);
						reqPRDJSONObj.put("PCode", sapCode);
						reqPRDJSONObj.put("Prod", productName);
						reqPRDJSONObj.put("ROD", sapCode);
						reqPRDJSONObj.put("ProductType", "Fuel");
						reqPRDJSONObj.put("ProductUnit", "Ltr.");
						reqPRDJSONObj.put("ProductPrice", 0.0);
						reqPRDJSONObj.put("Product_id", globalID);
						reqPRDJSONObj.put("RequestFlag", "A");
						reqPRDJSONObj.put("RequestingUser", conf.getValue("HOS_USERID"));
						reqPRDJSONObj.put("RequestingTime", df);
						products.add(reqPRDJSONObj);
						logger.info(uuid + " >> PRODUCT MAPPING CREATION");
					}
					if (!tankAvailable(siteId, productName, tankNo)) {
						// create tank
						reqTNKJSONObj = new JSONObject();
						reqTNKJSONObj.put("SiteId", siteId);
						reqTNKJSONObj.put("ProductNo", sapCode);
						reqTNKJSONObj.put("TankNo", tankNo);
						reqTNKJSONObj.put("Capacity", "25000");
						reqTNKJSONObj.put("MinimumCapacity", "2500");
						reqTNKJSONObj.put("TankDiameter", "12.25");
						reqTNKJSONObj.put("ProbeType", "18");
						reqTNKJSONObj.put("ProbeId", "4");
						reqTNKJSONObj.put("ProductOffset", 55);
						reqTNKJSONObj.put("WaterOffset", -17.0);
						reqTNKJSONObj.put("WaterFloat", 1);
						reqTNKJSONObj.put("DensityFloat", 0);
						reqTNKJSONObj.put("HighWater", 50);
						reqTNKJSONObj.put("DeliveryDelay", 5);
						reqTNKJSONObj.put("MaxCapacity", 24000);
						reqTNKJSONObj.put("RequestFlag", "A");
						reqTNKJSONObj.put("RequestingUser", conf.getValue("HOS_USERID"));
						reqTNKJSONObj.put("RequestingTime", df);
						tanks.add(reqTNKJSONObj);
						logger.info(uuid + " >> TANK CREATION");
					}

					// Update Site Status
					reqSSJSONObj = new JSONObject();
					reqSSJSONObj.put("SiteId", siteId);
					timestamp = new Timestamp(System.currentTimeMillis());
					invId = sdf.format(timestamp);
					invId = invId.concat(String.valueOf(index));
					
					reqSSJSONObj.put("InventoryId", invId);
					reqSSJSONObj.put("TankNo", tankNo);
					reqSSJSONObj.put("Capacity", obj.getDouble("VOLUME"));
					reqSSJSONObj.put("NVol", obj.getDouble("PRODUCTLEVEL"));
					reqSSJSONObj.put("ProductHeight", obj.getDouble("PRODUCTLEVEL"));
					reqSSJSONObj.put("Ullage", obj.getDouble("ULLAGE"));
					reqSSJSONObj.put("WaterHeight", obj.getDouble("WATERLEVEL"));
					reqSSJSONObj.put("Temperature", obj.getDouble("TEMPERATURE"));
					reqSSJSONObj.put("Density", 0);
					reqSSJSONObj.put("TDst", 0);
					reqSSJSONObj.put("TVol", 0);
					reqSSJSONObj.put("WaterVolume", 0);
					reqSSJSONObj.put("DstStat", "1");
					reqSSJSONObj.put("InventoryDate", df);
					siteStatus.add(reqSSJSONObj);

				}

				String res = null;
				if (products.size() > 0) {
					prd = new JSONObject();
					prd.put("ProductDetails", products);
					res = HOSConnect(hosPRDURL, prd.toString());
					//System.out.println(" >>>>>>>>>>>>>>> " + prd.toString());
					logger.info(uuid + " >> PRODUCT MAPPING CREATED :: " + res);
				}

				if (tanks.size() > 0) {
					tnk = new JSONObject();
					tnk.put("TankDetails", tanks);
					res = HOSConnect(hosTNKURL, tnk.toString());
					//System.out.println(" >>>>>>>>>>>>>>> " + tnk.toString());
					logger.info(uuid + " >> TANK CREATED :: " + res);
				}

				///////////////////////
				String siteId1 = null;
				int tankNo1 = 0;
				String productName1 = null;
				String productNo1 = null;
				for (int index = 0; index < siteDataSet.size(); index++) {

					JSONObject obj = siteDataSet.getJSONObject(index);
					siteId1 = obj.getString("LOCATIONID");
					productName1 = obj.getString("PRODUCTNAME");
					tankNo1 = obj.getInt("TANKNO");

					// Site Status update
					SQL = " INSERT INTO "+schema+".\"SiteStatusChangeHistory\" (\"SiteID\",\"NewStatus\",\"LastUpdated\") " + 
							" VALUES ('"+siteId1+"',1,extract('epoch' from CURRENT_TIMESTAMP)::bigint) ";
					stm.execute(SQL);
					
					
					// DU / PUMP / NOZZLE
					SQL = " SELECT \"PRODUCT_NO\" FROM  " + schema + ".\"MS_PRODUCTS\" PRD WHERE PRD.\"SITE_ID\" = '" + siteId1
							+ "' AND UPPER(PRD.\"PRODUCT_NAME\") = UPPER('" + productName1 + "') AND PRD.\"ADRM_STATUS\" != 'D' ";
					//SQL += " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) ";
					//System.out.println("SQL DU = " + SQL);
					rs = stm.executeQuery(SQL);
					while (rs.next()) {
						productNo1 = rs.getString("PRODUCT_NO");
					}

					// DU CHECK
					count = 0;
					SQL = "SELECT COUNT(*) AS COUNT FROM " + schema + ".\"MS_DISPENSER\" WHERE \"SITE_ID\" = '"
							+ siteId1 + "' AND \"DU_NO\" = '1' ";
					//System.out.println("SQL DU = " + SQL);
					rs = stm.executeQuery(SQL);
					while (rs.next()) {
						count = rs.getInt("COUNT");
					}

					if (count == 0) {
						SQL = " INSERT INTO " + schema
								+ ".\"MS_DISPENSER\"(\"DU_NO\", \"SITE_ID\", \"STATUS\", \"CREATED_BY\", \"CREATED_DATE\") SELECT '1','"
								+ siteId1 + "','0','SITEINFO',CURRENT_TIMESTAMP ";
						//System.out.println("SQL DU = " + SQL);
						stm.execute(SQL);
					}

					// PUMP CHECK
					count = 0;
					SQL = "SELECT COUNT(*) AS COUNT FROM " + schema + ".\"MS_PUMP_LIST\" WHERE \"SITE_ID\" = '"
							+ siteId1 + "' AND \"PUMP_NO\" = '1' ";
					//System.out.println("SQL PUMP = " + SQL);
					rs = stm.executeQuery(SQL);
					while (rs.next()) {
						count = rs.getInt("COUNT");
					}

					if (count == 0) {
						SQL = " INSERT INTO " + schema
								+ ".\"MS_PUMP_LIST\"(\"PUMP_NO\", \"SITE_ID\",\"PUMP_TYPE\", \"STATUS\", \"DU_NO\", \"CREATED_BY\", \"CREATION_TIME\") SELECT '1','"
								+ siteId1 + "','7','0','1','SITEINFO',CURRENT_TIMESTAMP ";
						//System.out.println("SQL PUMP = " + SQL);
						stm.execute(SQL);
					}

					// NOZZLE CHECK
					count = 0;
					SQL = " SELECT COUNT(*) AS \"COUNT\" FROM " + schema + ".\"MS_NOZZLE_LIST\" WHERE \"SITE_ID\" = '"
							+ siteId1 + "' AND \"PRODUCT_NO\" = '" + productNo1 + "' " + " AND \"TANK_NO\" = '"
							+ tankNo1 + "' AND \"DU_NO\" = '1' AND \"PUMP_NO\" = '1' ";
					//System.out.println("SQL NOZZLE = " + SQL);
					rs = stm.executeQuery(SQL);
					while (rs.next()) {
						count = rs.getInt("COUNT");
					}

					if (count == 0) {
						SQL = " SELECT PRD.\"PRODUCT_ID\",TNK.\"TANK_ID\",DU.\"DISPENSER_ID\",PMP.\"PUMP_ID\" FROM   "
								+ " " + schema + ".\"MS_PRODUCTS\" PRD   " + " INNER JOIN " + schema
								+ ".\"MS_TANK\" TNK ON TNK.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND TNK.\"SITE_ID\" = PRD.\"SITE_ID\"  "
								+ " INNER JOIN " + schema + ".\"MS_DISPENSER\" DU ON DU.\"SITE_ID\" = PRD.\"SITE_ID\"  "
								+ " INNER JOIN " + schema
								+ ".\"MS_PUMP_LIST\" PMP ON PMP.\"SITE_ID\" = PRD.\"SITE_ID\"  " + " WHERE 1 = 1  "
								+ " AND PRD.\"SITE_ID\" = '" + siteId1 + "' " + " AND PRD.\"PRODUCT_NO\" = '"
								+ productNo1 + "'  " + " AND TNK.\"TANK_NO\" = '" + tankNo1 + "'  "
								+ " AND DU.\"DU_NO\" = '1'  " + " AND PMP.\"PUMP_NO\" = '1'  AND PRD.\"ADRM_STATUS\" != 'D' "
								//+ " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) "
								+ " GROUP BY PRD.\"PRODUCT_ID\",TNK.\"TANK_ID\",DU.\"DISPENSER_ID\",PMP.\"PUMP_ID\" ";
						//System.out.println("SQL NOZZLE = " + SQL);
						rs = stm.executeQuery(SQL);
						pid = 0;
						tankid = 0;
						duid = 0;
						pumpid = 0;
						while (rs.next()) {
							pid = rs.getInt("PRODUCT_ID");
							tankid = rs.getInt("TANK_ID");
							duid = rs.getInt("DISPENSER_ID");
							pumpid = rs.getInt("PUMP_ID");
						}

						if (pid > 0 && tankid > 0 && duid > 0 && pumpid > 0) {

							int nozzleNo = 0;
							SQL = " SELECT MAX(\"NOZZLE_NO\")+1 AS \"NOZZLE_NO\" FROM " + schema
									+ ".\"MS_NOZZLE_LIST\" " + " WHERE \"SITE_ID\" = '" + siteId1 + "'";
							rs = stm.executeQuery(SQL);
							while (rs.next()) {
								nozzleNo = rs.getInt("NOZZLE_NO");
							}

							if (nozzleNo == 0)
								nozzleNo++;

							SQL = "  INSERT INTO " + schema + ".\"MS_NOZZLE_LIST\"( "
									+ "	\"NOZZLE_NO\", \"SITE_ID\", \"PRODUCT_NO\", \"PRODUCT_ID\", \"TANK_NO\", \"TANK_ID\", \"DU_NO\", \"DU_ID\", "
									+ "	\"PUMP_NO\", \"PUMP_ID\", \"CREATED_BY\", \"CREATED_DATE\") " + "	SELECT '"
									+ nozzleNo + "','" + siteId1 + "','" + productNo1 + "','" + pid + "','" + tankNo1
									+ "','" + tankid + "','1','" + duid + "','1','" + pumpid
									+ "','SITEINFO',CURRENT_TIMESTAMP";
							//System.out.println("SQL NOZZLE = " + SQL);
							stm.execute(SQL);
						}
					}

				}

				//////////////////////

				if (siteStatus.size() > 0) {
					JSONObject ss = new JSONObject();
					ss.put("Inventory", siteStatus);
					res = HOSConnect(hosSITEINVURL, ss.toString());
					//System.out.println(" >>>>>>>>>>>>>>> " + ss.toString());
					logger.info(uuid + " >> SITE STATUS UPDATED :: " + res);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Get item contains and return index if exist
	 * 
	 * @param mArrayList
	 * @param str
	 * @return
	 */
	private int getItempos(ArrayList<String> mArrayList, String str) {
		for (int i = 0; i < mArrayList.size(); i++) {
			if (mArrayList.get(i).indexOf(str) != -1) {
				return i;
			}
		}
		return -1;
	}

	/*
	 * 
	 */
	public boolean productAvailable(String siteId, String productName) {
		boolean result = false;
		try {
			String SQL = " SELECT COUNT(*) AS \"COUNT\" FROM " + schema + ".\"MS_PRODUCTS\" PRD " + " INNER JOIN "
					+ schema + ".\"MS_SITE\" ST ON ST.\"SITE_ID\" = PRD.\"SITE_ID\" " + " WHERE 1 = 1 "
					+ " AND UPPER(PRD.\"PRODUCT_NAME\") = UPPER('" + siteId + "') " + " AND ST.\"SITE_ID\" = '" + productName + "' AND PRD.\"ADRM_STATUS\" != 'D' ";
			//SQL += " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) ";

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			int count = 0;
			while (rs.next()) {
				count = rs.getInt("COUNT");
				if (count > 0)
					result = true;
			}
			rs = null;
			stmt = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/*
	 * 
	 */
	public boolean tankAvailable(String siteId, String productName, int tankNo) {
		boolean result = false;
		try {
			String SQL = " SELECT COUNT(*) AS \"COUNT\" FROM " + schema + ".\"MS_PRODUCTS\" PRD " + " INNER JOIN "
					+ schema
					+ ".\"MS_TANK\" TNK ON TNK.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND TNK.\"SITE_ID\" = PRD.\"SITE_ID\" "
					+ " INNER JOIN " + schema + ".\"MS_SITE\" ST ON ST.\"SITE_ID\" = PRD.\"SITE_ID\" "
					+ " WHERE 1 = 1  " + " AND UPPER(PRD.\"PRODUCT_NAME\") = UPPER('" + productName + "') "
					+ " AND ST.\"SITE_ID\" = '" + siteId + "' " + " AND TNK.\"TANK_NO\" = " + tankNo + " AND PRD.\"ADRM_STATUS\" != 'D' ";
			//SQL += " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) ";

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(SQL);
			int count = 0;
			while (rs.next()) {
				count = rs.getInt("COUNT");
				if (count > 0)
					result = true;
			}
			rs = null;
			stmt = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String HOSConnect(String serviceName, String params) {
		String res = null;
		try {
			URL url = new URL(serviceName);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
			wr.write(params.toString());
			wr.flush();

			StringBuilder sb = new StringBuilder();
			int HttpResult = connection.getResponseCode();
			if (HttpResult == HttpURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
				//System.out.println("" + sb.toString());
			} else {
				//System.out.println(connection.getResponseMessage());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * 
	 * @param siteDataSet
	 */
	public void processSales(JSONArray siteDataSet) {
		String SQL = null;
		Statement stm = null;
		ResultSet rs = null;
		try {
			Random random = new Random();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());

			stm = conn.createStatement();
			SQL = " SELECT MS.\"SITE_ID\",DIS.\"DU_NO\",PML.\"PUMP_NO\",NZL.\"NOZZLE_NO\",NZL.\"PRODUCT_NO\",UPPER(PRD.\"PRODUCT_NAME\") AS \"PRODUCT_NAME\",NZL.\"TANK_NO\" FROM "
					+ schema + ".\"MS_SITE\" MS " + " INNER JOIN " + schema
					+ ".\"MS_DISPENSER\" DIS ON DIS.\"SITE_ID\" = MS.\"SITE_ID\" " + " INNER JOIN " + schema
					+ ".\"MS_PUMP_LIST\" PML ON PML.\"SITE_ID\" = DIS.\"SITE_ID\" AND PML.\"DU_NO\" = DIS.\"DU_NO\"  "
					+ " INNER JOIN " + schema
					+ ".\"MS_NOZZLE_LIST\" NZL ON NZL.\"SITE_ID\" = DIS.\"SITE_ID\" AND PML.\"DU_NO\" = NZL.\"DU_NO\" AND PML.\"PUMP_NO\" = NZL.\"PUMP_NO\" "
					+ " INNER JOIN " + schema
					+ ".\"MS_PRODUCTS\" PRD ON PRD.\"SITE_ID\" = DIS.\"SITE_ID\" AND PRD.\"PRODUCT_NO\" = NZL.\"PRODUCT_NO\" "
					+ " WHERE 1 = 1 " + " AND MS.\"SITE_TYPE\" = 'SITEINFO' AND PRD.\"ADRM_STATUS\" != 'D' "
					//+ " AND PRD.\"MODIFIED_DATE\" = (SELECT MAX(\"MODIFIED_DATE\") FROM "+schema+".\"MS_PRODUCTS\" PRD1 WHERE PRD1.\"PRODUCT_NO\" = PRD.\"PRODUCT_NO\" AND PRD1.\"SITE_ID\" = PRD.\"SITE_ID\" ) "
					+ " GROUP BY MS.\"SITE_ID\",DIS.\"DU_NO\",PML.\"PUMP_NO\",NZL.\"NOZZLE_NO\",NZL.\"PRODUCT_NO\",PRD.\"PRODUCT_NAME\",NZL.\"TANK_NO\"  "
					+ " ORDER BY MS.\"SITE_ID\",DIS.\"DU_NO\",PML.\"PUMP_NO\",NZL.\"NOZZLE_NO\",NZL.\"PRODUCT_NO\",PRD.\"PRODUCT_NAME\",NZL.\"TANK_NO\" ";

			//System.out.println("processSales = " + SQL);
			rs = stm.executeQuery(SQL);

			String siteId = null;
			String tankNo = null;
			String duNo = null;
			String pumpNo = null;
			String nozzleNo = null;
			String productNo = null;
			String productName = null;

			String siteNo = null;
			String prdName = null;
			String sale = null;
			String startDate = null;
			String startTime = null;
			String endDate = null;
			String endTime = null;
			String transId = null;

			HashMap<String, HashMap<String, HashMap<String, String>>> siteSet = new HashMap<String, HashMap<String, HashMap<String, String>>>();
			HashMap<String, HashMap<String, String>> productHash = null;
			HashMap<String, String> innerHash = null;
			while (rs.next()) {
				innerHash = new HashMap<String, String>();
				siteId = rs.getString("SITE_ID");
				duNo = rs.getString("DU_NO");
				tankNo = rs.getString("TANK_NO");
				pumpNo = rs.getString("PUMP_NO");
				nozzleNo = rs.getString("NOZZLE_NO");
				productNo = rs.getString("PRODUCT_NO");
				productName = rs.getString("PRODUCT_NAME");

				innerHash.put("DU_NO", duNo);
				innerHash.put("PUMP_NO", pumpNo);
				innerHash.put("NOZZLE_NO", nozzleNo);
				innerHash.put("PRODUCT_NO", productNo);
				innerHash.put("TANK_NO", tankNo);

				if (siteSet.containsKey(siteId)) {
					if (!siteSet.get(siteId).containsKey(productName)) {
						productHash = siteSet.get(siteId);
						productHash.put(productName, innerHash);
						siteSet.put(siteId, productHash);
					}
				} else {
					productHash = new HashMap<String, HashMap<String, String>>();
					productHash.put(productName, innerHash);
					siteSet.put(siteId, productHash);
				}
			}

			//System.out.println("siteSet is ==== " + siteSet);
			HashMap<String, HashMap<String, String>> innerMap = null;
			HashMap<String, String> dataMap = null;
			//System.out.println("SALES ===== " + siteDataSet.toString());
			for (int i = 0, size = siteDataSet.size(); i < size; i++) {
				JSONObject obj = siteDataSet.getJSONObject(i);
				if (obj != null) {
					if (obj.containsKey("siteId")) {

						siteNo = obj.getString("siteId");
						prdName = obj.getString("productName");
						sale = obj.getString("sale");
						startDate = obj.getString("startDate");
						startTime = obj.getString("startTime");
						endDate = obj.getString("endDate");
						endTime = obj.getString("endTime");

						//System.out.println("prdName ==== " + prdName);

						innerMap = siteSet.get(siteNo);
						//System.out.println("innerMap (" + siteNo + ") -==== " + innerMap);
						if (innerMap != null)
							dataMap = innerMap.get(prdName);

						if (dataMap != null && Double.parseDouble(sale) > 0) {
							duNo = dataMap.get("DU_NO");
							tankNo = dataMap.get("TANK_NO");
							pumpNo = dataMap.get("PUMP_NO");
							nozzleNo = dataMap.get("NOZZLE_NO");
							productNo = dataMap.get("PRODUCT_NO");

							//transId = sdf.format(timestamp);
							//transId = transId.concat(String.valueOf(i));
							long lngTrnsId = new SimpleDateFormat("yyyymmdd").parse("2021-06-27").getTime();
							transId = String.valueOf(lngTrnsId+(long) (random.nextDouble() * lngTrnsId));
							
							SQL = " INSERT INTO " + schema + ".\"TRANSACTIONS\"(   "
									+ " \"TRANSACTION_ID\", \"SITE_ID\", \"TRANSACTION_DATE\",    "
									+ " \"TRANSACTION_TYPE\", \"TRANSACTION_SUB_TYPE\",    "
									+ " \"TANK_NO\", \"PUMP_NO\", \"NOZZLE_NO\", \"PRODUCT_NO\",    "
									+ " \"UNIT_PRICE\", \"VOLUME\", \"AMOUNT\", "
									+ " \"START_TOTALIZER\", \"END_TOTALIZER\", " + " \"IS_RECEIPT_PRINTED\",    "
									+ " \"PURE_VOLUME\", \"2T_VOLUME\", \"VAT_AMOUNT\", \"SALES_TAX\", "
									+ " \"MOPD6\",   " + " \"CREATED_BY\", \"CREATION_DATE\")   " + "VALUES ('"
									+ transId + "','" + siteNo + "','" + startDate + ' ' + startTime + "','1','0','"
									+ tankNo + "','" + pumpNo + "','" + nozzleNo + "','" + productNo + "','0.00','"
									+ sale + "','0.00',"
									+ "'0.00','0.00','N','0.00','0.00','0.0000','0.00','0','SITEINFO',CURRENT_TIMESTAMP)";
							//System.out.println("SQL--" + SQL);
							try {
								stm.execute(SQL);
							} catch (org.postgresql.util.PSQLException e) {
								System.out.println("ErrOR in QUERY!...");
							}
						}
					}
				}
				dataMap = null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				dbc.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
