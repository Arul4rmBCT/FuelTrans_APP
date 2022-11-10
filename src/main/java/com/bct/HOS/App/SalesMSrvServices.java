package com.bct.HOS.App;

import java.util.HashMap;

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
import com.bct.HOS.App.DAO.SalesDAO;
import com.bct.HOS.App.utils.RequestLogger;
import com.bct.HOS.App.utils.RestUtils;
import com.google.gson.Gson;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Path("/v1")
public class SalesMSrvServices {

	@POST
	@Consumes({ "application/json" })
	@Path("/getSalesByFilter/")
	@RequestLogger
	public Response getSalesByFilter(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
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
					String siteIDs = null;

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

					if (siteIDs == null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					//System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						dataSet = new SalesDAO().getSalesByFilter(siteIDs, timePeriodType, recordLimit, fromDate,
								toDate, paymentMode, productName);
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
					responseObj.setError("Wrong Token @getSalesByFilter");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getSalesByFilter");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getSalesByFilter");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getSalesDetails/")
	@RequestLogger
	public Response getSalesDetails(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
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
					System.out.println("token1->"+token);
					String userId = null;
					String roleId = null;
					String fromDate = null;
					String toDate = null;
					String country = null;
					String productName = null;
					String siteIDs = null;

					String state = null;
					String region = null;
					String district = null;
					String subDistrict = null;
					String city = null;
					String division = null;
					String pump = null;
					String tank = null;
					String nozzle = null;
					String du = null;
					String mode = null;
					
					String fromTime = null;
					String toTime = null;
					System.out.println("token2->"+token);
					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					fromDate = tsmBO.getFromDate();
					toDate = tsmBO.getToDate();
					fromTime = tsmBO.getFromTime();
					toTime = tsmBO.getToTime();
					country = tsmBO.getCountry();
					siteIDs = tsmBO.getSiteID();
					productName = tsmBO.getProductName();
					state = tsmBO.getState();
					region = tsmBO.getRegion();
					district = tsmBO.getDistrict();
					subDistrict = tsmBO.getSubDistrict();
					city = tsmBO.getCity();
					division = tsmBO.getDivision();
					pump = tsmBO.getPump();
					tank = tsmBO.getTank();
					nozzle = tsmBO.getNozzle(); 
					du = tsmBO.getDu();
					mode = tsmBO.getMode();

					if (siteIDs == null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					//System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						dataSet = new SalesDAO().getSalesTransactions(siteIDs, fromDate, toDate, fromTime,
								toTime, productName, country,
								state, region, district, subDistrict, city, division, pump, tank, nozzle, du, mode);
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
					responseObj.setError("Wrong Token @getSalesDetails");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getSalesDetails");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getSalesDetails");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getPaymentMode/")
	@RequestLogger
	public Response getPaymentMode(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
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
					String fromDate = null;
					String toDate = null;
					String country = null;
					String siteIDs = null;
					boolean grouping = false;

					//System.out.println("reqJsonString>>>" + reqJsonString);

					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					fromDate = tsmBO.getFromDate();
					toDate = tsmBO.getToDate();
					country = tsmBO.getCountry();
					siteIDs = tsmBO.getSiteID();

					if (siteIDs == null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					//System.out.println("siteIDs:" + siteIDs);
					//System.out.println("isGrouping" + grouping);
					if (siteIDs != null) {
						dataSet = new SalesDAO().getPaymentMode(siteIDs, fromDate, toDate, grouping,userId);
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
					responseObj.setError("Wrong Token @getPaymentMode");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getPaymentMode");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getPaymentMode");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getPaymentModeGroup/")
	@RequestLogger
	public Response getPaymentModeGroup(@Context HttpHeaders headers, @Context UriInfo ui,
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
					String siteIDs = null;
					boolean grouping = true;

					//System.out.println("reqJsonString>>>" + reqJsonString);

					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					fromDate = tsmBO.getFromDate();
					toDate = tsmBO.getToDate();
					country = tsmBO.getCountry();
					siteIDs = tsmBO.getSiteID();

					if (siteIDs == null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					//System.out.println("siteIDs:" + siteIDs);
					//System.out.println("isGrouping" + grouping);
					if (siteIDs != null) {
						dataSet = new SalesDAO().getPaymentMode(siteIDs, fromDate, toDate, grouping,userId);
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
					responseObj.setError("Wrong Token @getPaymentModeGroup");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getPaymentModeGroup");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getPaymentModeGroup");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getSalesForChart/")
	@RequestLogger
	public Response getSalesForChart(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
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
					String fromDate = null;
					String toDate = null;
					String timePeriodType = null;
					String productCode = null;
					String country = null;
					String siteIDs = null;
					boolean grouping  = false;
					
					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					fromDate = tsmBO.getFromDate();
					toDate = tsmBO.getToDate();
					productCode = tsmBO.getProductName();
					timePeriodType = tsmBO.getTimePeriodType();
					country = tsmBO.getCountry();
					siteIDs = tsmBO.getSiteID();
					grouping = tsmBO.isGrouping();
					//System.out.println("siteIDs:" + siteIDs);
					//if (siteIDs == null)
						//siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					//System.out.println("siteIDs for country ("+country+") :" + siteIDs);
					//if (siteIDs != null) {
						if(grouping) {
							dataSet = new SalesDAO().getSalesForChartGrouping(userId,siteIDs,country, fromDate, toDate, productCode,
									timePeriodType,roleId);
						}else {
							dataSet = new SalesDAO().getSalesForChart(userId,siteIDs,country, fromDate, toDate, productCode,
									timePeriodType);
						}
						responseObj.setDataSet(dataSet);
						responseObj.setError(null);
						responseObj.setErrorCode("0000");
						responseObj.setStatus("Success");
						//} else {
						//responseObj.setError("No Data!.");
						//responseObj.setErrorCode("0001");
						//responseObj.setStatus("Success");
						//}
				} else {
					responseObj.setError("Wrong Token @getSalesForChart");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getSalesForChart");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getSalesForChart");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getLastFewSalesChart/")
	@RequestLogger
	public Response getLastFewSalesChart(@Context HttpHeaders headers, @Context UriInfo ui,
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
					String dayDiff = null;
					String country = null;
					String siteIDs = null;
					String productName = null;

					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					dayDiff = tsmBO.getDayDiff();
					country = tsmBO.getCountry();
					siteIDs = tsmBO.getSiteID();
					productName = tsmBO.getProductName();

					if (siteIDs == null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					//System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						dataSet = new SalesDAO().getLastFewSalesChart(siteIDs, dayDiff, productName);
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
					responseObj.setError("Wrong Token @getLastFewSalesChart");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getLastFewSalesChart");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getLastFewSalesChart");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getSiteStockDetails/")
	@RequestLogger
	public Response getSiteStockDetails(@Context HttpHeaders headers, @Context UriInfo ui,
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
					String siteIDs = null;

					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					siteIDs = tsmBO.getSiteID();

					if (siteIDs != null) {
						dataSet = new SalesDAO().getSiteStockDetails(siteIDs);
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
					responseObj.setError("Wrong Token @getSiteStockDetails");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getSiteStockDetails");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getSiteStockDetails");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getDeviceLevelSalesDetails/")
	@RequestLogger
	public Response getDeviceLevelSalesDetails(@Context HttpHeaders headers, @Context UriInfo ui,
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
					String siteIDs = null;
					String fromDate = null;
					String toDate = null;
					String productName = null;

					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					siteIDs = tsmBO.getSiteID();
					fromDate = tsmBO.getFromDate();
					toDate = tsmBO.getToDate();
					productName = tsmBO.getProductName();

					if (siteIDs != null) {
						dataSet = new SalesDAO().getDeviceLevelSalesDetails(siteIDs, fromDate, toDate, productName);
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
					responseObj.setError("Wrong Token @getDeviceLevelSalesDetails");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getDeviceLevelSalesDetails");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getDeviceLevelSalesDetails");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getStockLGDetails/")
	@RequestLogger
	public Response getStockLGDetails(@Context HttpHeaders headers, @Context UriInfo ui,
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
					String siteIDs = null;

					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					siteIDs = tsmBO.getSiteID();

					if (siteIDs != null) {
						dataSet = new SalesDAO().getSiteStockDetails(siteIDs);

						JSONObject obj = null;

						JSONObject objLG = null;
						JSONArray datasetNew = new JSONArray();

						JSONArray objOS = null;
						JSONArray objCS = null;
						JSONArray objPS = null;
						JSONArray objSS = null;
						int osValue = 0;
						int csValue = 0;
						int psValue = 0;
						int ssValue = 0;
						double lg = 0;
						HashMap<String, HashMap<String, Integer>> dataHash = new HashMap<String, HashMap<String, Integer>>();
						HashMap<String, Integer> innerHash;
						for (int i = 0; i < dataSet.size(); i++) {
							innerHash = new HashMap<String, Integer>();
							obj = (JSONObject) dataSet.get(i);
							objOS = (JSONArray) obj.get("OPENING_STOCK");
							objCS = (JSONArray) obj.get("CLOSING_STOCK");
							objPS = (JSONArray) obj.get("PRIMARY_RECEIPT");
							objSS = (JSONArray) obj.get("SECONDARY_SALES");

							osValue = getJSONValue(objOS);
							csValue = getJSONValue(objCS);
							psValue = getJSONValue(objPS);
							ssValue = getJSONValue(objSS);

							innerHash.put("OPENING_STOCK", osValue);
							innerHash.put("CLOSING_STOCK", csValue);
							innerHash.put("PRIMARY_RECEIPT", psValue);
							innerHash.put("SECONDARY_SALES", ssValue);

							objLG = new JSONObject();

							lg = ((ssValue + csValue) - (osValue + psValue));
							objLG.put("PRODUCT_NAME", obj.get("PRODUCT_NAME"));
							objLG.put("PRODUCT_NO", obj.get("PRODUCT_NO"));
							objLG.put("UNIT", obj.get("UNIT"));
							objLG.put("LOSS_GAIN", lg);
							datasetNew.add(objLG);

							dataHash.put(obj.get("PRODUCT_NAME").toString(), innerHash);
						}

						responseObj.setDataSet(datasetNew);
						responseObj.setDataHash(dataHash);
						responseObj.setError(null);
						responseObj.setErrorCode("0000");
						responseObj.setStatus("Success");
					} else {
						responseObj.setError("No Data!.");
						responseObj.setErrorCode("0001");
						responseObj.setStatus("Success");
					}
				} else {
					responseObj.setError("Wrong Token @getStockLGDetails");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getStockLGDetails");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getStockLGDetails");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getSitesStockReport/")
	@RequestLogger
	public Response getSitesStockReport(@Context HttpHeaders headers, @Context UriInfo ui,
			@Context ServletContext context, String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		JSONArray dataSet = new JSONArray();
		JSONArray innerDataSet = null;
		HashMap<String, HashMap<String, Object>> datasetHash = new HashMap<String, HashMap<String, Object>>();
		ResponseBO responseObj = new ResponseBO();
		try {
			String userId = null;
			String roleId = null;
			String country = null;
			String date = null;
			String fromDate = null;
			String toDate = null;
			int indexKey = 0;

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					String[] siteIDs = null;
					String siteID = null;

					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					country = tsmBO.getCountry();
					date = tsmBO.getDate();
					fromDate = tsmBO.getFromDate();
					toDate = tsmBO.getToDate();
					siteID = tsmBO.getSiteID();
					
					if(siteID==null)
						siteID = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					
					if (siteID != null) {
						dataSet = new SalesDAO().getSitesStockDetails(siteID,date,fromDate,toDate);

						responseObj.setDataSet(dataSet);
						responseObj.setDataHash(null);
						responseObj.setError(null);
						responseObj.setErrorCode("0000");
						responseObj.setStatus("Success");
					} else {
						responseObj.setError("No Data!.");
						responseObj.setErrorCode("0001");
						responseObj.setStatus("Success");
					}
				} else {
					responseObj.setError("Wrong Token @getSitesStockReport");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getSitesStockReport");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getSitesStockReport");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	private int getJSONValue(JSONArray arr) {
		int val = 0;
		JSONObject obj = null;
		if (arr != null) {
			if (arr.size() > 0) {
				for (int index = 0; index < arr.size(); index++) {
					obj = (JSONObject) arr.get(index);
					val = obj.getInt("VOLUME");
				}
			}
		}

		return val;
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getToDaySales/")
	@RequestLogger
	public Response getToDaySales(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
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
					String fromDate = null;
					String toDate = null;
					String country = null;
					String siteIDs = null;

					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					fromDate = tsmBO.getFromDate();
					toDate = tsmBO.getToDate();
					country = tsmBO.getCountry();
					siteIDs = tsmBO.getSiteID();

					if (siteIDs == null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					//System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						dataSet = new SalesDAO().getToDaySales(siteIDs, fromDate, toDate);
						String totalValue = new SalesDAO().getToDaySalesTotal(siteIDs, fromDate, toDate);
						HashMap hash = new HashMap();
						hash.put("TOTAL", totalValue);

						responseObj.setDataHash(hash);
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
					responseObj.setError("Wrong Token @getToDaySales");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getToDaySales");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getToDaySales");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getAverageFill/")
	@RequestLogger
	public Response getAverageFill(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		JSONArray avgDataSet = new JSONArray();
		JSONArray ttlDataSet = new JSONArray();
		JSONArray prdttlDataSet = new JSONArray();
		HashMap hash = new HashMap();
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
					String siteIDs = null;
					String productName = null;
					boolean grouping = false;

					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					fromDate = tsmBO.getFromDate();
					toDate = tsmBO.getToDate();
					country = tsmBO.getCountry();
					siteIDs = tsmBO.getSiteID();
					productName = tsmBO.getProductName();
					grouping = tsmBO.isGrouping();

					if (siteIDs == null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					//System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						avgDataSet = new SalesDAO().getAverageFill(userId,siteIDs, fromDate, toDate,country,grouping);
						ttlDataSet = new SalesDAO().getTotalFills(userId,siteIDs, fromDate, toDate,country);
						prdttlDataSet = new SalesDAO().getTotalFillsProducts(userId, siteIDs, fromDate, toDate, country, productName, grouping);
						hash.put("AVERAGE_FILL", avgDataSet);
						hash.put("TOTAL_FILL", ttlDataSet);
						hash.put("PRODUCT_TOTAL_FILL", prdttlDataSet);
						responseObj.setDataHash(hash);
						responseObj.setDataSet(null);
						responseObj.setError(null);
						responseObj.setErrorCode("0000");
						responseObj.setStatus("Success");
					} else {
						responseObj.setError("No Data!.");
						responseObj.setErrorCode("0001");
						responseObj.setStatus("Success");
					}
				} else {
					responseObj.setError("Wrong Token @getAverageFill");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getAverageFill");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getAverageFill");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	
	
	@POST
	@Consumes({ "application/json" })
	@Path("/getSalesByTagID/")
	@RequestLogger
	public Response getSalesByTagID(@Context HttpHeaders headers, @Context UriInfo ui,
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
					String siteID = null;
					String tagId = null;
					String attendeeName = null;
					String productName = null;
					boolean grouping = false;
					
					TSMBO tsmBO = gson.fromJson(reqJsonString, TSMBO.class);
					userId = tsmBO.getUserId();
					roleId = tsmBO.getRoleId();
					fromDate = tsmBO.getFromDate();
					toDate = tsmBO.getToDate();
					country = tsmBO.getCountry();
					siteID = tsmBO.getSiteID();
					tagId = tsmBO.getTagId();
					attendeeName = tsmBO.getAttendeeName();
					grouping = tsmBO.isGrouping();
					productName = tsmBO.getProductName();
					
					if (userId != null) {
						dataSet = new SalesDAO().getSalesByTagID(userId,siteID,fromDate, toDate,country,tagId,attendeeName,grouping,productName);
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
					responseObj.setError("Wrong Token @getSalesByTagID");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getSalesByTagID");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getSalesByTagID");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

}
