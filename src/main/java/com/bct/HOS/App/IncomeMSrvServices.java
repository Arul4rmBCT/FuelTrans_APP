package com.bct.HOS.App;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.bct.HOS.App.BO.IncomeHeaderBO;
import com.bct.HOS.App.BO.ResponseBO;
import com.bct.HOS.App.DAO.IncomeDAO;
import com.bct.HOS.App.utils.RequestLogger;
import com.bct.HOS.App.utils.RestUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Path("/v1")
public class IncomeMSrvServices {

	@POST
	@Consumes({ "application/json" })
	@Path("/storeIncomeDetails/")
	@RequestLogger
	public Response storeIncomeDetails(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		IncomeHeaderBO reqObj = null;
		JsonObject result =  new JsonObject();
		
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					
					String siteId = null;
					
					reqObj = gson.fromJson(reqJsonString, IncomeHeaderBO.class);
		
					siteId = reqObj.getRo_id();
					//System.out.println("SITE_ID"+siteId+"Transaction" + reqObj.getTransaction_no());
					if (siteId != null) {
						if(reqObj.getTransaction_no() == null || reqObj.getTransaction_no().isEmpty()) 
							result = new IncomeDAO().storeIncomeDetails(siteId, reqObj);
						else 
							result = new IncomeDAO().updateIncomeDetails(reqObj);
							if (result !=null) {
								responseObj.setDataObject(result);
								responseObj.setError(null);
								responseObj.setErrorCode("0000");
								responseObj.setStatus("Success");
							} else {
								responseObj.setError("Unable to insert the Income details.");
								responseObj.setErrorCode("0002");
								responseObj.setStatus("ERROR");
							}
												
					} else {
						responseObj.setErrorCode("0");
						responseObj.setError("Error Site Id NULL");
						responseObj.setStatus("Failed");
					}
				} else {
					responseObj.setError("Wrong Token @storeIncomeDetails");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @storeIncomeDetails");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @storeIncomeDetails" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	@POST
	@Consumes({ "application/json" })
	@Path("/updateIncomeDetails/")
	@RequestLogger
	public Response updateIncomeDetailsStatus(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		IncomeHeaderBO reqObj = null;
		JsonObject result = null;
		
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					
					String siteId = null;
					
					reqObj = gson.fromJson(reqJsonString, IncomeHeaderBO.class);
		
					siteId = reqObj.getRo_id();
					if (siteId != null) {
						
						result = new IncomeDAO().updateIncomeDetails(reqObj);
						if (result != null) {
							responseObj.setDataObject(result);
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						} else {
							responseObj.setError("Unable to insert the Income details.");
							responseObj.setErrorCode("0002");
							responseObj.setStatus("ERROR");
						}
					} else {
						responseObj.setErrorCode("0");
						responseObj.setError("Error Site Id NULL");
						responseObj.setStatus("Failed");
					}
				} else {
					responseObj.setError("Wrong Token @updateIncomeDetailsStatus");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @updateIncomeDetailsStatus");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @updateIncomeDetailsStatus" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/deleteIncomeDetails/")
	@RequestLogger
	public Response deleteIncomeDetails(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		IncomeHeaderBO reqObj = null;
		boolean result = false;
		
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					
					String siteId = null;
					
					reqObj = gson.fromJson(reqJsonString, IncomeHeaderBO.class);
		
					siteId = reqObj.getRo_id();
					if (siteId != null) {
						
						result = new IncomeDAO().deleteIncomeDetailsByRO(siteId);
						if (result) {
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						} else {
							responseObj.setError("Unable to delete the Income details.");
							responseObj.setErrorCode("0002");
							responseObj.setStatus("ERROR");
						}
					} else {
						responseObj.setErrorCode("0");
						responseObj.setError("Error Site Id NULL");
						responseObj.setStatus("Failed");
					}
				} else {
					responseObj.setError("Wrong Token @deleteIncomeDetails");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @deleteIncomeDetails");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @deleteIncomeDetails" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	@POST
	@Consumes({ "application/json" })
	@Path("/getIncomeDetails/")
	@RequestLogger
	public Response getIncomeDetails(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		IncomeHeaderBO reqObj = null;
		JSONArray result =  new JSONArray();
		
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					
					String siteId = null;
					String fromDate = null;
					String mode = null;
					reqObj = gson.fromJson(reqJsonString, IncomeHeaderBO.class);
		
					siteId = reqObj.getRo_id();
					fromDate = reqObj.getFromDate();
					mode = reqObj.getMode();
							
					if (fromDate != null) {
						
						result = new IncomeDAO().getIncomeDetailsByFromdate(siteId, fromDate);
						if (result.size() > 0) {
							responseObj.setDataSet(result);
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						} else {
							responseObj.setError("Unable to fetch the Income details.");
							responseObj.setErrorCode("0002");
							responseObj.setStatus("ERROR");
						}
					}  else  if ("ByRO".equalsIgnoreCase(mode)) {
						
						result = new IncomeDAO().getIncomeDetailsByRO(siteId);
						if (result.size() > 0) {
							responseObj.setDataSet(result);
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						} else {
							responseObj.setError("Unable to fetch the Income details.");
							responseObj.setErrorCode("0002");
							responseObj.setStatus("ERROR");
						}
					}  else if("ByTransNo".equalsIgnoreCase(mode)) {
						JSONObject obj = new IncomeDAO().getIncomeDetailsByTransactionNo(siteId,reqObj.getTransaction_no());
						if (obj !=null) {
							responseObj.setDataObj(obj);
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						} else {
							responseObj.setError("Unable to fetch the income details.");
							responseObj.setErrorCode("0002");
							responseObj.setStatus("ERROR");
						}
						
					}else {
						responseObj.setErrorCode("0");
						responseObj.setError("Error mode is not matching");
						responseObj.setStatus("Failed");
					}
				} else {
					responseObj.setError("Wrong Token @getIncomeDetails");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getIncomeDetails");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getIncomeDetails" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
}
