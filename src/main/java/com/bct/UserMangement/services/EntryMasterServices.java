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

import com.bct.UserMangement.bo.EntryMaster;
import com.bct.UserMangement.bo.ResponseBO;
import com.bct.UserMangement.dao.EntryMasterDAO;
import com.bct.UserMangement.utils.Errors;
import com.bct.UserMangement.utils.RequestLogger;
import com.bct.UserMangement.utils.RestUtils;
import com.bct.UserMangement.utils.UserMangementException;
import com.google.gson.Gson;

import net.sf.json.JSONArray;

@Path("/v1")
public class EntryMasterServices {
	
	EntryMasterDAO dao = new EntryMasterDAO();
    Gson gson = new Gson();
	
	@POST
	@Consumes({ "application/json" })
	@Path("/entry")
	@RequestLogger
	public Response entryOperation(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context, String reqJsonString) {
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
		    EntryMaster entry= gson.fromJson(reqJsonString,EntryMaster.class);
			if(entry == null)
				return RestUtils.constructError(Errors.INVALID_INPUT);
			System.out.println("falg"+entry.getFlag());
			System.out.println("user"+entry.getEntry_code());
			
			delegateOperations(entry,dataSet,responseObj);
			
			String str = gson.toJson(responseObj);
			return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
			
		} catch (UserMangementException e) {
			e.printStackTrace();
			return RestUtils.constructError(e.getError());
		}
		
	}
	
	private void delegateOperations(EntryMaster entry, JSONArray dataSet, ResponseBO responseObj) throws UserMangementException {
		if(entry.getFlag().equalsIgnoreCase("c")) {
			createEntry(entry,dataSet,responseObj);
		} else if(entry.getFlag().equalsIgnoreCase("r")) {
			getEntries(entry.getEntry_code(),dataSet,responseObj);
		/*} else if(entry.getFlag().equalsIgnoreCase("u")) {
			dao.updateRole(entry);
			responseObj.setErrorCode("1");
			responseObj.setError("");
			responseObj.setStatus("Success");*/
		}else if(entry.getFlag().equalsIgnoreCase("d")) {
			dao.deleteEntry(entry.getEntry_code());
			responseObj.setErrorCode("1");
			responseObj.setError("");
			responseObj.setStatus("Success");
		}
	}

	private void getEntries(String entryCode,JSONArray dataSet, ResponseBO responseObj) throws UserMangementException{
		if(entryCode !=null && !entryCode.isEmpty()) {
			EntryMaster response = dao.getEntry(entryCode);
			if(response == null)
				throw new UserMangementException(Errors.ENTRY_NOT_FOUND);
			dataSet.add(gson.toJson(response));
			responseObj.setDataArr(dataSet);
		}else {
			List<EntryMaster> entries = dao.getEntryList();
			if(entries.isEmpty())
				throw new UserMangementException(Errors.ENTRY_NOT_FOUND);
			dataSet.add(gson.toJson(entries));
			responseObj.setDataArr(dataSet);
     	}
		responseObj.setErrorCode("0000");
		responseObj.setError("");
		responseObj.setStatus("Success");
	}
	
	private void createEntry(EntryMaster entry, JSONArray dataSet, ResponseBO responseObj) throws UserMangementException {
		String res = dao.createEntry(entry);
		if(res.isEmpty()) {
			dataSet.add(gson.toJson(entry));
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
