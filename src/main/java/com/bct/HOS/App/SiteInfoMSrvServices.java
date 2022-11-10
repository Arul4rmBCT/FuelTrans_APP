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
import com.bct.HOS.App.BO.SiteInfoSalesBO;
import com.bct.HOS.App.BO.SiteInfoSitesBO;
import com.bct.HOS.App.BO.UserBO;
import com.bct.HOS.App.DAO.HierarchyFT;
import com.bct.HOS.App.DAO.SiteInfoDAO;
import com.bct.HOS.App.DAO.SiteStatusDAO;
import com.bct.HOS.App.utils.RequestLogger;
import com.bct.HOS.App.utils.RestUtils;
import com.google.gson.Gson;

import net.sf.json.JSONArray;

@Path("/v1")
public class SiteInfoMSrvServices {

	@POST
	@Consumes({ "application/json" })
	@Path("/siteStatus/")
	@RequestLogger
	public Response siteStatus(@Context HttpHeaders headers, @Context UriInfo ui,
			@Context ServletContext context, String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		JSONArray dataSet = new JSONArray();
		SiteInfoSitesBO sitesObj = null;
		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					//System.out.println("sitesObj (reqJsonString) = "+reqJsonString);
					sitesObj = gson.fromJson(reqJsonString, SiteInfoSitesBO.class);
					new SiteInfoDAO().processSites(sitesObj.getDataset());
					
					responseObj.setDataSet(dataSet);
					responseObj.setError(null);
					responseObj.setErrorCode("0000");
					responseObj.setStatus("Success");

				} else {
					responseObj.setError("Wrong Token @siteStatus");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @siteStatus");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @siteStatus");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	
	@POST
	@Consumes({ "application/json" })
	@Path("/siteSales/")
	@RequestLogger
	public Response siteSales(@Context HttpHeaders headers, @Context UriInfo ui,
			@Context ServletContext context, String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		JSONArray dataSet = new JSONArray();
		SiteInfoSalesBO sitesObj = null;
		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					//System.out.println("sitesObj Sales (reqJsonString) = "+reqJsonString);
					sitesObj = gson.fromJson(reqJsonString, SiteInfoSalesBO.class);
					new SiteInfoDAO().processSales(sitesObj.getTransactions());
					
					responseObj.setDataSet(dataSet);
					responseObj.setError(null);
					responseObj.setErrorCode("0000");
					responseObj.setStatus("Success");

				} else {
					responseObj.setError("Wrong Token @siteSales");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @siteSales");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @siteSales");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
}
