package com.bct.HOS.App;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.bct.HOS.App.BO.NCBO;
import com.bct.HOS.App.BO.ResponseBO;
import com.bct.HOS.App.BO.TSMBO;
import com.bct.HOS.App.DAO.DeviceDAO;
import com.bct.HOS.App.DAO.HierarchyFT;
import com.bct.HOS.App.DAO.NCDAO;
import com.bct.HOS.App.utils.RequestLogger;
import com.bct.HOS.App.utils.RestUtils;
import com.google.gson.Gson;

import net.sf.json.JSONArray;

@Path("/v1")
public class NCMSrvServices {

	@POST
	@Consumes({ "application/json" })
	@Path("/getNCList/")
	@RequestLogger
	public Response getNCList(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
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

					NCBO ncBO = gson.fromJson(reqJsonString, NCBO.class);
					userId = ncBO.getUserId();
					roleId = ncBO.getRoleId();
					country = ncBO.getCountry();
					siteIDs = ncBO.getSiteID();

					if (siteIDs == null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);

					//System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						dataSet = new NCDAO().getNotificationAlerts(ncBO.getNcName(), ncBO.getStatus(),
								ncBO.getNotificationType());
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
					responseObj.setError("Wrong Token @getNCList");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getNCList");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (

		Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getNCList");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/setNCList/")
	@RequestLogger
	public Response setNCList(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
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

					NCBO ncBO = gson.fromJson(reqJsonString, NCBO.class);
					userId = ncBO.getUserId();
					roleId = ncBO.getRoleId();
					country = ncBO.getCountry();
					siteIDs = ncBO.getSiteID();

					if (siteIDs == null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);

					//System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {

						boolean result = new NCDAO().setNotificationAlerts(ncBO);
						if (result) {
							responseObj.setDataSet(dataSet);
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						} else {
							responseObj.setError("DB Error!.");
							responseObj.setErrorCode("0002");
							responseObj.setStatus("Failed");
						}
					} else {
						responseObj.setError("No Data!.");
						responseObj.setErrorCode("0001");
						responseObj.setStatus("Success");
					}
				} else {
					responseObj.setError("Wrong Token @setNCList");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @setNCList");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @setNCList");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

}
