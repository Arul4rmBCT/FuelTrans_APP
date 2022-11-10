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
import com.bct.UserMangement.bo.Role;
import com.bct.UserMangement.dao.RoleDAO;
import com.bct.UserMangement.utils.Errors;
import com.bct.UserMangement.utils.RequestLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.sf.json.JSONArray;

@Path("/v1")
public class RoleServices {
	
	RoleDAO roleDAO = new RoleDAO();
	Gson gson = new Gson();
	
	@POST
	@Consumes({ "application/json" })
	@Path("/role")
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
            
		    if(htoken == null)
		    	return RestUtils.constructError(Errors.INVALID_TOKEN);
			
			Role role= gson.fromJson(reqJsonString,Role.class);
			if(role == null)
				return RestUtils.constructError(Errors.INVALID_INPUT);
			System.out.println("falg"+role.getFlag());
			System.out.println("user"+role.getRole_title());
			
			delegateOperations(role,dataSet,responseObj);
			
			String str = gson.toJson(responseObj);
			return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
			
		} catch (UserMangementException e) {
			e.printStackTrace();
			return RestUtils.constructError(e.getError());
		}
		
	}
	
	private void delegateOperations(Role role, JSONArray dataSet, ResponseBO responseObj) throws UserMangementException {
		if(role.getFlag().equalsIgnoreCase("c")) {
			createRoles(role,dataSet,responseObj);
		} else if(role.getFlag().equalsIgnoreCase("r")) {
			getRoles(role.getRole_title(),dataSet,responseObj);
		} else if(role.getFlag().equalsIgnoreCase("u")) {
			roleDAO.updateRole(role);
			responseObj.setErrorCode("1");
			responseObj.setError("");
			responseObj.setStatus("Success");
		}else if(role.getFlag().equalsIgnoreCase("d")) {
			roleDAO.deleteRole(role.getRole_title());
			responseObj.setErrorCode("1");
			responseObj.setError("");
			responseObj.setStatus("Success");
		}
	}
	private void getRoles(String role,JSONArray dataSet, ResponseBO responseObj) throws UserMangementException{
		if(role !=null && !role.isEmpty()) {
			Role response = roleDAO.getRole(role);
			if(response == null)
				throw new UserMangementException(Errors.ROLE_NOT_FOUND);
			dataSet.add(gson.toJson(response));
			responseObj.setDataArr(dataSet);
		}else {
			List<Role> roles = roleDAO.getRoleList();
			if(roles.isEmpty())
				throw new UserMangementException(Errors.ROLE_NOT_FOUND);
			dataSet.add(gson.toJson(roles));
			responseObj.setDataArr(dataSet);
     	}
		responseObj.setErrorCode("0000");
		responseObj.setError("");
		responseObj.setStatus("Success");
	}
	
	private void createRoles(Role role,JSONArray dataSet, ResponseBO responseObj) throws UserMangementException {
		String res = roleDAO.createRole(role);
		if(res.isEmpty()) {
			dataSet.add(gson.toJson(role));
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