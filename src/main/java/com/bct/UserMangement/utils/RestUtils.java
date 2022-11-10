package com.bct.UserMangement.utils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class RestUtils {


	public static Response constructError(Errors errorCode) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("isError", true);
		jsonObject.addProperty("errorCode", errorCode.getCode());
		jsonObject.addProperty("error", errorCode.getErrorMsg());
		Response.ResponseBuilder res = Response.status(Status.OK).entity(new Gson().toJson(jsonObject));
		return res.build();
	}
	
	public static Response getResponse(Exception exception) {

		ResponseBuilder responseBuilder = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
		responseBuilder.type("text/html");
		responseBuilder.entity("<h1>Exception has occured  Refer log for more details</h1><br>The failure message is "
				+ exception.getMessage());
		responseBuilder.header("Access-Control-Allow-Origin", "*");
		return responseBuilder.build();
	}
	
	public static Response getReponse(Response.Status status, String message, String contentType) {
		Response.ResponseBuilder responseBuilder = Response.status(status);
		responseBuilder.type(contentType);
		responseBuilder.entity(message);
		//responseBuilder.header("Access-Control-Allow-Origin", "*");

		return responseBuilder.build();
	}
}
