package com.bct.HOS.App.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ServiceInvoker {

	public static JsonObject invokeService(String urlLink, String jsonString) {
		
		String responseJson = "";
		try {
			
			//System.out.println("URL"+urlLink);
			URL url = new URL(urlLink);
			//System.out.println("URL"+ url.toString());
			JsonParser parser = new JsonParser();
			JsonObject reqObj = (JsonObject) parser.parse(jsonString);
					
			byte[] postDataBytes = reqObj.toString().getBytes("UTF-8");
			System.out.println("request" + reqObj);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(postDataBytes);
			
			int status = conn.getResponseCode();
			StringBuilder sb = new StringBuilder();
			
				BufferedReader br = new BufferedReader(new InputStreamReader(status >=400 ? conn.getErrorStream() : conn.getInputStream()));
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line + "\n");
				}
				br.close();
			
			responseJson = sb.toString();
			System.out.println("responseJson>  "+responseJson);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		JsonParser parser = new JsonParser();
		JsonObject responseObj = (JsonObject) parser.parse(responseJson);
		return responseObj;
	}
}
