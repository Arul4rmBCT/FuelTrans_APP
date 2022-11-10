package com.bct.HOS.App.BO;

import java.util.ArrayList;

import net.sf.json.JSONArray;

public class hdrcache {

	String strRoleId;
	String USER_NAME;
	String ROLE_NAME;
	String strToken;
	String hdnAuthToken;

	String strCustomerCode = null;
	String strCustomerName = null;
	String strPhone1 = null;
	String strPhone2 = null;
	String strEmail = null;
	String strAddress = null;
	String strArea = null;
	String strCity = null;
	String strState = null;
	String strCountry = null;
	String strZipCode = null;
	String strCreatedBy = null;
	String dtCreatedDate = null;
	String strModifiedBy = null;
	String dtModifiedDate = null;
	String strOrgId = null;
	String strStatus = null;
	String strCustomerCategory = null;
	String strORPICCustCode = null;
	String strAccNo = null;
	String strContactAccNo = null;
	String strItemAccNo = null;
	String strDistAccNo = null;
	String strFuelAccNo = null;
	String strSiteNo = null;
	String strItemSiteNo = null;
	String strDistSiteNo = null;
	String strFuelSiteNo = null;
	String strParamAccNo = null;
	String strParamSiteNo = null;
	String strMobNumber = null;
	String strEmailId = null;

	public String getStrMobNumber() {
		return strMobNumber;
	}

	public void setStrMobNumber(String strMobNumber) {
		this.strMobNumber = strMobNumber;
	}

	public String getStrEmailId() {
		return strEmailId;
	}

	public void setStrEmailId(String strEmailId) {
		this.strEmailId = strEmailId;
	}

	public String getHdnAuthToken() {
		return hdnAuthToken;
	}

	public void setHdnAuthToken(String hdnAuthToken) {
		this.hdnAuthToken = hdnAuthToken;
	}

	public String getStrRoleId() {
		return strRoleId;
	}

	public void setStrRoleId(String strRoleId) {
		this.strRoleId = strRoleId;
	}

	public String getUSER_NAME() {
		return USER_NAME;
	}

	public void setUSER_NAME(String uSER_NAME) {
		USER_NAME = uSER_NAME;
	}

	public String getROLE_NAME() {
		return ROLE_NAME;
	}

	public void setROLE_NAME(String rOLE_NAME) {
		ROLE_NAME = rOLE_NAME;
	}

	public String getStrToken() {
		return strToken;
	}

	public void setStrToken(String strToken) {
		this.strToken = strToken;
	}

	public String getStrCustomerCode() {
		return strCustomerCode;
	}

	public void setStrCustomerCode(String strCustomerCode) {
		this.strCustomerCode = strCustomerCode;
	}

	public String getStrCustomerName() {
		return strCustomerName;
	}

	public void setStrCustomerName(String strCustomerName) {
		this.strCustomerName = strCustomerName;
	}

	public String getStrPhone1() {
		return strPhone1;
	}

	public void setStrPhone1(String strPhone1) {
		this.strPhone1 = strPhone1;
	}

	public String getStrPhone2() {
		return strPhone2;
	}

	public void setStrPhone2(String strPhone2) {
		this.strPhone2 = strPhone2;
	}

	public String getStrEmail() {
		return strEmail;
	}

	public void setStrEmail(String strEmail) {
		this.strEmail = strEmail;
	}

	public String getStrAddress() {
		return strAddress;
	}

	public void setStrAddress(String strAddress) {
		this.strAddress = strAddress;
	}

	public String getStrArea() {
		return strArea;
	}

	public void setStrArea(String strArea) {
		this.strArea = strArea;
	}

	public String getStrCity() {
		return strCity;
	}

	public void setStrCity(String strCity) {
		this.strCity = strCity;
	}

	public String getStrState() {
		return strState;
	}

	public void setStrState(String strState) {
		this.strState = strState;
	}

	public String getStrCountry() {
		return strCountry;
	}

