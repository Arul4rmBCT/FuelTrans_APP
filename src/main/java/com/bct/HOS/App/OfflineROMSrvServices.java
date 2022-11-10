package com.bct.HOS.App;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.bct.HOS.App.BO.FuelSalesHeaderBO;
import com.bct.HOS.App.BO.OfflineROInventoryBO;
import com.bct.HOS.App.BO.ResponseBO;
import com.bct.HOS.App.DAO.OfflineRODAO;

import com.bct.HOS.App.utils.RequestLogger;
import com.bct.HOS.App.utils.RestUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.sf.json.JSONArray;

@Path("/v1")
public class OfflineROMSrvServices {

	
	@POST
	@Consumes({ "application/json" })
	@Path("/storeInventory/")
	@RequestLogger
	public Response storeBankDetails(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		OfflineROInventoryBO reqObj = null;
		JsonObject result =  null;
		
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					
					String siteId = null;
					
					reqObj = gson.fromJson(reqJsonString, OfflineROInventoryBO.class);
		
					siteId = reqObj.getSite_id();
					if (siteId != null && reqObj.getTransaction_no() == null  || reqObj.getTransaction_no().isEmpty()) {
						result = new OfflineRODAO().storeInventoryDetails(siteId, reqObj);
						if (result != null) {
							responseObj.setDataObject(result);
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						} 
						else {
							responseObj.setError("Unable to insert the inventory details.");
							responseObj.setErrorCode("0002");
							responseObj.setStatus("ERROR");
						}
						
					} else {
						responseObj.setErrorCode("0");
						responseObj.setError("Error Site Id NULL");
						responseObj.setStatus("Failed");
					}
				} else {
					responseObj.setError("Wrong Token @storeInventoryDetails");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @storeInventoryDetails");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @storeInventoryDetails" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
		
	@POST
	@Consumes({ "application/json" })
	@Path("/getInventorySummary")
	@RequestLogger
	public Response getInventorySummary(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		JSONArray result =  new JSONArray();
		
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					
					String siteId = null;
										
					JsonElement jsonElement =  new JsonParser().parse(reqJsonString);
					JsonObject jsonObject = jsonElement.getAsJsonObject();
					
					siteId = jsonObject.get("site_id").getAsString();
					if (siteId != null) {
						
						result = new OfflineRODAO().getInventorySummary(siteId);
						if (result != null) {
							responseObj.setDataSet(result);
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						} else {
							responseObj.setError("Unable to fetch the inventory details.");
							responseObj.setErrorCode("0002");
							responseObj.setStatus("ERROR");
						}
					}  else {
						responseObj.setErrorCode("0");
						responseObj.setError("Error Site Id NULL");
						responseObj.setStatus("Failed");
					}
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
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getInventorySummary" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	@POST
	@Consumes({ "application/json" })
	@Path("/getTankListByRO")
	@RequestLogger
	public Response getTankListByRO(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		JSONArray result =  new JSONArray();
		
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					
					String siteId = null;
										
					JsonElement jsonElement =  new JsonParser().parse(reqJsonString);
					JsonObject jsonObject = jsonElement.getAsJsonObject();
					
					siteId = jsonObject.get("site_id").getAsString();
					if (siteId != null) {
						result = new OfflineRODAO().getTanklistBySiteID(siteId);
						if (result != null ) {
							responseObj.setDataSet(result);
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						} else {
							responseObj.setError("Unable to fetch the TANK LIST details.");
							responseObj.setErrorCode("0002");
							responseObj.setStatus("ERROR");
						}
					}  else {
						responseObj.setErrorCode("0");
						responseObj.setError("Error Site Id NULL");
						responseObj.setStatus("Failed");
					}
				} else {
					responseObj.setError("Wrong Token @getTankListByRO");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getTankListByRO");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getTankListByRO" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getProductListByRO")
	@RequestLogger
	public Response getProductListByRO(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		JSONArray result =  new JSONArray();
		
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					
					String siteId = null;
										
					JsonElement jsonElement =  new JsonParser().parse(reqJsonString);
					JsonObject jsonObject = jsonElement.getAsJsonObject();
					
					siteId = jsonObject.get("site_id").getAsString();
					if (siteId != null) {
						result = new OfflineRODAO().getProductlistBySiteID(siteId);
						if (result != null ) {
							responseObj.setDataSet(result);
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						} else {
							responseObj.setError("Unable to fetch the Product LIST details.");
							responseObj.setErrorCode("0002");
							responseObj.setStatus("ERROR");
						}
					}  else {
						responseObj.setErrorCode("0");
						responseObj.setError("Error Site Id NULL");
						responseObj.setStatus("Failed");
					}
				} else {
					responseObj.setError("Wrong Token @getProductListByRO");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getProductListByRO");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getProductListByRO" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getNozzleListByRO")
	@RequestLogger
	public Response getNozzleListByRO(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		JSONArray result =  new JSONArray();
		
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					
					String siteId = null;
										
					JsonElement jsonElement =  new JsonParser().parse(reqJsonString);
					JsonObject jsonObject = jsonElement.getAsJsonObject();
					
					siteId = jsonObject.get("site_id").getAsString();
					int productNO = jsonObject.get("product_no").getAsInt();
					if (siteId != null && productNO != 0) {
						result = new OfflineRODAO().getNozzleListBySiteID(siteId, productNO);
						if (result != null ) {
							responseObj.setDataSet(result);
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						} else {
							responseObj.setError("Unable to fetch the Nozzle LIST details.");
							responseObj.setErrorCode("0002");
							responseObj.setStatus("ERROR");
						}
					}  else {
						responseObj.setErrorCode("0");
						responseObj.setError("Error Site Id or product no NULL ");
						responseObj.setStatus("Failed");
					}
				} else {
					responseObj.setError("Wrong Token @getNozzleListByRO");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getNozzleListByRO");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getNozzleListByRO" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	@POST
	@Consumes({ "application/json" })
	@Path("/getFuelSalesSummary")
	@RequestLogger
	public Response getFuelSalesSummary(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		JSONArray result =  new JSONArray();
		
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					
					String siteId = null;
					String transactionDate = null; 					
					JsonElement jsonElement =  new JsonParser().parse(reqJsonString);
					JsonObject jsonObject = jsonElement.getAsJsonObject();
					
					siteId = jsonObject.get("site_id").getAsString();
					if(reqJsonString.contains("transaction_date"))
						transactionDate = jsonObject.get("transaction_date").getAsString();
					
					if (siteId != null && transactionDate == null) 
						result = new OfflineRODAO().getFuelSalesSummary(siteId);
					else if (siteId != null && transactionDate != null) 
						result = new OfflineRODAO().getFuelSalesSummary(siteId, transactionDate);
					else {
						responseObj.setErrorCode("0");
						responseObj.setError("Error Site Id or transaction date is NULL");
						responseObj.setStatus("Failed");
					}
					if (result != null) {
						responseObj.setDataSet(result);
						responseObj.setError(null);
						responseObj.setErrorCode("0000");
						responseObj.setStatus("Success");
					} else {
						responseObj.setError("Unable to fetch the fuel sales details.");
						responseObj.setErrorCode("0002");
						responseObj.setStatus("ERROR");
					}
				} else {
					responseObj.setError("Wrong Token @getFuelSalesSummary");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getFuelSalesSummary");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getFuelSalesSummary" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	@POST
	@Consumes({ "application/json" })
	@Path("/storeFuelSales/")
	@RequestLogger
	public Response storeFuelSales(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		FuelSalesHeaderBO reqObj = null;
		JsonObject result =  null;
		
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					
					String siteId = null;
					
					reqObj = gson.fromJson(reqJsonString, FuelSalesHeaderBO.class);
		
					siteId = reqObj.getSite_id();
					if (siteId != null) {
						result = new OfflineRODAO().storeFuelSales(siteId, reqObj);
						if (result != null) {
							responseObj.setDataObject(result);
							responseObj.setError(null);
							responseObj.setErrorCode("0000");
							responseObj.setStatus("Success");
						} 
						else {
							responseObj.setError("Unable to insert the fuel sales details.");
							responseObj.setErrorCode("0002");
							responseObj.setStatus("ERROR");
						}
						
					} else {
						responseObj.setErrorCode("0");
						responseObj.setError("Error Site Id NULL");
						responseObj.setStatus("Failed");
					}
				} else {
					responseObj.setError("Wrong Token @storeFuelSales");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @storeFuelSales");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @storeFuelSales" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
		


}
