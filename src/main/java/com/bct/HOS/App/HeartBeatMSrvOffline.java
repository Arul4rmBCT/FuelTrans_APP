package com.bct.HOS.App;

import javax.annotation.security.PermitAll;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.bct.HOS.App.BO.ResponseBO;
import com.bct.HOS.App.utils.RequestLogger;
import com.bct.HOS.App.utils.RestUtils;
import com.google.gson.Gson;

@Path("/v1")
public class HeartBeatMSrvOffline {

	/*
	 * User Management
	 */
	@PermitAll
	@POST
	@RequestLogger
	@Consumes({ "application/json" })
	@Path("/checkAlive/")
	public Response UserAuth(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		Gson gson = new Gson();

		ResponseBO responseObj = new ResponseBO();
		try {
			responseObj.setStatus("Success");
			responseObj.setErrorCode("0");
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setErrorCode("1");
			responseObj.setStatus("Failure");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
		
}
