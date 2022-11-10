package com.bct.HOS.App;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.bct.HOS.App.BO.ResponseBO;
import com.bct.HOS.App.BO.TSMBO;
import com.bct.HOS.App.BO.UserBO;
import com.bct.HOS.App.DAO.DeviceDAO;
import com.bct.HOS.App.DAO.HierarchyFT;
import com.bct.HOS.App.DAO.SiteStatusDAO;
import com.bct.HOS.App.utils.RestUtils;
import com.bct.HOS.LVM.DeviceStatus;
import com.bct.HOS.LVM.SiteLiveView;
import com.google.gson.Gson;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Path("/v1")
public class DeviceMSrvServices {

	@POST
	@Consumes({ "application/json" })
	@Path("/getNozzleStatus/")
	public Response getNozzleStatus(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		JSONArray dataSet = new JSONArray();
		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					String userId = null;
					String roleId = null;
					String date = null;
					String country = null;
					String siteIDs = null;

					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					date = tsmBO.getDate();
					country = tsmBO.getCountry();
					siteIDs = tsmBO.getSiteID();

					if (siteIDs == null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);

					// System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						dataSet = new DeviceDAO().getNozzleDetail(siteIDs, date);
						responseObj.setDataSet(dataSet);
						responseObj.setError(null);
						responseObj.setErrorCode("0000");
						responseObj.setStatus("Success");
					} else {
						responseObj.setError("No Data!.");
						responseObj.setErrorCode("0001");
						responseObj.setStatus("Success");
					}
				} else {
					responseObj.setError("Wrong Token @getNozzleStatus");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getNozzleStatus");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getNozzleStatus");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getPumpStatus/")
	public Response getPumpStatus(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		JSONArray dataSet = new JSONArray();
		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					String userId = null;
					String roleId = null;
					String date = null;
					String country = null;
					String siteIDs = null;

					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					date = tsmBO.getDate();
					country = tsmBO.getCountry();
					siteIDs = tsmBO.getSiteID();

					if (siteIDs == null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					// System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						dataSet = new DeviceDAO().getPumpDetail(siteIDs, date);
						responseObj.setDataSet(dataSet);
						responseObj.setError(null);
						responseObj.setErrorCode("0000");
						responseObj.setStatus("Success");
					} else {
						responseObj.setError("No Data!.");
						responseObj.setErrorCode("0001");
						responseObj.setStatus("Success");
					}
				} else {
					responseObj.setError("Wrong Token @getPumpStatus");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getPumpStatus");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getPumpStatus");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getTankStatus/")
	public Response getTankStatus(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		JSONArray dataSet = new JSONArray();
		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					String userId = null;
					String roleId = null;
					String date = null;
					String country = null;
					String siteIDs = null;

					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					date = tsmBO.getDate();
					country = tsmBO.getCountry();
					siteIDs = tsmBO.getSiteID();

					if (siteIDs == null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);

					// System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						// dataSet = new DeviceDAO().getTankDetail(siteIDs, date);
						dataSet = new DeviceStatus().getTankStatus(siteIDs);
						responseObj.setDataSet(dataSet);
						responseObj.setError(null);
						responseObj.setErrorCode("0000");
						responseObj.setStatus("Success");
					} else {
						responseObj.setError("No Data!.");
						responseObj.setErrorCode("0001");
						responseObj.setStatus("Success");
					}
				} else {
					responseObj.setError("Wrong Token @getTankStatus");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getTankStatus");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getTankStatus");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getDeviceStatus/")
	public Response getDeviceStatus(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		JSONArray dataSet = new JSONArray();
		JSONArray nozzleDataSet = new JSONArray();
		JSONArray pumpDataSet = new JSONArray();
		JSONArray pumpDataSetDetails = new JSONArray();
		JSONArray pumpDataSalesSet = new JSONArray();
		JSONArray tankDataSet = new JSONArray();
		JSONArray deviceDataSet = new JSONArray();

		JSONArray tankAlarmDataSet = new JSONArray();
		JSONArray pumpAlarmDataSet = new JSONArray();

		JSONObject jsonObj = null;

		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					String userId = null;
					String roleId = null;
					String date = null;
					String country = null;
					String siteIDs = null;
					String fromDate = null;
					String toDate = null;

					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					date = tsmBO.getDate();
					country = tsmBO.getCountry();
					siteIDs = tsmBO.getSiteID();
					fromDate = tsmBO.getFromDate();
					toDate = tsmBO.getToDate();

					if (siteIDs == null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					// System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						String[] sites = siteIDs.split(",");
						DeviceStatus dsObj = new DeviceStatus();

						if (sites != null) {
							HashMap salesMap = null;
							for (String siteId : sites) {
								jsonObj = new JSONObject();
								jsonObj.put("SITE_ID", siteId);

								nozzleDataSet = new DeviceDAO().getNozzleDetail(siteId, date);
								jsonObj.put("NOZZLE", nozzleDataSet);

								pumpDataSetDetails = new DeviceDAO().getPumpSales(siteId, fromDate, toDate, date);
								jsonObj.put("DU_PUMP_SALES", pumpDataSetDetails);
								String duNo = null;
								String pumpNo = null;
								String product = null;
								String sales = null;
								HashMap dus = new HashMap();
								HashMap pmps = new HashMap();
								HashMap pumpSaleMap = null;
								for (int i = 0; i < pumpDataSetDetails.size(); i++) {
									JSONObject jsonData = new JSONObject();
									jsonData = pumpDataSetDetails.getJSONObject(i);

									duNo = jsonData.getString("DU_NO");
									pumpNo = jsonData.getString("PUMP_NO");
									product = jsonData.getString("PRODUCT_NAME");
									sales = jsonData.getString("SUM");
									pumpSaleMap = new HashMap();
									pumpSaleMap.put(product, sales);

									if (dus.containsKey(duNo)) {
										pmps = (HashMap) dus.get(duNo);
										if (pmps.containsKey(pumpNo)) {
											pumpSaleMap = (HashMap) pmps.get(pumpNo);
											pumpSaleMap.put(product, sales);
											pmps.put(pumpNo, pumpSaleMap);
										} else {
											pmps.put(pumpNo, pumpSaleMap);
										}
									} else {
										pmps = new HashMap();
										pmps.put(pumpNo, pumpSaleMap);
										dus.put(duNo, pmps);
									}
								}
								responseObj.setDataHash(dus);

								pumpDataSet = dsObj.getDUStatus(siteId);

								salesMap = new DeviceDAO().getDUSales(siteId, fromDate, toDate, date);
								String du = null;
								for (int i = 0; i < pumpDataSet.size(); i++) {
									JSONObject jsonData = new JSONObject();
									jsonData = pumpDataSet.getJSONObject(i);

									du = jsonData.getString("DispenserNo");
									if (salesMap.containsKey(du)) {
										jsonData.put("SALES", salesMap.get(du));
									} else {
										jsonData.put("SALES", "0");
									}
									pumpDataSalesSet.add(jsonData);
								}

								// pumpDataSet = new DeviceDAO().getPumpDetail(siteId, date);
								jsonObj.put("PUMP", pumpDataSalesSet);

								deviceDataSet = dsObj.getDUpumpStatus(siteId);
								jsonObj.put("DU_PUMP", deviceDataSet);

								tankDataSet = dsObj.getTankStatus(siteId);
								// tankDataSet = new DeviceDAO().getTankDetail(siteId, date);
								jsonObj.put("TANK", tankDataSet);

								tankAlarmDataSet = new DeviceDAO().getTankAlarm(siteIDs, date);
								jsonObj.put("TANK_ALARM", tankAlarmDataSet);

								pumpAlarmDataSet = new DeviceDAO().getPumpAlarm(siteIDs, date);
								jsonObj.put("PUMP_ALARM", pumpAlarmDataSet);

								dataSet.add(jsonObj);
							}

							responseObj.setDataSet(dataSet);
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						}
					} else {
						responseObj.setError("No Data!.");
						responseObj.setErrorCode("0001");
						responseObj.setStatus("Success");
					}
				} else {
					responseObj.setError("Wrong Token @getTankStatus");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getTankStatus");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getTankStatus");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getRODeviceStatus/")
	public Response getRODeviceStatus(@Context HttpHeaders headers, @Context UriInfo ui,
			@Context ServletContext context, String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		JSONArray dataSet = new JSONArray();

		JSONObject jsonObj = null;

		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					String userId = null;
					String roleId = null;
					String date = null;
					String country = null;
					String siteIDs = null;
					String state = null;
					String region = null;
					String district = null;
					String city = null;

					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					date = tsmBO.getDate();
					country = tsmBO.getCountry();
					siteIDs = tsmBO.getSiteID();
					state = tsmBO.getState();
					region = tsmBO.getRegion();
					district = tsmBO.getDistrict();
					city = tsmBO.getCity();

					if (siteIDs == null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					// System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						String[] sites = siteIDs.split(",");
						DeviceStatus dsObj = new DeviceStatus();

						if (sites != null) {
							int i = 0;
							String siteName = null;
							for (String siteId : sites) {
								i++;
								siteName = new SiteStatusDAO().getSiteDetail(siteId, country, state, region, district,
										city);
								if (siteName != null) {
									jsonObj = dsObj.getROStatus(siteId, siteName, i);
									if (jsonObj != null)
										dataSet.add(jsonObj);
									else {
										jsonObj = new JSONObject();
										jsonObj.put("SNO", i);
										jsonObj.put("Site_ID", siteId.replaceAll("'", ""));
										jsonObj.put("Site_Name", siteName);
										jsonObj.put("Status", "");
										jsonObj.put("Tank_Total", "");
										jsonObj.put("Tank_Online", "");
										jsonObj.put("Tank_Offline", "");
										jsonObj.put("DU_Total", "");
										jsonObj.put("DU_Online", "");
										jsonObj.put("DU_Offline", "");
										jsonObj.put("SiteName", siteName);
										jsonObj.put("LastUpdated", "");
										dataSet.add(jsonObj);
									}
								}
							}

							responseObj.setDataSet(dataSet);
							responseObj.setGRID_COLUMN(dataSet);
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						}
					} else {
						responseObj.setError("No Data!.");
						responseObj.setErrorCode("0001");
						responseObj.setStatus("Success");
					}
				} else {
					responseObj.setError("Wrong Token @getRODeviceStatus");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getRODeviceStatus");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getRODeviceStatus");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getRODUStatus/")
	public Response getRODUStatus(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		JSONArray dataSet = new JSONArray();
		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					String date = null;
					String siteIDs = null;
					String du = null;

					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					date = tsmBO.getDate();
					siteIDs = tsmBO.getSiteID();
					du = tsmBO.getDu();

					if (siteIDs != null) {
						DeviceDAO ddao = new DeviceDAO();
						dataSet = ddao.getDuPumpSales(siteIDs, date, du);

						responseObj.setDataSet(dataSet);
						responseObj.setGRID_COLUMN(dataSet);
						responseObj.setError(null);
						responseObj.setErrorCode("0000");
						responseObj.setStatus("Success");
					} else {
						responseObj.setError("No Data!.");
						responseObj.setErrorCode("0001");
						responseObj.setStatus("Success");
					}
				} else {
					responseObj.setError("Wrong Token @getRODUStatus");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getRODUStatus");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getRODUStatus");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getROFCC/")
	public Response getROFCC(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		JSONArray dataSet = new JSONArray();
		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					String country = null;
					String siteIDs = null;
					String state = null;
					String region = null;
					String district = null;
					String city = null;

					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					country = tsmBO.getCountry();
					siteIDs = tsmBO.getSiteID();
					state = tsmBO.getState();
					region = tsmBO.getRegion();
					district = tsmBO.getDistrict();
					city = tsmBO.getCity();

					dataSet = new DeviceDAO().getROFCC(siteIDs, country, state, region, district, city);
					responseObj.setDataSet(dataSet);
					responseObj.setError(null);
					responseObj.setErrorCode("0000");
					responseObj.setStatus("Success");

				} else {
					responseObj.setError("Wrong Token @getROFCC");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getROFCC");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getROFCC");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getROStatus/")
	public Response getROStatus(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		JSONArray dataSet = new JSONArray();
		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					String userId = null;
					String roleId = null;
					String country = null;
					String siteIDs = null;
					String state = null;
					String region = null;
					String district = null;
					String city = null;

					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					country = tsmBO.getCountry();
					siteIDs = tsmBO.getSiteID();
					state = tsmBO.getState();
					region = tsmBO.getRegion();
					district = tsmBO.getDistrict();
					city = tsmBO.getCity();

					if (siteIDs == null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					HashMap hash = null;
					// System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						HashMap<String, Integer> roSummary = new HashMap<String, Integer>();
						HashMap<String, Integer> tankSummary = new HashMap<String, Integer>();
						HashMap<String, Integer> duSummary = new HashMap<String, Integer>();

						hash = new SiteLiveView().getSiteStatus(roSummary, tankSummary, duSummary, siteIDs);

						// For print data
						Set<String> set = hash.keySet();
						Iterator<String> itr = set.iterator();
						String key = null;
						JSONArray dataArr = new JSONArray();
						HashMap innerMap = null;
						JSONObject obj;
						while (itr.hasNext()) {
							key = itr.next().toString();
							obj = new JSONObject();
							innerMap = (HashMap) hash.get(key);
							obj.put("Status", innerMap.get("Status"));
							obj.put("SiteID", innerMap.get("SiteID"));
							obj.put("SiteName", innerMap.get("SiteName"));

							int duOn = 0;
							int duOff = 0;
							int duCnt = 0;
							if (innerMap.containsKey("DUs")) {
								HashMap dusMap = (HashMap) innerMap.get("DUs");
								if (dusMap != null) {
									if (dusMap.containsKey("Offline")) {
										duOff = (Integer) dusMap.get("Offline");
									}
									if (dusMap.containsKey("Online")) {
										duOn = (Integer) dusMap.get("Online");
									}
								}
							}
							duCnt = duOn + duOff;
							obj.put("DUs", duCnt);
							obj.put("DUsOnline", duOn);
							obj.put("DUsOffline", duOff);

							int tnkOn = 0;
							int tnkOff = 0;
							int tnkCnt = 0;
							if (innerMap.containsKey("Tanks")) {
								HashMap tnksMap = (HashMap) innerMap.get("Tanks");
								if (tnksMap != null) {
									if (tnksMap.containsKey("Offline")) {
										tnkOff = (Integer) tnksMap.get("Offline");
									}
									if (tnksMap.containsKey("Online")) {
										tnkOn = (Integer) tnksMap.get("Online");
									}
								}
							}
							tnkCnt = tnkOn + tnkOff;
							obj.put("Tanks", tnkCnt);
							obj.put("TanksOnline", tnkOn);
							obj.put("TanksOffline", tnkOff);

							obj.put("LastTransaction", convertTime(innerMap.get("LastTransaction").toString()));
							obj.put("LastUpdated", innerMap.get("LastUpdated"));
							obj.put("LastInventory", convertTime(innerMap.get("LastInventory").toString()));
							obj.put("ToDayAlarms", innerMap.get("ToDayAlarms"));
							dataArr.add(obj);
						}
						responseObj.setDataArr(dataArr);
						responseObj.setGRID_COLUMN(dataArr);

						JSONObject roObj = new JSONObject();

						JSONObject obj1 = new JSONObject();
						obj1.accumulateAll(roSummary);
						roObj.put("RO_SUMMARY", obj1);

						JSONObject tnkObj = new JSONObject();
						JSONObject obj2 = new JSONObject();
						obj2.accumulateAll(tankSummary);
						tnkObj.put("TANK_SUMMARY", obj2);

						JSONObject duObj = new JSONObject();
						JSONObject obj3 = new JSONObject();
						obj3.accumulateAll(duSummary);
						duObj.put("DU_SUMMARY", obj3);

						dataSet.add(roObj);
						dataSet.add(tnkObj);
						dataSet.add(duObj);

						// System.out.println("************************************");
						// System.out.println(new Gson().toJson(hash));
						// System.out.println("************************************");
						responseObj.setDataHash(hash);
						responseObj.setDataSet(dataSet);
						responseObj.setError(null);
						responseObj.setErrorCode("0000");
						responseObj.setStatus("Success");
					} else {
						responseObj.setError("No Data!.");
						responseObj.setErrorCode("0001");
						responseObj.setStatus("Success");
					}
				} else {
					responseObj.setError("Wrong Token @getROStatus");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getROStatus");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getROStatus");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	private String convertTime(String timeinlong) {
		if (timeinlong != null) {
			if (timeinlong != "") {
				if (timeinlong != "null") {
					try {
						SimpleDateFormat inputParser = new SimpleDateFormat("yyyyMMddHHmmss");
						Date date_out = inputParser.parse(timeinlong);
						SimpleDateFormat formatterout = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						return formatterout.format(date_out);
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

	public static void main(String args[]) {
		System.out.println(new DeviceMSrvServices().convertTime("20220804220844"));
	}

}
