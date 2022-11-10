package com.bct.HOS.App;

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
import com.bct.HOS.App.DAO.HierarchyFT;
import com.bct.HOS.App.DAO.InventoryDAO;
import com.bct.HOS.App.DAO.SalesDAO;
import com.bct.HOS.App.utils.RequestLogger;
import com.bct.HOS.App.utils.RestUtils;
import com.google.gson.Gson;

import net.sf.json.JSONArray;

@Path("/v1")
public class InventoryMSrvServices {

	@POST
	@Consumes({ "application/json" })
	@Path("/getInvByFilter/")
	@RequestLogger
	public Response getInvByFilter(@Context HttpHeaders headers, @Context UriInfo ui,
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
					String timePeriodType = null;
					String recordLimit = null;
					String fromDate = null;
					String toDate = null;
					String country = null;
					String siteIDs  = null;
					String productName = null;
					
					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					fromDate = tsmBO.getFromDate();
					toDate =tsmBO.getToDate();
					recordLimit = tsmBO.getRecordLimit();
					timePeriodType = tsmBO.getTimePeriodType();
					country = tsmBO.getCountry();
					siteIDs = tsmBO.getSiteID();
					productName = tsmBO.getProductName();
					
					if(siteIDs==null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId,country);
					
					//System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						dataSet = new InventoryDAO().getInventoryByFilter(siteIDs, timePeriodType, recordLimit, fromDate, toDate,productName);
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
					responseObj.setError("Wrong Token @getInvByFilter");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getInvByFilter");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getInvByFilter");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	
	@POST
	@Consumes({ "application/json" })
	@Path("/getForcast/")
	@RequestLogger
	public Response getForcasting(@Context HttpHeaders headers, @Context UriInfo ui,
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
					String recordLimit = null;
					String country = null;
					String siteIDs  = null;
					boolean grouping = false;
					String state = null;
					
					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					recordLimit = tsmBO.getRecordLimit();
					country = tsmBO.getCountry();
					siteIDs = tsmBO.getSiteID();
					grouping = tsmBO.isGrouping();
					state = tsmBO.getState();
					
					if(siteIDs==null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId,country);
					//System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						dataSet = new InventoryDAO().getForcasting(siteIDs, recordLimit,grouping,state);
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
					responseObj.setError("Wrong Token @getForcasting");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getForcasting");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getForcasting");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	
	
	@POST
	@Consumes({ "application/json" })
	@Path("/getInvForChart/")
	@RequestLogger
	public Response getInvForChart(@Context HttpHeaders headers, @Context UriInfo ui,
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
					String timePeriodType = null;
					String productCode = null;
					String country = null;
					String siteIDs  = null;
					boolean grouping = false;
					
					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					fromDate = tsmBO.getFromDate();
					toDate =tsmBO.getToDate();
					productCode = tsmBO.getProductName();
					timePeriodType = tsmBO.getTimePeriodType();
					country = tsmBO.getCountry();
					siteIDs = tsmBO.getSiteID();
					grouping = tsmBO.isGrouping();
					
					if(siteIDs==null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId,country);
					//System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						if(grouping) {
							dataSet = new InventoryDAO().getInvForChartGrouping(siteIDs, userId, country, fromDate, toDate, productCode, timePeriodType);
						}else {
							dataSet = new InventoryDAO().getInvForChart(siteIDs, fromDate, toDate,productCode,timePeriodType);
						}
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
					responseObj.setError("Wrong Token @getInvForChart");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getInvForChart");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getInvForChart");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	
	@POST
	@Consumes({ "application/json" })
	@Path("/getInvDetails/")
	@RequestLogger
	public Response getInvDetails(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
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
					String timePeriodType = null;
					String recordLimit = null;
					String fromDate = null;
					String toDate = null;
					String country = null;
					String productName = null; 
					boolean paymentMode = false;
					boolean latestRecord = false;
					String siteIDs = null;

					String state = null;
					String region = null;
					String district = null;
					String subDistrict = null;
					String city = null;
					String division = null;
					String tank = null;
					
					
					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					fromDate = tsmBO.getFromDate();
					toDate = tsmBO.getToDate();
					recordLimit = tsmBO.getRecordLimit();
					timePeriodType = tsmBO.getTimePeriodType();
					paymentMode = tsmBO.isPaymentMode();
					country = tsmBO.getCountry();
					siteIDs = tsmBO.getSiteID();
					productName = tsmBO.getProductName();
					latestRecord = tsmBO.isLatestRecord();
							
					state =tsmBO.getState();
					region=tsmBO.getRegion();
					district=tsmBO.getDistrict();
					subDistrict=tsmBO.getSubDistrict();
					city=tsmBO.getCity();
					division=tsmBO.getDivision();
					tank =tsmBO.getTank();


					if(siteIDs==null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					//System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						dataSet = new InventoryDAO().getInvTransactions(siteIDs, fromDate,
								toDate, productName,country,state, region, district, subDistrict, city, division, tank,latestRecord);
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
					responseObj.setError("Wrong Token @getInvDetails");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getInvDetails");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getInvDetails");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
}
