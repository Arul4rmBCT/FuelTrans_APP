package com.bct.HOS.App;

import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang.StringUtils;

import com.bct.HOS.App.BO.ResponseBO;
import com.bct.HOS.App.BO.TSMBO;
import com.bct.HOS.App.BO.UserBO;
import com.bct.HOS.App.DAO.HierarchyFT;
import com.bct.HOS.App.DAO.InventoryDAO;
import com.bct.HOS.App.DAO.SalesDAO;
import com.bct.HOS.App.DAO.SiteStatusDAO;
import com.bct.HOS.App.DAO.UtilDAO;
import com.bct.HOS.App.utils.RequestLogger;
import com.bct.HOS.App.utils.RestUtils;
import com.bct.HOS.LVM.DeviceStatus;
import com.google.gson.Gson;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Path("/v1")
public class TSMMSrvServices {

	@POST
	@Consumes({ "application/json" })
	@Path("/roStatusSummary/")
	@RequestLogger
	public Response getROStatusSummary(@Context HttpHeaders headers, @Context UriInfo ui,
			@Context ServletContext context, String reqJsonString) {
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
					String status = null;
					String country = null;
					JSONObject jobj = new JSONObject();
					JSONArray dataSetSummary = null;
					JSONArray groupingSummary = new JSONArray();
					JSONArray dataSets = null;
					String siteIDs = null;
					String state = null;
					String region = null;
					String district = null;
					String city = null;
					String siteName = null;
					boolean grouping = false; 
					String groupData = null;


					UserBO usrBO = gson.fromJson(reqJsonString, UserBO.class);
					System.out.println(reqJsonString);
					
					userId = usrBO.getUserId();
					roleId = usrBO.getRoleId();
					status = usrBO.getStatus();
					country = usrBO.getCountry();
					siteIDs = usrBO.getSiteID();
					state = usrBO.getState();
					region = usrBO.getRegion();
					district = usrBO.getDistrict();
					city = usrBO.getCity();
					siteName = usrBO.getSiteName();
					grouping=usrBO.isGrouping();
					groupData = usrBO.getGroupData();
					
					if(siteIDs==null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					//System.out.println("getROStatusSummary siteIDs:" + siteIDs);
					
					if (siteIDs != null) {
						
						//dataSetSummary = new SiteStatusDAO().getSiteStatusSummary(siteIDs, status);
						dataSetSummary = new DeviceStatus().getSiteStatus(siteIDs, country, status);
						//System.out.println("getROStatusSummary status is == "+status);
						if(status!=null) {
							if(dataSetSummary!=null) {
								String siteStatusTmp = null;
								if (dataSetSummary.size() > 0) {									
									for(int i = 0 ; i<dataSetSummary.size();i++) {
										JSONObject objects = dataSetSummary.getJSONObject(i);
										siteStatusTmp = (String) objects.get("SITE_STATUS");
										System.out.println("siteStatusTmp>>"+siteStatusTmp);
										StringBuffer siteBF= new StringBuffer();
										if(siteStatusTmp.equalsIgnoreCase(status)) {
											siteIDs = (String) objects.get("SITES");
											siteIDs=siteIDs.replaceAll("''", "','");
										}
									}
									//System.out.println("siteIDs----"+siteIDs);
									//System.out.println("grouping-----------"+grouping);
									if(groupData!=null) {
										if(groupData.equalsIgnoreCase("YES")) {
											JSONArray ctlist = new UtilDAO().getUserCountryList(userId);
											String cnt = null;
											JSONObject cntSet = null;
											JSONArray sites = null;
											for(int i = 0 ; i<ctlist.size();i++) {
												JSONObject objects = ctlist.getJSONObject(i);
												cnt = (String) objects.get("COUNTRY");
												cntSet = new JSONObject();
												cntSet.put("country", cnt);
												sites = new SiteStatusDAO().getSiteStatus(siteIDs, null, siteName, cnt, state, region, district, city);
												fillSiteStatusAndLastUpdatedTime(sites);
												cntSet.put("count",sites.size());
												cntSet.put("sites", sites);
												groupingSummary.add(cntSet);
											}
										}else {							
											dataSets = fillSiteStatus(new SiteStatusDAO().getSiteStatus(siteIDs, null, siteName, country, state, region, district, city));
										}
									}
								}
							}
						}else {
							dataSets = fillSiteStatus(new SiteStatusDAO().getSiteStatus(siteIDs, status, siteName, country, state, region, district, city));							
						}
								
						jobj.put("SUMMARY", dataSetSummary);
						jobj.put("SUMMARY_GROUPED",groupingSummary);
						jobj.put("DETAILS", dataSets);

						dataSet.add(jobj);
						responseObj.setDataSet(dataSet);
						responseObj.setGRID_COLUMN(dataSets);
						responseObj.setError(null);
						responseObj.setErrorCode("0000");
						responseObj.setStatus("Success");
					} else {
						responseObj.setError("No Data!.");
						responseObj.setErrorCode("0001");
						responseObj.setStatus("Success");
					}
				} else {
					responseObj.setError("Wrong Token @getROStatusSummary");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getROStatusSummary");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getROStatusSummary");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}
		System.out.println("getROStatusSummary str == "+str);
		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	
	private JSONArray fillSiteStatus(JSONArray inputSet){		
		JSONArray dataSet = new JSONArray();
		try {
			HashMap siteStatus= new DeviceStatus().getSiteStatus();
			String siteId = null;
			String status = null;
			for(int i = 0 ; i<inputSet.size();i++) {
				JSONObject objects = inputSet.getJSONObject(i);
				siteId = (String) objects.get("SITES");
				
				if(siteStatus.containsKey(siteId)) {
					status = (String) siteStatus.get(siteId);
					objects.put("SITE_STATUS", status);
				}else {
					objects.put("SITE_STATUS", "OFFLINE");
				}
				String siteName = CaptializeFirstCharacterOfWord((String)objects.get("SITE_NAME"));
				String dealer = CaptializeFirstCharacterOfWord((String)objects.get("DEALER_NAME"));
				objects.put("SITE_NAME", siteName);
				objects.put("DEALER_NAME", dealer);
				dataSet.add(objects);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataSet;
	}

	private void fillSiteStatusAndLastUpdatedTime(JSONArray inputSet){		
		try {
			HashMap siteStatus= new DeviceStatus().getSiteStatus();
			String siteId = null;
			String status = null;
			//JSONObject object = inputSet.getJSONObject(0);
			JSONArray sites = inputSet;
			//System.out.println("siteIDs---->>>"+object);
			for(int i = 0 ; i<sites.size();i++) {
				JSONObject objects = sites.getJSONObject(i);
				siteId = (String) objects.get("SITE_ID");
				
				if(siteStatus.containsKey(siteId)) {
					HashMap map =  (HashMap)siteStatus.get(siteId);
					objects.remove("LAST_CONNECTION_TIME");
					sites.getJSONObject(i).put("LAST_CONNECTION_TIME", map.get("LastUpdated"));
					objects.remove("SITE_STATUS");
					sites.getJSONObject(i).put("SITE_STATUS", map.get("Status"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
	@POST
	@Consumes({ "application/json" })
	@Path("/roStatus/")
	@RequestLogger
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
					String status = null;
					String siteName = null;
					String country = null;
					String siteIDs = null;

					UserBO usrBO = gson.fromJson(reqJsonString, UserBO.class);
					userId = usrBO.getUserId();
					roleId = usrBO.getRoleId();
					status = usrBO.getStatus();
					siteName = usrBO.getSiteName();
					country = usrBO.getCountry();
					siteIDs = usrBO.getSiteID();

					if(siteIDs==null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					//System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						dataSet = new SiteStatusDAO().getSiteStatus(siteIDs, status, siteName);
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

	@POST
	@Consumes({ "application/json" })
	@Path("/roSalesVolume/")
	@RequestLogger
	public Response getSalesVolume(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
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
					String fromDate = null;
					String toDate = null;
					String productName = null;
					String country = null;
					String siteIDs = null;
					boolean grouping = false;
					
					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					fromDate = tsmBO.getFromDate();
					toDate = tsmBO.getToDate();
					productName = tsmBO.getProductName();
					country = tsmBO.getCountry();
					siteIDs = tsmBO.getSiteID();
					grouping = tsmBO.isGrouping();
					
					if(siteIDs==null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					//System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						int siteReceivedCount = 0;
						dataSet = new SiteStatusDAO().getSalesVolume(siteIDs, fromDate, toDate, productName,grouping,userId);
						siteReceivedCount = new SiteStatusDAO().getSalesVolume(siteIDs, fromDate, toDate,userId);
						responseObj.setDataSet(dataSet);
						HashMap hm = new HashMap();
						//int siteCount = siteIDs.split(",").length;
						int siteCount = new InventoryDAO().getROStatus(userId,siteIDs,country);
						hm.put("TOTAL_SITES",siteCount);
						hm.put("ACTIVE_SITES",siteReceivedCount);
						responseObj.setDataHash(hm);						
						responseObj.setError(null);
						responseObj.setErrorCode("0000");
						responseObj.setStatus("Success");
					} else {
						responseObj.setError("No Data!.");
						responseObj.setErrorCode("0001");
						responseObj.setStatus("Success");
					}
				} else {
					responseObj.setError("Wrong Token @getSalesVolume");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getSalesVolume");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getSalesVolume");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/inventorySummary/")
	@RequestLogger
	public Response getInventorySummary(@Context HttpHeaders headers, @Context UriInfo ui,
			@Context ServletContext context, String reqJsonString) {
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
					String fromDate = null;
					String toDate = null;
					boolean grouping = false;
					
					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					country = tsmBO.getCountry();
					siteIDs = tsmBO.getSiteID();
					fromDate = tsmBO.getFromDate();
					toDate = tsmBO.getToDate();
					grouping = tsmBO.isGrouping();
					
					//if(siteIDs==null)
					//siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					//System.out.println("siteIDs:" + siteIDs);
					//if (siteIDs != null) {
						int siteReceivedCount = 0;
						dataSet = new InventoryDAO().getInventorySummary(userId,siteIDs,fromDate,toDate,country,grouping);
						siteReceivedCount = new InventoryDAO().getInventoryActive(userId,siteIDs,fromDate,toDate,country);
						responseObj.setDataSet(dataSet);
						HashMap hm = new HashMap();
						//int siteCount = siteIDs.split(",").length;
						int siteCount = new InventoryDAO().getROStatus(userId,siteIDs,country);
						hm.put("TOTAL_SITES",siteCount);
						hm.put("ACTIVE_SITES",siteReceivedCount);
						responseObj.setDataHash(hm);				
						responseObj.setError(null);
						responseObj.setErrorCode("0000");
						responseObj.setStatus("Success");
						//} else {
						//responseObj.setError("No Data!.");
						//responseObj.setErrorCode("0001");
						//responseObj.setStatus("Success");
						//}
				} else {
					responseObj.setError("Wrong Token @getInventorySummary");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getInventorySummary");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getInventorySummary");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/inventoryDetails/")
	@RequestLogger
	public Response getInventoryDetails(@Context HttpHeaders headers, @Context UriInfo ui,
			@Context ServletContext context, String reqJsonString) {
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

					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					country = tsmBO.getCountry();
					siteIDs = tsmBO.getSiteID();

					if(siteIDs==null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					//System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						dataSet = new InventoryDAO().getInventoryDetails(siteIDs);
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
					responseObj.setError("Wrong Token @getInventoryDetails");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getInventoryDetails");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getInventoryDetails");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getNilSales/")
	@RequestLogger
	public Response getNilSales(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
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
					String fromDate = null;
					String toDate = null;
					String productName = null;
					String state = null;
					String region = null;
					String district = null;
					String city = null;
					boolean grouping = false;
					
					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					country = tsmBO.getCountry();
					siteIDs = tsmBO.getSiteID();
					fromDate = tsmBO.getFromDate();
					toDate = tsmBO.getToDate();
					productName = tsmBO.getProductName();
					state = tsmBO.getState();
					region = tsmBO.getRegion();
					district = tsmBO.getDistrict();
					city = tsmBO.getCity();
					grouping = tsmBO.isGrouping();
					
					if(siteIDs==null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					//System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						dataSet = new SiteStatusDAO().getNilSales(siteIDs, fromDate, toDate, productName, country, state, region, district, city,grouping);
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
					responseObj.setError("Wrong Token @getNilSales");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getNilSales");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getInventorySummary");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getTopSales/")
	@RequestLogger
	public Response getTopSales(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
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
					String fromDate = null;
					String toDate = null;
					String count = null;
					String country = null;
					String siteIDs = null;

					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					fromDate = tsmBO.getFromDate();
					toDate = tsmBO.getToDate();
					count = tsmBO.getCount();
					country = tsmBO.getCountry();
					siteIDs = tsmBO.getSiteID();

					if(siteIDs==null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					//System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						dataSet = new SalesDAO().getTOPSalesSites(userId,null,country, fromDate, toDate, count);
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
					responseObj.setError("Wrong Token @getTopSales");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getTopSales");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getTopSales");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	
	@POST
	@Consumes({ "application/json" })
	@Path("/getSalesPercentage/")
	@RequestLogger
	public Response getSalesPercentage(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
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
					String fromDate = null;
					String toDate = null;
					String country = null;
					String siteIDs = null;
					boolean grouping  = false;
					
					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					fromDate = tsmBO.getFromDate();
					toDate = tsmBO.getToDate();
					country = tsmBO.getCountry();
					siteIDs = tsmBO.getSiteID();
					grouping = tsmBO.isGrouping();
					
					if(siteIDs==null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					//System.out.println("siteIDs @ getSalesPercentage=================:" + siteIDs);
					if (siteIDs != null) {
						dataSet = new SalesDAO().getSalesPercentage(userId,siteIDs, fromDate, toDate,country,grouping);
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
					responseObj.setError("Wrong Token @getSalesPercentage");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getSalesPercentage");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getSalesPercentage");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	
	@POST
	@Consumes({ "application/json" })
	@Path("/getCountrySummary/")
	@RequestLogger
	public Response getCountrySummary(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
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
					String fromDate = null;
					String toDate = null;
					String country = null;
					String siteIDs = null;

					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					fromDate = tsmBO.getFromDate();
					toDate = tsmBO.getToDate();
					country = tsmBO.getCountry();
					siteIDs = tsmBO.getSiteID();

					if(siteIDs==null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					//System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						dataSet = new SalesDAO().getCountrySummary(siteIDs, fromDate, toDate);
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
					responseObj.setError("Wrong Token @getCountrySummary");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getCountrySummary");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getCountrySummary");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getSalesAndInventory/")
	@RequestLogger
	public Response getSalesAndInventory(@Context HttpHeaders headers, @Context UriInfo ui,
			@Context ServletContext context, String reqJsonString) {
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
					String fromDate = null;
					String toDate = null;
					String productName = null;
					String country = null;
					String period = null;
					String siteIDs = null;

					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					fromDate = tsmBO.getFromDate();
					toDate = tsmBO.getToDate();
					productName = tsmBO.getProductName();
					period = tsmBO.getTimePeriodType();
					country = tsmBO.getCountry();
					siteIDs = tsmBO.getSiteID();

					if(siteIDs==null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					//System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						InventoryDAO inv = new InventoryDAO();
						JSONArray salesDataSet = inv.getSalesData(siteIDs, period, fromDate, toDate);
						JSONArray invDataSet = inv.getInventoryData(siteIDs, period, fromDate, toDate);
						JSONObject jObj = new JSONObject();
						jObj.put("SALES_DATA", salesDataSet);
						jObj.put("INVENTORY_DATA", invDataSet);
						dataSet.add(jObj);

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
					responseObj.setError("Wrong Token @getSalesAndInventory");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getSalesAndInventory");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getSalesAndInventory");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	
	private String CaptializeFirstCharacterOfWord(String value) {
		String result = "";
		String[] split = value.split(" ");
		for(String sp : split){
			if(!StringUtils.isNumeric(sp))
				sp = sp.substring(0, 1).toUpperCase()+ sp.substring(1).toLowerCase();
			result = result + sp + " ";
		}
		System.out.println(result);
		return result.trim();
	}
}
