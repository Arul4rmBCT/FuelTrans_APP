package com.bct.HOS.App.BO;

import java.util.ArrayList;

public class NCBO {

	String userId = null;
	String roleId = null;
	String country = null;
	String ncName = null;
	String description = null;
	String status = null;
	String ymlData = null;
	String createdBy = null;
	String notificationType = null;
	String toType = null;
	String to = null;
	String ccType = null;
	String cc = null;
	String subject = null;
	String template = null;
	String siteIDs = null;
	ArrayList<String> alarmType = null;
	ArrayList<String> counties = null;
	ArrayList<String> region = null;
	ArrayList<String> state = null;
	ArrayList<String> ro = null;

	public ArrayList<String> getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(ArrayList<String> alarmType) {
		this.alarmType = alarmType;
	}

	public ArrayList<String> getCounties() {
		return counties;
	}

	public void setCounties(ArrayList<String> counties) {
		this.counties = counties;
	}

	public ArrayList<String> getRegion() {
		return region;
	}

	public void setRegion(ArrayList<String> region) {
		this.region = region;
	}

	public ArrayList<String> getState() {
		return state;
	}

	public void setState(ArrayList<String> state) {
		this.state = state;
	}

	public ArrayList<String> getRo() {
		return ro;
	}

	public void setRo(ArrayList<String> ro) {
		this.ro = ro;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSiteID() {
		return siteIDs;
	}

	public void setSiteID(String siteIDs) {
		this.siteIDs = siteIDs;
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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getNcName() {
		return ncName;
	}

	public void setNcName(String ncName) {
		this.ncName = ncName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getYmlData() {
		return ymlData;
	}

	public void setYmlData(String ymlData) {
		this.ymlData = ymlData;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	public String getToType() {
		return toType;
	}

	public void setToType(String toType) {
		this.toType = toType;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getCcType() {
		return ccType;
	}

	public void setCcType(String ccType) {
		this.ccType = ccType;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

}
