package com.bct.HOS.App;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.bct.HOS.App.utils.DynamicFeatureRegister;

@ApplicationPath("income")
public class IncomeApp extends Application{
	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> classes = new HashSet<Class<?>>();

	public IncomeApp() {
		singletons.add(new IncomeMSrvServices());
		classes.add(DynamicFeatureRegister.class);
	}
	
	@Override
	public Set<Class<?>> getClasses()
	{
	  return classes;
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
	
	
}
