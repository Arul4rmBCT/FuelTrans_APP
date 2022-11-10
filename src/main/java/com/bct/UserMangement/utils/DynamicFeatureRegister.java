package com.bct.UserMangement.utils;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

@Provider
public class DynamicFeatureRegister implements DynamicFeature {

   
	public void configure(ResourceInfo resourceInfo, FeatureContext context) {
    	try {
    		//if (resourceInfo.getResourceMethod().isAnnotationPresent(RequestLogger.class)) {
              //  context.register(RequestLoggerFilter.class);
            //}
		} catch (Exception e) {
			System.out.println("Logger Error>>>>>>>>>");
			e.printStackTrace();
		}

    }

}