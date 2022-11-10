package com.bct.HOS.App;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.bct.HOS.App.BO.BankDetailsBO;
import com.bct.HOS.App.BO.LoginReqBO;
import com.bct.HOS.App.BO.ResponseBO;
import com.bct.HOS.App.DAO.BankDAO;
import com.bct.HOS.App.utils.HOSConfig;
import com.bct.HOS.App.utils.RequestLogger;
import com.bct.HOS.App.utils.RestUtils;
import com.bct.HOS.App.utils.ServiceInvoker;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.sf.json.JSONArray;


@Path("/v1")
public class LoginMSrvServices {
	
	@POST
	@Consumes({ "application/json" })
	@Path("/forgetPassword/")
	@RequestLogger
	public Response forgetPassword(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		LoginReqBO reqObj = null;
		JsonObject result =  null;
		
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					
					JsonElement jsonElement =  new JsonParser().parse(reqJsonString);
					JsonObject jsonObject = jsonElement.getAsJsonObject();
					
					String userId = jsonObject.get("user_id").getAsString();
					if (isNotNullAndNonEmpty(userId)) {
						
					    reqObj = LoginReqBO.frameForgetPasswordReq(userId);
						result = ServiceInvoker.invokeService(new HOSConfig().getValue("LOGIN_URL"), gson.toJson(reqObj));
						if (result != null) {
								responseObj.setDataObject(result);
								responseObj.setError(null);
								responseObj.setErrorCode("0000");
								responseObj.setStatus("Success");
						} 
						else {
								responseObj.setError("Unable to do forget password.");
								responseObj.setErrorCode("0002");
								responseObj.setStatus("ERROR");
						}
						
					} else {
						responseObj.setErrorCode("0");
						responseObj.setError("Error user Id NULL");
						responseObj.setStatus("Failed");
					}
				} else {
					responseObj.setError("Wrong Token @storeBankDetails");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @forgetPassword");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @forgetPassword" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	@POST
	@Consumes({ "application/json" })
	@Path("/enterOTP/")
	@RequestLogger
	public Response enterOTP(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		LoginReqBO reqObj = null;
		JsonObject result =  null;
		
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					
					JsonElement jsonElement =  new JsonParser().parse(reqJsonString);
					JsonObject jsonObject = jsonElement.getAsJsonObject();
					
					String otp = jsonObject.get("otp").getAsString();
					String newPwd = jsonObject.get("newpassword").getAsString();
					String confrimPwd = jsonObject.get("confrimpassword").getAsString();
					
					if (isNotNullAndNonEmpty(otp) && isNotNullAndNonEmpty(newPwd) && isNotNullAndNonEmpty(confrimPwd)) {
						
					    reqObj = LoginReqBO.frameEnterOTPReq(otp, newPwd, confrimPwd);
						result = ServiceInvoker.invokeService(new HOSConfig().getValue("LOGIN_URL"), gson.toJson(reqObj));
						if (result != null) {
								responseObj.setDataObject(result);
								responseObj.setError(null);
								responseObj.setErrorCode("0000");
								responseObj.setStatus("Success");
						} 
						else {
								responseObj.setError("Unable to do forget password.");
								responseObj.setErrorCode("0002");
								responseObj.setStatus("ERROR");
						}
						
					} else {
						responseObj.setErrorCode("0");
						responseObj.setError("Error user Id NULL");
						responseObj.setStatus("Failed");
					}
				} else {
					responseObj.setError("Wrong Token @storeBankDetails");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @forgetPassword");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @forgetPassword" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	@POST
	@Consumes({ "application/json" })
	@Path("/changePassword/")
	@RequestLogger
	public Response changePassword(@Context HttpHeaders headers, @Context UriInfo ui, @Context ServletContext context,
			String reqJsonString) {
		String str = null;
		String token = context.getInitParameter("authKey");
		String htoken = null;
		Gson gson = new Gson();
		ResponseBO responseObj = new ResponseBO();
		LoginReqBO reqObj = null;
		JsonObject result =  null;
		
		try {

			if (headers.getRequestHeader("token") != null && headers.getRequestHeader("token").size() > 0)
				htoken = headers.getRequestHeader("token").get(0);

			if (htoken != null) {
				if (htoken.equalsIgnoreCase(token)) {
					
					JsonElement jsonElement =  new JsonParser().parse(reqJsonString);
					JsonObject jsonObject = jsonElement.getAsJsonObject();
					
					String oldPwd = jsonObject.get("oldpassword").getAsString();
					String newPwd = jsonObject.get("newpassword").getAsString();
					String confrimPwd = jsonObject.get("confrimpassword").getAsString();
					String userId = jsonObject.get("user_id").getAsString();
					String ftToken = jsonObject.get("ft_token").getAsString();
					
					if (isNotNullAndNonEmpty(oldPwd) && isNotNullAndNonEmpty(newPwd) && isNotNullAndNonEmpty(confrimPwd) && isNotNullAndNonEmpty(userId)) {
					    reqObj = LoginReqBO.frameChangePasswordReq(oldPwd, newPwd, confrimPwd, userId, ftToken);
						result = ServiceInvoker.invokeService(new HOSConfig().getValue("LOGIN_URL"), gson.toJson(reqObj));
						if (result != null) {
								responseObj.setDataObject(result);
								responseObj.setError(null);
								responseObj.setErrorCode("0000");
								responseObj.setStatus("Success");
						} 
						else {
								responseObj.setError("Unable to do forget password.");
								responseObj.setErrorCode("0002");
								responseObj.setStatus("ERROR");
						}
						
					} else {
						responseObj.setErrorCode("0");
						responseObj.setError("Error user Id NULL");
						responseObj.setStatus("Failed");
					}
				} else {
					responseObj.setError("Wrong Token @storeBankDetails");
					responseObj.setErrorCode("9997");
					responseObj.setStatus("ERROR");
				}
			} else {
				responseObj.setError("Required header detail missing @forgetPassword");
				responseObj.setErrorCode("9998");
				responseObj.setStatus("ERROR");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			responseObj.setError("Unexpected Error @forgetPassword" + e.getMessage());
			responseObj.setErrorCode("9999");
			responseObj.setStatus("ERROR");
		}

		str = gson.toJson(responseObj);
		return RestUtils.getReponse(Response.Status.CREATED, str, "application/json");
	}
	
	public boolean isNotNullAndNonEmpty(String value) {
		return value != null && !value.isEmpty();
	}
	
}
