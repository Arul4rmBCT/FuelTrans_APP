package com.bct.HOS.App;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.bct.HOS.App.utils.DynamicFeatureRegister;
import com.bct.UserMangement.services.EntryMasterServices;
import com.bct.UserMangement.services.ModuleMasterServices;
import com.bct.UserMangement.services.OrganizationServices;
import com.bct.UserMangement.services.RoleMappingServices;
import com.bct.UserMangement.services.RoleServices;
import com.bct.UserMangement.services.ScreenMasterServices;
import com.bct.UserMangement.services.SiteServices;
import com.bct.UserMangement.services.UserServices;
import com.bct.UserMangement.services.UserSiteMappingServices;

@ApplicationPath("usrmng")
public class UserManagementApp extends Application{

	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> classes = new HashSet<Class<?>>();

	public UserManagementApp() {
		singletons.add(new UserServices());
		singletons.add(new OrganizationServices());
		singletons.add(new RoleServices());	
		singletons.add(new RoleMappingServices());
		singletons.add(new EntryMasterServices());
		singletons.add(new ModuleMasterServices());
		singletons.add(new ScreenMasterServices());
		singletons.add(new UserSiteMappingServices());
		singletons.add(new SiteServices());
		
	
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
