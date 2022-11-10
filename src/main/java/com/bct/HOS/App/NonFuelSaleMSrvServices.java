package com.bct.HOS.App;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.bct.HOS.App.BO.NFSalesHeaderBO;
import com.bct.HOS.App.BO.ResponseBO;
import com.bct.HOS.App.DAO.NonFuelProductDAO;
import com.bct.HOS.App.DAO.NonFuelSalesDAO;
import com.bct.HOS.App.utils.RequestLogger;
import com.bct.HOS.App.utils.RestUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Path("/v1")
public class NonFuelSaleMSrvServices {

	@POST
	@Consumes({ "application/json" })
	@Path("/storeNFSalesDetails/")
	@RequestLogger
	public Response storeNFSalesDetails(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		NFSalesHeaderBO reqObj = null;
		JsonObject result =  new JsonObject();
		
		try {
			String siteId = null;
			
			reqObj = gson.fromJson(reqJsonString, NFSalesHeaderBO.class);

			siteId = reqObj.getRo_id();
			if (siteId != null) {
				if(reqObj.getTransaction_no() == null || reqObj.getTransaction_no().isEmpty()) 
					result = new NonFuelSalesDAO().storeNonFuelSales(siteId, reqObj);
				else 
					result = new NonFuelSalesDAO().updateNonFuelSales(reqObj);
					if (result !=null) {
						responseObj.setDataObject(result);
						responseObj.setError(null);
						responseObj.setErrorCode("0000");
						responseObj.setStatus("Success");
					} else {
						responseObj.setError("Unable to insert the NFSales details.");
						responseObj.setErrorCode("0002");
						responseObj.setStatus("ERROR");
					}
										
			} else {
				responseObj.setErrorCode("0");
				responseObj.setError("Error Site Id NULL");
				responseObj.setStatus("Failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @storeNFSalesDetails" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	@POST
	@Consumes({ "application/json" })
	@Path("/updateNFSalesDetails/")
	@RequestLogger
	public Response updateNFSalesDetails(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		NFSalesHeaderBO reqObj = null;
		JsonObject result = null;
		try {

			String siteId = null;
			
			reqObj = gson.fromJson(reqJsonString, NFSalesHeaderBO.class);

			siteId = reqObj.getRo_id();
			if (siteId != null) {
				
				result = new NonFuelSalesDAO().updateNonFuelSales(reqObj);
				if (result != null) {
					responseObj.setDataObject(result);
					responseObj.setError(null);
					responseObj.setErrorCode("0000");
					responseObj.setStatus("Success");
				} else {
					responseObj.setError("Unable to insert the Non fuel details.");
					responseObj.setErrorCode("0002");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setErrorCode("0");
				responseObj.setError("Error Site Id NULL");
				responseObj.setStatus("Failed");
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @updateNFSalesDetails" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/deleteNFSalesDetails/")
	@RequestLogger
	public Response deleteNFSalesDetails(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		NFSalesHeaderBO reqObj = null;
		boolean result = false;
		
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					
					String siteId = null;
					
					reqObj = gson.fromJson(reqJsonString, NFSalesHeaderBO.class);
		
					siteId = reqObj.getRo_id();
					if (siteId != null) {
						
						result = new NonFuelSalesDAO().deleteNonFuelSalesByRO(siteId);
						if (result) {
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						} else {
							responseObj.setError("Unable to delete the Non Fuel details.");
							responseObj.setErrorCode("0002");
							responseObj.setStatus("ERROR");
						}
					} else {
						responseObj.setErrorCode("0");
						responseObj.setError("Error Site Id NULL");
						responseObj.setStatus("Failed");
					}
				} else {
					responseObj.setError("Wrong Token @deleteNFSalesDetails");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @deleteNFSalesDetails");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @deleteNFSalesDetails" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	@POST
	@Consumes({ "application/json" })
	@Path("/getNFSalesSummary/")
	@RequestLogger
	public Response getNFSalesSummary(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		NFSalesHeaderBO reqObj = null;
		JSONArray result =  new JSONArray();
		
		try {
				String siteId = null;
				reqObj = gson.fromJson(reqJsonString, NFSalesHeaderBO.class);
				siteId = reqObj.getRo_id();
					
				 if (siteId != null) {
					result = new NonFuelSalesDAO().getNFSalesByRO(siteId);
						if (result.size() > 0) {
							responseObj.setDataSet(result);
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						} else {
							responseObj.setError("Unable to fetch Non Fuel Sales.");
							responseObj.setErrorCode("0002");
							responseObj.setStatus("ERROR");
						}
					}  
			
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @Non Fuel Sales" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	@POST
	@Consumes({ "application/json" })
	@Path("/getNFSalesDetails/")
	@RequestLogger
	public Response getNFSalesDetails(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		NFSalesHeaderBO reqObj = null;
		JSONObject result =  null;
		
		try {

			String siteId = null;
			String transNo = null;
			
			reqObj = gson.fromJson(reqJsonString, NFSalesHeaderBO.class);

			siteId = reqObj.getRo_id();
			transNo = reqObj.getTransaction_no();
			
			if (siteId != null && transNo != null) {
				
				result = new NonFuelSalesDAO().getNFDetailsByTransNo(siteId,transNo);
				if (result.size() > 0) {
					responseObj.setDataObj(result);
					responseObj.setError(null);
					responseObj.setErrorCode("0000");
					responseObj.setStatus("Success");
				} else {
					responseObj.setError("Unable to fetch Non Fuel Sales.");
					responseObj.setErrorCode("0002");
					responseObj.setStatus("ERROR");
				}
			}  
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @Non Fuel Sales" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	@POST
	@Consumes({ "application/json" })
	@Path("/getNFProductList/")
	@RequestLogger
	public Response getNFProductList(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		JSONArray result =  new JSONArray();
		
		try {
				result = new NonFuelProductDAO().getNonFuelProductList();
				if (result.size() > 0) {
					responseObj.setDataSet(result);
					responseObj.setError(null);
					responseObj.setErrorCode("0000");
					responseObj.setStatus("Success");
				} else {
					responseObj.setError("Unable to fetch the NF product details.");
					responseObj.setErrorCode("0002");
					responseObj.setStatus("ERROR");
				}
		
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @ NF product" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
}
