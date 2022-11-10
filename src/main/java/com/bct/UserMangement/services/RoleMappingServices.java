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
import com.bct.UserMangement.bo.Role;
import com.bct.UserMangement.bo.RoleScreenMapping;
import com.bct.UserMangement.dao.RoleScreenMappingDAO;
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
public class RoleMappingServices {
	
	RoleScreenMappingDAO dao = new RoleScreenMappingDAO();
	Gson gson = new Gson();
	
	@POST
	@Consumes({ "application/json" })
	@Path("/rolescreenmapping")
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
		    JsonElement jsonElement =  new JsonParser().parse(reqJsonString);
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			
			String flag = jsonObject.get("flag").getAsString();
			if(flag.equals("c")) {
				JsonArray rolesArray = jsonObject.get("roles").getAsJsonArray();
				RoleScreenMapping[] roles= gson.fromJson(rolesArray,RoleScreenMapping[].class);
				System.out.println(roles[0].getComponent_id()+roles[0].getRole_id());
				createRoles(roles, dataSet, responseObj);
				
			} else {
				RoleScreenMapping role= gson.fromJson(reqJsonString,RoleScreenMapping.class);
			
				if(role == null)
					return RestUtils.constructError(Errors.INVALID_INPUT);
				System.out.println("falg"+role.getFlag());
				System.out.println("user"+role.getRole_id());
				
				delegateOperations(role,dataSet,responseObj);
			}
			String str = gson.toJson(responseObj);
			return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
			
		} catch (UserMangementException e) {
			e.printStackTrace();
			return RestUtils.constructError(e.getError());
		}
		
	}
	
	private void delegateOperations(RoleScreenMapping role, JSONArray dataSet, ResponseBO responseObj) throws UserMangementException {
		if(role.getFlag().equalsIgnoreCase("r")) {
			getRoleScreenMapping(role.getRole_id(),dataSet,responseObj);
		} else if(role.getFlag().equalsIgnoreCase("u")) {
			dao.updateRoleScreenMapping(role);
			responseObj.setErrorCode("1");
			responseObj.setError("");
			responseObj.setStatus("Success");
		}else if(role.getFlag().equalsIgnoreCase("d")) {
			dao.deleteRoleScreenMapping(role.getRole_id());
			responseObj.setErrorCode("1");
			responseObj.setError("");
			responseObj.setStatus("Success");
		}
	}
	
	private void getRoleScreenMapping(Integer role,JSONArray dataSet, ResponseBO responseObj) throws UserMangementException{

		if(role !=0 ) {
			RoleScreenMapping response = dao.getRoleScreenMapping(role);
			if(response == null)
				throw new UserMangementException(Errors.ROLE_SCREEN_NOT_FOUND);
			dataSet.add(gson.toJson(response));
			responseObj.setDataArr(dataSet);
		}else {
			List<RoleScreenMapping> roles = dao.getRoleScreenMappingList();
			if(roles.isEmpty())
				throw new UserMangementException(Errors.ROLE_SCREEN_NOT_FOUND);
			dataSet.add(gson.toJson(roles));
			responseObj.setDataArr(dataSet);
     	}
		responseObj.setErrorCode("0000");
		responseObj.setError("");
		responseObj.setStatus("Success");
	}
	
	private void createRoles(RoleScreenMapping[] roles,JSONArray dataSet, ResponseBO responseObj) throws UserMangementException {
		String res = dao.createRoleScreenMapping(roles);
		if(res.isEmpty()) {
			dataSet.add(gson.toJson(roles));
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