	public void setStrCountry(String strCountry) {
		this.strCountry = strCountry;
	}

	public String getStrZipCode() {
		return strZipCode;
	}

	public void setStrZipCode(String strZipCode) {
		this.strZipCode = strZipCode;
	}

	public String getStrCreatedBy() {
		return strCreatedBy;
	}

	public void setStrCreatedBy(String strCreatedBy) {
		this.strCreatedBy = strCreatedBy;
	}

	public String getDtCreatedDate() {
		return dtCreatedDate;
	}

	public void setDtCreatedDate(String dtCreatedDate) {
		this.dtCreatedDate = dtCreatedDate;
	}

	public String getStrModifiedBy() {
		return strModifiedBy;
	}

	public void setStrModifiedBy(String strModifiedBy) {
		this.strModifiedBy = strModifiedBy;
	}

	public String getDtModifiedDate() {
		return dtModifiedDate;
	}

	public void setDtModifiedDate(String dtModifiedDate) {
		this.dtModifiedDate = dtModifiedDate;
	}

	public String getStrOrgId() {
		return strOrgId;
	}

	public void setStrOrgId(String strOrgId) {
		this.strOrgId = strOrgId;
	}

	public String getStrStatus() {
		return strStatus;
	}

	public void setStrStatus(String strStatus) {
		this.strStatus = strStatus;
	}

	public String getStrCustomerCategory() {
		return strCustomerCategory;
	}

	public void setStrCustomerCategory(String strCustomerCategory) {
		this.strCustomerCategory = strCustomerCategory;
	}

	public String getStrORPICCustCode() {
		return strORPICCustCode;
	}

	public void setStrORPICCustCode(String strORPICCustCode) {
		this.strORPICCustCode = strORPICCustCode;
	}

	public String getStrAccNo() {
		return strAccNo;
	}

	public void setStrAccNo(String strAccNo) {
		this.strAccNo = strAccNo;
	}

	public String getStrContactAccNo() {
		return strContactAccNo;
	}

	public void setStrContactAccNo(String strContactAccNo) {
		this.strContactAccNo = strContactAccNo;
	}

	public String getStrItemAccNo() {
		return strItemAccNo;
	}

	public void setStrItemAccNo(String strItemAccNo) {
		this.strItemAccNo = strItemAccNo;
	}

	public String getStrDistAccNo() {
		return strDistAccNo;
	}

	public void setStrDistAccNo(String strDistAccNo) {
		this.strDistAccNo = strDistAccNo;
	}

	public String getStrFuelAccNo() {
		return strFuelAccNo;
	}

	public void setStrFuelAccNo(String strFuelAccNo) {
		this.strFuelAccNo = strFuelAccNo;
	}

	public String getStrSiteNo() {
		return strSiteNo;
	}

	public void setStrSiteNo(String strSiteNo) {
		this.strSiteNo = strSiteNo;
	}

	public String getStrItemSiteNo() {
		return strItemSiteNo;
	}

	public void setStrItemSiteNo(String strItemSiteNo) {
		this.strItemSiteNo = strItemSiteNo;
	}

	public String getStrDistSiteNo() {
		return strDistSiteNo;
	}

	public void setStrDistSiteNo(String strDistSiteNo) {
		this.strDistSiteNo = strDistSiteNo;
	}

	public String getStrFuelSiteNo() {
		return strFuelSiteNo;
	}

	public void setStrFuelSiteNo(String strFuelSiteNo) {
		this.strFuelSiteNo = strFuelSiteNo;
	}

	public String getStrParamAccNo() {
		return strParamAccNo;
	}

	public void setStrParamAccNo(String strParamAccNo) {
		this.strParamAccNo = strParamAccNo;
	}

	public String getStrParamSiteNo() {
		return strParamSiteNo;
	}

	public void setStrParamSiteNo(String strParamSiteNo) {
		this.strParamSiteNo = strParamSiteNo;
	}

}
