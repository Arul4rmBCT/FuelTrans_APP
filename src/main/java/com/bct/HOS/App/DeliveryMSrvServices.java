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
import com.bct.HOS.App.DAO.DeliveryDAO;
import com.bct.HOS.App.DAO.HierarchyFT;
import com.bct.HOS.App.DAO.SalesDAO;
import com.bct.HOS.App.utils.RequestLogger;
import com.bct.HOS.App.utils.RestUtils;
import com.google.gson.Gson;

import net.sf.json.JSONArray;

@Path("/v1")
public class DeliveryMSrvServices {

	
	@POST
	@Consumes({ "application/json" })
	@Path("/getDeliveryDetails/")
	@RequestLogger
	public Response getDeliveryByFilter(@Context HttpHeaders headers, @Context UriInfo ui,
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
					String country = null;
					String siteIDs  = null;
					String state = null;
					String region = null;
					String district = null;
					String city = null;
					
					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					fromDate = tsmBO.getFromDate();
					toDate =tsmBO.getToDate();
					country = tsmBO.getCountry();
					siteIDs = tsmBO.getSiteID();
					state = tsmBO.getState();
					region = tsmBO.getRegion();
					district = tsmBO.getDistrict();
					city = tsmBO.getCity();
					
					if(siteIDs==null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId,country);
					
					//System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						dataSet = new DeliveryDAO().getDeliveryByFilter(siteIDs, fromDate, toDate, country, state, region, district, city);
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
					responseObj.setError("Wrong Token @getDeliveryByFilter");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getDeliveryByFilter");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getDeliveryByFilter");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	
	@POST
	@Consumes({ "application/json" })
	@Path("/getIrregularTotalizer/")
	@RequestLogger
	public Response getIrregularTotalizer(@Context HttpHeaders headers, @Context UriInfo ui,
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
					String country = null;
					String siteIDs  = null;
					String state = null;
					String region = null;
					String district = null;
					String city = null;
					

					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					fromDate = tsmBO.getFromDate();
					toDate =tsmBO.getToDate();
					country = tsmBO.getCountry();
					siteIDs = tsmBO.getSiteID();
					state = tsmBO.getState();
					region = tsmBO.getRegion();
					district = tsmBO.getDistrict();
					city = tsmBO.getCity();
					
					if(siteIDs==null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId,country);
					
					//System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						dataSet = new DeliveryDAO().getIrrTotalizer(siteIDs, fromDate, toDate,country, state, region, district, city);
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
					responseObj.setError("Wrong Token @getIrregularTotalizer");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getIrregularTotalizer");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getIrregularTotalizer");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
}
