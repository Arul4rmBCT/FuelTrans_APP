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

import com.bct.HOS.App.BO.TSMBO;
import com.bct.HOS.App.DAO.HierarchyFT;
import com.bct.HOS.App.DAO.UtilDAO;
import com.bct.UserMangement.bo.ResponseBO;
import com.bct.UserMangement.bo.UserSiteMapping;
import com.bct.UserMangement.dao.UserSiteMappingDAO;
import com.bct.UserMangement.utils.Errors;
import com.bct.UserMangement.utils.RequestLogger;
import com.bct.UserMangement.utils.RestUtils;
import com.bct.UserMangement.utils.UserMangementException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Path("/v1")
public class UserSiteMappingServices {
	
	UserSiteMappingDAO dao = new UserSiteMappingDAO();
	Gson gson = new Gson();
	
	@POST
	@Consumes({ "application/json" })
	@Path("/usersitemapping")
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
//            System.out.println("htoken"+htoken); 
//            System.out.println("token"+token); 
            
		    if(htoken == null)
		    	return RestUtils.constructError(Errors.INVALID_TOKEN);
		    JsonElement jsonElement =  new JsonParser().parse(reqJsonString);
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			
			String flag = jsonObject.get("flag").getAsString();
			if(flag.equalsIgnoreCase("c")) {
				JsonArray rolesArray = jsonObject.get("users").getAsJsonArray();
				UserSiteMapping[] users= gson.fromJson(rolesArray,UserSiteMapping[].class);
				//System.out.println(roles[0].getComponent_id()+roles[0].getRole_id());
				createUserMapping(users, dataSet, responseObj);
				
			} else if (flag.equalsIgnoreCase("d")) {
				JsonArray rolesArray = jsonObject.get("users").getAsJsonArray();
				UserSiteMapping[] users= gson.fromJson(rolesArray,UserSiteMapping[].class);
				dao.deleteUserSiteMapping(users);
				responseObj.setErrorCode("000");
				responseObj.setError("");
				responseObj.setStatus("Success");
			}else {
			
				UserSiteMapping user= gson.fromJson(reqJsonString,UserSiteMapping.class);
			
				if(user == null)
					return RestUtils.constructError(Errors.INVALID_INPUT);
				System.out.println("falg"+user.getFlag());
				//System.out.println("user"+role.getRole_id());
				
				delegateOperations(user,dataSet,responseObj);
			}
			String str = gson.toJson(responseObj);
			return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
			
		} catch (UserMangementException e) {
			e.printStackTrace();
			return RestUtils.constructError(e.getError());
		}
		
	}
	
	private void delegateOperations(UserSiteMapping user, JSONArray dataSet, ResponseBO responseObj) throws UserMangementException {
		if(user.getFlag().equalsIgnoreCase("r")) {
			getUserSiteMapping(user.getUser_id(),dataSet,responseObj);
		} else if(user.getFlag().equalsIgnoreCase("u")) {
			dao.updateUserSiteMapping(user);
			responseObj.setErrorCode("1");
			responseObj.setError("");
			responseObj.setStatus("Success");
		}else if(user.getFlag().equalsIgnoreCase("d")) {
			//dao.deleteUserSiteMapping(user.getUser_id());
			responseObj.setErrorCode("1");
			responseObj.setError("");
			responseObj.setStatus("Success");
		}
	}
	
	private void getUserSiteMapping(String user,JSONArray dataSet, ResponseBO responseObj) throws UserMangementException{
		if(user !=null && !user.isEmpty() && !user.equals("default")) {
			UserSiteMapping response = dao.getUserSiteMapping(user);
			if(response == null)
				throw new UserMangementException(Errors.ROLE_SCREEN_NOT_FOUND);
			dataSet.add(gson.toJson(response));
			responseObj.setDataArr(dataSet);
		}else {
			List<UserSiteMapping> users = dao.getUserSiteMappingList();
			if(users.isEmpty())
				throw new UserMangementException(Errors.ROLE_SCREEN_NOT_FOUND);
			dataSet.add(gson.toJson(users));
			responseObj.setDataArr(dataSet);
     	}
		responseObj.setErrorCode("0000");
		responseObj.setError("");
		responseObj.setStatus("Success");
	}
	
	private void createUserMapping(UserSiteMapping[] userSiteMapping,JSONArray dataSet, ResponseBO responseObj) throws UserMangementException {
		String res = dao.createUserSiteMapping(userSiteMapping);
		if(res.isEmpty()) {
			dataSet.add(gson.toJson(userSiteMapping));
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
	
	@POST
	@Consumes({ "application/json" })
	@Path("/newROList")
	@RequestLogger
	public Response getNewROList(@Context HttpHeaders headers, @Context UriInfo ui,
			@Context ServletContext context, String reqJsonString) {
		String htoken = null;
		String str = null;
		JSONArray dataSet = new JSONArray();
		ResponseBO responseObj = new ResponseBO();
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);
				String userId = null;
            
		    if(htoken == null)
		    	return RestUtils.constructError(Errors.INVALID_TOKEN);
		    UserSiteMapping userBO = gson.fromJson(reqJsonString,UserSiteMapping.class);;
			userId = userBO.getUser_id();

			dataSet = dao.newROList(userId);
			responseObj.setDataSet(dataSet);
			responseObj.setError(null);
			responseObj.setErrorCode("0000");
			responseObj.setStatus("Success");
			
			
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getROList");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}
		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	@POST
	@Consumes({ "application/json" })
	@Path("/existingROList")
	@RequestLogger
	public Response getExistingROList(@Context HttpHeaders headers, @Context UriInfo ui, 
			@Context ServletContext context, String reqJsonString) {
		
		String htoken = null;
		String str = null;
		ResponseBO responseObj = new ResponseBO();
		JSONArray dataSet = new JSONArray();

		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);
				String userId = null;
            
		    if(htoken == null)
		    	return RestUtils.constructError(Errors.INVALID_TOKEN);
		    UserSiteMapping userBO = gson.fromJson(reqJsonString,UserSiteMapping.class);;
			userId = userBO.getUser_id();

			dataSet = dao.existingROList(userId);
			responseObj.setDataSet(dataSet);
			responseObj.setError(null);
			responseObj.setErrorCode("0000");
			responseObj.setStatus("Success");
			
			
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @getROList");
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}
		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
}
