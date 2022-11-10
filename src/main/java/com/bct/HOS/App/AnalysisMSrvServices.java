package com.bct.HOS.App;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.bct.HOS.App.BO.AnalysisReqBO;
import com.bct.HOS.App.BO.ResponseBO;
import com.bct.HOS.App.DAO.AnalysisDAO;
import com.bct.HOS.App.DAO.HierarchyFT;
import com.bct.HOS.App.DAO.PurchaseDAO;
import com.bct.HOS.App.DAO.SalesSummaryDAO;
import com.bct.HOS.App.DAO.TrialBalanceDAO;
import com.bct.HOS.App.utils.RequestLogger;
import com.bct.HOS.App.utils.RestUtils;
import com.google.gson.Gson;

import net.sf.json.JSONArray;

@Path("/v1")
public class AnalysisMSrvServices {

	@POST
	@Consumes({ "application/json" })
	@Path("/getSalesVolumeComparision/")
	@RequestLogger
	public Response salesVolumeComparision(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
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
					int currentYear = 0;
					int previousYear = 0;
					int currentMonth = 0;
					int previousMonth = 0;
					int currentQuarter = 0;
					int previousQuarter = 0;
					boolean productFilter = false;
					boolean regionFilter = false;

					AnalysisReqBO analysisBO = gson.fromJson(reqJsonString, AnalysisReqBO.class);
					userId = analysisBO.getUserId();
					roleId = analysisBO.getRoleId();
					country = analysisBO.getCountry();
					siteIDs = analysisBO.getSiteID();
					currentYear = analysisBO.getCurrentYear();
					previousYear = analysisBO.getPreviousYear();
					currentQuarter=analysisBO.getCurrentQuarter();
					previousQuarter=analysisBO.getPreviousQuarter();
					currentMonth=analysisBO.getCurrentMonth();
					previousMonth=analysisBO.getPreviousMonth();
					productFilter = analysisBO.isProductFilter();
					regionFilter=analysisBO.isRegionFilter();

					if(siteIDs==null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					////System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						dataSet = new AnalysisDAO().salesVolumeComparision(siteIDs, country, currentYear, previousYear, currentMonth, previousMonth, currentQuarter, previousQuarter, productFilter, regionFilter);								
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
					responseObj.setError("Wrong Token @salesVolumeComparision");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @salesVolumeComparision");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @salesVolumeComparision");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	
	@POST
	@Consumes({ "application/json" })
	@Path("/getSiteLevelSalesComparision/")
	@RequestLogger
	public Response getSiteLevelSalesComparision(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
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
					int currentYear = 0;
					int previousYear = 0;
					int currentMonth = 0;
					int previousMonth = 0;
					int currentQuarter = 0;
					int previousQuarter = 0;
					boolean productFilter = false;
					boolean regionFilter = false;

					AnalysisReqBO analysisBO = gson.fromJson(reqJsonString, AnalysisReqBO.class);
					userId = analysisBO.getUserId();
					roleId = analysisBO.getRoleId();
					country = analysisBO.getCountry();
					siteIDs = analysisBO.getSiteID();
					if(siteIDs!=null)
						siteIDs="'"+siteIDs+"'";
					currentYear = analysisBO.getCurrentYear();
					previousYear = analysisBO.getPreviousYear();
					currentQuarter=analysisBO.getCurrentQuarter();
					previousQuarter=analysisBO.getPreviousQuarter();
					currentMonth=analysisBO.getCurrentMonth();
					previousMonth=analysisBO.getPreviousMonth();
					productFilter = analysisBO.isProductFilter();
					regionFilter=analysisBO.isRegionFilter();

					if(siteIDs==null)
						siteIDs = new HierarchyFT().getUserHierarchyFT(userId, roleId, country);
					////System.out.println("siteIDs:" + siteIDs);
					if (siteIDs != null) {
						dataSet = new AnalysisDAO().getSiteLevelSalesComparision(siteIDs, country, currentYear, previousYear, currentMonth, previousMonth, currentQuarter, previousQuarter, productFilter, regionFilter);								
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
					responseObj.setError("Wrong Token @getSiteLevelSalesComparision");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @getSiteLevelSalesComparision");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getSiteLevelSalesComparision");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	
	@POST
	@Consumes({ "application/json" })
	@Path("/getSalesReport1/")
	@RequestLogger
	public Response getSalesReport1(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		HashMap dataHash = new HashMap();
		try {

			AnalysisReqBO analysisBO = gson.fromJson(reqJsonString, AnalysisReqBO.class);
			String startDate = analysisBO.getSt_date();
			String endDate = analysisBO.getEnd_date();
			String stationName = analysisBO.getSt_name();
			String type = analysisBO.getType();
			dataHash = new AnalysisDAO().salesReport1(startDate, endDate, stationName, type);								
			responseObj.setDataHash(dataHash);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getSalesReport1");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/getSalesSummaryReport/")
	@RequestLogger
	public Response getSalesSummaryReport(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		HashMap dataHash = new HashMap();
		try {

			AnalysisReqBO analysisBO = gson.fromJson(reqJsonString, AnalysisReqBO.class);
			String startDate = analysisBO.getSt_date();
			String endDate = analysisBO.getEnd_date();
			//String stationName = analysisBO.getSt_name();
			dataHash = new SalesSummaryDAO().salesSummaryReport(startDate, endDate);								
			responseObj.setDataHash(dataHash);
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getSalesSummaryReport");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	
	@POST
	@Consumes({ "application/json" })
	@Path("/getPurchaseReport/")
	@RequestLogger
	public Response getPurchaseReport(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		HashMap dataHash = new HashMap();
		try {

			AnalysisReqBO analysisBO = gson.fromJson(reqJsonString, AnalysisReqBO.class);
			String startDate = analysisBO.getSt_date();
			String endDate = analysisBO.getEnd_date();
			String stationName = analysisBO.getSt_name();
			dataHash = new PurchaseDAO().purchaseReport(startDate, endDate, stationName);								
			responseObj.setDataHash(dataHash);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getPurchaseREpor");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	@POST
	@Consumes({ "application/json" })
	@Path("/getTrialBalReport/")
	@RequestLogger
	public Response getTrialBalReport(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		try {

			AnalysisReqBO analysisBO = gson.fromJson(reqJsonString, AnalysisReqBO.class);
			String startDate = analysisBO.getSt_date();
			String endDate = analysisBO.getEnd_date();
			String stationName = analysisBO.getSt_name();
			str = new TrialBalanceDAO().trialBalanceRep(startDate, endDate, stationName);								
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getTrialBalReport");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	@POST
	@Path("/allSalesSummaryToNavision/")
	@Consumes({ "application/json"})
	@Produces({"application/xml"})
	@RequestLogger
	public Response allSalesSummaryReportToNavision(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		HashMap dataHash = new HashMap();
		String dateStr = null;
		try {
			AnalysisReqBO analysisBO = null;
			if(reqJsonString != null) {
				analysisBO = gson.fromJson(reqJsonString, AnalysisReqBO.class);
				dateStr = analysisBO.getSt_date();
				if(dateStr == null) {
					dateStr = getPreviousDt();
				}
			} else {
				dateStr = getPreviousDt();
			}
			str = new SalesSummaryDAO().salesSummaryNavAPI(dateStr);								
			responseObj.setDataHash(dataHash);
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @salesSummaryNavAPI");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}
		//str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/xml");
	}
	
	public String getPreviousDt() {
		
		   DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		   Date today = new Date();
	       Calendar calendar = Calendar.getInstance();
	       calendar.setTime(today);
	       calendar.add(Calendar.DAY_OF_YEAR, -1);
	       Date previousDate = calendar.getTime();
	       String result = dateFormat.format(previousDate);
	       System.out.println(result);
	       return result;
		}
}
