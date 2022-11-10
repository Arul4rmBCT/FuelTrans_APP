package com.bct.UserMangement.services;

import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.bct.UserMangement.utils.RestUtils;
import com.bct.UserMangement.utils.UserMangementException;
import com.bct.UserMangement.bo.ResponseBO;
import com.bct.UserMangement.bo.Site;
import com.bct.UserMangement.dao.SiteDAO;
import com.bct.UserMangement.utils.Errors;
import com.bct.UserMangement.utils.RequestLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.sf.json.JSONArray;

@Path("/v1")
public class SiteServices {
	
	SiteDAO roleDAO = new SiteDAO();
	Gson gson = new Gson();
	
	@POST
	@Consumes({ "application/json" })
	@Path("/addsite")
	@RequestLogger
	public Response userOperation(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context, String reqJsonString) {
		String token = context.getInitParameter("authKey");
		String htoken = null;
		
		ResponseBO responseObj = new ResponseBO();	
		JSONArray dataSet = new JSONArray();
		try {
			System.out.println(reqJsonString);
			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);
                        
		    if(htoken == null)
		    	return RestUtils.constructError(Errors.INVALID_TOKEN);
			
			Site site= gson.fromJson(reqJsonString,Site.class);
			if(site == null)
				return RestUtils.constructError(Errors.INVALID_INPUT);
			
			delegateOperations(site,dataSet,responseObj);
			
			String str = gson.toJson(responseObj);
			return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
			
		} catch (UserMangementException e) {
			e.printStackTrace();
			return RestUtils.constructError(e.getError());
		}
		
	}
	
	private void delegateOperations(Site site, JSONArray dataSet, ResponseBO responseObj) throws UserMangementException {
		if(site.getFlag().equalsIgnoreCase("c")) {
			createSites(site,dataSet,responseObj);
		}
	}
	
	
	private void createSites(Site site,JSONArray dataSet, ResponseBO responseObj) throws UserMangementException {
		String res = roleDAO.createRole(site);
		if(res.isEmpty()) {
			dataSet.add(gson.toJson(site));
			responseObj.setDataArr(dataSet);
			responseObj.setErrorCode("0000");
			responseObj.setError("");
			responseObj.setStatus("Success");
		}else {
			if(res.contains("violates unique constraint")) { 
				throw new UserMangementException(Errors.ROLE_ORG_UNIQUE);
			} else {
				throw new UserMangementException(Errors.INVALID,res);	
			}
		}	
		
	}
	
}