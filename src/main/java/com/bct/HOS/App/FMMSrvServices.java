package com.bct.HOS.App;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.postgresql.util.LruCache.CreateAction;

import com.bct.HOS.App.utils.RequestLogger;
import com.bct.HOS.App.utils.RestUtils;
import com.bct.HOS.Fleet.BPMSIn_Msg;
import com.bct.HOS.Fleet.BPMSResponse;
import com.bct.HOS.Fleet.CardDetails;
import com.bct.HOS.Fleet.FMLogic;
import com.bct.HOS.Fleet.OfflineDataReqBO;
import com.bct.HOS.Fleet.OfflineDataResBO;
import com.bct.HOS.Fleet.PosOfflineTransReq;
import com.bct.HOS.Fleet.PosOfflineTransRes;
import com.bct.HOS.Fleet.PosTransReqBO;
import com.bct.HOS.Fleet.PosTransResBO;
import com.bct.HOS.Fleet.PreTransReqBO;
import com.bct.HOS.Fleet.PreTransResBO;
import com.bct.HOS.Fleet.RuleParams;
import com.bct.HOS.Fleet.Rules;
import com.bct.HOS.Fleet.SitesBO;
import com.google.gson.Gson;

import net.sf.json.JSONObject;

@Path("/v1")
public class FMMSrvServices {

	@POST
	@Consumes({ "application/json" })
	@Path("/preTrans/")
	@RequestLogger
	public Response preTransaction(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		Gson gson = new Gson();
		PreTransResBO responseObj = new PreTransResBO();
		PreTransReqBO reqObj = null;
		String pattern = "yyyy-MM-dd hh:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = null;
		try {
			//System.out.println("reqJsonString>>>>>>>>" + reqJsonString);
			reqObj = gson.fromJson(reqJsonString, PreTransReqBO.class);
			FMLogic logic = new FMLogic();
			responseObj = logic.preTrans(reqObj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (reqObj != null)
				responseObj.setPre_Tranx_ID(reqObj.getPre_Tranx_ID());
			date = simpleDateFormat.format(new Date());
			responseObj.setRes_Time(date);
			responseObj.setRes_Code("0");
			responseObj.setRes_Message("Failed");
		}
		//System.out.println(":::::::::" + str);
		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/posTrans/")
	@RequestLogger
	public Response postTransaction(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		Gson gson = new Gson();
		PosTransResBO responseObj = new PosTransResBO();
		PosTransReqBO reqObj = null;
		String pattern = "yyyy-MM-dd hh:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = null;
		try {
			//System.out.println("reqJsonString>>>>>>>>" + reqJsonString);
			reqObj = gson.fromJson(reqJsonString, PosTransReqBO.class);

			FMLogic logic = new FMLogic();
			responseObj = logic.postTrans(reqObj);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (reqObj != null)
				responseObj.setPre_Tranx_ID(reqObj.getPre_Tranx_ID());
			date = simpleDateFormat.format(new Date());
			responseObj.setRes_Time(date);
			responseObj.setRes_Code("0");
			responseObj.setRes_Message("Failed");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/posOfflineTrans/")
	@RequestLogger
	public Response postOfflineTransaction(@Context HttpHeaders headers, @Context UriInfo ui,
			@Context ServletContext context, String reqJsonString) {
		String str = null;
		Gson gson = new Gson();
		ArrayList<BPMSIn_Msg> list = null;
		ArrayList<PosTransResBO> listRes = null;
		PosOfflineTransReq reqObj = null;
		PosOfflineTransRes resObj = null;
		String pattern = "yyyy-MM-dd hh:mm:ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = null;
		try {
			date = simpleDateFormat.format(new Date());
			list = gson.fromJson(reqJsonString, ArrayList.class);
			
			//System.out.println(list.size());
			FMLogic logic = new FMLogic();
			listRes = logic.postOfflineTrans(list);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		str = gson.toJson(listRes);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

	@POST
	@Consumes({ "application/json" })
	@Path("/offlineDataSet/")
	@RequestLogger
	public Response offlineDataSet(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		Gson gson = new Gson();
		OfflineDataReqBO reqObj = null;
		//OfflineDataResBO resObj = null;
		JSONObject resObj = null;
		try {
			reqObj = gson.fromJson(reqJsonString, OfflineDataReqBO.class);
			//System.out.println(reqObj.getSites().size());
			str = new FMLogic().offLineDataSet(reqObj);
			//gson.fromJson(strResObj, BPMSResponse.class);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//str = gson.toJson(resObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}

}
