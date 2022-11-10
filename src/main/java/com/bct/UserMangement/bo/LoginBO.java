package com.bct.UserMangement.bo;

import java.util.List;

import com.bct.HOS.App.BO.WidgetData;

public class LoginBO {

	public String userId;
	public String roleId;
	public String roleName;
	public String loginId;
	public WidgetData widgetdata;
	public String siteName;
	public String siteID;
	public List<UserMenu> UserMenu;
	public String ftToken;
	public boolean isToken;
	public boolean isWithFleet;
	public String userMail;
	public String userMobile;
	public boolean isGrouping;
	public String isSiteAutomated;
	

	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public String getSiteID() {
		return siteID;
	}
	public void setSiteID(String siteID) {
		this.siteID = siteID;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	public String getRoleName() {
		return roleName;
	}
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
	public String getLoginId() {
		return loginId;
	}
	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}
	public WidgetData getWidgetdata() {
		return widgetdata;
	}
	public void setWidgetdata(WidgetData widgetdata) {
		this.widgetdata = widgetdata;
	}
	public List<UserMenu> getUserMenu() {
		return UserMenu;
	}
	public void setUserMenu(List<UserMenu> userMenu) {
		UserMenu = userMenu;
	}
	public String getFtToken() {
		return ftToken;
	}
	public void setFtToken(String ftToken) {
		this.ftToken = ftToken;
	}
	public boolean isToken() {
		return isToken;
	}
	public void setToken(boolean isToken) {
		this.isToken = isToken;
	}
	public boolean isWithFleet() {
		return isWithFleet;
	}
	public void setWithFleet(boolean isWithFleet) {
		this.isWithFleet = isWithFleet;
	}
	public String getUserMail() {
		return userMail;
	}
	public void setUserMail(String userMail) {
		this.userMail = userMail;
	}
	public String getUserMobile() {
		return userMobile;
	}
	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}
	public boolean isGrouping() {
		return isGrouping;
	}
	public void setGrouping(boolean isGrouping) {
		this.isGrouping = isGrouping;
	}
	public String getIsSiteAutomated() {
		return isSiteAutomated;
	}
	public void setIsSiteAutomated(String isSiteAutomated) {
		this.isSiteAutomated = isSiteAutomated;
	}
	
	
}
