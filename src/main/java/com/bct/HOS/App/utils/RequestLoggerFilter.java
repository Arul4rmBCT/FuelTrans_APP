package com.bct.HOS.App.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

public class RequestLoggerFilter implements ContainerRequestFilter, ContainerResponseFilter {
	
    @Context
    private HttpServletRequest httpServletRequest;


	public void filter(ContainerRequestContext requestContext) throws IOException {
		requestContext.getHeaders().add("InTime", String.valueOf(System.nanoTime()));
		String service = requestContext.getUriInfo().getAbsolutePath().toString();
		String remoteIpAddress;
        if (httpServletRequest != null) {
            remoteIpAddress = httpServletRequest.getRemoteAddr();
            requestContext.getHeaders().add("IPAddr", remoteIpAddress);
        	System.out.println("Service call request ("+service+") received @ "+getCurrentTime() +" from " +remoteIpAddress);
        }
        
	}

	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		String service = requestContext.getUriInfo().getAbsolutePath().toString();
		String strInTime = requestContext.getHeaders().getFirst("InTime");
		String remoteIpAddress = requestContext.getHeaders().getFirst("IPAddr");
		
		if (strInTime != null) {
			long startTime = Long.parseLong(strInTime);
			long stopTime = System.nanoTime();
			double seconds = (double) (stopTime - startTime) / 1000000000;
			System.out.println("Service call Time Taken (" + service + ") from ("+remoteIpAddress+") = " + seconds + " Seconds.");
		} else {
			System.out.println("No Request Time (" + service + ").");
		}
	}
	
	private static String getCurrentTime() {
		String currentTime = null;
		try {
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			currentTime =  sdf.format(cal.getTime());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return currentTime;
	}
}
