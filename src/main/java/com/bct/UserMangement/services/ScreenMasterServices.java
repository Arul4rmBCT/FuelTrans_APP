package com.bct.UserMangement.services;

import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.bct.UserMangement.bo.ResponseBO;
import com.bct.UserMangement.bo.ScreenMaster;
import com.bct.UserMangement.dao.ScreenMasterDAO;
import com.bct.UserMangement.utils.Errors;
import com.bct.UserMangement.utils.RequestLogger;
import com.bct.UserMangement.utils.RestUtils;
import com.bct.UserMangement.utils.UserMangementException;
import com.google.gson.Gson;

import net.sf.json.JSONArray;

@Path("/v1")
public class ScreenMasterServices {
	
	ScreenMasterDAO dao = new ScreenMasterDAO();
	Gson gson = new Gson();
	
	@POST
	@Consumes({ "application/json" })
	@Path("/screen")
	@RequestLogger
	public Response serviceOperation(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context, String reqJsonString) {
		String token = context.getInitParameter("authKey");
		String htoken = null;
		
		ResponseBO responseObj = new ResponseBO();	
		JSONArray dataSet = new JSONArray();
		try {
			System.out.println(reqJsonString);
			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);
            System.out.println("htoken"+htoken); 
            System.out.println("token"+token); 
            
		    if(htoken == null)
		    	return RestUtils.constructError(Errors.INVALID_TOKEN);
			ScreenMaster screen= gson.fromJson(reqJsonString,ScreenMaster.class);
			if(screen == null)
				return RestUtils.constructError(Errors.INVALID_INPUT);
			System.out.println("falg"+screen.getFlag());
			System.out.println("user"+screen.getScreen_id());
			
			delegateOperations(screen,dataSet,responseObj);
			
			String str = gson.toJson(responseObj);
			return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
			
		} catch (UserMangementException e) {
			e.printStackTrace();
			return RestUtils.constructError(e.getError());
		}
		
	}
	
	private void delegateOperations(ScreenMaster screen, JSONArray dataSet, ResponseBO responseObj) throws UserMangementException {
		if(screen.getFlag().equalsIgnoreCase("c")) {
			createScreen(screen,dataSet,responseObj);
		} else if(screen.getFlag().equalsIgnoreCase("r")) {
			getScreens(screen.getScreen_id(),dataSet,responseObj);
		} else if(screen.getFlag().equalsIgnoreCase("u")) {
			dao.updateScreen(screen);
			responseObj.setErrorCode("1");
			responseObj.setError("");
			responseObj.setStatus("Success");
		}else if(screen.getFlag().equalsIgnoreCase("d")) {
			dao.deleteScreen(screen.getScreen_id());
			responseObj.setErrorCode("1");
			responseObj.setError("");
			responseObj.setStatus("Success");
		}
	}
	
	private void getScreens(String screen,JSONArray dataSet, ResponseBO responseObj) throws UserMangementException{
		
		if(screen !=null && !screen.isEmpty()) {
			System.out.println("if screen"+screen);
			ScreenMaster response = dao.getScreen(screen);
			if(response == null)
				throw new UserMangementException(Errors.SCREEN_NOT_FOUND);
			dataSet.add(gson.toJson(response));
			responseObj.setDataArr(dataSet);
		}else {
			System.out.println("else screen"+screen);
			List<ScreenMaster> screens = dao.getScreenList();
			if(screens.isEmpty())
				throw new UserMangementException(Errors.SCREEN_NOT_FOUND);
			dataSet.add(gson.toJson(screens));
			responseObj.setDataArr(dataSet);
     	}
		responseObj.setErrorCode("0000");
		responseObj.setError("");
		responseObj.setStatus("Success");
	}
	
	private void createScreen(ScreenMaster screen,JSONArray dataSet, ResponseBO responseObj) throws UserMangementException {
		String res = dao.createScreen(screen);
		if(res.isEmpty()) {
			dataSet.add(gson.toJson(res));
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