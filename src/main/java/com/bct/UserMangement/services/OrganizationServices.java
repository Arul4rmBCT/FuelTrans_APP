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

import com.bct.UserMangement.utils.RestUtils;
import com.bct.UserMangement.bo.ResponseBO;
import com.bct.UserMangement.bo.Organisation;
import com.bct.UserMangement.dao.OrganizationDAO;
import com.bct.UserMangement.utils.RequestLogger;
import com.google.gson.Gson;
import net.sf.json.JSONArray;

@Path("/v1")
public class OrganizationServices {
	
	OrganizationDAO orgDAO = new OrganizationDAO();
	
	@POST
	@Consumes({ "application/json" })
	@Path("/organization")
	@RequestLogger
	public Response userOperation(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context, String reqJsonString) {
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
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
					
					Organisation org= gson.fromJson(reqJsonString,Organisation.class);
					System.out.println("org flag"+org.getFlag());
					System.out.println("org"+org.getOrganization_code());
					if(org !=null) {
						if(org.getFlag().equalsIgnoreCase("c")) {
							orgDAO.createOrganization(org);
							dataSet.add(gson.toJson(org));
							responseObj.setDataArr(dataSet);
							responseObj.setErrorCode("0000");
							responseObj.setError("");
							responseObj.setStatus("Success");
						} else if(org.getFlag().equalsIgnoreCase("r")) {
							System.out.println("inside");
							if(org.getOrganization_code() !=null) {
								Organisation responseOrg = orgDAO.getOrganization(org.getOrganization_code());
								dataSet.add(gson.toJson(responseOrg));
								responseObj.setDataArr(dataSet);
							} else {
								List<Organisation> orgs = orgDAO.getOrganizationList();
								dataSet.add(gson.toJson(orgs));
								responseObj.setDataArr(dataSet);
							}
							responseObj.setErrorCode("0000");
							responseObj.setError("");
							responseObj.setStatus("Success");
						} else if(org.getFlag().equalsIgnoreCase("u")) {
							orgDAO.updateOrganization(org);
							responseObj.setErrorCode("1");
							responseObj.setError("");
							responseObj.setStatus("Success");
						}else if(org.getFlag().equalsIgnoreCase("d")) {
							orgDAO.deleteOrganization(org.getOrganization_code());
							responseObj.setErrorCode("1");
							responseObj.setError("");
							responseObj.setStatus("Success");
						}
				    } else {
						responseObj.setErrorCode("0");
						responseObj.setError("Error org is NULL");
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
	
	
	
}