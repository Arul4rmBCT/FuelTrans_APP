package com.bct.UserMangement.services;

import java.util.HashMap;
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
import com.bct.HOS.App.utils.InMem;
import com.bct.UserMangement.bo.LoginResponse;
import com.bct.UserMangement.bo.ResponseBO;
import com.bct.UserMangement.bo.Role;
import com.bct.UserMangement.bo.User;
import com.bct.UserMangement.bo.UserSiteMapping;
import com.bct.UserMangement.dao.UserDAO;
import com.bct.UserMangement.dao.UserSiteMappingDAO;
import com.bct.UserMangement.utils.Errors;
import com.bct.UserMangement.utils.RequestLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.sf.json.JSONArray;

@Path("/v1")
public class UserServices {
	
	UserDAO userDAO = new UserDAO();
	UserSiteMappingDAO userSiteMappingDAO= new UserSiteMappingDAO();
	Gson gson = new Gson();

	@POST
	@Consumes({"application/json"})
	@Path("/auth")
	@RequestLogger
	public Response authenticate(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context, String reqJsonString) {
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
			if (htoken != null) {
				if (htoken != null) {
					
					JsonElement jsonElement =  new JsonParser().parse(reqJsonString);
					JsonObject jsonObject = jsonElement.getAsJsonObject();
					
					String username = jsonObject.get("loginId").getAsString();
					String password = jsonObject.get("password").getAsString();
					
					//System.out.println("pass"+username);
					//System.out.println("user"+password);
					
					LoginResponse loginRes = userDAO.authenticate(username, password);
					boolean isAuthenticated = (loginRes != null);
					if(isAuthenticated) {
						
						InMem mem = InMem.getInstance();
						HashMap hmRO = new HashMap();
						hmRO.put("RO_MAP", new com.bct.HOS.App.DAO.UserDAO().getUserSites(username));
						mem.put(username, hmRO);
						
						dataSet.add(gson.toJson(loginRes));
						responseObj.setDataArr(dataSet);
						responseObj.setStatus("Success");
					} else {
						responseObj.setErrorCode("0");
						responseObj.setError("Invalid username & password");
						responseObj.setStatus("Failed");
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setErrorCode("0");
			responseObj.setError("Error");
			responseObj.setStatus("Failed");
		}
		
		String str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	
	}
	
	@POST
	@Consumes({ "application/json" })
	@Path("/user")
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
                System.out.println("htoken"+htoken); 
                System.out.println("token"+token); 
			if (htoken != null) {
				if (htoken != null) {
					User user= gson.fromJson(reqJsonString,User.class);
					System.out.println("falg"+user.getFlag());
					System.out.println("user"+user.getUsername());
					
					delegateOperations(user, dataSet, responseObj);
							}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setErrorCode("0");
			responseObj.setError("Error");
			responseObj.setStatus("Failed");
		}
		
		String str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	private void delegateOperations(User user, JSONArray dataSet, ResponseBO responseObj) throws UserMangementException {
		if(user.getFlag().equalsIgnoreCase("c")) {
			createUser(user, dataSet, responseObj);
		} else if(user.getFlag().equalsIgnoreCase("r")) {
			getUsers(user.getUsername(),dataSet,responseObj);
		} else if(user.getFlag().equalsIgnoreCase("u")) {
			userDAO.updateUser(user);
			responseObj.setErrorCode("1");
			responseObj.setError("");
			responseObj.setStatus("Success");
		}else if(user.getFlag().equalsIgnoreCase("d")) {
			userDAO.deleteUser(user.getUsername());
			responseObj.setErrorCode("1");
			responseObj.setError("");
			responseObj.setStatus("Success");
		}
	}
	
	private void getUsers(String user,JSONArray dataSet, ResponseBO responseObj) throws UserMangementException{
		if(user !=null && !user.isEmpty()) {
			User response = userDAO.getUser(user);
			if(response == null)
				throw new UserMangementException(Errors.USER_NOT_FOUND);
			dataSet.add(gson.toJson(response));
			responseObj.setDataArr(dataSet);
		}else {
			List<User> users = userDAO.getUserList();
			if(users.isEmpty())
				throw new UserMangementException(Errors.USER_NOT_FOUND);
			dataSet.add(gson.toJson(users));
			responseObj.setDataArr(dataSet);
     	}
		responseObj.setErrorCode("0000");
		responseObj.setError("");
		responseObj.setStatus("Success");
	}
	
	private void createUser(User user,JSONArray dataSet, ResponseBO responseObj) throws UserMangementException {
		String res = userDAO.createUser(user);;
		if(res.isEmpty()) {
			dataSet.add(gson.toJson(user));
			responseObj.setDataArr(dataSet);
			responseObj.setErrorCode("0000");
			responseObj.setError("");
			responseObj.setStatus("Success");
		}else {
			if(res.contains("violates unique constraint")) { 
				throw new UserMangementException(Errors.USER_ROLE_UNIQUE);
			} else {
				throw new UserMangementException(Errors.INVALID,res);	
			}
		}	
		
	}
	
	
	
}