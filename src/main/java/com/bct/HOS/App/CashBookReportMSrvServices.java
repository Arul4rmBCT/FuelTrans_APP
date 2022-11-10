package com.bct.HOS.App;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.bct.HOS.App.BO.CashBookReportReqBO;
import com.bct.HOS.App.BO.ResponseBO;
import com.bct.HOS.App.DAO.CashBookReportDAO;
import com.bct.HOS.App.utils.RequestLogger;
import com.bct.HOS.App.utils.RestUtils;
import com.google.gson.Gson;

import net.sf.json.JSONArray;

@Path("/v1")
public class CashBookReportMSrvServices {

	@POST
	@Consumes({ "application/json" })
	@Path("/getCashBookReport/")
	@RequestLogger
	public Response getCashBookReport(@Context HttpHeaders headers, @Context UriInfo ui,
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
					String st_date = null;
					String end_date = null;
					String st_name = null;
					
					CashBookReportReqBO mannualRptBO = gson.fromJson(reqJsonString, CashBookReportReqBO.class);
					st_date = mannualRptBO.getSt_date(); 
					end_date = mannualRptBO.getEnd_date();
					st_name =  mannualRptBO.getSt_name();

						dataSet = new CashBookReportDAO().cashBookReport(
								st_date,end_date,st_name);		
						
						responseObj.setDataSet(dataSet);
						responseObj.setError(null);
						responseObj.setErrorCode("0000");
						responseObj.setStatus("Success");

				} else {
					responseObj.setError("Wrong Token @getCashBookReport");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getCashBookReport");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getCashBookReport");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		
		System.out.println("@getCashBookReport Response ->" +str);
		
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
}
