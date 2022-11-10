package com.bct.HOS.App;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.bct.HOS.App.BO.ResponseBO;
import com.bct.HOS.App.BO.SiteStatusBO;
import com.bct.HOS.App.BO.SiteStatusRequestBO;
import com.bct.HOS.App.BO.UserBO;
import com.bct.HOS.App.BO.WidgetData;
import com.bct.HOS.App.DAO.SiteStatusDAO;
import com.bct.HOS.App.DAO.UserDAO;
import com.bct.HOS.App.utils.HOSConfig;
import com.bct.HOS.App.utils.RequestLogger;
import com.bct.HOS.App.utils.RestUtils;
import com.google.gson.Gson;

import net.sf.json.JSONObject;

@Path("/micro.srv")
public class HOSMicroServices {

	@POST
	@Consumes({ "application/json" })
	@Path("/getSiteStatus/")
	@RequestLogger
	public Response siteStatus(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		SiteStatusRequestBO requestObj = new SiteStatusRequestBO();
		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					requestObj = gson.fromJson(reqJsonString, SiteStatusRequestBO.class);


				} else {
					responseObj.setError("Wrong Token @SiteStatus");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @SiteStatus");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @SiteStatus");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

}
