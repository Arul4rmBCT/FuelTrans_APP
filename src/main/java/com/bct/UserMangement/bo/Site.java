package com.bct.UserMangement.bo;

import java.sql.Date;

public class Site {
	
	private String siteID;
	private String siteName;
	private String clientId;
	private String country;
	private String siteType;
	private String dealerName;
	private String address1;
	private String address2;
	private String address3;
	private String city;
	private String pinCode;
	private String district;
	private String state;
	private String region;
	private String requestFlag;
	private String requestingUser;
	private String Flag;


	
	public String getFlag() {
		return Flag;
	}
	public void setFlag(String flag) {
		Flag = flag;
	}
	public String getSiteID() {
		return siteID;
	}
	public void setSiteID(String siteID) {
		this.siteID = siteID;
	}
	public String getSiteName() {
		return siteName;
	}
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getSiteType() {
		return siteType;
	}
	public void setSiteType(String siteType) {
		this.siteType = siteType;
	}
	public String getDealerName() {
		return dealerName;
	}
	public void setDealerName(String dealerName) {
		this.dealerName = dealerName;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getAddress3() {
		return address3;
	}
	public void setAddress3(String address3) {
		this.address3 = address3;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getPinCode() {
		return pinCode;
	}
	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getRequestFlag() {
		return requestFlag;
	}
	public void setRequestFlag(String requestFlag) {
		this.requestFlag = requestFlag;
	}
	public String getRequestingUser() {
		return requestingUser;
	}
	public void setRequestingUser(String requestingUser) {
		this.requestingUser = requestingUser;
	}
	
	
	
}