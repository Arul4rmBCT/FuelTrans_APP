package com.bct.HOS.App.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class WSInvocationNTService {

	 public Map<String,String> invokeWebService(String urlString, String payLoadStr, Map<String, String> parameterMap) {
		   String bodyAsString = getInputMessage(); //Provide Input SOAP Message
		   String userName = parameterMap.get("userName");
		   String password = parameterMap.get("password");
		   String domain = parameterMap.get("domain");
		   String workStation = parameterMap.get("workStation");
		   String soapAction = parameterMap.get("soapAction");
		   Map<String, String> returnMap = new HashMap<>();
		   CredentialsProvider credsProvider = new BasicCredentialsProvider();
		  /* credsProvider.setCredentials(AuthScope.ANY,
		      new NTCredentials("FSAS", "FS*1650$As", "srv22.almaha.com.om", "MAHA"));*/
		   credsProvider.setCredentials(AuthScope.ANY,
				      new NTCredentials(userName, password, workStation, domain));
		   HttpClient client = HttpClientBuilder.create().setDefaultCredentialsProvider(credsProvider).build();
		   HttpPost post = new HttpPost(urlString); //Provide Request URL
		   try {

		      StringEntity input = new StringEntity(payLoadStr);
		      input.setContentType("text/xml; charset=utf-8");
		      post.setEntity(input);
		      post.setHeader("Content-type", "text/xml; charset=utf-8");
		      //post.setHeader("SOAPAction", "urn:microsoft-dynamics-schemas/page/fserrorlog:ReadMultiple"); //Provide Soap action
		      post.setHeader("SOAPAction", soapAction);
		      org.apache.http.HttpResponse response = client.execute(post);
		      HttpEntity responseEntity = response.getEntity();
		      if (responseEntity != null) {
		    	  returnMap.put("responseStr", EntityUtils.toString(responseEntity));
		      }
		   } catch (IOException ex) {
			   returnMap.put("errorMsg", ex.toString());
			   System.out.println("Exception occurred while invoking Navision webservice: "+ex.getMessage());
		      ex.printStackTrace();
		   }
		   return returnMap;
		}
	 
	 private String getInputMessage() {
		 
		 String inputMsg = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:fser=\"urn:microsoft-dynamics-schemas/page/fserrorlog\"> " +
				   "<soapenv:Header/>" +
				   "<soapenv:Body>" +
				      "<fser:ReadMultiple>" +
				         "<fser:filter>" +
				            "<fser:Field></fser:Field>" +
				            "<fser:Criteria></fser:Criteria>" +
				         "</fser:filter>" +
				         "<fser:bookmarkKey></fser:bookmarkKey>" +
				         "<fser:setSize></fser:setSize>" +
				      "</fser:ReadMultiple>" +
				   "</soapenv:Body>" +
				"</soapenv:Envelope>";
		 return inputMsg;
	 }
}
