package com.bct.HOS.App.BO;

import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class UserBO {

	String langCode = null;
	String userId = null;
	String date = null;
	String roleId = null;
	String userName = null;
	String roleName = null;
	String loginId = null;
	String password = null;
	WidgetData widgetData = null;
	String status = null;
	String siteName = null;
	String recordCount = null;
	String country = null;
	String siteID = null;
	String notifyType = null;
	String fromDate = null;
	String toDate = null;
	String state = null;
	String region = null;
	String district = null;
	String subDistrict = null;
	String city = null;
	String division = null;
	String pump = null;
	String tank = null;
	String nozzle = null;
	JSONArray UserMenu = null;
	String ftToken = null;
	boolean isToken = false;
	String strToken = null;
	boolean isWithFleet = false;
	String oldPassword = null;
	String confPassword = null;
	String email = null;
	String userEmail = null;
	String userMobile = null;
	String param1 = null;
	String param2 = null;
	String param3 = null;
	String param4 = null;
	String param5 = null;
	String param6 = null;
	String param7 = null;
	String param8 = null;
	String otp = null;
	boolean isGrouping = false;
	String strMobOTP = null;
	String strEmailOTP = null;
	String groupData = null;
	String isSiteAutomated = null;
	
	

	public String getGroupData() {
		return groupData;
	}

	public void setGroupData(String groupData) {
		this.groupData = groupData;
	}

	public String getLangCode() {
		return langCode;
	}

	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}

	public String getStrMobOTP() {
		return strMobOTP;
	}

	public void setStrMobOTP(String strMobOTP) {
		this.strMobOTP = strMobOTP;
	}

	public String getStrEmailOTP() {
		return strEmailOTP;
	}

	public void setStrEmailOTP(String strEmailOTP) {
		this.strEmailOTP = strEmailOTP;
	}

	public boolean isGrouping() {
		return isGrouping;
	}

	public void setGrouping(boolean isGrouping) {
		this.isGrouping = isGrouping;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}

	public String getParam1() {
		return param1;
	}

	public void setParam1(String param1) {
		this.param1 = param1;
	}

	public String getParam2() {
		return param2;
	}

	public void setParam2(String param2) {
		this.param2 = param2;
	}

	public String getParam3() {
		return param3;
	}

	public void setParam3(String param3) {
		this.param3 = param3;
	}

	public String getParam4() {
		return param4;
	}

	public void setParam4(String param4) {
		this.param4 = param4;
	}

	public String getParam5() {
		return param5;
	}

	public void setParam5(String param5) {
		this.param5 = param5;
	}

	public String getParam6() {
		return param6;
	}

	public void setParam6(String param6) {
		this.param6 = param6;
	}

	public String getParam7() {
		return param7;
	}

	public void setParam7(String param7) {
		this.param7 = param7;
	}

	public String getParam8() {
		return param8;
	}

	public void setParam8(String param8) {
		this.param8 = param8;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getConfPassword() {
		return confPassword;
	}

	public void setConfPassword(String confPassword) {
		this.confPassword = confPassword;
	}

	public boolean isWithFleet() {
		return isWithFleet;
	}

	public void setWithFleet(boolean isWithFleet) {
		this.isWithFleet = isWithFleet;
	}

	public boolean isToken() {
		return isToken;
	}

	public void setToken(boolean isToken) {
		this.isToken = isToken;
	}

	public String getStrToken() {
		return strToken;
	}

	public void setStrToken(String strToken) {
		this.strToken = strToken;
	}

	public static boolean isNullOrEmpty(String str) {
		if (str != null && !str.isEmpty())
			return false;
		return true;
	}

	public String getSiteID() {

		if (isNullOrEmpty(siteID)) {
			return null;
		} else {
			if (!siteID.equalsIgnoreCase("null"))
				if (siteID.startsWith("'"))
					return siteID;
				else
					return "'" + siteID + "'";
			else
				return null;
		}
	}

	public String getNotifyType() {
		return notifyType;
	}

	public void setNotifyType(String notifyType) {
		this.notifyType = notifyType;
	}

	public void setSiteID(String siteID) {
		this.siteID = siteID;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
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

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public WidgetData getWidgetData() {
		return widgetData;
	}

	public void setWidgetData(WidgetData widgetData) {
		this.widgetData = widgetData;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(String recordCount) {
		this.recordCount = recordCount;
	}

	public String getPump() {
		return pump;
	}

	public void setPump(String pump) {
		this.pump = pump;
	}

	public String getTank() {
		return tank;
	}

	public void setTank(String tank) {
		this.tank = tank;
	}

	public String getNozzle() {
		return nozzle;
	}

	public void setNozzle(String nozzle) {
		this.nozzle = nozzle;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getSubDistrict() {
		return subDistrict;
	}

	public void setSubDistrict(String subDistrict) {
		this.subDistrict = subDistrict;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public JSONArray getUserMenu() {
		return UserMenu;
	}

	public void setUserMenu(JSONArray menuArray) {
		UserMenu = menuArray;
	}

	public String getFtToken() {
		return ftToken;
	}

	public void setFtToken(String ftToken) {
		this.ftToken = ftToken;
	}

	public String isSiteAutomated() {
		return isSiteAutomated;
	}

	public void setSiteAutomated(String isSiteAutomated) {
		this.isSiteAutomated = isSiteAutomated;
	}
}
